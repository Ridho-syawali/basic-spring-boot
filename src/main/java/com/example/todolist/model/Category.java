package com.example.todolist.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data // anotasi lombok untuk membuat getter dan setter secara otomatis
@AllArgsConstructor // untuk membuat constructor yang membuat semua field(argument)
@NoArgsConstructor // untuk constructor kosong / tanpa argument
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist // anotasi untuk membuat data secara otomatis ketika data pertama kali dibuat
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate // anotasi untuk membuat data waktu secara otomatis ketika data diupdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
