package com.taskflow.application.auth;

import com.taskflow.domain.dto.auth.AuthResponse;
import com.taskflow.domain.dto.auth.LoginRequest;
import com.taskflow.domain.dto.auth.RegisterRequest;
import com.taskflow.domain.dto.auth.UserResponse;
import com.taskflow.domain.entity.User;
import com.taskflow.domain.exception.ValidationException;
import com.taskflow.domain.repository.UserRepository;
import com.taskflow.infrastructure.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ValidationException("Email already registered", Map.of("email", "Email is already in use"));
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        user = userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());

        return new AuthResponse(token, new UserResponse(user.getId(), user.getName(), user.getEmail()));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ValidationException("Invalid credentials", Map.of("email", "User not found")));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ValidationException("Invalid credentials", Map.of("password", "Incorrect password"));
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail());
        return new AuthResponse(token, new UserResponse(user.getId(), user.getName(), user.getEmail()));
    }
}