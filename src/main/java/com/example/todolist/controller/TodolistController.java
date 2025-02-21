package com.example.todolist.controller;

import com.example.todolist.dto.exception.DataNotFoundException;
import com.example.todolist.dto.exception.DuplicateDataException;
import com.example.todolist.dto.request.TodolistRequest;
import com.example.todolist.dto.response.ApiResponse;
import com.example.todolist.dto.response.PaginatedResponse;
import com.example.todolist.dto.response.TodolistResponse;
import com.example.todolist.service.TodolistService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/todolist")
public class TodolistController {
    @Autowired
    private TodolistService todolistService;

    @Value("${file.IMAGE_DIR}")
    private String imageDirectory;

    @Operation(summary = "Get All Todolist")
    @GetMapping
    public ResponseEntity<?> getAllTodolist(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10")int size){
        try{
            Page<TodolistResponse> response = todolistService.findAll(page, size);
            return ResponseEntity
                    .status(HttpStatus.OK.value())
                    .body(new PaginatedResponse<>(200, response));
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @Operation(summary = "Get Todolist By Id")
    @GetMapping("/{id}")
    public ResponseEntity<?> getTodolistById(@PathVariable Long id){
        try{
            TodolistResponse response = todolistService.findById(id)
                    .orElseThrow(()-> new RuntimeException("Todolist with id " + id + " not found"));
            return ResponseEntity.ok(new ApiResponse<>(200, response));
        }catch (DataNotFoundException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND.value())
                    .body(new ApiResponse<>(404, e.getMessage()));
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @GetMapping("/detail/{title}")
    public ResponseEntity<?> getTodolistById(@PathVariable String title){
        try{
            List<TodolistResponse> response = todolistService.searchByTitle(title); // Menggunakan metode searchByTitle dari TodolistService
            return ResponseEntity.ok(new ApiResponse<>(200, response));
        }catch (DataNotFoundException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND.value())
                    .body(new ApiResponse<>(404, e.getMessage()));
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @Operation(summary = "Create Todolist")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createTodolist(@ModelAttribute @Valid TodolistRequest todolistRequest){
        try{
            TodolistResponse response = todolistService.createTodolist(todolistRequest);
            return ResponseEntity.ok(new ApiResponse<>(200, response));
        }catch (DuplicateDataException e){
            return ResponseEntity
                    .status(HttpStatus.CONFLICT.value())
                    .body(new ApiResponse<>(HttpStatus.CONFLICT.value(), e.getMessage()));
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @Operation(summary = "Update Todolist")
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateUser(@ModelAttribute @PathVariable("id") Long id, @Valid
    TodolistRequest request){
        try{
            TodolistResponse todolistResponse = todolistService.update(id, request);
            return ResponseEntity
                    .ok(new ApiResponse<>(200, request));
        } catch (DataNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND.value())
                    .body(new ApiResponse<>(404, e.getMessage()));
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    // hardDelete
    @Operation(summary = "Delete Todolist")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodolist(@PathVariable("id")Long id){
        try{
            todolistService.deleteTodolist(id);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "=====Todolist deleted successfully===="));
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Delete Failed"));
        }
    }

    // softDelete
    @Operation(summary = "Soft Delete Todolist")
    @DeleteMapping("/soft-delete/{id}")
    public ResponseEntity<?> softDeleteTodolist(@PathVariable("id")Long id){
        try{
            todolistService.softDelete(id);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "=====Todolist deleted successfully===="));
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Delete Failed"));
        }
    }

    @Operation(summary = "Restore Todolist")
    @PutMapping("/restore/{id}")
    public ResponseEntity<?> restoreTodolist(@PathVariable("id") Long id) {
        try {
            todolistService.restore(id);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Todolist restored successfully"));
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Restore Failed"));
        }
    }

    @Operation(summary = "Search Todolist")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<TodolistResponse>>> searchTodolist(@RequestParam String title){
        List<TodolistResponse> response = todolistService.searchByTitle(title); // Menggunakan metode searchByTitle dari TodolistService
        return ResponseEntity.ok(new ApiResponse<>(200, response));
    }

    @Operation(summary = "Filter Todolist")
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<TodolistResponse>>> filterTodolist(@RequestParam Long categoryId){
        List<TodolistResponse> response = todolistService.filterByCategory(categoryId);
        return ResponseEntity.ok(new ApiResponse<>(200, response));
    }

    @Operation(summary = "Get Todolist By User Id")
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<List<TodolistResponse>>> getTodolistByUserId(@PathVariable UUID userId) {
        List<TodolistResponse> todolistResponses = todolistService.findByUserId(userId);
        return ResponseEntity.ok(new ApiResponse<>(200, todolistResponses));
    }


//    @GetMapping(value = "/images/{id}", consumes = MediaType.ALL_VALUE, produces = MediaType.IMAGE_PNG_VALUE)
//    public ResponseEntity<byte[]> getImage(@PathVariable("id")String id) {
//        try {
//            byte[] image = todolistService.getImage(id);
//            return ResponseEntity.ok(image);
//        } catch (Exception e) {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @Operation(summary = "Get Todolist Image")
    @GetMapping("/images/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) throws IOException{
        // mencari file di direktori
        Path path = Paths.get(imageDirectory + filename);
        // cek apakah file ada
        if (!Files.exists(path)){
            return ResponseEntity.notFound().build();
        }
        // membaca file
        byte[] image= Files.readAllBytes(path);
        // ambil extension filenya
        String fileExtension = filename.substring(filename.lastIndexOf('.') + 1 ).toLowerCase();
        // menentukan tipe media dengan switch case
        MediaType mediaType = switch(fileExtension){
            case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "png" -> MediaType.IMAGE_PNG;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };

        return ResponseEntity.ok().contentType(mediaType).body(image);
    }

    @Operation(summary = "Get Todolist By User with deletedAt is null")
    @GetMapping("/todos")
    public ResponseEntity<?> getTodolistByUser(@RequestParam String username) {
       try{
           List<TodolistResponse> todolistResponses = todolistService.getTodolistByUser(username);
           return ResponseEntity.ok(new ApiResponse<>(200, todolistResponses));
       } catch (DataNotFoundException e) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND)
                   .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage()));
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
       }
    }

    @Operation(summary = "Get Todolist By User with deletedAt is not null")
    @GetMapping("/trash")
    public ResponseEntity<?> getAllTodoListByUsername(@RequestParam String username) {
        try {
            List<TodolistResponse> todolistResponses = todolistService.findAllThrash(username);
            return ResponseEntity.ok(new ApiResponse<>(200, todolistResponses));
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }
}


