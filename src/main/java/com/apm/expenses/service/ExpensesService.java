package com.apm.expenses.service;

import com.apm.expenses.dao.StatementDetailsDao;
import com.apm.expenses.dto.BankStatementDetailsDto;
import com.apm.expenses.dto.TotalDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExpensesService {

    @Autowired
    StatementDetailsDao statementDetailsDao;

    @Autowired
    StatementService statementService;

    public List<TotalDto> calculateExpenses(String userId, LocalDate fromDate, LocalDate toDate) {
        //TODO
        //1. Fetch statements
        //2. Calculate total for category and subcategory
        try{
            List<BankStatementDetailsDto> bankStatementDetailsDtoList = statementService.getStatement(userId,fromDate,toDate);
            List<TotalDto> totalDtoList = new ArrayList<>();
            for (BankStatementDetailsDto bankStatementDetailsDto : bankStatementDetailsDtoList) {
                addOrUpdateTotal(totalDtoList, bankStatementDetailsDto.getCategory(), bankStatementDetailsDto.getDebitAmount(), "Category");
                addOrUpdateTotal(totalDtoList, bankStatementDetailsDto.getSubCategory(), bankStatementDetailsDto.getDebitAmount(), "SubCategory");
            }
            ObjectMapper objectMapper  = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(totalDtoList);
            FileOutputStream fileOutputStream = new FileOutputStream("expenses.json");
            fileOutputStream.write(jsonString.getBytes());
            fileOutputStream.close();
            System.out.println(totalDtoList);
            return totalDtoList;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void addOrUpdateTotal(List<TotalDto> totalDtoList, String name, double amount, String type) {
        for (TotalDto totalDto : totalDtoList) {
            if (totalDto.getName().equals(name)) {
                totalDto.setAmount(totalDto.getAmount()+amount);
                return;
            }
        }
        totalDtoList.add(TotalDto.builder().name(name).amount(amount).type(type).build());
    }
}

