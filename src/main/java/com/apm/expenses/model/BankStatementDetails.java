package com.apm.expenses.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document("statement_details")
public class BankStatementDetails {
    private LocalDate transactionDate;
    private String description;
    private String category;
    private String subCategory;
    private double debitAmount;
    private double creditAmount;
    private double closingBalance;
    private String userId = "System";
    private String createdOn;
    private String modifiedOn;
    private String modifiedBy;

}
