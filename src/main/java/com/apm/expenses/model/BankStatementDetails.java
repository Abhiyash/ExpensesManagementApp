package com.apm.expenses.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BankStatementDetails {
    private LocalDate date;
    private String description;
    private String category;
    private String subCategory;
    private double debitAmount;
    private double creditAmount;
    private double closingBalance;
}
