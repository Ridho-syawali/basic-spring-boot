package com.example.todolist.service;

import com.example.todolist.dto.request.LoginRequest;
import com.example.todolist.dto.request.UsersRequest;
import com.example.todolist.dto.response.UsersResponse;
import com.example.todolist.model.Users;
import com.example.todolist.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsersResponse usersResponse;
    private UsersRequest registerRequest;
    private LoginRequest loginRequest;
    private Users users;

    //  buat data dummy
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        registerRequest = new UsersRequest();
        loginRequest = new LoginRequest();
        users = new Users();

        registerRequest.setUsername("admin");
        registerRequest.setEmail("admin@gmail.com");
        registerRequest.setPassword("123");
        registerRequest.setRole("ADMIN");

        loginRequest.setUsername("admin");
        loginRequest.setPassword("123");

        users.setId(UUID.randomUUID());
        users.setUsername("admin");
        users.setEmail("admin@gmail.com");
        users.setPassword("123");
    }

    @Test
    public void testRegisterUser_success() {
        // ekspektasi
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("");
        when(usersRepository.save(any(Users.class))).thenReturn(users);

        UsersResponse usersResponse = userService.registerUser(registerRequest);

        assertThat(usersResponse).isNotNull(); // memastikan respon tidak null
        assertThat(users.getUsername()).isEqualTo(usersResponse.getUsername());
        assertThat(users.getEmail()).isEqualTo(usersResponse.getEmail());
        verify(usersRepository, times(1)).save(any(Users.class));
    }

    @Test
    public void testLoginUser_success() {
        when(usersRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(users));
        when(passwordEncoder.matches(loginRequest.getPassword(), users.getPassword())).thenReturn(true);

        UsersResponse usersResponse = userService.loginUser(loginRequest);

        assertThat(usersResponse).isNotNull();
        assertThat(usersResponse.getUsername()).isEqualTo(usersResponse.getUsername());
        assertThat(usersResponse.getEmail()).isEqualTo(usersResponse.getEmail());
        verify(usersRepository,times(1)).findByUsername(loginRequest.getUsername());
    }
}
