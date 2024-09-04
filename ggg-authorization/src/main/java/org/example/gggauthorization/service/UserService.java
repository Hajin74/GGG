package org.example.gggauthorization.service;

import lombok.RequiredArgsConstructor;
import org.example.gggauthorization.domain.entity.User;
import org.example.gggauthorization.dto.UserJoinRequest;
import org.example.gggauthorization.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void joinUser(UserJoinRequest request) {
        String username = request.username();
        String password = request.password();

        boolean isExistedUser = userRepository.existsByUsername(username);
        if (isExistedUser) {
            // todo : 중복 예외 던지기
            return;
        }

        User newUser = User.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(password))
                .build();
        userRepository.save(newUser);
    }

}
