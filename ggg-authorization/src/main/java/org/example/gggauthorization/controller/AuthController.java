package org.example.gggauthorization.controller;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.gggauthorization.auth.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final Long ACCESS_TOKEN_EXPIRED_MS = 600000L;

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

        if (refreshToken == null) {
            return new ResponseEntity<>("Refresh Token 이 없습니다.", HttpStatus.BAD_REQUEST);
        }

        // 만료 여부 확인
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException exception) {
            return new ResponseEntity<>("만료된 토큰입니다.", HttpStatus.BAD_REQUEST);
        }

        // 토큰 타입 확인
        String tokenType = jwtUtil.getTokenType(refreshToken);
        if (!tokenType.equals("RefreshToken")) {
            return new ResponseEntity<>("유효하지 않은 토큰입니다", HttpStatus.BAD_REQUEST);
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
