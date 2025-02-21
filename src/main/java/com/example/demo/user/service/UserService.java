package com.example.demo.user.service;

import com.example.demo.global.exception.CertificationCodeNotMatchedException;
import com.example.demo.global.exception.ResourceNotFoundException;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.service.port.UserRepository;
import java.time.Clock;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CertificationService certificationService;

    public Optional<User> findById(long id) {
        return userRepository.findByIdAndStatus(id, UserStatus.ACTIVE);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
            .orElseThrow(() -> new ResourceNotFoundException("Users", email));
    }

    public User getById(long id) {
        return userRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
            .orElseThrow(() -> new ResourceNotFoundException("Users", id));
    }

    @Transactional
    public User create(UserCreate userCreate) {

        User newUser = User.create(userCreate);

        User user = userRepository.save(newUser);

        certificationService.send(user.getEmail(), user.getId(), user.getCertificationCode());

        return user;
    }

    @Transactional
    public User update(long id, UserUpdate userUpdate) {
        User user = getById(id);
        return userRepository.save(user.update(userUpdate));
    }

    @Transactional
    public void login(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Users", id));
        user.changeLastLoginAt(Clock.systemUTC().millis());
        userRepository.save(user);
    }

    @Transactional
    public void verifyEmail(long id, String certificationCode) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Users", id));

        if (user.isNotEqualCertificationCode(certificationCode)) {
            throw new CertificationCodeNotMatchedException();
        }

        user.changeStatus(UserStatus.ACTIVE);

        userRepository.save(user);
    }
}