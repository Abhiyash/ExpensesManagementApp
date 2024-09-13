package com.apm.expenses.service;

import com.apm.expenses.model.BankStatementDetails;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ClassificationService {
    public void classify(List<BankStatementDetails> bankStatementDetailsList){
        /*TODO
        * 1. Read the properties file and load all the categories and subcategories
        * 2. Classify the entries in list
        *
        * */
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/config.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String wantsList = properties.getProperty("Wants");
        String[] wants = wantsList.split(",");
        List<String> wants2 = Arrays.stream(wants).toList();
        System.out.println(wants2);
    }
}
