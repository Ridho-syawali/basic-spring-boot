package com.example.todolist.service;


import com.example.todolist.dto.request.LoginRequest;
import com.example.todolist.dto.request.UsersRequest;
import com.example.todolist.dto.response.TodolistResponse;
import com.example.todolist.dto.response.UsersResponse;
import com.example.todolist.model.Users;
import com.example.todolist.repository.TodolistRepository;
import com.example.todolist.repository.UsersRepository;
import com.example.todolist.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private TodolistRepository todolistRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails  loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = usersRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(users);
    }

    @Transactional
    public List<UsersResponse> getAllUsers(){
        return usersRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UsersResponse getUserByUsername(String username){
        return usersRepository.findByUsername(username)
                .map(this::convertToResponse)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public UsersResponse registerUser(UsersRequest usersRequest) {
        if(Objects.equals(usersRequest.getUsername(), "") || Objects.equals(usersRequest.getEmail(), "") ){
            throw new RuntimeException("All fields must be filled");
        }

        if(usersRequest.getPassword().length() < 8){
            throw new RuntimeException("Password must be at least 8 characters");
        }

        if (usersRepository.findByUsername(usersRequest.getUsername()).isPresent()){
            throw new RuntimeException("Username already exists");
        }

        if (usersRepository.findByEmail(usersRequest.getEmail()).isPresent()){
            throw new RuntimeException("Email already exists");
        }

        Users users = new Users();
        users.setUsername(usersRequest.getUsername());
        users.setEmail(usersRequest.getEmail());
        users.setPassword(passwordEncoder.encode(usersRequest.getPassword()));
        users.setRole(Optional.ofNullable(usersRequest.getRole()).orElse("USER"));
        Users register = usersRepository.save(users);
        return convertToResponse(register);
    }

    @Transactional
    public UsersResponse loginUser(LoginRequest loginRequest){
        Optional<Users> usersOptional= usersRepository.findByUsername(loginRequest.getUsername());
        if(Objects.equals(loginRequest.getUsername(), "") || Objects.equals(loginRequest.getPassword(), "") ){
            throw new RuntimeException("All fields must be filled");
        }

        if (usersOptional.isEmpty()){
            throw new RuntimeException("User not found");
        }

        Users user = usersOptional.get();

        if(!user.getUsername().equals(loginRequest.getUsername())){
            throw new RuntimeException("Invalid username or password");
        }

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid username or password");
        }

        return convertToResponse(user);
    }


    @Transactional
    public UsersResponse updatePassword(String username, UsersRequest usersRequest){
        Users users = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(usersRequest.getPassword().length() < 8){
            throw new RuntimeException("Password must be at least 8 characters");

        }
        users.setPassword(passwordEncoder.encode(usersRequest.getPassword()));
        Users update = usersRepository.save(users);
        return convertToResponse(update);
    }


    @Transactional
    public void deleteUser(String username){
        Users users = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        usersRepository.delete(users);
    }


    @Transactional
    public UsersResponse updateUsers(String username, UsersRequest usersRequest){
        Users users = usersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(usersRequest.getUsername() != null){
            users.setUsername(usersRequest.getUsername());
        }

        if(usersRequest.getEmail() != null){
            users.setEmail(usersRequest.getEmail());
        }

        if(usersRequest.getPassword() != null){
            users.setPassword(passwordEncoder.encode(usersRequest.getPassword()));
        }

        if(usersRequest.getRole() != null){
            users.setRole(usersRequest.getRole());
        }

        Users update = usersRepository.save(users);
        return convertToResponse(update);
    }


    @Transactional
    public List<UsersResponse> searchByUsername(String username){
        return usersRepository.findByUsernameContainingIgnoreCase(username)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }


    private UsersResponse convertToResponse(Users users){
        UsersResponse usersResponse = new UsersResponse();
        usersResponse.setUsername(users.getUsername());
        usersResponse.setEmail(users.getEmail());
        usersResponse.setRole(users.getRole());
        usersResponse.setCreatedAt(users.getCreatedAt());
        usersResponse.setUpdatedAt(users.getUpdatedAt());
        return usersResponse;
    }
}
