package com.example.todolist.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TodolistResponse {
    private Long id;
    private String title;
    private String description;
    private UUID userId;
    private int categoryId;
    private boolean isCompleted;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
