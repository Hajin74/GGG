package org.example.gggauthorization.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gggauthorization.auth.JwtService;
import org.example.gggauthorization.domain.entity.RefreshToken;
import org.example.gggauthorization.exception.CustomException;
import org.example.gggauthorization.exception.ErrorCode;
import org.example.gggauthorization.repository.RefreshTokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final Long ACCESS_TOKEN_EXPIRED_MS = 600000L;
    private final Long REFRESH_TOKEN_EXPIRED_MS = 86400000L;

    /*
     * Access Token 재발급 - Update
     * Refresh Token 을 가지고 Access Token 을 재발급 한다.
     */
    @PostMapping("/reissue")
    @Tag(name = "인증 관련 API")
    @Operation(summary = "Access Token 재발급", description = "Refresh Token 을 가지고 Access Token 을 재발급 한다.")
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
            jwtService.isExpired(refreshToken);
        } catch (ExpiredJwtException exception) {
            throw new CustomException(ErrorCode.TOKEN_IS_EXPIRED);
        }

        // 토큰 타입 확인
        String tokenType = jwtService.getTokenType(refreshToken);
        if (!tokenType.equals("RefreshToken")) {
            throw new CustomException(ErrorCode.INVALID_TOKEN_TYPE);
        }

        // 서버 DB에 저장된 토큰인지 확인
        boolean isExistedToken = refreshTokenRepository.existsByToken(refreshToken);
        if (!isExistedToken) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // 인증 정보 추출
        String username = jwtService.getUsername(refreshToken);
        Long id = jwtService.getId(refreshToken);

        // AccessToken, RefreshToken 재발급
        String newAccessToken = jwtService.createJwt("AccessToken", username, id, ACCESS_TOKEN_EXPIRED_MS);
        String newRefreshToken = jwtService.createJwt("RefreshToken", username, id, REFRESH_TOKEN_EXPIRED_MS);

        // 기존 RefreshToken 은 삭제하고, newRefreshToken 은 서버에 저장
        refreshTokenRepository.deleteByToken(refreshToken);
        addRefreshToken(username, newRefreshToken, REFRESH_TOKEN_EXPIRED_MS);

        // 응답 설정
        response.setHeader("AccessToken", newAccessToken);
        response.addCookie(createCookie("RefreshToken", newRefreshToken));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setHttpOnly(true);
        return cookie;
    }

    private void addRefreshToken(String username, String token, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshToken refreshToken = RefreshToken.builder()
                .username(username)
                .token(token)
                .expiration(date.toString())
                .build();
        refreshTokenRepository.save(refreshToken);
    }

}
