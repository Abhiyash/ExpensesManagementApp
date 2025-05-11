package com.apm.expenses.dao;

import com.apm.expenses.dto.BankStatementDetailsDto;
import com.apm.expenses.model.BankStatementDetails;
import com.apm.expenses.utility.ExpensesUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

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

    public List<BankStatementDetails> fetchStatements(Query query){
        List<BankStatementDetails> bankStatementDetailsList = mongoTemplate.find(query, BankStatementDetails.class);
        System.out.println(bankStatementDetailsList);
        return bankStatementDetailsList;
    }

    public void insertStatements(List<BankStatementDetails> bankStatementDetailsList){
        mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, BankStatementDetails.class)
                .insert(bankStatementDetailsList)
                .execute();
    }

    public void updateStatements(List<BankStatementDetailsDto> bankStatementDetailsDtoList){
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, BankStatementDetails.class);
        for(BankStatementDetailsDto bankStatementDetailsDto : bankStatementDetailsDtoList){
            Query query = Query.query(Criteria.where("_id").is(bankStatementDetailsDto.getId()));
            System.out.println("ID :: " + bankStatementDetailsDto.getId());
            Update update = new Update();
            if (!ObjectUtils.isEmpty(bankStatementDetailsDto.getCategory())){
                System.out.println("category :: " + bankStatementDetailsDto.getCategory());
                update.set("category", bankStatementDetailsDto.getCategory());
            }
            if (!ObjectUtils.isEmpty(bankStatementDetailsDto.getSubCategory())){
                System.out.println("subcategory :: " + bankStatementDetailsDto.getSubCategory());
                update.set("subCategory", bankStatementDetailsDto.getSubCategory());
            }
            if (!ObjectUtils.isEmpty(bankStatementDetailsDto.getTag())){
                System.out.println("tag :: " + bankStatementDetailsDto.getTag());
                update.set("tag", bankStatementDetailsDto.getTag());
            }

            bulkOperations.updateOne(query, update);
        }
        bulkOperations.execute();
    }
}
