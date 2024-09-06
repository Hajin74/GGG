package org.example.gggauthorization.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String token;

    private String expiration;

    @Builder
    public RefreshToken(String username, String token, String expiration) {
        this.username = username;
        this.token = token;
        this.expiration = expiration;
    }

}
