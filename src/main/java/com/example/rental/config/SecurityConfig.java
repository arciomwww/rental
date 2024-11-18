package com.example.rental.config;

import com.example.rental.security.TokenFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpStatus;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final TokenFilter tokenFilter;

    @Autowired
    public SecurityConfig(TokenFilter tokenFilter) {
        this.tokenFilter = tokenFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        //Swagger
                        .requestMatchers("/v3/api-docs/**", "/swagger-resources/**", "/swagger-ui/**", "/webjars/**").permitAll()
                        //Registration and Login
                        .requestMatchers("/auth/**").permitAll()
                        //Transaction
                        .requestMatchers("/api/transactions/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/transactions/**").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/transactions/read/all").hasRole("ADMIN")
                        //USER ACCOUNT
                        .requestMatchers(HttpMethod.POST, "/api/accounts/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/accounts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/accounts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/accounts/read/{id}").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,"/api/accounts/all").hasRole("ADMIN")
                        //APPLICATION ACCOUNT
                        .requestMatchers(HttpMethod.POST, "/api/application-accounts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/application-accounts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/application-accounts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/application-accounts/**").hasRole("ADMIN")
                        //Location
                        .requestMatchers(HttpMethod.POST, "/api/locations/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/locations/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/locations/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/locations/**").hasAnyRole("USER", "ADMIN")
                        //CarModel
                        .requestMatchers(HttpMethod.POST, "/api/car-models/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/car-models/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/car-models/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/car-models/**").hasAnyRole("USER", "ADMIN")
                        //Feedback
                        .requestMatchers(HttpMethod.POST, "/api/feedback/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/feedback/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/feedback/**").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/feedback/**").hasAnyRole("USER", "ADMIN")
                        //Car
                        .requestMatchers(HttpMethod.POST, "/api/cars/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/cars/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/cars/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/cars/**").hasAnyRole("USER", "ADMIN")
                        //Rental
                        .requestMatchers(HttpMethod.POST, "/api/rentals/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/rentals/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/rentals/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/rentals/read/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/rentals/read/all").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/rentals/admin/car/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/rentals//user/{userId}/history").hasRole("USER")
                        //Role
                        .requestMatchers(HttpMethod.POST, "/api/roles/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/roles/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/roles/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/roles/**").hasRole("ADMIN")
                        //UserRoles
                        .requestMatchers(HttpMethod.POST, "/api/user-roles/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/user-roles/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/user-roles/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/user-roles/**").hasRole("ADMIN")
                        //User
                        .requestMatchers(HttpMethod.POST, "/api/users/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/read/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users/filter/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

