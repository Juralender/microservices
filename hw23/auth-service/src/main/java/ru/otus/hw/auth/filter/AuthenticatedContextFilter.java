package ru.otus.hw.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Defense in depth: reject direct hits that skip the gateway's auth check.
@Component
@Order(1)
public class AuthenticatedContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var uri = request.getRequestURI();
        var isProtected = uri.startsWith("/api/users") || uri.equals("/api/auth/logout");
        if (isProtected && (!StringUtils.hasText(request.getHeader("X-User-Id"))
                || !StringUtils.hasText(request.getHeader("X-Username")))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"detail\":\"Missing authenticated user context\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
