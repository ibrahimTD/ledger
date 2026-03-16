package com.ibrahim.ledger.service;

import com.ibrahim.ledger.config.JwtUtil;
import com.ibrahim.ledger.dto.AuthResponseDto;
import com.ibrahim.ledger.dto.LoginRequestDto;
import com.ibrahim.ledger.dto.RegisterRequestDto;
import com.ibrahim.ledger.mapper.UserMapper;
import com.ibrahim.ledger.model.UserModel;
import com.ibrahim.ledger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;


    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found " + username));
    }

    public AuthResponseDto register(RegisterRequestDto registerRequestDto) {

        if (userRepository.findByUserName(registerRequestDto.getUserName()).isPresent()) {
            throw new RuntimeException("username already exists");
        }

        UserModel user = userMapper.toEntity(registerRequestDto);
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        user.setCreatedAt(Instant.now());

        UserModel savedUser = userRepository.save(user);
        String token = jwtUtil.generateToken(savedUser);

        return new AuthResponseDto(token);
    }

    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        UserModel user = userRepository.findByUserName(loginRequestDto.getUserName())
                .orElseThrow(() -> new RuntimeException("invalid username or password"));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("invalid password");
        }

        String token = jwtUtil.generateToken(user);
        return new AuthResponseDto(token);
    }
}
