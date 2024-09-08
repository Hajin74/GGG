package org.example.gggresource.service;

import lombok.RequiredArgsConstructor;
import org.example.gggresource.domain.entity.Order;
import org.example.gggresource.domain.entity.Product;
import org.example.gggresource.dto.OrderCreateRequest;
import org.example.gggresource.dto.OrderCreateResponse;
import org.example.gggresource.dto.OrderStatusUpdateResponse;
import org.example.gggresource.dto.UserResponse;
import org.example.gggresource.enums.OrderStatus;
import org.example.gggresource.enums.OrderType;
import org.example.gggresource.enums.ProductType;
import org.example.gggresource.exception.CustomException;
import org.example.gggresource.exception.ErrorCode;
import org.example.gggresource.repository.OrderRepository;
import org.example.gggresource.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
}
