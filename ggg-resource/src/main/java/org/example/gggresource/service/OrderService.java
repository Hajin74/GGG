package org.example.gggresource.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gggresource.domain.entity.Order;
import org.example.gggresource.domain.entity.Product;
import org.example.gggresource.dto.*;
import org.example.gggresource.enums.OrderStatus;
import org.example.gggresource.enums.OrderType;
import org.example.gggresource.enums.ProductType;
import org.example.gggresource.exception.CustomException;
import org.example.gggresource.exception.ErrorCode;
import org.example.gggresource.repository.OrderRepository;
import org.example.gggresource.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;


    @Transactional
    public OrderCreateResponse createOrderBuy(UserResponse user, OrderCreateRequest request) {
        // 상품 가져오기
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // 판매용 상품만 구매 주문할 수 있게 예외 처리
        if (!product.getProductType().equals(ProductType.SELL)) {
            throw new CustomException(ErrorCode.PRODUCT_ONLY_FOR_PURCHASE);
        }

        // 사용자 정보 가져오기
        // todo: user 에 배달정보 컬럼 추가
        String deliverInfo = "서울시 마포구";

        // 주문 시간
        LocalDateTime orderDate = LocalDateTime.now();

        // 주문 번호
        String orderNumber = generateHumanReadableOrderNumber(orderDate, OrderType.BUY, request.productId(), request.customerId());

        // 주문 생성
        Order newOrder = Order.builder()
                .orderNumber(orderNumber)
                .orderProduct(product)
                .customerId(request.customerId())
                .orderPrice(product.getUnitPrice())
                .quantity(request.quantity())
                .totalPrice(getTotalPrice(request.quantity(), product.getUnitPrice()))
                .orderDate(orderDate)
                .deliverInfo(deliverInfo)
                .orderStatus(OrderStatus.ORDERED)
                .orderType(OrderType.BUY)
                .build();
        orderRepository.save(newOrder);

        return new OrderCreateResponse(
                newOrder.getOrderNumber(),
                newOrder.getOrderPrice(),
                newOrder.getQuantity(),
                newOrder.getTotalPrice(),
                deliverInfo
        );
    }

    @Transactional
    public OrderStatusUpdateResponse completeDeposit(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        // 주문 완료 상태인지 확인
        if (!order.getOrderStatus().equals(OrderStatus.ORDERED)) {
            throw new CustomException(ErrorCode.INVALID_ORDER_STATUS);
        }

        // 입금 완료
        order.completeDeposit();

        return new OrderStatusUpdateResponse(
                order.getOrderNumber(),
                order.getOrderStatus()
        );
    }

    @Transactional
    public OrderStatusUpdateResponse completeDelivery(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        // 입금 완료 상태인지 확인
        if (!order.getOrderStatus().equals(OrderStatus.PAID)) {
            throw new CustomException(ErrorCode.INVALID_ORDER_STATUS);
        }

        // 발송 완료
        order.completeDelivery();

        return new OrderStatusUpdateResponse(
                order.getOrderNumber(),
                order.getOrderStatus()
        );
    }

    @Transactional
    public void cancelOrderBuy(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        // 발송 완료 상태가 아닌지 확인
        if (order.getOrderStatus().equals(OrderStatus.DELIVERED)) {
            throw new CustomException(ErrorCode.ORDER_ALREADY_DELIVERED);
        }

        // 주문 취소
        order.cancelOrder();
    }

    @Transactional(readOnly = true)
    public PaginationResponse getOrderInvoices(UserResponse user, LocalDate date, OrderType invoiceType, PageRequest pageRequest) {

        LocalDateTime startOfDay = null;
        LocalDateTime endOfDay = null;

        if (date != null) {
            startOfDay = date.atStartOfDay();
            endOfDay = date.atTime(LocalTime.MAX);
        }

        Page<Order> orders = orderRepository.searchOrders(user.id(), startOfDay, endOfDay, invoiceType, pageRequest);

        List<InvoiceResponse> invoiceResponses = orders.getContent().stream()
                .map(order -> new InvoiceResponse(
                        order.getOrderNumber(),
                        order.getOrderPrice(),
                        order.getQuantity(),
                        order.getTotalPrice(),
                        order.getDeliverInfo()
                )).toList();

        LinkResponse linkResponse = generateLinks(orders, pageRequest, invoiceType, date);

        return new PaginationResponse(invoiceResponses, linkResponse);
    }

    private BigDecimal getTotalPrice(int quantity, BigDecimal unitPrice) {
        BigDecimal quantityAsBigDecimal = BigDecimal.valueOf(quantity);
        return unitPrice.multiply(quantityAsBigDecimal);
    }

    private String generateHumanReadableOrderNumber(LocalDateTime orderDate, OrderType orderType, Long productId, Long customerId) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
        String dateTime = orderDate.format(dateFormatter);

        // 상품 번호와 손님 번호를 4자리로 포맷
        String formattedProductId = String.format("%04d", productId);
        String formattedCustomerId = String.format("%04d", customerId);

        return String.format("ORD-%s-%s-%s-%s", dateTime, orderType, formattedProductId, formattedCustomerId);
    }

    private LinkResponse generateLinks(Page<Order> orders, PageRequest pageRequest, OrderType invoiceType, LocalDate date) {
        String baseUrl = "/api/orders";

        int limit = pageRequest.getPageSize();
        long offset = pageRequest.getOffset();
        long currentPage = (offset / limit) + 1;

        log.info("limit: {}, offset: {}, currentPage: {}", limit, offset, currentPage);

        String prev = null;
        if (pageRequest.hasPrevious() && currentPage <= orders.getTotalPages()) {
            prev = String.format("%s?date=%s&limit=%d&offset=%d&invoiceType=%s",
                    baseUrl,
                    date != null ? date.toString() : "",
                    limit,
                    Math.max(offset / limit - 1, 0),
                    invoiceType
            );
            log.info("prev: {}", prev);
        }

        String next = null;
        if (orders.hasNext()) {
            next = String.format("%s?date=%s&limit=%d&offset=%d&invoiceType=%s",
                    baseUrl,
                    date != null ? date.toString() : "",
                    limit,
                    offset / limit + 1,
                    invoiceType
            );
            log.info("next: {}, url: {}", pageRequest.next(), next);
        }

        return new LinkResponse(prev, next, currentPage, orders.getTotalPages(), orders.getTotalElements());
    }
}
