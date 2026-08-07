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

                        // Auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()

                        // Users
                        .requestMatchers(HttpMethod.GET, "/api/users").permitAll()
                        // role updates remain admin-only (now without id in path)
                        .requestMatchers(HttpMethod.PUT, "/api/users/role").hasRole("ADMIN")
                        // admins can still delete any user (kept for backwards compatibility)
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                        // allow authenticated users to delete their own account
                        .requestMatchers(HttpMethod.DELETE, "/api/users").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users").authenticated()

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
                        .requestMatchers(HttpMethod.DELETE, "/api/vendors/**").hasAnyRole("VENDOR", "ADMIN")

                        // Verification endpoints
                        // Vendors submit verification requests
                        .requestMatchers(HttpMethod.POST, "/api/verification/**").hasRole("VENDOR")
                        // Vendors and admins can view verification requests
                        .requestMatchers(HttpMethod.GET, "/api/verification/**").hasAnyRole("VENDOR","ADMIN")
                        // Only ADMIN can approve/reject
                        .requestMatchers(HttpMethod.PUT, "/api/verification/*/approve").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/verification/*/reject").hasRole("ADMIN")

                        // Reviews endpoints
                        .requestMatchers(HttpMethod.GET, "/api/reviews/my").hasRole("CUSTOMER")
                        // Public can view reviews
                        .requestMatchers(HttpMethod.GET, "/api/reviews/vendor/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/*").permitAll()
                        // Only CUSTOMERS can create or update reviews (ownership enforced in service)
                        .requestMatchers(HttpMethod.POST, "/api/reviews/**").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.PUT, "/api/reviews/**").hasRole("CUSTOMER")
                        // Customers or Admins can delete (service enforces ownership unless admin)
                        .requestMatchers(HttpMethod.DELETE, "/api/reviews/**").hasAnyRole("CUSTOMER","ADMIN")

                        // Trust Score endpoints (public - anyone can view vendor ratings)
                        .requestMatchers(HttpMethod.GET, "/api/trust-score/**").permitAll()

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
