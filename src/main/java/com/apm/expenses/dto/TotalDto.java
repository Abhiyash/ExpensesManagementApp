package com.apm.expenses.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class TotalDto {
    private String name;
    private double amount;
    private String type;
}
