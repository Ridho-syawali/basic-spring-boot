package com.example.todolist.repository;

import com.example.todolist.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    //JpaRepository ini adalah dependensi yang menyediakan query otomatis
    Optional<Category> findByName(String name);

//    @Query(value = "Select * from category where name = ?1", nativeQuery = true)
//    Optional<Category> findByNameNative(String name);
}
