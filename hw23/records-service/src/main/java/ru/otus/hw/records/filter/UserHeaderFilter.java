package ru.otus.hw.records.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.otus.hw.records.config.UserHeaderProperties;

import java.io.IOException;

// Defense in depth: reject direct hits that skip the gateway's auth check.
@Component
@Order(1)
@RequiredArgsConstructor
public class UserHeaderFilter extends OncePerRequestFilter {

    private final UserHeaderProperties userHeaderProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/api/records")) {
            var username = request.getHeader(userHeaderProperties.getUserHeader());
            if (!StringUtils.hasText(username)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"detail\":\"Missing authenticated user context\"}");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
