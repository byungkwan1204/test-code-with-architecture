package com.example.demo.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.demo.mock.FakeMailSender;
import com.example.demo.user.service.port.MailSender;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@TestPropertySource("classpath:test-application.yml")
@Transactional
class CertificationServiceTest {

    @DisplayName("title, content가 정상적으로 생성되어 전송되는지 확인한다.")
    @Test
    void test() {

        // given
        final long userId = 1L;
        final String certificationCode = UUID.randomUUID().toString().replaceAll("-", "");
        final String certificationUrl = generateCertificationUrl(userId, certificationCode);

        final String email = "foo@gmail.com";
        String title = "Please certify your email address";
        String content = "Please click the following link to certify your email address : " + certificationUrl;

        FakeMailSender fakeMailSender = new FakeMailSender(email, title, content);
        CertificationService certificationService = new CertificationService(fakeMailSender);

        // when
        certificationService.send(email, userId, certificationCode);

        // then
        assertThat(fakeMailSender.getEmail()).isEqualTo(email);
        assertThat(fakeMailSender.getTitle()).isEqualTo(title);
        assertThat(fakeMailSender.getContent()).isEqualTo(content);
    }

    private String generateCertificationUrl(long userId, String certificationCode) {
        return String.format("http://localhost:8080/api/users/%d/verify?certificationCode=%s", userId, certificationCode);
    }
}