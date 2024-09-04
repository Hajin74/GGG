package org.example.gggauthorization.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.gggauthorization.domain.entity.User;
import org.example.gggauthorization.exception.CustomException;
import org.example.gggauthorization.exception.ErrorCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청 Header 에서 Authorization 추출
        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            throw new CustomException(ErrorCode.TOKEN_NOT_EXISTED);
        }

        // Token 추출
        String token = authorization.split(" ")[1];

        if (jwtUtil.isExpired(token)) {
            filterChain.doFilter(request, response);
            throw new CustomException(ErrorCode.TOKEN_IS_EXPIRED);
        }

        // 토큰에서 사용자 정보 획득 및 사용자 생성
        User user = User.builder()
                .id(jwtUtil.getId(token))
                .username(jwtUtil.getUsername(token))
                .password("tempPassword")
                .build();

        // UserDetails 에 사용자 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        // 인증 토큰 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, null);

        // 세션에 사용자 등록하여 현재 사용자가 인증되었다고 판단
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
