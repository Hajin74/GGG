package org.example.gggauthorization.service;

import lombok.RequiredArgsConstructor;
import org.example.gggauthorization.auth.JwtService;
import org.example.gggauthorization.domain.entity.User;
import org.example.gggauthorization.dto.UserJoinRequest;
import org.example.gggauthorization.exception.CustomException;
import org.example.gggauthorization.exception.ErrorCode;
import org.example.gggauthorization.repository.RefreshTokenRepository;
import org.example.gggauthorization.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;

    @Transactional
    public void joinUser(UserJoinRequest request) {
        String username = request.username();
        String password = request.password();

        boolean isExistedUser = userRepository.existsByUsername(username);
        if (isExistedUser) {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTED);
        }

        User newUser = User.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(password))
                .build();
        userRepository.save(newUser);
    }

    @Transactional
    public void signOut(String accessToken) {
        // 사용자 삭제
        User user = jwtService.validateAccessToken(accessToken);
        userRepository.delete(user);

        // Refresh Token 삭제
        refreshTokenRepository.deleteAllByUsername(user.getUsername());
    }

}
