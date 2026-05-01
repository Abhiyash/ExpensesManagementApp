package com.apm.expenses.utility;

import com.apm.expenses.dto.auth.UserPrinciple;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ExpensesUtility {
    public String genrateUUID(String fileName){
        long timeStamp = System.currentTimeMillis();
        String uniqueID = String.valueOf(UUID.nameUUIDFromBytes(String.join("_",fileName,String.valueOf(timeStamp)).getBytes()));
        System.out.println(uniqueID);
        return uniqueID;
    }

    public LocalDateTime getCurrentTimeStamp(){
        return LocalDateTime.now();
    }

    public String currentTimeStamp(){
        return String.valueOf(System.currentTimeMillis());
    }

    public String getUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
            return userPrinciple.getUsername();
        }
        throw new RuntimeException("User not authenticated");
    }
}
