package org.example.gggauthorization.controller;

import lombok.RequiredArgsConstructor;
import org.example.gggauthorization.dto.UserJoinRequest;
import org.example.gggauthorization.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/api/users/join")
    public void joinUser(@RequestBody @Valid UserJoinRequest request) {
        userService.joinUser(request);
    }

    @GetMapping("/api/test")
    public String test() {
        return "ok";
    }

}
