package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

import com.example.demo.global.exception.CertificationCodeNotMatchedException;
import com.example.demo.global.exception.ResourceNotFoundException;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.infrastructure.UserEntity;
import com.example.demo.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@TestPropertySource("classpath:test-application.yml")
@Sql("/sql/user-service-test-data.sql")
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;

    @MockBean
    JavaMailSender javaMailSender;

    @DisplayName("이메일로 유저를 조회할 때 PENDING 상태인 유저는 조회하지 않는다.")
    @Test
    void canNotFindPendingUserWithEmail() {

        // given
        final String email = "foofoo@gmail.com";

        //when & then
        assertThatThrownBy(() -> userService.getByEmail(email))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage(String.format("Users에서 ID %s를 찾을 수 없습니다.", email));
    }

    @DisplayName("이메일로 유저를 조회할 때는 ACTIVE 상태인 유저만 조회한다.")
    @Test
    void canFindActiveUserWithEmail() {

        // given
        final String email = "foo@gmail.com";

        // when
        User user = userService.getByEmail(email);

        // then
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(email);
    }

    @DisplayName("유저를 생성 할 수 있다.")
    @Test
    void canCreate() {

        // given
        UserCreate userCreateRequest = UserCreate.builder()
            .email("foofoofoo@gmail.com")
            .address("daejeon")
            .nickname("foofoofoo")
            .build();

        // when
        User user = userService.create(userCreateRequest);
        BDDMockito.doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));
        // then
        assertThat(user.getId()).isNotNull();
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);
    }

    @DisplayName("유저를 수정 할 수 있다.")
    @Test
    void canUpdate() {

        // given
        UserUpdate userUpdate = UserUpdate.builder()
            .address("daejeon")
            .nickname("foo-N")
            .build();

        // when
        User user = userService.update(2L, userUpdate);

        // then
        assertThat(user.getId()).isNotNull();
        assertThat(user.getId()).isEqualTo(2L);
        assertThat(user.getAddress()).isEqualTo("daejeon");
        assertThat(user.getNickname()).isEqualTo("foo-N");
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @DisplayName("로그인 할 수 있다.")
    @Test
    void canLogin() {

        // given
        final long id = 2L;

        // when
        userService.login(id);
        User user = userService.getById(id);

        // then
        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getLastLoginAt()).isGreaterThan(0);
    }

    @DisplayName("PENDING 상태의 사용자는 인증 코드를 통해 ACTIVE 상태로 변경할 수 있다.")
    @Test
    void activatePendingUserWithCertificationCode() {

        // given
        final long id = 3L;
        final String certificationCode = "abcdefgh-ijkl-mnop-qrst-uvwxyzabcdeg";

        // when
        userService.verifyEmail(id, certificationCode);
        User user = userService.getById(id);

        // then
        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getCertificationCode()).isEqualTo(certificationCode);
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @DisplayName("잘못된 인증 코드는 ACTIVE 상태로 변경할 수 없다.")
    @Test
    void notActivatePendingUserWithInvalidCertificationCode() {

        // given
        final long id = 3L;
        final String certificationCode = "abcdefgh-ijkl-mnop-qrst";

        // when & then
        assertThatThrownBy(() -> userService.verifyEmail(id, certificationCode))
            .isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}