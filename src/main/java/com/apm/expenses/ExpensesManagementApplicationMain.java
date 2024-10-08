package com.apm.expenses;

import com.apm.expenses.model.BankStatementDetails;
import com.apm.expenses.service.ClassificationService;
import com.apm.expenses.service.ExpenseCalculationService;
import com.apm.expenses.service.FileParsingService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.util.List;

@SpringBootApplication(scanBasePackages = "com.apm.expenses")

public class ExpensesManagementApplicationMain {
    static ApplicationContext applicationContext;

    static FileParsingService fileParsingService = new FileParsingService();

    static ClassificationService classificationService = new ClassificationService();

    static ExpenseCalculationService expenseCalculationService =new ExpenseCalculationService();

    public static void main(String[] args) {
        applicationContext =  SpringApplication.run(ExpensesManagementApplicationMain.class,args);
        List<BankStatementDetails> bankStatementDetailsList = fileParsingService.parseInputFiles();
        classificationService.classify(bankStatementDetailsList);
        //bankStatementDetailsList.forEach(System.out::println);
        expenseCalculationService.calculateExpenses(bankStatementDetailsList);
        //displayAllBeans();
        try {
            fileParsingService.exportData(bankStatementDetailsList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void displayAllBeans() {
        String[] allBeanNames = applicationContext.getBeanDefinitionNames();
        for(String beanName : allBeanNames) {
            System.out.println(beanName);
        }
    }
}
