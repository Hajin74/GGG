package org.example.gggauthorization.controller;

import lombok.RequiredArgsConstructor;
import org.example.gggauthorization.dto.UserJoinRequest;
import org.example.gggauthorization.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public void joinUser(UserJoinRequest request) {
        userService.joinUser(request);
    }

}
