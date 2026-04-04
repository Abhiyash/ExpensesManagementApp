package com.apm.expenses.config;

import com.apm.expenses.dto.auth.UserPrinciple;
import com.apm.expenses.service.TokenService;
import com.apm.expenses.utility.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
@Order(1)
public class TokenValidationFilter implements Filter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenService tokenService;

    // endpoints to exclude from token validation
    private final List<String> excludedPrefixes = List.of(
            "/auth/login",
            "/auth/signup",
            "/actuator",
            "/swagger",
            "/v3/api-docs"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getServletPath();
        // allow excluded paths
        for (String prefix : excludedPrefixes) {
            if (path.startsWith(prefix)) {
                chain.doFilter(request, response);
                return;
            }
        }

        // Allow preflight
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())){
            chain.doFilter(request, response);
            return;
        }

        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            unauthorized(resp, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);

        // check blacklist first
        if (!tokenService.isTokenValid(token)){
            unauthorized(resp, "Token has been invalidated");
            return;
        }

        // verify token exists / valid in token store
        var info = jwtUtil.getClaims(token);
        if (info == null){
            unauthorized(resp, "Invalid or expired token");
            return;
        }
        String username = info.getSubject();
        String userId = info.get("userId", String.class);

        UserPrinciple principle = new UserPrinciple(userId, username);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principle, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }

    private void unauthorized(HttpServletResponse resp, String message) throws IOException {
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        resp.setContentType("application/json");
        String body = String.format("{\"error\": \"%s\"}", message);
        resp.getWriter().write(body);
    }
}
