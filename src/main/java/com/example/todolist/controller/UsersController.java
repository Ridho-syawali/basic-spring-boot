package com.example.todolist.controller;

import com.example.todolist.dto.exception.AuthenticationException;
import com.example.todolist.dto.exception.DataNotFoundException;
import com.example.todolist.dto.exception.DuplicateDataException;
import com.example.todolist.dto.request.CategoryRequest;
import com.example.todolist.dto.request.UsersRequest;
import com.example.todolist.dto.response.ApiResponse;
import com.example.todolist.dto.response.CategoryResponse;
import com.example.todolist.dto.response.UsersResponse;
import com.example.todolist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/todolist/users")
public class UsersController {
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllUsers(){
        try{
            List<UsersResponse> response = userService.findAll();
            return ResponseEntity.status(HttpStatus.OK.value())
                    .body(new ApiResponse<>(HttpStatus.OK.value(), response));
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UsersRequest usersRequest){
        try{
            UsersResponse response = userService.create(usersRequest);
            return ResponseEntity.status(HttpStatus.OK.value())
                    .body(new ApiResponse<>(HttpStatus.OK.value(), response));
        }catch (DuplicateDataException e){
            return ResponseEntity
                    .status(HttpStatus.CONFLICT.value())
                    .body(new ApiResponse<>(HttpStatus.CONFLICT.value(), e.getMessage()));
        }catch (AuthenticationException e){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") UUID id, @RequestBody UsersRequest request){
        try{
            UsersResponse usersResponse = userService.update(id, request);
            return ResponseEntity
                    .status(HttpStatus.OK.value())
                    .body(new ApiResponse<>(HttpStatus.OK.value(), request));
        } catch (DataNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND.value())
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }catch (DuplicateDataException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT.value())
                    .body(new ApiResponse<>(HttpStatus.CONFLICT.value(), e.getMessage()));
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id")UUID id){
        try{
            userService.deleteUser(id);
            return ResponseEntity.status(HttpStatus.OK.value())
                    .body(new ApiResponse<>(HttpStatus.OK.value(), "=====User deleted successfully===="));
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Delete Failed"));
        }
    }


}
