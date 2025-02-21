package com.example.todolist.controller;

import com.example.todolist.dto.exception.AuthenticationException;
import com.example.todolist.dto.exception.DataNotFoundException;
import com.example.todolist.dto.exception.DuplicateDataException;
import com.example.todolist.dto.request.LoginRequest;
import com.example.todolist.dto.request.UsersRequest;
import com.example.todolist.dto.response.ApiResponse;
import com.example.todolist.dto.response.UsersResponse;
import com.example.todolist.service.UserService;
import com.example.todolist.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UsersRequest usersRequest){
        try{
            UsersResponse response = userService.registerUser(usersRequest);
            return ResponseEntity.ok(new ApiResponse<>(200, response));
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

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest){
        try{
            UsersResponse response = userService.loginUser(loginRequest);
            String token = jwtUtil.generateToken(response.getUsername());
            return ResponseEntity.ok(new ApiResponse<>(200, token));
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }


    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(){
        try{
            List<UsersResponse> response = userService.getAllUsers();
            return ResponseEntity.ok(new ApiResponse<>(200, response));
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable("username")String username){
        try{
            UsersResponse response = userService.getUserByUsername(username);
            return ResponseEntity.ok(new ApiResponse<>(200, response));
        }catch (DataNotFoundException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND.value())
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }


    @PutMapping("/{username}")
    public ResponseEntity<?> updateUser(@PathVariable("username") String username, @RequestBody UsersRequest request){
        try {
            UsersResponse response = userService.updatePassword(username, request);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), response));
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Restore Failed"));
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable("username")String username){
        try {
            userService.deleteUser(username);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "User deleted successfully"));
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Delete Failed"));
        }
    }


    @PutMapping("/update/{username}")
    public ResponseEntity<?> updateUsers(@PathVariable("username") String username, @RequestBody UsersRequest request){
        try {
            UsersResponse response = userService.updateUsers(username, request);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), response));
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Update Failed"));
        }
    }


    @GetMapping("/search/{username}")
    public ResponseEntity<?> searchByUsername(@PathVariable("username")String username){
        try {
            List<UsersResponse> response = userService.searchByUsername(username);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), response));
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Search Failed"));
        }
    }
}
