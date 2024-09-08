package org.example.gggresource.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gggresource.dto.OrderCreateRequest;
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
     * todo: 주문 정보와 구매 타입에 맞는 인보이스가 생성됩니다.
     */
    @PostMapping("/buy")
    public void createOrderBuy(@RequestHeader("accessToken") String accessToken, @RequestBody @Validated OrderCreateRequest orderCreateRequest) {
        // 인증 서버에 토큰을 보내어 사용자 검증을 하고 사용자 정보를 응답값으로 받음
        UserResponse user = authServiceClient.authenticateUser(accessToken);

        orderService.createOrderBuy(user, orderCreateRequest);

    }

}
