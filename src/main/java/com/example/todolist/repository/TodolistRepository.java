package com.example.todolist.repository;

import com.example.todolist.dto.response.TodolistResponse;
import com.example.todolist.model.Todolist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface TodolistRepository extends JpaRepository<Todolist, Long> {
    //select * from todolist where title like lower('%title%')
    List<Todolist> findByTitleContainingIgnoreCase(String title);

    List<Todolist> findByCategoryId(Long categoryId);

    List<Todolist> findByUsersId(UUID usersId);

    Optional<Todolist> findByImagePath(String imagePath);

    Page<Todolist> findAllByDeletedAtIsNull(Pageable pageable);

    List<Todolist> findAllByUsersIdAndDeletedAtIsNotNull(UUID usersId);

    List<Todolist> findByUsersIdAndDeletedAtIsNull(UUID usersId);

}
