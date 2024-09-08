package org.example.gggresource.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gggresource.dto.OrderCreateRequest;
import org.example.gggresource.dto.OrderCreateResponse;
import org.example.gggresource.dto.OrderStatusUpdateResponse;
import org.example.gggresource.dto.UserResponse;
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

    /* 구매 주문 생성 - Create
     * 소비자 입장에서 구매 입니다.
     * 판매용 상품만 구매 가능합니다.
     * todo: 주문 정보와 구매 타입에 맞는 주문이 생성됩니다.
     */
    @PostMapping("/buy")
    public OrderCreateResponse createOrderBuy(@RequestHeader("accessToken") String accessToken, @RequestBody @Validated OrderCreateRequest orderCreateRequest) {
        // 인증 서버에 토큰을 보내어 사용자 검증을 하고 사용자 정보를 응답값으로 받음
        UserResponse user = authServiceClient.authenticateUser(accessToken);

        return orderService.createOrderBuy(user, orderCreateRequest);
    }


    @PatchMapping("/{orderNumber}/completeDeposit")
    public OrderStatusUpdateResponse completeDeposit(@RequestHeader("accessToken") String accessToken, @PathVariable String orderNumber) {
        UserResponse user = authServiceClient.authenticateUser(accessToken);

        return orderService.completeDeposit(orderNumber);
    }

    @PatchMapping("/{orderNumber}/completeDelivery")
    public OrderStatusUpdateResponse completeDelivery(@RequestHeader("accessToken") String accessToken, @PathVariable String orderNumber) {
        UserResponse user = authServiceClient.authenticateUser(accessToken);

        return orderService.completeDelivery(orderNumber);
    }

}
