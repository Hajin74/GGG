package org.example.gggresource.controller;

import lombok.RequiredArgsConstructor;
import org.example.gggresource.grpc.AuthServiceClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Service
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceClient authServiceClient;

    @GetMapping
    public String authenticateUser(@RequestParam String accessToken) {
        return authServiceClient.authenticateUser(accessToken);
    }
}
