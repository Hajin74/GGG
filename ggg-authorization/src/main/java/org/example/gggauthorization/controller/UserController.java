package org.example.gggauthorization.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.gggauthorization.dto.UserJoinRequest;
import org.example.gggauthorization.exception.CustomException;
import org.example.gggauthorization.exception.ErrorCode;
import org.example.gggauthorization.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
    @Tag(name = "사용자 관련 API")
    @Operation(summary = "회원가입")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "회원가입 성공"),
                    @ApiResponse(responseCode = "400", description = "요청 형식이 잘못되었습니다.")
            })
    public ResponseEntity<String> joinUser(@RequestBody @Validated UserJoinRequest request) {
        userService.joinUser(request);
        return new ResponseEntity<>(request.username() + "님, 가입을 환영합니다.", HttpStatus.OK);
    }

    /*
     * 회원 탈퇴 - Delete
     * 발급했던 RefreshToken 과 사용자 정보를 삭제합니다.
     */
    @DeleteMapping
    @Tag(name = "사용자 관련 API")
    @Operation(summary = "회원탈퇴", description = "발급했던 RefreshToken 과 사용자 정보를 삭제합니다.")
    public ResponseEntity<String> signOut(@RequestHeader("accessToken") String accessToken) {
        userService.signOut(accessToken);
        return new ResponseEntity<>("회원탈퇴 되었습니다.", HttpStatus.OK);
    }

}
