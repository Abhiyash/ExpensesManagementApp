package com.apm.expenses.service;

import com.apm.expenses.config.MongoConfig;
import com.apm.expenses.constant.Constants;
import com.apm.expenses.dao.ClassificationDao;
import com.apm.expenses.dto.TagInfo;
import com.apm.expenses.model.BankStatementDetails;
import com.apm.expenses.model.Category;
import com.apm.expenses.model.SubCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class ClassificationService {

    @Autowired
    private ClassificationDao classificationDao;

    public ClassificationService(){
    }
    public void classify(List<BankStatementDetails> bankStatementDetailsList) throws IOException {

        List<Category> categoryList = classificationDao.loadConfigs(Constants.categoryName);
        List<Category> nextMonthExpenses = classificationDao.loadConfigs(Constants.nextMonthExpense

        );
        Map<String, TagInfo> tagInfoMap = buildTagMap(categoryList);
        Map<String,TagInfo> nextMonthExpensesTagInfoMap = buildTagMap(nextMonthExpenses);
        for(BankStatementDetails bankStatementDetails : bankStatementDetailsList){
            String description = bankStatementDetails.getDescription().toLowerCase();
            String expenseMonth = bankStatementDetails.getTransactionDate().getMonth().toString() + bankStatementDetails.getTransactionDate().getYear();

            Boolean salaryCredited = checkIfSalaryCredited(description);
            /**TODO
             * 1. Get the expense month from the transaction date
             * 2. Check whether description is for salary.
             * 3. Set salaryCredited Flag to true.
             * 4. Check if expenses are from expenseForNextMonth. If yes then tag the expense as month+1. If no then continue
             */
            boolean statementClassified = false;
            if (salaryCredited){
                for(Map.Entry<String,TagInfo> entry : nextMonthExpensesTagInfoMap.entrySet()){
                    if (description.contains(entry.getKey())){
                        TagInfo tagInfo = entry.getValue();
                        bankStatementDetails.setCategory(tagInfo.getCategory());
                        bankStatementDetails.setSubCategory(tagInfo.getSubCategory());
                        bankStatementDetails.setTag(entry.getKey());
                        expenseMonth = getExpenseMonthFromDate(bankStatementDetails.getTransactionDate(),Boolean.TRUE);
                        bankStatementDetails.setExpenseMonth(expenseMonth);
                        statementClassified = true;
                        break;
                    }
                }
            }
            if (!statementClassified){
                for(Map.Entry<String, TagInfo> entry : tagInfoMap.entrySet()){
                    if (description.contains(entry.getKey().toLowerCase())){
                        TagInfo tagInfo = entry.getValue();
                        bankStatementDetails.setCategory(tagInfo.getCategory());
                        bankStatementDetails.setSubCategory(tagInfo.getSubCategory());
                        bankStatementDetails.setTag(entry.getKey());
                        expenseMonth = getExpenseMonthFromDate(bankStatementDetails.getTransactionDate(),Boolean.FALSE);
                        bankStatementDetails.setExpenseMonth(expenseMonth);
                        break;
                    }
                }
            }
        }
    }

    private String getExpenseMonthFromDate(LocalDate transactionDate, boolean incrementMonth) {
        LocalDate adjustedDate = incrementMonth ? transactionDate.plusMonths(1) : transactionDate;

        String month = adjustedDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int year = adjustedDate.getYear();

        return month + year;
    }

    private Boolean checkIfSalaryCredited(String description){
        List<String> salaryDescriptions = Constants.salaryDescipton;
        boolean flag = false;
        for (String desc : salaryDescriptions) {
            if (desc != null && desc.contains(description)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    private Map<String, TagInfo> buildTagMap(List<Category> categoryList) {
        Map<String, TagInfo> tagMap = new HashMap<>();
        for (Category category : categoryList) {
            for (SubCategory subCategory : category.getSubCategory()) {
                for (String tag : subCategory.getTags()) {
                    tagMap.put(tag.toLowerCase(), new TagInfo(category.getName(), subCategory.getName()));
                }
            }
        }
        return tagMap;
    }
}
