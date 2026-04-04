package com.apm.expenses.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private String userId;

    public AuthResponse() {
    }

    public AuthResponse(String token, String username, String userId) {
        this.token = token;
        this.username = username;
        this.userId = userId;
    }

}
