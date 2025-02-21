package com.example.todolist.config;

import com.example.todolist.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

/* file ini dibuat untuk mengatur konfigurasi seperti menentukan request mana yang diizinkan,
* aturan cors,authentikasi
* */

@Configuration
public class SecurityConfig {
    private final JwtRequestFilter jwtRequestFilter;
    private final UserService userService;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter, UserService userService) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.userService = userService;
    }

    // method untuk mengkonfigurasi keamanan spring
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Http.csrf : disable csrf(cross site request forgery )
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration corsConfiguration = new CorsConfiguration();
                        corsConfiguration.setAllowCredentials(true); // mengizinkan credentials
                        corsConfiguration.addAllowedOrigin("http://localhost:3000"); // mengizikan app apa saja yang bisa mengakses resource ini
                        corsConfiguration.addAllowedHeader("*"); // mengizinkan semua header
                        corsConfiguration.addAllowedMethod("*"); // mengizinkan semua method(post,put,get,delete)
                        corsConfiguration.setMaxAge(3600L); // durasi cors dalam detik
                        return corsConfiguration;
                    }
                }))
                // pengaturan otorisasi(siapa saja yang mengakses endpoint)
                .authorizeHttpRequests(session->session
                        .requestMatchers(HttpMethod.GET, "/api/users/all").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/users/register").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/users/login").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/users/update").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/delete").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/category/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/category/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/category/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/category/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/todolist/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/todolist/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/todolist/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/todolist/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/todolist/todos").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/todolist/trash/{username}").permitAll()
                        .anyRequest().authenticated()
                )
                // mengatur session untuk tidak menyimpan informasi user kedalam session tapi menggunakan jwt
                .sessionManagement(session->session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // methode untuk authentikasi user
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    //mengambil data user dari database
    @Bean
    public UserDetailsService userDetailsService(){
        return userService::loadUserByUsername;
    }

    // untuk mengencode password
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // untuk mengambil authentication manager untuk keperluan authentikasi user(login)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
        throws Exception{
        return authConfig.getAuthenticationManager();
    }
}
