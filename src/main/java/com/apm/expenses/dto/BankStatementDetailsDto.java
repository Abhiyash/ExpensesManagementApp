package com.apm.expenses.dto;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document("statement_details")
public class BankStatementDetailsDto {

    @Id
    private String id;

    private LocalDate transactionDate;
    private String bankAccountNumber;
    private String description;
    private String category;
    private String subCategory;
    private String tag;
    private double debitAmount;
    private double creditAmount;
}
