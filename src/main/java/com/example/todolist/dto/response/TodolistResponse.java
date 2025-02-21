package com.example.todolist.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TodolistResponse {
    private Long id;
    private String title;
    private String description;
    private String username;
    private CategoryData category;
    private Boolean isCompleted;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String imagePath;

    //constructor
    public TodolistResponse() {
        this.category = new CategoryData();
    }

    public void setCategoryId(Long id) {
        this.category.setId(id);
    }

    public void setCategoryName(String name) {
        this.category.setName(name);
    }

}
// untuk menyimpan category objek lalu memanggilnya di todolistRespond
@Data
class CategoryData{
    private Long id;
    private String name;
}