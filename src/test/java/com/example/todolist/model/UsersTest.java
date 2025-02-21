package com.example.todolist.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class UsersTest {
    private Users users;

    @BeforeEach // untuk menjalankan method ini sebelum di test
    public void setUp() { // settup data yang akan digunakan untuk unitest
        MockitoAnnotations.openMocks(this); // untuk bikin data tiruan
        users = new Users();
        users.setUsername("admin");
        users.setEmail("admin@gmail.com");
        users.setPassword("123");
        users.setRole("ADMIN");
    }

    // unit test untuk create user
    @Test // anotasi untuk membuat unit test
    public void testCreateUser() {
        users.onCreate();
        users.setId(UUID.randomUUID());
        // assert digunakan untuk membuat pernyataan
        assertNotNull(users.getId());  // assert untuk emastikan id tidak null
        assertEquals("admin", users.getUsername()); // assert untuk memastikan username benar sesuai data setup
        assertEquals("admin@gmail.com", users.getEmail());
        assertEquals("123", users.getPassword());
        assertEquals("ADMIN", users.getRole());
        // cara kedua pake asserthat
        assertThat(users.getCreatedAt()).isNotNull(); // assert untuk memastikan createdAt tidak null
        assertThat(users.getUpdatedAt()).isNotNull();
        assertThat(users.getCreatedAt().isEqual(users.getUpdatedAt()));
    }
}
