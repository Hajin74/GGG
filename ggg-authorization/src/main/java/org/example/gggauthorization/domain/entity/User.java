package org.example.gggauthorization.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="users")
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    private String deliverAddress;

    @Builder
    public User(Long id, String username, String password, String deliverAddress) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.deliverAddress = deliverAddress;
    }

}
