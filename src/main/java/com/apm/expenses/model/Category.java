package com.apm.expenses.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("configs")
public class Category {

    private String name;
    private List<SubCategory> subCategory;
}
