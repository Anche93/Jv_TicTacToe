package org.example.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Service
public class AuthFilter extends GenericFilterBean {

    private final JwtProvider jwtProvider;
    private final JwtUtil jwtUtil;

    public AuthFilter(JwtProvider jwtProvider,
                      JwtUtil jwtUtil) {
        this.jwtProvider = jwtProvider;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        if (httpRequest.getMethod().equalsIgnoreCase("OPTIONS")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String path = httpRequest.getRequestURI();
        if (path.startsWith("/user/reg") ||
                path.startsWith("/auth/login") ||
                path.startsWith("/auth/refresh-access") ||
                path.startsWith("/auth/refresh-refresh")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            String token = authHeader.substring(7);
            Claims claims = jwtProvider.extractAllClaims(token);

            JwtAuthentication jwtAuth = jwtUtil.createAuthentication(claims);
            SecurityContextHolder.getContext().setAuthentication(jwtAuth);

            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception ex) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
