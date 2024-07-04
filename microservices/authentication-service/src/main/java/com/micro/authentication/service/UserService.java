package com.micro.authentication.service;

import com.micro.authentication.config.CustomUserDetailsService;
import com.micro.authentication.dto.UserCredential;
import com.micro.authentication.dto.UserDetailsDto;
import com.micro.authentication.model.User;
import com.micro.authentication.repository.UserRepository;
import com.micro.authentication.util.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    public Long createUser(UserDetailsDto userDetailsDto) {
        User user = User.builder()
                .email(userDetailsDto.getEmail())
                .name(userDetailsDto.getName())
                .password(passwordEncoder.encode(userDetailsDto.getPassword()))
                .build();
        return userRepository.save(user).getId();
    }

    public Optional<UserCredential> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(user -> {
            return UserCredential.builder().username(user.getEmail())
                    .password(user.getPassword()).build();
        });
    }

    public String generateToken(String username) {

        return jwtService.generateToken(customUserDetailsService.loadUserByUsername(username));
    }

    public boolean validateToken(String token) {
        if (!jwtService.isTokenExpired(token)) {
            return getUserByEmail(jwtService.extractUsername(token)).isPresent();
        } else {
            return false;
        }
    }
}
