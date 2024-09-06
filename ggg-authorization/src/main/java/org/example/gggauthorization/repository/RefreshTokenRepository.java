package org.example.gggauthorization.repository;

import org.example.gggauthorization.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    boolean existsByToken(String token);

    @Transactional
    void deleteByToken(String token);
}
