package org.example.gggauthorization.auth;


import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.example.gggauthorization.domain.entity.User;
import org.example.gggauthorization.exception.CustomException;
import org.example.gggauthorization.exception.ErrorCode;
import org.example.gggauthorization.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtService {

    private SecretKey secretKey;
    private UserRepository userRepository;

    public JwtService(@Value("${spring.jwt.secret}") String secret, UserRepository userRepository) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.userRepository = userRepository;
    }

    public String getTokenType(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("tokenType", String.class);
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
    }

    public Long getId(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("id", Long.class);
    }

    public Boolean isExpired(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

    // 사용자 정보를 담아서 Jwt 발급
    public String createJwt(String tokenType, String username, Long id, Long expiredMs) {
        return Jwts.builder()
                .claim("tokenType", tokenType)
                .claim("username", username)
                .claim("id", id)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    // 토큰 유효성 검사
    public User validateAccessToken(String accessToken) {
        boolean isExpired = isExpired(accessToken);
        String tokenType = getTokenType(accessToken);

        if (isExpired) {
            throw new CustomException(ErrorCode.TOKEN_IS_EXPIRED);
        }

        if (!tokenType.equals("AccessToken")) {
            throw new CustomException(ErrorCode.INVALID_TOKEN_TYPE);
        }

        // 토큰에서 사용자 정보 획득
        String username = getUsername(accessToken);
        Long id = getId(accessToken);

        // DB 에서 사용자 배송지 정보 가져오기
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        String deliverAddress = user.getDeliverAddress();

        // 사용자 객체 반환
        return User.builder()
                .id(id)
                .username(username)
                .deliverAddress(deliverAddress)
                .build();
    }

}
