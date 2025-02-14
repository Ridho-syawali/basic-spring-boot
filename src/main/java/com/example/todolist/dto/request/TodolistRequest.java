package com.example.todolist.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class TodolistRequest {
    @NotBlank
    @Size(max = 255, message = "Title should not be greater than 255 characters")
    private String title;

    @NotBlank
    @Size
    private String description;

    @NotBlank
    @Size
    private UUID userId;

    @NotBlank
    @Size
    private int categoryId;

}
