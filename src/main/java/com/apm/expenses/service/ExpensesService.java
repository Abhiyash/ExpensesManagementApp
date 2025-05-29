package com.apm.expenses.service;

import com.apm.expenses.dao.StatementDetailsDao;
import com.apm.expenses.dto.BankStatementDetailsDto;
import com.apm.expenses.dto.TotalDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

