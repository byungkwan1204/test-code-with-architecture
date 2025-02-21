package com.example.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.infrastructure.UserEntity;
import com.example.demo.user.infrastructure.UserJpaRepository;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest
@TestPropertySource("classpath:test-application.yml")
@Sql("/sql/user-repository-test-data.sql")
class UserJpaRepositoryTest {

    @Autowired
    UserJpaRepository userJpaRepository;

    @AfterEach
    void tearDown() {
        userJpaRepository.deleteAllInBatch();
    }

    @DisplayName("유저 아이디와 상태 값으로 유저를 조회할 수 있다.")
    @Test
    void findByIdAndStatusTest() {

        // given
        final long id = 1;

        // when
        Optional<UserEntity> findUser = userJpaRepository.findByIdAndStatus(id, UserStatus.ACTIVE);

        // then
        assertThat(findUser.isPresent()).isTrue();
    }

    @DisplayName("유저 이메일과 상태 값으로 유저를 조회할 수 있다.")
    @Test
    void findByEmailAndStatusTest() {

        // given
        final String email = "foo@gmail.com";

        // when
        Optional<UserEntity> optionalFindUser = userJpaRepository.findByEmailAndStatus(email, UserStatus.ACTIVE);
        UserEntity findUser = optionalFindUser.get();

        // then
        assertThat(findUser).isNotNull();
        assertThat(findUser.getEmail()).isEqualTo(email);
    }

    @DisplayName("존재하지 않는 유저 ID를 조회하려고 하면 Optional.Empty를 반환한다.")
    @Test
    void findByIdAndStatusTestWithNotExistsUser() {

        // given
        final long id = -1;

        // when
        Optional<UserEntity> optionalFindUser = userJpaRepository.findByIdAndStatus(id, UserStatus.ACTIVE);

        // then
        assertThat(optionalFindUser.isEmpty()).isTrue();
    }

    @DisplayName("유저 ID는 존재하지만, 적절하지 않은 상태로 조회하는 경우 Optional.Empty를 반환한다.")
    @Test
    void findByIdAndStatusTestWithNotExistsStatus() {

        // given
        final long id = 1;

        // when
        Optional<UserEntity> optionalFindUser = userJpaRepository.findByIdAndStatus(id, UserStatus.PENDING);

        // then
        assertThat(optionalFindUser.isEmpty()).isTrue();
    }

    //    @DisplayName("UserRepository 의 연결을 확인한다.")
    //    @Test
    //    void userRepositoryConnectTest() {
    //
    //        // given
    //        UserEntity userEntity = new UserEntity();
    //        userEntity.setEmail("foo@gmail.com");
    //        userEntity.setAddress("Seoul");
    //        userEntity.setNickname("foo");
    //        userEntity.setStatus(UserStatus.ACTIVE);
    //        userEntity.setCertificationCode(UUID.randomUUID().toString());
    //
    //        // when
    //        UserEntity savedUser = userRepository.save(userEntity);
    //
    //        // then
    //        assertThat(savedUser.getId()).isNotNull();
    //    }
}