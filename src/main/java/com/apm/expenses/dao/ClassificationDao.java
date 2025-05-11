package com.apm.expenses.dao;

import com.apm.expenses.dto.BankStatementDetailsDto;
import com.apm.expenses.model.Category;
import com.apm.expenses.model.SubCategory;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClassificationDao {

    @Autowired
    MongoTemplate mongoTemplate;

    public List<Category> loadConfigsForClassification(){
        return mongoTemplate.findAll(Category.class);
    }

    public void updateConfigsForClassification(List<BankStatementDetailsDto> bankStatementDetailsDtoList ){
        for(BankStatementDetailsDto bankStatementDetailsDto : bankStatementDetailsDtoList){
            String category = bankStatementDetailsDto.getCategory();
            if ("UNKNOWN".equals(category)){
                continue;
            }
            String subCategory = bankStatementDetailsDto.getSubCategory();
            String tag = bankStatementDetailsDto.getTag();
            Query query = new Query(Criteria.where("name").is(category)
                    .and("subCategory.name").is(subCategory));

            Update update = new Update().addToSet("subCategory.$.tags", tag);
            UpdateResult result = mongoTemplate.updateFirst(query, update, Category.class);

            if (result.getModifiedCount() == 0) {
                Query categoryQuery = new Query(Criteria.where("name").is(category));

                SubCategory sub = new SubCategory();
                sub.setName(subCategory);
                sub.setTags(List.of(tag));

                Update push = new Update().push("subCategory", sub);
                mongoTemplate.updateFirst(categoryQuery, push, Category.class);
            }
        }
    }
}
