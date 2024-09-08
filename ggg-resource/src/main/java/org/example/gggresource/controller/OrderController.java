package org.example.gggresource.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gggresource.dto.OrderCreateRequest;
import org.example.gggresource.dto.OrderCreateResponse;
import org.example.gggresource.dto.OrderStatusUpdateResponse;
import org.example.gggresource.dto.UserResponse;
import org.example.gggresource.exception.CustomException;
import org.example.gggresource.exception.ErrorCode;
import org.example.gggresource.grpc.AuthServiceClient;
import org.example.gggresource.service.OrderService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        validateUser(accessToken);

        return orderService.completeTransfer(orderNumber);
    }

    /*
     * 구매 주문 입금 완료 처리 - Update
     * 소비자 입장에서 구매 입니다.
     * 주문 완료된 상태만 입금 완료 처리할 수 있습니다.
     */
    @PatchMapping("/{orderNumber}/completeDeposit")
    public OrderStatusUpdateResponse completeDeposit(@RequestHeader("accessToken") String accessToken, @PathVariable String orderNumber) {
        validateUser(accessToken);

        return orderService.completeDeposit(orderNumber);
    }

    /*
     * 구매 주문 발송 완료 처리 - Update
     * 소비자 입장에서 구매 입니다.
     * 입금 완료된 상태만 발송 완료 처리할 수 있습니다.
     */
    @PatchMapping("/{orderNumber}/completeDelivery")
    public OrderStatusUpdateResponse completeDelivery(@RequestHeader("accessToken") String accessToken, @PathVariable String orderNumber) {
        validateUser(accessToken);

        return orderService.completeDelivery(orderNumber);
    }

    /*
     * 판매 주문 수령 완료 처리 - Update
     * 소비자 입장에서 판매 입니다.
     * 송금 완료된 상태만 수령 완료 처리할 수 있습니다.
     */
    @PatchMapping("/{orderNumber}/completeReceipt")
    public OrderStatusUpdateResponse completeReceipt(@RequestHeader("accessToken") String accessToken, @PathVariable String orderNumber) {
        validateUser(accessToken);

        return orderService.completeReceipt(orderNumber);
    }

    /*
     * 구매 주문 취소 - Delete
     * 소비자 입장에서 구매 입니다.
     * 발송 완료 이전 주문만 구매 주문을 취소할 수 있습니다.
     */
    @DeleteMapping("/buy/{orderNumber}")
    public String cancelOrderBuy(@RequestHeader("accessToken") String accessToken, @PathVariable String orderNumber) {
        validateUser(accessToken);

        orderService.cancelOrderBuy(orderNumber);

        return "주문 번호 " + orderNumber + "가 취소 처리 되었습니다.";
    }

    /*
     * 판매 주문 취소 - Delete
     * 소비자 입장에서 판매 입니다.
     * 수령 완료 이전 주문만 판매 주문을 취소할 수 있습니다.
     */
    @DeleteMapping("/sell/{orderNumber}")
    public String cancelOrderSell(@RequestHeader("accessToken") String accessToken, @PathVariable String orderNumber) {
        validateUser(accessToken);

        orderService.cancelOrderSell(orderNumber);

        return "주문 번호 " + orderNumber + "가 취소 처리 되었습니다.";
    }

    private UserResponse validateUser(String accessToken) {
        UserResponse user = authServiceClient.authenticateUser(accessToken);
        if (!user.success()) {
            throw new CustomException(ErrorCode.AUTHORIZATION_FAILED);
        }
        return user;
    }

}
