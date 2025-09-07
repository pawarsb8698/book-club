package com.library.bookclub.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserAuthenticationProvider userAuthenticationProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = extractTokenFromCookies(request);

        if (token != null) {
            try {
                if ("GET".equalsIgnoreCase(request.getMethod())) {
                    SecurityContextHolder.getContext().setAuthentication(
                            userAuthenticationProvider.validateToken(token)
                    );
                } else {
                    SecurityContextHolder.getContext().setAuthentication(
                            userAuthenticationProvider.validateTokenStrongly(token)
                    );
                }
            } catch (RuntimeException e) {
                SecurityContextHolder.clearContext();
                // Optional: log the exception
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if ("jwt".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
