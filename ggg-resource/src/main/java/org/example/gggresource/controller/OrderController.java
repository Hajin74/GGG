package org.example.gggresource.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @Operation(summary = "구매 주문 생성", description = "소비자 입장에서 구매 입니다. 판매용 상품만 구매 가능합니다.")
    public ResponseEntity<OrderCreateResponse> createOrderBuy(@RequestHeader("accessToken") String accessToken, @RequestBody @Validated OrderCreateRequest orderCreateRequest) {
        UserResponse user = validateUser(accessToken);

        OrderCreateResponse orderCreateResponse = orderService.createOrderBuy(user, orderCreateRequest);
        return new ResponseEntity<>(orderCreateResponse, HttpStatus.OK);
    }

    /*
     * 판매 주문 생성 - Create
     * 소비자 입장에서 판매 입니다.
     * 매입용 상품만 구매 가능합니다.
     */
    @PostMapping("/sell")
    public ResponseEntity<OrderCreateResponse> createOrderSell(@RequestHeader("accessToken") String accessToken, @RequestBody @Validated OrderCreateRequest orderCreateRequest) {
        UserResponse user = validateUser(accessToken);

        OrderCreateResponse orderCreateResponse =  orderService.createOrderSell(user, orderCreateRequest);
        return new ResponseEntity<>(orderCreateResponse, HttpStatus.OK);
    }

    /*
     * 판매 주문 송금 완료 처리 - Update
     * 소비자 입장에서 판매 입니다.
     * 주문 완료된 상태만 송금 완료 처리할 수 있습니다.
     */
    @PatchMapping("/{orderNumber}/completeTransfer")
    public ResponseEntity<OrderStatusUpdateResponse> completeTransfer(@RequestHeader("accessToken") String accessToken, @PathVariable String orderNumber) {
        UserResponse user = validateUser(accessToken);

        OrderStatusUpdateResponse orderStatusUpdateResponse =  orderService.completeTransfer(user, orderNumber);
        return new ResponseEntity<>(orderStatusUpdateResponse, HttpStatus.OK);
    }

    /*
     * 구매 주문 입금 완료 처리 - Update
     * 소비자 입장에서 구매 입니다.
     * 주문 완료된 상태만 입금 완료 처리할 수 있습니다.
     */
    @PatchMapping("/{orderNumber}/completeDeposit")
    public ResponseEntity<OrderStatusUpdateResponse> completeDeposit(@RequestHeader("accessToken") String accessToken, @PathVariable String orderNumber) {
        UserResponse user = validateUser(accessToken);

        OrderStatusUpdateResponse orderStatusUpdateResponse =  orderService.completeDeposit(user, orderNumber);
        return new ResponseEntity<>(orderStatusUpdateResponse, HttpStatus.OK);
    }

    /*
     * 구매 주문 발송 완료 처리 - Update
     * 소비자 입장에서 구매 입니다.
     * 입금 완료된 상태만 발송 완료 처리할 수 있습니다.
     */
    @PatchMapping("/{orderNumber}/completeDelivery")
    public ResponseEntity<OrderStatusUpdateResponse> completeDelivery(@RequestHeader("accessToken") String accessToken, @PathVariable String orderNumber) {
        UserResponse user = validateUser(accessToken);

        OrderStatusUpdateResponse orderStatusUpdateResponse = orderService.completeDelivery(user, orderNumber);
        return new ResponseEntity<>(orderStatusUpdateResponse, HttpStatus.OK);
    }

    /*
     * 판매 주문 수령 완료 처리 - Update
     * 소비자 입장에서 판매 입니다.
     * 송금 완료된 상태만 수령 완료 처리할 수 있습니다.
     */
    @PatchMapping("/{orderNumber}/completeReceipt")
    public ResponseEntity<OrderStatusUpdateResponse> completeReceipt(@RequestHeader("accessToken") String accessToken, @PathVariable String orderNumber) {
        UserResponse user = validateUser(accessToken);

        OrderStatusUpdateResponse orderStatusUpdateResponse =  orderService.completeReceipt(user, orderNumber);
        return new ResponseEntity<>(orderStatusUpdateResponse, HttpStatus.OK);
    }

    /*
     * 구매 주문 취소 - Update
     * 소비자 입장에서 구매 입니다.
     * 발송 완료 이전 주문만 구매 주문을 취소할 수 있습니다.
     */
    @PatchMapping("/buy/{orderNumber}")
    public ResponseEntity<String> cancelOrderBuy(@RequestHeader("accessToken") String accessToken, @PathVariable String orderNumber) {
        UserResponse user = validateUser(accessToken);

        orderService.cancelOrderBuy(user, orderNumber);

        String message =  "주문 번호 " + orderNumber + "가 취소 처리 되었습니다.";
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    /*
     * 판매 주문 취소 - Update
     * 소비자 입장에서 판매 입니다.
     * 수령 완료 이전 주문만 판매 주문을 취소할 수 있습니다.
     */
    @PatchMapping("/sell/{orderNumber}")
    public ResponseEntity<String> cancelOrderSell(@RequestHeader("accessToken") String accessToken, @PathVariable String orderNumber) {
        UserResponse user = validateUser(accessToken);

        orderService.cancelOrderSell(user, orderNumber);

        String message =  "주문 번호 " + orderNumber + "가 취소 처리 되었습니다.";
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    /*
     * 주문 목록 조회 - Read
     * 사용자 권한에 맞는 invoice 를 출력합니다. 즉, 본인의 주문 건만 조회할 수 있습니다.
     * 날짜(date), 개수(limit), 데이터 위치(offset), 주문 유형(invoiceType) 을 입력받습니다.
     * 인보이스와 페이지네이션(이전 페이지(prev link), 현재 페이지(currentPage), 다음 페이지(next link), 총 페이지 수(totalPage), 총 아이템 수(totalItems)) 정보를 응답합니다.
     */
    @GetMapping
    public ResponseEntity<PaginationResponse> getOrderInvoices(@RequestHeader("accessToken") String accessToken,
                                               @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                               @RequestParam(value = "limit", defaultValue = "5") int limit,
                                               @RequestParam(value = "offset", defaultValue = "0") int offset,
                                               @RequestParam(value = "invoiceType", required = false) OrderType invoiceType) {
        UserResponse user = validateUser(accessToken);
        PageRequest pageRequest = PageRequest.of(offset, limit);

        PaginationResponse paginationResponse =  orderService.getOrderInvoices(user, date, invoiceType, pageRequest);
        return new ResponseEntity<>(paginationResponse, HttpStatus.OK);
    }

    /*
     * 주문 상세 조회 - Read
     * 주문번호, 주문일자, 주문자, 상태, 상품타입(품목), 수량, 금액, 배송지 정보를 응답합니다.
     */
    @GetMapping("/{orderNumber}")
    public ResponseEntity<OrderDetailResponse> getDetailOrder (@RequestHeader("accessToken") String accessToken, @PathVariable String orderNumber) {
        UserResponse user = validateUser(accessToken);
        OrderDetailResponse orderDetailResponse = orderService.getDetailOrder(user, orderNumber);
        return new ResponseEntity<>(orderDetailResponse, HttpStatus.OK);
    }

    /*
     * 주문 삭제 - Delete
     * 해당 주문은 DB 에서 데이터가 삭제되지 않고, 비활성화(soft deleted) 처리 됩니다.
     */
    @DeleteMapping("/{orderNumber}")
    public ResponseEntity<String> deleteOrder(@RequestHeader("accessToken") String accessToken, @PathVariable String orderNumber) {
        UserResponse user = validateUser(accessToken);

        orderService.deleteOrder(user, orderNumber);

        String message =  "주문 번호 " + orderNumber + "가 삭제 되었습니다.";
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    private UserResponse validateUser(String accessToken) {
        UserResponse user = authServiceClient.authenticateUser(accessToken);
        if (!user.success()) {
            throw new CustomException(ErrorCode.AUTHORIZATION_FAILED);
        }
        return user;
    }

}
