package com.apm.expenses.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Setter
@Getter
@Document(collection = "user_token_details")
@Builder
public class UserTokenDetails {

    @Id
    private String id;
    private String token;
    private String userId;
    private boolean blacklisted = false;

    // MongoDB will automatically delete this doc after this date
    @Indexed(expireAfterSeconds = 0)
    private Date expiryDate;

}
