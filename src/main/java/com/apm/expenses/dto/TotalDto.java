package com.apm.expenses.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class TotalDto {
    private String name;
    private double amount;
    private String type;
}
