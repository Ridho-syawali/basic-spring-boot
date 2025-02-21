package com.example.todolist.service;


import com.example.todolist.dto.exception.AuthenticationException;
import com.example.todolist.dto.exception.DataNotFoundException;

import com.example.todolist.dto.request.TodolistRequest;

import com.example.todolist.dto.response.TodolistResponse;

import com.example.todolist.model.Category;
import com.example.todolist.model.Todolist;
import com.example.todolist.model.Users;
import com.example.todolist.repository.CategoryRepository;
import com.example.todolist.repository.TodolistRepository;
import com.example.todolist.repository.UsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TodolistService {
    @Autowired
    private TodolistRepository todolistRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserService userService;

    private static String imageDirectory = "src/main/resources/static/images/";

    private static long maxFileSize = 5 * 1024 * 1024; // 5mb
    private static String[] allowedFileTypes = {"image/jpeg", "image/png", "image/jpg"}; //format yang diizinkan

    @Transactional
    public TodolistResponse createTodolist(TodolistRequest todolistRequest) {
        try{
            Todolist todolist = new Todolist();
            todolist.setTitle(todolistRequest.getTitle());
            todolist.setDescription(todolistRequest.getDescription());
            Users users = usersRepository.findByUsername(todolistRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User Not Found"));
            todolist.setUsers(users);
            Category category = categoryRepository.findById(todolistRequest.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category Not Found"));
            todolist.setCategory(category);
            todolist.setIsCompleted(todolistRequest.getIsCompleted());
            if (todolistRequest.getImagePath() != null && !todolistRequest.getImagePath().isEmpty()) {
                MultipartFile file = todolistRequest.getImagePath();
                if (file.getSize() > maxFileSize) {
                    throw new RuntimeException("File size exceeds the maximum limit of "+ maxFileSize / (1024 * 1024) + "MB");
                }

                String fileType = file.getContentType();
                boolean isValidType = false;
                for (String allowedType : allowedFileTypes) {
                    if(allowedType.equals(fileType)){
                        isValidType = true;
                        break;
                    }
                }

                if(!isValidType){
                    throw new RuntimeException("Invalid file type. Only JPEG,PNG, and JPG files are allowed");
                }

                String originalFilename = file.getOriginalFilename();
                String customFilename = "todolist_image" + "_"+originalFilename;

                Path path= Path.of(imageDirectory + customFilename);
                Files.copy(file.getInputStream(),path);
                todolist.setImagePath(customFilename);
            }
            Todolist createdTodolist = todolistRepository.save(todolist);
            return convertToResponse(createdTodolist);
        } catch (DataNotFoundException e) {
            throw e;
        } catch (Exception e){
            throw new RuntimeException("Failed to create todolist", e);
        }
    }
    public Optional<TodolistResponse> findById(Long id){
        try{
            return todolistRepository.findById(id).map(this::convertToResponse);
        }catch (Exception e){
            throw new RuntimeException("Failed to get todolist by ID", e);
        }
    }

    public Page<TodolistResponse> findAll(int page, int size){
        try{
            Pageable pageable = PageRequest.of(page, size);
            Page<Todolist> todolists = todolistRepository.findAllByDeletedAtIsNull(pageable);
            if (!todolists.isEmpty()){
                return todolists.map(this::convertToResponse);
            }else {
                return null;
            }

        }catch (Exception e){
            throw new RuntimeException("Failed to get all todolist", e);
        }
    }

    public List<TodolistResponse> findAllThrash(String username){
        UUID usersName = usersRepository.findByUsername(username).get().getId();
        try{
            List<Todolist> todolists = todolistRepository.findAllByUsersIdAndDeletedAtIsNotNull(usersName);
            return todolists.stream()
                    .map(this::convertToResponse)
                    .toList();
        }catch (Exception e){
            throw new RuntimeException("Failed to get all todolist", e);
        }
    }

//    public List<TodolistResponse> getTodolistByTitle(String title) {
//        try {
//            return todolistRepository.findByTitleContainingIgnoreCase(title)
//                    .stream()
//                    .map(this::convertToResponse)
//                    .toList();
//        }
//        catch (Exception e) {
//            throw new RuntimeException("Failed to get todolist by title", e);
//        }
//    }



    @Transactional
    public TodolistResponse update(Long id, TodolistRequest todolistRequest) {
        try{
            Todolist todolist = todolistRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("Todolist with ID " + id + " not found"));

            todolist.setTitle(todolistRequest.getTitle());
            todolist.setDescription(todolistRequest.getDescription());
            Users users = usersRepository.findByUsername(todolistRequest.getUsername())
                    .orElseThrow(()-> new RuntimeException("User not found"));
            todolist.setUsers(users);
            Category category = categoryRepository.findById(todolistRequest.getCategoryId())
                    .orElseThrow(()-> new RuntimeException("Category not found"));
            todolist.setCategory(category);
            todolist.setUsers(users);
            todolist.setIsCompleted(todolistRequest.getIsCompleted());
            if (todolistRequest.getImagePath() != null && !todolistRequest.getImagePath().isEmpty()) {
                MultipartFile file = todolistRequest.getImagePath();
                if (file.getSize() > maxFileSize) {
                    throw new RuntimeException("File size exceeds the maximum limit of "+ maxFileSize / (1024 * 1024) + "MB");
                }

                String fileType = file.getContentType();
                boolean isValidType = false;
                for (String allowedType : allowedFileTypes) {
                    if(fileType.equals(allowedType)){
                        isValidType = true;
                        break;
                    }
                }

                if(!isValidType){
                    throw new RuntimeException("Invalid file type. Only JPEG,PNG, and JPG files are allowed");
                }

                String originalFilename = file.getOriginalFilename();
                String customFilename = "todolist_image" + "_"+originalFilename;

                Path path= Path.of(imageDirectory + customFilename );
                Files.copy(file.getInputStream(),path);
                todolist.setImagePath(customFilename);
            }

            todolistRepository.save(todolist);
            return convertToResponse(todolist);
        } catch (DataNotFoundException e){
            throw e;
        } catch (Exception e){
            throw new RuntimeException("Failed to update todolist"+e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteTodolist(Long id){
        try{
            if (!todolistRepository.existsById(id)){
                throw new DataNotFoundException("Todolist id not found");
            }
            todolistRepository.deleteById(id);
        }catch (DataNotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException("Failed to delete todolist" + e.getMessage(), e);
        }
    }

    public List<TodolistResponse> searchByTitle(String title){
        try{
            return todolistRepository.findByTitleContainingIgnoreCase(title)
                    .stream()
                    .map(this::convertToResponse)
                    .toList();
        }catch (Exception e){
            throw new RuntimeException("Failed to search todolist by title", e);
        }
    }

    public List<TodolistResponse> filterByCategory(Long categoryId){
        try {
            return todolistRepository.findByCategoryId(categoryId)
                    .stream()
                    .map(this::convertToResponse)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get todolist by category", e);
        }
    }

    public List<TodolistResponse> findByUserId(UUID userId) {
        try {
            return todolistRepository.findByUsersId(userId)
                    .stream()
                    .map(this::convertToResponse)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get todolist by user ID", e);
        }
    }

    public byte[] getImage(String id) {
        Todolist todolist = todolistRepository.findByImagePath(id)
                .orElseThrow(() -> new RuntimeException("Todolist with id " + id + " not found"));
        String imagePath = todolist.getImagePath();
        if (imagePath != null) {
            try {
                Path path = Paths.get(imageDirectory + imagePath);
                return Files.readAllBytes(path);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read image file", e);
            }
        } else {
            return null;
        }
    }


    public void softDelete(Long id){
        try{
            Todolist todolist = todolistRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("Todolist with ID " + id + " not found"));
            todolist.setDeletedAt(LocalDateTime.now());
            todolistRepository.save(todolist);
        }catch (DataNotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException("Failed to soft delete todolist" + e.getMessage(), e);
        }
    }

    public void restore(Long id){
        try{
            Todolist todolist = todolistRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("Todolist with ID " + id + " not found"));
            todolist.setDeletedAt(null);
            todolistRepository.save(todolist);
        }catch (DataNotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException("Failed to restore todolist" + e.getMessage(), e);
        }
    }

    public List<TodolistResponse> getTodolistByUser(String username) {
        UUID usersName = usersRepository.findByUsername(username).get().getId();
        List<Todolist> todolists = todolistRepository.findByUsersIdAndDeletedAtIsNull(usersName);
        return todolists.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private TodolistResponse convertToResponse(Todolist todolist){
        TodolistResponse response = new TodolistResponse();
        response.setId(todolist.getId());
        response.setTitle(todolist.getTitle());
        response.setDescription(todolist.getDescription());
        response.setUsername(todolist.getUsers().getUsername());
        response.setCategoryId(todolist.getCategory().getId());
        response.setCategoryName(todolist.getCategory().getName());
        response.setIsCompleted(todolist.getIsCompleted());
        response.setDeletedAt(todolist.getDeletedAt());
        response.setCreatedAt(todolist.getCreatedAt());
        response.setUpdatedAt(todolist.getUpdatedAt());
        response.setImagePath(todolist.getImagePath());
        return response;
    }


}
