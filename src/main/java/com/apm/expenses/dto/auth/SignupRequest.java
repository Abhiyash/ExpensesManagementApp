package com.apm.expenses.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignupRequest {

    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String emailId;

    @NotBlank
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    public SignupRequest() {
    }

}
