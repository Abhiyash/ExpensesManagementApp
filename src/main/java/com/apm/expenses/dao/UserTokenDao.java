package com.apm.expenses.dao;

import com.apm.expenses.model.UserTokenDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public class UserTokenDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void saveToken(UserTokenDetails tokenDetails){
        try {
            mongoTemplate.save(tokenDetails);
        } catch (DuplicateKeyException ex) {
            // token already persisted concurrently; ignore to keep idempotent
        }
    }

    public Optional<UserTokenDetails> findByToken(String token){
        Query query = new Query(Criteria.where("token").is(token));
        UserTokenDetails utd = mongoTemplate.findOne(query, UserTokenDetails.class);
        return Optional.ofNullable(utd);
    }

    public int deleteByToken(String token){
        Query query = new Query(Criteria.where("token").is(token));
        var res = mongoTemplate.remove(query, UserTokenDetails.class);
        return (int) res.getDeletedCount();
    }

    public void deleteExpiredTokens(Date now){
        Query query = new Query(Criteria.where("expiryAt").lte(now));
        mongoTemplate.remove(query, UserTokenDetails.class);
    }
}
