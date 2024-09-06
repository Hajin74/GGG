package org.example.gggauthorization.controller;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gggauthorization.auth.JwtUtil;
import org.example.gggauthorization.exception.CustomException;
import org.example.gggauthorization.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final Long ACCESS_TOKEN_EXPIRED_MS = 600000L;

    @GetMapping("/test")
    public String test() {
        return "ok";
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        // Cookie 에서 Refresh 토큰 추출
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("RefreshToken")) {
                refreshToken = cookie.getValue();
            }
        }

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_EXISTED);
        }

        // 만료 여부 확인
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException exception) {
            throw new CustomException(ErrorCode.TOKEN_IS_EXPIRED);
        }

        // 토큰 타입 확인
        String tokenType = jwtUtil.getTokenType(refreshToken);
        if (!tokenType.equals("RefreshToken")) {
            throw new CustomException(ErrorCode.INVALID_TOKEN_TYPE);
        }

        // 인증 정보 추출
        String username = jwtUtil.getUsername(refreshToken);
        Long id = jwtUtil.getId(refreshToken);

        // AccessToken 재발급
        String newAccessToken = jwtUtil.createJwt("AccessToken", username, id, ACCESS_TOKEN_EXPIRED_MS);

        // 응답 설정
        response.setHeader("AccessToken", newAccessToken);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
