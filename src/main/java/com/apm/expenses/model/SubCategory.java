package com.apm.expenses.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SubCategory {
    private String name;
    private List<String> tags;
}
