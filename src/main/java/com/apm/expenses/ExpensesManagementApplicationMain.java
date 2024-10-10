package com.apm.expenses;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = "com.apm.expenses")
public class ExpensesManagementApplicationMain {

    public static void main(String[] args) {
        SpringApplication.run(ExpensesManagementApplicationMain.class,args);
    }
}
