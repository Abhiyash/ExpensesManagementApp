package com.apm.expenses.dao;

import com.apm.expenses.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Optional<User> findByUsername(String username) {
        Query query = new Query(Criteria.where("username").is(username));
        User user = mongoTemplate.findOne(query, User.class);
        return Optional.ofNullable(user);
    }

    public Optional<User> findByEmailId(String emailId) {
        Query query = new Query(Criteria.where("emailId").is(emailId));
        User user = mongoTemplate.findOne(query, User.class);
        return Optional.ofNullable(user);
    }

    public User save(User user) {
        return mongoTemplate.save(user);
    }

    public Optional<User> findById(String id) {
        User user = mongoTemplate.findById(id, User.class);
        return Optional.ofNullable(user);
    }
}
