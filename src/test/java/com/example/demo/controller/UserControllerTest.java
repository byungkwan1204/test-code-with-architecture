package com.example.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.infrastructure.UserEntity;
import com.example.demo.user.infrastructure.UserJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Sql("/sql/user-controller-test-data.sql")
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    JavaMailSender mailSender;

    @DisplayName("특정 ID로 유저를 조회할 수 있다.")
    @Test
    void getUserWithExistId() throws Exception {

        // given
        final long id = 2L;

        // when & then
        MvcResult mvcResult = mockMvc.perform(get("/api/users/{id}", id))
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.email").value("foo@gmail.com"))
            .andExpect(jsonPath("$.nickname").value("foo"))
            .andExpect(jsonPath("$.status").value("ACTIVE"))
            .andExpect(status().isOk())
            .andReturn();

        log.info("mvcResult : {}", mvcResult.getResponse().getContentAsString());
    }

    @DisplayName("존재하지 않는 유저의 ID는 조회 할 수 없다.")
    @Test
    void getUserWithNotExistId() throws Exception {

        // given
        final long id = 100000L;

        // when & then
        mockMvc.perform(get("/api/users/{id}", id))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Users에서 ID 100000를 찾을 수 없습니다."));
    }

    @DisplayName("유저의 인증 코드를 받아서 PENDING -> ACTIVE 상태로 변경할 수 있다.")
    @Test
    void verifyWithCorrectCertificationCode() throws Exception {

        // given
        final long id = 2L;
        final String certificationCode = "abcdefgh-ijkl-mnop-qrst-uvwxyzabcdef";

        // when
        mockMvc.perform(get("/api/users/{id}/verify", id)
                            .queryParam("certificationCode", certificationCode))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl("http://localhost:3000"));

        // getReferenceById -> 프록시 객체 반환, 실제로 프록시 객체를 사용할 때 DB 쿼리 수행 (= Lazy Loading 과 비슷하다.)
        UserEntity user = userJpaRepository.getReferenceById(id);

        // then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @DisplayName("유저의 인증 코드가 잘못된 경우 상태를 변경할 수 없다.")
    @Test
    void verifyWithInvalidCertificationCode() throws Exception {

        // given
        final long id = 2L;
        final String certificationCode = "abcdefgh-ijkl-mnop-qrst-abcdefghizkl";

        // when & then
        mockMvc.perform(get("/api/users/{id}/verify", id)
                            .queryParam("certificationCode", certificationCode))
            .andExpect(status().isForbidden())
            .andExpect(content().string("자격 증명에 실패하였습니다."));
    }

    @DisplayName("유저의 닉네임을 변경할 수 있다.")
    @Test
    void updateUserNicknameWithCorrectEmail() throws Exception {

        // given
        final long id = 2L;
        final String email = "foo@gmail.com";

        UserUpdate userUpdate = UserUpdate.builder()
            .nickname("pool")
            .build();

        // when
        mockMvc.perform(put("/api/users/me")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("EMAIL", email)
                            .content(objectMapper.writeValueAsString(userUpdate)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.nickname").value("pool"))
            .andReturn();

        UserEntity user = userJpaRepository.getReferenceById(id);

        // then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getNickname()).isEqualTo("pool");
    }

    @DisplayName("유저의 주소를 변경할 수 있다.")
    @Test
    void updateUserAddressWithCorrectEmail() throws Exception {

        // given
        final long id = 2L;
        final String email = "foo@gmail.com";

        UserUpdate userUpdate = UserUpdate.builder()
            .address("BUSAN")
            .build();

        // when
        mockMvc.perform(put("/api/users/me")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("EMAIL", email)
                            .content(objectMapper.writeValueAsString(userUpdate)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.address").value("BUSAN"));

        UserEntity user = userJpaRepository.getReferenceById(id);

        // then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getAddress()).isEqualTo("BUSAN");
    }

    @DisplayName("잘못된 이메일을 입력했을 경우 유저의 닉네임을 변경할 수 없다.")
    @Test
    void canNotUpdateUserNicknameWithInvalidEmail() throws Exception {

        // given
        final String email = "foo@invalid.email.com";

        UserUpdate userUpdate = UserUpdate.builder()
            .nickname("pool")
            .build();

        // when & then
        mockMvc.perform(put("/api/users/me")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .header("EMAIL", email)
                            .content(objectMapper.writeValueAsString(userUpdate)))
            .andExpect(status().isNotFound())
            .andExpect(content().string(String.format("Users에서 ID %s를 찾을 수 없습니다.", email)));
    }

    @DisplayName("유저를 생성할 수 있다.")
    @Test
    void canCreateUser() throws Exception {

        // given
        UserCreate userCreateRequest = UserCreate.builder()
            .email("foofoofoo@gmail.com")
            .nickname("foofoofoo")
            .address("DAEGU")
            .build();

        BDDMockito.doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // when & then
        mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(userCreateRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.email").value("foofoofoo@gmail.com"))
            .andExpect(jsonPath("$.nickname").value("foofoofoo"))
            .andExpect(jsonPath("$.status").value("PENDING"));
    }
}