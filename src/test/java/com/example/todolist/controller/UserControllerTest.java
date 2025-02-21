package com.example.todolist.controller;

import com.example.todolist.dto.request.UsersRequest;
import com.example.todolist.dto.response.ApiResponse;
import com.example.todolist.dto.response.UsersResponse;
import com.example.todolist.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserControllerTest {
    @InjectMocks
    private UsersController usersController;

    @Mock
    private UserService userService;

    private UsersRequest registerRequest;
    private UsersResponse usersResponse;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        registerRequest = new UsersRequest();
        usersResponse = new UsersResponse();
    }

    @Test
    public void testRegisterUser_success() {
        when(userService.registerUser(any(UsersRequest.class))).thenReturn(usersResponse);

        ResponseEntity<?> response = usersController.registerUser(registerRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(new ApiResponse<>(200,usersResponse));

        verify(userService, times(1)).registerUser(registerRequest);
    }
}
