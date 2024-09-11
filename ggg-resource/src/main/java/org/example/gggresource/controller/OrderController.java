package org.example.gggresource.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gggresource.dto.*;
import org.example.gggresource.enums.OrderType;
import org.example.gggresource.exception.CustomException;
import org.example.gggresource.exception.ErrorCode;
import org.example.gggresource.grpc.AuthServiceClient;
import org.example.gggresource.service.OrderService;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final AuthServiceClient authServiceClient;

    /*
     * 구매 주문 생성 - Create
     * 소비자 입장에서 구매 입니다.
     * 판매용 상품만 구매 가능합니다.
     */
    @PostMapping("/buy")
    public OrderCreateResponse createOrderBuy(@RequestHeader("accessToken") String accessToken, @RequestBody @Validated OrderCreateRequest orderCreateRequest) {
        UserResponse user = validateUser(accessToken);

        return orderService.createOrderBuy(user, orderCreateRequest);
    }

    /*
     * 판매 주문 생성 - Create
     * 소비자 입장에서 판매 입니다.
     * 매입용 상품만 구매 가능합니다.
     */
    @PostMapping("/sell")
    public OrderCreateResponse createOrderSell(@RequestHeader("accessToken") String accessToken, @RequestBody @Validated OrderCreateRequest orderCreateRequest) {
        UserResponse user = validateUser(accessToken);

        return orderService.createOrderSell(user, orderCreateRequest);
    }

    /*
     * 판매 주문 송금 완료 처리 - Update
     * 소비자 입장에서 판매 입니다.
     * 주문 완료된 상태만 송금 완료 처리할 수 있습니다.
     */
    @PatchMapping("/{orderNumber}/completeTransfer")
    public OrderStatusUpdateResponse completeTransfer(@RequestHeader("accessToken") String accessToken, @PathVariable String orderNumber) {
        UserResponse user = validateUser(accessToken);

        return orderService.completeTransfer(user, orderNumber);
    }

    /*
     * 구매 주문 입금 완료 처리 - Update
     * 소비자 입장에서 구매 입니다.
     * 주문 완료된 상태만 입금 완료 처리할 수 있습니다.
     */
    @PatchMapping("/{orderNumber}/completeDeposit")
    public OrderStatusUpdateResponse completeDeposit(@RequestHeader("accessToken") String accessToken, @PathVariable String orderNumber) {
        UserResponse user = validateUser(accessToken);

        return orderService.completeDeposit(user, orderNumber);
    }

    /*
     * 구매 주문 발송 완료 처리 - Update
     * 소비자 입장에서 구매 입니다.
     * 입금 완료된 상태만 발송 완료 처리할 수 있습니다.
     */
    @PatchMapping("/{orderNumber}/completeDelivery")
    public OrderStatusUpdateResponse completeDelivery(@RequestHeader("accessToken") String accessToken, @PathVariable String orderNumber) {
        UserResponse user = validateUser(accessToken);

        return orderService.completeDelivery(user, orderNumber);
    }

    /*
     * 판매 주문 수령 완료 처리 - Update
     * 소비자 입장에서 판매 입니다.
     * 송금 완료된 상태만 수령 완료 처리할 수 있습니다.
     */
    @PatchMapping("/{orderNumber}/completeReceipt")
    public OrderStatusUpdateResponse completeReceipt(@RequestHeader("accessToken") String accessToken, @PathVariable String orderNumber) {
        UserResponse user = validateUser(accessToken);

        return orderService.completeReceipt(user, orderNumber);
    }

    /*
     * 구매 주문 취소 - Update
     * 소비자 입장에서 구매 입니다.
     * 발송 완료 이전 주문만 구매 주문을 취소할 수 있습니다.
     */
    @PatchMapping("/buy/{orderNumber}")
    public String cancelOrderBuy(@RequestHeader("accessToken") String accessToken, @PathVariable String orderNumber) {
        UserResponse user = validateUser(accessToken);

        orderService.cancelOrderBuy(user, orderNumber);

        return "주문 번호 " + orderNumber + "가 취소 처리 되었습니다.";
    }

    /*
     * 판매 주문 취소 - Update
     * 소비자 입장에서 판매 입니다.
     * 수령 완료 이전 주문만 판매 주문을 취소할 수 있습니다.
     */
    @PatchMapping("/sell/{orderNumber}")
    public String cancelOrderSell(@RequestHeader("accessToken") String accessToken, @PathVariable String orderNumber) {
        UserResponse user = validateUser(accessToken);

        orderService.cancelOrderSell(user, orderNumber);

        return "주문 번호 " + orderNumber + "가 취소 처리 되었습니다.";
    }

    /*
     * 주문 목록 조회 - Read
     * 사용자 권한에 맞는 invoice 를 출력합니다. 즉, 본인의 주문 건만 조회할 수 있습니다.
     * 날짜(date), 개수(limit), 데이터 위치(offset), 주문 유형(invoiceType) 을 입력받습니다.
     * 인보이스와 페이지네이션(이전 페이지(prev link), 현재 페이지(currentPage), 다음 페이지(next link), 총 페이지 수(totalPage), 총 아이템 수(totalItems)) 정보를 응답합니다.
     */
    @GetMapping
    public PaginationResponse getOrderInvoices(@RequestHeader("accessToken") String accessToken,
                                               @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                               @RequestParam(value = "limit", defaultValue = "5") int limit,
                                               @RequestParam(value = "offset", defaultValue = "0") int offset,
                                               @RequestParam(value = "invoiceType", required = false) OrderType invoiceType) {
        UserResponse user = validateUser(accessToken);
        PageRequest pageRequest = PageRequest.of(offset, limit);

        return orderService.getOrderInvoices(user, date, invoiceType, pageRequest);
    }

    /*
     * 주문 상세 조회 - Read
     * 주문번호, 주문일자, 주문자, 상태, 상품타입(품목), 수량, 금액, 배송지 정보를 응답합니다.
     */
    @GetMapping("/{orderNumber}")
    public OrderDetailResponse getDetailOrder (@RequestHeader("accessToken") String accessToken, @PathVariable String orderNumber) {
        UserResponse user = validateUser(accessToken);
        return orderService.getDetailOrder(user, orderNumber);
    }

    /*
     * 주문 삭제 - Delete
     * 해당 주문은 DB 에서 데이터가 삭제되지 않고, 비활성화(soft deleted) 처리 됩니다.
     */
    @DeleteMapping("/{orderNumber}")
    public String deleteOrder(@RequestHeader("accessToken") String accessToken, @PathVariable String orderNumber) {
        UserResponse user = validateUser(accessToken);

        orderService.deleteOrder(user, orderNumber);

        return "주문 번호 " + orderNumber + "가 삭제 되었습니다.";
    }

    private UserResponse validateUser(String accessToken) {
        UserResponse user = authServiceClient.authenticateUser(accessToken);
        if (!user.success()) {
            throw new CustomException(ErrorCode.AUTHORIZATION_FAILED);
        }
        return user;
    }

}
