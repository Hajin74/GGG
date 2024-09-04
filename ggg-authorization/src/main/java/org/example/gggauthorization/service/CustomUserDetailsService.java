package org.example.gggauthorization.service;

import lombok.RequiredArgsConstructor;
import org.example.gggauthorization.auth.CustomUserDetails;
import org.example.gggauthorization.domain.entity.User;
import org.example.gggauthorization.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 실제 인증을 수행하는 AuthenticationManager 가 호출
        // username 을 받아서, 인증 과정에서 필요한 사용자 정보가 담긴 CustomUserDetails 객체를 반환

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + "님을 찾을 수 없습니다"));

        return new CustomUserDetails(user);
    }

}
