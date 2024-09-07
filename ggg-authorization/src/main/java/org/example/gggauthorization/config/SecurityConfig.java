package org.example.gggauthorization.config;

import lombok.RequiredArgsConstructor;
import org.example.gggauthorization.auth.CustomLoginFilter;
import org.example.gggauthorization.auth.CustomLogoutFilter;
import org.example.gggauthorization.auth.JwtFilter;
import org.example.gggauthorization.auth.JwtService;
import org.example.gggauthorization.repository.RefreshTokenRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // AuthenticationManager 가 인자로 받을 authenticationConfiguration 객체, 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;

    private final JwtService jwtService;

    private final RefreshTokenRepository refreshTokenRepository;

    @Bean // AuthenticationManager 빈 등록
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 로그인 필터 객체 생성 및 Url 커스텀
        CustomLoginFilter customLoginFilter = new CustomLoginFilter(refreshTokenRepository, authenticationManager(authenticationConfiguration), "/api/users/login", jwtService);

        // 로그아웃 필터 객체 생성
        CustomLogoutFilter customLogoutFilter = new CustomLogoutFilter(jwtService, refreshTokenRepository);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        /* 경로별 인가 작업 */
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/api/users/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated());

        /* 필터 등록 */
        http
                .addFilterAt(customLoginFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new JwtFilter(jwtService), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(customLogoutFilter, LogoutFilter.class);

        return http.build();
    }

}
