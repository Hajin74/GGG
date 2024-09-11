package org.example.gggauthorization.auth;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gggauthorization.domain.entity.User;
import org.example.gggauthorization.exception.CustomException;
import org.example.gggauthorization.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("[JwtFilter] 요청 URL: " + request.getRequestURI());

        // 특정 경로에 대해 필터 적용을 제외
        if (request.getRequestURI().startsWith("/api/users/join") || request.getRequestURI().startsWith("/api/users/login") || request.getRequestURI().startsWith("/api/auth/reissue")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 요청 Header 에서 AccessToken 추출
        String accessToken = request.getHeader("AccessToken");

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            throw new CustomException(ErrorCode.ACCESS_TOKEN_NOT_EXISTED);
        }


        // 토큰 만료 여부 확인
        try {
            jwtService.isExpired(accessToken);
        } catch (ExpiredJwtException exception) {
            PrintWriter writer = response.getWriter();
            writer.print("만료된 AccessToken 입니다.");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        // 토큰 타입 확인
        String tokenType = jwtService.getTokenType(accessToken);
        if (!tokenType.equals("AccessToken")) {
            PrintWriter writer = response.getWriter();
            writer.print("유효하지 않은 토큰입니다.");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        // 토큰에서 사용자 정보 획득 및 사용자 생성
        User user = User.builder()
                .id(jwtService.getId(accessToken))
                .username(jwtService.getUsername(accessToken))
                .password("tempPassword")
                .build();

        // UserDetails 에 사용자 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        // 인증 토큰 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, null);

        // 세션에 사용자 등록하여 현재 사용자가 인증되었다고 판단
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}
