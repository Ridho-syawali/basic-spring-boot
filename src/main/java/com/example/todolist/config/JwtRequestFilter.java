package com.example.todolist.config;

import com.example.todolist.service.UserService;
import com.example.todolist.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// untuk autentikasi dan validasi token

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    private final UserService userService;

    @Autowired
    public JwtRequestFilter(@Lazy UserService userService) {
        this.userService = userService;
    }

    // Method doFilterInternal untuk memfilter setiap request yang masuk
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException{
        // mengambil header tokennya
        String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // ambil informasi token
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            jwt = authorizationHeader.substring(7); // token setelah urutan ke 7
            username = jwtUtil.extractUsername(jwt); // mengambil username dari token yang diekstrak
        }

        // Validasi User
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            // object untuk mengambil informasi user yang di ekstrak dari token jwt
            UserDetails userDetails = this.userService.loadUserByUsername(username);
            // lalu menampung object yang isinya adalah user yang sudah di validasi
            if (jwtUtil.validateToken(jwt, userDetails)){
                // menampung object yang isinya informasi username, pass, role/otoritas
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                // untuk menambahkan detail informasi dari request yang dikirim
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // menetapkan user yang telah terautentikasi dan terotorisasi
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            }
        }
        // untuk menjalankan konfigurasi filter
        filterChain.doFilter(request, response);
    }

}
