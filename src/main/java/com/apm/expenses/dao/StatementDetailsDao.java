package com.apm.expenses.dao;

import com.apm.expenses.dto.BankStatementDetailsDto;
import com.apm.expenses.model.BankStatementDetails;
import com.apm.expenses.utility.ExpensesUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class StatementDetailsDao {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ExpensesUtility expensesUtility;

    public void save(List<BankStatementDetails> bankStatementDetailsList){
        LocalDateTime localDateTime = expensesUtility.getCurrentTimeStamp();
        for(BankStatementDetails bankStatementDetails : bankStatementDetailsList){
            bankStatementDetails.setCreatedOn(localDateTime);
            bankStatementDetails.setModifiedOn(localDateTime);
            bankStatementDetails.setCreatedBy("SYSTEM");
            bankStatementDetails.setModifiedBy("SYSTEM");
        }
        mongoTemplate.insert(bankStatementDetailsList,"statement_details");
    }

    public List<BankStatementDetailsDto> getStatements(Query query){

        List<BankStatementDetailsDto> bankStatementDetailsDtoList = mongoTemplate.find(query, BankStatementDetailsDto.class);
        System.out.println(bankStatementDetailsDtoList);
        return bankStatementDetailsDtoList;
    }
}
