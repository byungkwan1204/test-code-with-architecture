package com.example.demo.user.domain;

import java.time.Clock;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User {

    private Long id;

    private String email;

    private String nickname;

    private String address;

    private String certificationCode;

    private UserStatus status;

    private Long lastLoginAt;

    public static User create(UserCreate  userCreate) {
        return User.builder()
            .email(userCreate.getEmail())
            .nickname(userCreate.getNickname())
            .address(userCreate.getAddress())
            .certificationCode(UUID.randomUUID().toString())
            .status(UserStatus.PENDING)
            .build();
    }

    public User update(UserUpdate userUpdate) {
        return User.builder()
            .id(this.id)
            .email(this.email)
            .nickname(userUpdate.getNickname())
            .address(userUpdate.getAddress())
            .certificationCode(this.certificationCode)
            .status(this.status)
            .lastLoginAt(this.lastLoginAt)
            .build();
    }

    public void changeLastLoginAt(Long lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public void changeStatus(UserStatus status) {
        this.status = status;
    }

    public boolean isNotEqualCertificationCode(String certificationCode) {
        return !this.certificationCode.equals(certificationCode);
    }
}
