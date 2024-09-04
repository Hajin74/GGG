package org.example.gggauthorization.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public CustomLoginFilter(AuthenticationManager authenticationManager, String customLoginUrl) {
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl(customLoginUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        // 클라이언트 요청에서 추출
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        // username 과 password 를 검증하기 위해 토큰에 담음
        // 아직 인증되지 않은 Authentication 객체를 생성했고, 인증이 완료되면 인증된 생성자로 Authentication 객체를 생성가 생성됨
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password, null);

        // 검증을 위해 AuthenticationManager 로 전달
        // AuthenticationProvider 객체를 통해 인증이 완료되면 인증된 Authentication 객체를 반환
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // todo: 성공 시 로직, jwt 를 발급할 것
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        // todo: 실패 시 로직
    }

}
