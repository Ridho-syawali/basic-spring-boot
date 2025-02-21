package com.example.todolist.repository;

import com.example.todolist.model.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class UserRepositoryTest {
    @Mock
    private UsersRepository usersRepository;

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


    @Test
    public void testFindByUsername_success() {
            when(usersRepository.findByUsername("admin")).thenReturn(Optional.of(users));

         Optional<Users> users = usersRepository.findByUsername("admin");
        assertTrue(users.isPresent()); // jika data ada / true
        assertEquals("admin", users.get().getUsername());
    }

    @Test
    public void testFindByUsername_notFound() {
        // when : untuk memberitahu mockito kalau kita mau mencari data admin dan mengembalikan user
        when(usersRepository.findByUsername("user")).thenReturn(Optional.empty());

        // menampung object yang ditemukan dari findbyusername
        Optional<Users> users = this.usersRepository.findByUsername("user");
        assertThat(users.isPresent()).isFalse();
    }

    @Test
    public void testFindByEmail_success() {
        when(usersRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.of(users));
        Optional<Users> users = usersRepository.findByEmail("admin@gmail.com");
        assertTrue(users.isPresent());
        assertEquals("admin", users.get().getUsername());
    }

    @Test
    public void testFindByEmail_notFound() {
        when(usersRepository.findByEmail("admin@gmail.com")).thenReturn(Optional.empty());
        Optional<Users> users = usersRepository.findByEmail("admin@gmail.com");
        assertThat(users.isPresent()).isFalse();
    }
}
