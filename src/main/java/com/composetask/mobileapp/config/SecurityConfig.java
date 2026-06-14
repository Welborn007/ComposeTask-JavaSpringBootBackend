package com.composetask.mobileapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .authorizeHttpRequests(auth -> auth

                        // ✅ Swagger / OpenAPI
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Public
                        .requestMatchers("/api/auth/**").permitAll()

                        // Secure user endpoints
                        // Admins can update user roles
                        .requestMatchers(HttpMethod.PUT, "/api/users/*/role").hasRole("ADMIN")
                        .requestMatchers("/api/users/**").authenticated()

                        // Public GET for posts
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()

                        // Secure POST for posts
                        .requestMatchers(HttpMethod.POST, "/api/posts/**").authenticated()

                        .requestMatchers(HttpMethod.PUT, "/api/posts/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**").authenticated()

                        // Vendor endpoints
                        // VENDOR role may manage their own vendors
                        .requestMatchers(HttpMethod.GET, "/api/vendors/my").hasRole("VENDOR")
                        // Customers (and admins/vendors) can view/search vendors
                        .requestMatchers(HttpMethod.GET, "/api/vendors/**").hasAnyRole("CUSTOMER","VENDOR","ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/vendors/**").hasRole("VENDOR")
                        .requestMatchers(HttpMethod.PUT, "/api/vendors/**").hasRole("VENDOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/vendors/**").hasRole("VENDOR")

                        // Verification endpoints
                        // Vendors submit verification requests
                        .requestMatchers(HttpMethod.POST, "/api/verification/**").hasRole("VENDOR")
                        // Vendors and admins can view verification requests
                        .requestMatchers(HttpMethod.GET, "/api/verification/**").hasAnyRole("VENDOR","ADMIN")
                        // Only ADMIN can approve/reject
                        .requestMatchers(HttpMethod.PUT, "/api/verification/*/approve").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/verification/*/reject").hasRole("ADMIN")

                        // Everything else needs authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .formLogin(login -> login.disable())
                .httpBasic(basic -> basic.disable());

        // Very important for JWT
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
