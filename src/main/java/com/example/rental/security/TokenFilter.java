package com.example.rental.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;



@Slf4j
@Component
public class TokenFilter extends OncePerRequestFilter {

    private final JwtCore jwtCore;
    private final UserDetailsService userDetailsService;

    @Autowired
    public TokenFilter(JwtCore jwtCore, @Lazy UserDetailsService userDetailsService) {
        this.jwtCore = jwtCore;
        this.userDetailsService = userDetailsService;
    }



    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {


        String jwt = null;
        String username;

        try {
            String headerAuth = request.getHeader("Authorization");
            log.info("Authorization header: {}", headerAuth);
            if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
                jwt = headerAuth.substring(7);
                log.info("Extracted JWT: {}", jwt);
            }

            if (jwt != null) {
                try {
                    username = jwtCore.getNameFromToken(jwt);
                    log.info("Extracted username from JWT: {}", username);
                } catch (ExpiredJwtException e) {
                    log.warn("JWT expired", e);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token expired");
                    return;
                } catch (NullPointerException e) {
                    log.error("JwtCore is null", e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
                    return;
                } catch (Exception e) {
                    log.error("Invalid token", e);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                    return;
                }

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    log.info("Loading user details for username: {}", username);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    log.info("User details loaded: {}", userDetails);

                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            jwtCore.getRoles(jwt).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.info("Authentication set in SecurityContext for user: {}", username);
                }
            }
        } catch (Exception e) {
            log.error("Error processing token", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Server Error");
            return;
        }

        log.info("Proceeding with filter chain");
        filterChain.doFilter(request, response);
    }
}
