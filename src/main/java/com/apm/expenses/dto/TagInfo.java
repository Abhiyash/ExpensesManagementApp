package com.apm.expenses.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TagInfo {
    private String category;
    private String subCategory;
}
