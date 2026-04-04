package com.apm.expenses.controller;

import com.apm.expenses.dto.auth.AuthResponse;
import com.apm.expenses.dto.auth.LoginRequest;
import com.apm.expenses.dto.auth.SignupRequest;
import com.apm.expenses.model.User;
import com.apm.expenses.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest signupRequest){
        User created = authService.register(signupRequest);
        return ResponseEntity.status(201).body(created.getId());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signout")
    public ResponseEntity<String> signout(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        authService.signout(token);
        return ResponseEntity.ok("Signed out successfully");
    }
}
