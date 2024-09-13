package com.apm.expenses;

import com.apm.expenses.model.BankStatementDetails;
import com.apm.expenses.service.ClassificationService;
import com.apm.expenses.service.FileParsingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;

@SpringBootApplication
public class ExpensesManagementApplicationMain {
    static ApplicationContext applicationContext;


    static FileParsingService fileParsingService = new FileParsingService();

    static ClassificationService classificationService = new ClassificationService();

    public static void main(String[] args) {
        applicationContext =  SpringApplication.run(ExpensesManagementApplicationMain.class,args);
        List<BankStatementDetails> bankStatementDetailsList = fileParsingService.parseInputFiles();
        classificationService.classify(bankStatementDetailsList);
    }
    public static void displayAllBeans() {
        String[] allBeanNames = applicationContext.getBeanDefinitionNames();
        for(String beanName : allBeanNames) {
            System.out.println(beanName);
        }
    }
}
