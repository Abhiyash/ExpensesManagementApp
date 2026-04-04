package com.apm.expenses.service;

import com.apm.expenses.dao.UserDao;
import com.apm.expenses.dao.UserTokenDao;
import com.apm.expenses.dto.auth.AuthResponse;
import com.apm.expenses.dto.auth.LoginRequest;
import com.apm.expenses.dto.auth.SignupRequest;
import com.apm.expenses.model.User;
import com.apm.expenses.model.UserTokenDetails;
import com.apm.expenses.utility.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class AuthService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserTokenDao userTokenDao;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenService tokenService;

    private static final long DEFAULT_TTL_MS = 3600_000; // 1 hour

    private String hashPassword(String password){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }

    public User register(SignupRequest req) {
        // check duplicates
        if (userDao.findByUsername(req.getUsername()).isPresent()){
            throw new IllegalArgumentException("Username already exists");
        }
        if (userDao.findByEmailId(req.getEmailId()).isPresent()){
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmailId(req.getEmailId());
        user.setPasswordHash(hashPassword(req.getPassword()));

        return userDao.save(user);
    }

    public AuthResponse login(LoginRequest req){
        User user = userDao.findByUsername(req.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!hashPassword(req.getPassword()).equals(user.getPasswordHash())){
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getId());
        UserTokenDetails userTokenDetails = UserTokenDetails.builder().token(token)
                .userId(user.getId())
                .blacklisted(false)
                .expiryDate(jwtUtil.getClaims(token).getExpiration())
                .build();
        userTokenDao.saveToken(userTokenDetails);
        return new AuthResponse(token, user.getUsername(), user.getId());
    }

    public void signout(String token){
        if (token == null || token.isBlank()) return;
        var info = jwtUtil.getClaims(token);
        if (info != null){
            // persist detailed token info
            userTokenDao.deleteByToken(token);
        }
    }
}
