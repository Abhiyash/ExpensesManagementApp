package com.apm.expenses.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "bank_details")
public class BankDetails {
    @Id
    private String id; // MongoDB will auto-generate this as an ObjectId

    private String userId;
    private String accountName;
    private String accountNumber;
    private String bankName;
    private String IFSC_Code;
    private String createdOn;
    private String updatedOn;
}
