package org.example.gggauthorization.auth;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.gggauthorization.repository.RefreshTokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        if (!requestURI.matches("/api/users/logout")) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestMethod = request.getMethod();
        if (!requestMethod.equals("DELETE")) {
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("RefreshToken")) {
                refreshToken = cookie.getValue();
            }
        }

        if (refreshToken == null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }

        try {
            jwtService.isExpired(refreshToken);
        } catch (ExpiredJwtException exception) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }

        String tokenType = jwtService.getTokenType(refreshToken);
        if (!tokenType.equals("RefreshToken")) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }

        boolean isExistedToken = refreshTokenRepository.existsByToken(refreshToken);
        if (!isExistedToken) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }

        // 로그아웃 - 서버 DB 에서 토큰 제거
        refreshTokenRepository.deleteByToken(refreshToken);

        // 쿠키 초기화
        Cookie cookie = new Cookie("RefreshToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        response.setStatus(HttpStatus.OK.value());
    }

}
