package org.example.gggresource.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.gggresource.dto.UserResponse;
import org.example.gggresource.grpc.AuthServiceClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceClient authServiceClient;

    @GetMapping("/verify")
    @Tag(name = "gRPC 테스트용 API")
    @Operation(summary = "사용자 검증", description = "사용자 검증을 하고, 사용자 정보를 받습니다.")
    public UserResponse authenticateUser(@RequestParam String accessToken) {
        return authServiceClient.authenticateUser(accessToken);
    }
}
