package org.example.gggauthorization.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.gggauthorization.dto.UserLoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final Long EXPIRED_MS = 60 * 60 * 10L;

    public CustomLoginFilter(AuthenticationManager authenticationManager, String customLoginUrl, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl(customLoginUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        ObjectMapper objectMapper = new ObjectMapper();
        UserLoginRequest userLoginRequest;

        try {
            // RequestBody 에서 UserLoginRequest 를 읽어옴
            userLoginRequest = objectMapper.readValue(request.getInputStream(), UserLoginRequest.class);
        } catch (IOException exception) {
            throw new RuntimeException("로그인 요청 파라미터를 읽을 수 없습니다.", exception);
        }

        // 클라이언트 요청 RequestBody 에서 추출
        String username = userLoginRequest.username();
        String password = userLoginRequest.password();

        // username 과 password 를 검증하기 위해 토큰에 담음
        // 아직 인증되지 않은 Authentication 객체를 생성했고, 인증이 완료되면 인증된 생성자로 Authentication 객체를 생성가 생성됨
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password, null);

        // 검증을 위해 AuthenticationManager 로 전달
        // AuthenticationProvider 객체를 통해 인증이 완료되면 인증된 Authentication 객체를 반환
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // 로그인 성공하면, Jwt 발급
        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();
        String username = customUserDetails.getUsername();
        Long id = customUserDetails.getId();

        String token = jwtUtil.createJwt(username, id, EXPIRED_MS);

        response.addHeader("Authorization", "Bearer " + token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }

}
