package org.example.gggauthorization.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.gggauthorization.dto.UserJoinRequest;
import org.example.gggauthorization.exception.CustomException;
import org.example.gggauthorization.exception.ErrorCode;
import org.example.gggauthorization.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    /*
     * 회원 가입 - Create
     * 사용자 이름과 비밀번호를 요청값으로 받습니다.
     */
    @PostMapping("/join")
    public void joinUser(@RequestBody @Valid UserJoinRequest request) {
        userService.joinUser(request);
    }

    /*
     * 회원 탈퇴 - Delete
     * 발급했던 RefreshToken 과 사용자 정보를 삭제합니다.
     */
    @DeleteMapping
    public void signOut(@RequestHeader("accessToken") String accessToken) {
        userService.signOut(accessToken);
    }

}
