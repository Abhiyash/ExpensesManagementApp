package com.apm.expenses.service;

import com.apm.expenses.dao.UserTokenDao;
import com.apm.expenses.model.UserTokenDetails;
import com.apm.expenses.utility.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {

    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    @Autowired
    private UserTokenDao userTokenDao;

    @Autowired
    private JwtUtil jwtService;

    public boolean isTokenValid(String token) {
        // 1. Check if token is physically valid (expiry, signature)
        try {
            jwtService.getClaims(token);
        } catch (Exception e) {
            return false;
        }

        // 2. Check if token is blacklisted in MongoDB
        UserTokenDetails userTokenDetails = userTokenDao.findByToken(token).orElse(null);
        return userTokenDetails != null && !userTokenDetails.isBlacklisted();
    }
}
