package com.example.demo.user.infrastructure;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "address")
    private String address;

    @Column(name = "certification_code")
    private String certificationCode;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "last_login_at")
    private Long lastLoginAt;

    public User toModel() {
        return User.builder()
            .id(this.id)
            .email(this.email)
            .nickname(this.nickname)
            .address(this.address)
            .certificationCode(this.certificationCode)
            .status(this.status)
            .lastLoginAt(this.lastLoginAt)
            .build();
    }

    public static UserEntity fromModel(User user) {
        return UserEntity.builder()
            .id(user.getId())
            .email(user.getEmail())
            .nickname(user.getNickname())
            .address(user.getAddress())
            .certificationCode(user.getCertificationCode())
            .status(user.getStatus())
            .lastLoginAt(user.getLastLoginAt())
            .build();
    }
}