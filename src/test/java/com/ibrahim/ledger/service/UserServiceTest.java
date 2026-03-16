package com.ibrahim.ledger.service;

import com.ibrahim.ledger.config.JwtUtil;
import com.ibrahim.ledger.dto.AuthResponseDto;
import com.ibrahim.ledger.dto.LoginRequestDto;
import com.ibrahim.ledger.dto.RegisterRequestDto;
import com.ibrahim.ledger.mapper.UserMapper;
import com.ibrahim.ledger.model.UserModel;
import com.ibrahim.ledger.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private UserMapper userMapper;

    @InjectMocks private UserService userService;

    private RegisterRequestDto registerRequest;
    private LoginRequestDto loginRequest;
    private UserModel user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequestDto();
        registerRequest.setUserName("ibrahim");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("ibrahim@example.com");

        loginRequest = new LoginRequestDto();
        loginRequest.setUserName("ibrahim");
        loginRequest.setPassword("password123");

        user = new UserModel();
        user.setUserId(UUID.randomUUID());
        user.setUserName("ibrahim");
        user.setPassword("$2a$10$encodedPassword");
        user.setEmail("ibrahim@example.com");
        user.setCreatedAt(Instant.now());
    }

    @Test
    void register_validRequest_returnsJwtToken() {
        when(userRepository.findByUserName("ibrahim")).thenReturn(Optional.empty());
        when(userMapper.toEntity(registerRequest)).thenReturn(user);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(UserModel.class))).thenReturn(user);
        when(jwtUtil.generateToken(user)).thenReturn("mock.jwt.token");

        AuthResponseDto result = userService.register(registerRequest);

        assertThat(result.getToken()).isEqualTo("mock.jwt.token");
        verify(userRepository).save(user);
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void register_setsEncodedPasswordBeforeSave() {
        when(userRepository.findByUserName("ibrahim")).thenReturn(Optional.empty());
        when(userMapper.toEntity(registerRequest)).thenReturn(user);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any())).thenReturn(user);
        when(jwtUtil.generateToken(any())).thenReturn("token");

        userService.register(registerRequest);

        verify(passwordEncoder).encode("password123");
        assertThat(user.getPassword()).isEqualTo("$2a$10$encodedPassword");
    }

    @Test
    void register_duplicateUsername_throwsExceptionAndNeverSaves() {
        when(userRepository.findByUserName("ibrahim")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.register(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("username already exists");

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_validCredentials_returnsJwtToken() {
        when(userRepository.findByUserName("ibrahim")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "$2a$10$encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(user)).thenReturn("mock.jwt.token");

        AuthResponseDto result = userService.login(loginRequest);

        assertThat(result.getToken()).isEqualTo("mock.jwt.token");
    }

    @Test
    void login_unknownUsername_throwsGenericMessage() {
        when(userRepository.findByUserName("ibrahim")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("invalid username or password");
    }

    @Test
    void login_wrongPassword_throwsGenericMessage() {
        when(userRepository.findByUserName("ibrahim")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "$2a$10$encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("invalid username or password");
    }

    @Test
    void login_wrongPassword_and_unknownUsername_returnSameMessage() {
        when(userRepository.findByUserName("ibrahim")).thenReturn(Optional.empty()).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        String msgWhenNotFound = null, msgWhenWrongPwd = null;
        try {
            userService.login(loginRequest);
        } catch (RuntimeException e) {
            msgWhenNotFound = e.getMessage();
        }
        try {
            userService.login(loginRequest);
        } catch (RuntimeException e) {
            msgWhenWrongPwd = e.getMessage();
        }

        assertThat(msgWhenNotFound).isEqualTo(msgWhenWrongPwd);
    }

    @Test
    void loadUserByUsername_existingUser_returnsUserDetails() {
        when(userRepository.findByUserName("ibrahim")).thenReturn(Optional.of(user));

        UserDetails result = userService.loadUserByUsername("ibrahim");

        assertThat(result.getUsername()).isEqualTo("ibrahim");
    }

    @Test
    void loadUserByUsername_nonExistingUser_throwsUsernameNotFoundException() {
        when(userRepository.findByUserName("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("ghost")).isInstanceOf(UsernameNotFoundException.class);
    }
}

