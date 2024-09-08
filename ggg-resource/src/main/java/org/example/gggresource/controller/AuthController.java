package org.example.gggresource.controller;

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
    public UserResponse authenticateUser(@RequestParam String accessToken) {
        return authServiceClient.authenticateUser(accessToken);
    }
}
