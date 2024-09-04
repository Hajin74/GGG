package org.example.gggauthorization.service;

import lombok.RequiredArgsConstructor;
import org.example.gggauthorization.domain.entity.User;
import org.example.gggauthorization.dto.UserJoinRequest;
import org.example.gggauthorization.exception.CustomException;
import org.example.gggauthorization.exception.ErrorCode;
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
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTED);
        }

        User newUser = User.builder()
                .username(username)
                .password(bCryptPasswordEncoder.encode(password))
                .build();
        userRepository.save(newUser);
    }

}
