package com.example.todolist.service;


import com.example.todolist.dto.exception.DataNotFoundException;

import com.example.todolist.dto.request.TodolistRequest;

import com.example.todolist.dto.response.TodolistResponse;

import com.example.todolist.model.Todolist;
import com.example.todolist.repository.TodolistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TodolistService {
    @Autowired
    private TodolistRepository todolistRepository;

    public List<TodolistResponse> findAll() {
       try {
           return todolistRepository.findAll()
                   .stream()
                   .map(this::convertToResponse)
                   .toList();
       } catch (Exception e) {
           throw new RuntimeException("Failed to get all todolist", e);
       }
    }

    public TodolistResponse create(TodolistRequest todolistRequest) {
        try{
            Todolist todolist = new Todolist();
            todolist.setTitle(todolistRequest.getTitle());
            todolist.setDescription(todolistRequest.getDescription());
            todolist.setUserId(todolistRequest.getUserId());
            todolist.setCategoryId(todolistRequest.getCategoryId());
            todolistRepository.save(todolist);
            return convertToResponse(todolist);
        }  catch (Exception e){
            throw new RuntimeException("Failed to create todolist", e);
        }
    }

    public TodolistResponse update(Long id, TodolistRequest todolistRequest) {
        try{
            Todolist todolist = todolistRepository.findById(id)
                    .orElseThrow(() -> new DataNotFoundException("Todolist with ID " + id + " not found"));

            todolist.setTitle(todolistRequest.getTitle());
            todolist.setDescription(todolistRequest.getDescription());
            todolist.setUserId(todolistRequest.getUserId());
            todolist.setCategoryId(todolistRequest.getCategoryId());
            todolistRepository.save(todolist);
            return convertToResponse(todolist);
        } catch (DataNotFoundException e){
            throw e;
        } catch (Exception e){
            throw new RuntimeException("Failed to update todolist"+e.getMessage(), e);
        }
    }

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

    private TodolistResponse convertToResponse(Todolist todolist){
        TodolistResponse response = new TodolistResponse();
        response.setId(todolist.getId());
        response.setTitle(todolist.getTitle());
        response.setDescription(todolist.getDescription());
        response.setUserId(todolist.getUserId());
        response.setCategoryId(todolist.getCategoryId());
        response.setCompleted(todolist.isCompleted());
        response.setCreatedAt(todolist.getCreatedAt());
        response.setUpdatedAt(todolist.getUpdatedAt());
        return response;
    }


}
