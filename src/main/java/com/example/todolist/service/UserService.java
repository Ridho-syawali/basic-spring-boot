package com.example.todolist.service;

import com.example.todolist.dto.exception.AuthenticationException;
import com.example.todolist.dto.exception.DataNotFoundException;
import com.example.todolist.dto.exception.DuplicateDataException;
import com.example.todolist.dto.request.UsersRequest;
import com.example.todolist.dto.response.UsersResponse;
import com.example.todolist.model.Users;
import com.example.todolist.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UsersRepository usersRepository;

    public List<UsersResponse> findAll() {
       try {
           return usersRepository.findAll()
                   .stream()
                   .map(this::convertToResponse)
                   .toList();
       } catch (Exception e) {
           throw new RuntimeException("Failed to get all users", e);
       }
    }

    public UsersResponse create(UsersRequest usersRequest) {
        try{
            if (usersRepository.findByUsername(usersRequest.getUsername()).isPresent()){
                throw new DuplicateDataException("Username already exist");
            }
            String role = usersRequest.getRole();
            if (!role.equals("ADMIN") && !role.equals("USER")) {
                throw new AuthenticationException("Role must be ADMIN or USERS");
            }
            Users users = new Users();
            users.setUsername(usersRequest.getUsername());
            users.setPassword(usersRequest.getPassword());
            users.setEmail(usersRequest.getEmail());
            users.setRole(usersRequest.getRole());
            usersRepository.save(users);
            return convertToResponse(users);
        } catch (DuplicateDataException | AuthenticationException e){
            throw e;
        } catch (Exception e){
            throw new RuntimeException("Failed to create user", e);
        }
    }

    public UsersResponse update(UUID id, UsersRequest usersRequest) {
        try{
            Users users = usersRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("User with ID " + id + " not found"));
            if (usersRepository.findByUsername(usersRequest.getUsername()).isPresent()){
                throw new DuplicateDataException("Username already exist");
            }
            users.setUsername(usersRequest.getUsername());
            users.setPassword(usersRequest.getPassword());
            users.setEmail(usersRequest.getEmail());
            usersRepository.save(users);
            return convertToResponse(users);
        } catch (DataNotFoundException | DuplicateDataException e){
            throw e;
        } catch (Exception e){
            throw new RuntimeException("Failed to update category"+e.getMessage(), e);
        }
    }

    public void deleteUser(UUID id){
        try{
            if (!usersRepository.existsById(id)){
                throw new DataNotFoundException("User id not found");
            }
            usersRepository.deleteById(id);
        }catch (DataNotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException("Failed to delete user" + e.getMessage(), e);
        }
    }

    private UsersResponse convertToResponse(Users users){
        UsersResponse response = new UsersResponse();
        response.setId(users.getId());
        response.setUsername(users.getUsername());
        response.setPassword(users.getPassword());
        response.setEmail(users.getEmail());
        response.setRole(users.getRole());
        response.setCreatedAt(users.getCreatedAt());
        response.setUpdatedAt(users.getUpdatedAt());
        return response;
    }


}
