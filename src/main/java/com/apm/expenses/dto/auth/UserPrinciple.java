package com.apm.expenses.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserPrinciple {
    private String userId;
    private String username;
}
