package com.apm.expenses.utility;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
}
