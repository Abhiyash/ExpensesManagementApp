package com.apm.expenses.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BankDetailsRequest {
    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Account Name is required")
    private String accountName;

    @NotBlank(message = "Account Number is required")
    private String accountNumber;

    @NotBlank(message = "Bank Name is required")
    private String bankName;

    @NotBlank(message = "IFSC Code is required")
    private String ifsCode;
}
