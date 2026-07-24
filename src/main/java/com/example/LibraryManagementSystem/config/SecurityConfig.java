package com.example.LibraryManagementSystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.LibraryManagementSystem.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration configuration) throws Exception{

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception{
    
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, exception) ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                )
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/error").permitAll()

                .requestMatchers(HttpMethod.GET,
                                "/loans/me",
                                "/loans/me/**"
                ).authenticated()
                .requestMatchers(HttpMethod.PATCH,
                                "/loans/me/*/return"
                ).authenticated()
                .requestMatchers(HttpMethod.GET,
                                "/loans",
                                "/loans/**"
                ).hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH,
                               "/loans/**"
                ).hasRole("ADMIN")    

                .requestMatchers(HttpMethod.POST, 
                                "/books/**",
                                "/authors/**",
                                "/categories/**"   
                                ).hasRole("ADMIN")

                .requestMatchers(HttpMethod.PUT, 
                                "/books/**",
                                "/authors/**",
                                "/categories/**"   
                                ).hasRole("ADMIN")

                .requestMatchers(HttpMethod.DELETE, 
                                "/books/**",
                                "/authors/**",
                                "/categories/**"   
                                ).hasRole("ADMIN")

                .requestMatchers("/users/**").hasRole("ADMIN")

                .anyRequest().authenticated()
            )
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            )
            .build();
    }
}
