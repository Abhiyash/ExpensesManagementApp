package com.apm.expenses.service;

import com.apm.expenses.dto.BankDetailsRequest;
import com.apm.expenses.model.BankDetails;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class BankDetailsService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public BankDetails addBankDetails(BankDetailsRequest bankDetailsRequest){
        //TODO perform validation for bank details request
        BankDetails bankDetails = new BankDetails();
        BeanUtils.copyProperties(bankDetailsRequest, bankDetails);
        mongoTemplate.insert(bankDetails);
        return bankDetails;
    }

    public BankDetails getBankDetailsByUserId(String userId){
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        return mongoTemplate.findOne(query, BankDetails.class);
    }

    public BankDetails getBankDetailsById(String id){
        return mongoTemplate.findById(id, BankDetails.class);
    }

    public BankDetails updateBankDetails(String id, BankDetailsRequest bankDetailsRequest){
        BankDetails bankDetails = mongoTemplate.findById(id, BankDetails.class);
        if (bankDetails == null){
            return null;
        }
        BeanUtils.copyProperties(bankDetailsRequest, bankDetails);
        bankDetails.setId(id);
        mongoTemplate.save(bankDetails);
        return bankDetails;
    }

    public int deleteBankDetails(String id){
        var result = mongoTemplate.remove(Query.query(Criteria.where("id").is(id)), BankDetails.class);
        return (int) result.getDeletedCount();
    }
}
