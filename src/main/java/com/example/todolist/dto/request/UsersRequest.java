package com.example.todolist.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsersRequest {
    @NotBlank
    @Size(min = 3, max = 50, message = "Name must have at least 3 characters and maximum 50 characters")
    private String username;

    @NotBlank
    @Size(max = 100, message = "Email should not be greater than 100 characters")
    private String email;

    @NotBlank
    @Size(min = 6, max = 40, message = "Password must have at least 6 characters and maximum 40 characters")
    private String password;

    @NotBlank
    @Size(max = 10, message = "")
    private String role;
}
