package com.apm.expenses.dao;

import com.apm.expenses.constant.Constants;
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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Repository
public class ClassificationDao {

    @Autowired
    MongoTemplate mongoTemplate;

    public List<Category> loadConfigs(List<String> categoryNames){
        Query query = new Query();
        query.addCriteria(Criteria.where("name").in(categoryNames));
        return mongoTemplate.find(query, Category.class);
    }

    public List<Category> loadConfigsForNextMonthExpenses(){
        Query query = new Query();
        query.addCriteria(Criteria.where("name").is("NextMonthExpenses"));
        return mongoTemplate.find(query, Category.class);
    }

    public void updateConfigsForClassification(List<BankStatementDetailsDto> bankStatementDetailsDtoList ){
        List<Category> categoryList = loadConfigs(Constants.categoryName);
        for(BankStatementDetailsDto bankStatementDetailsDto : bankStatementDetailsDtoList){
            String category = bankStatementDetailsDto.getCategory();
            if ("UNKNOWN".equals(category)){
                continue;
            }
            String subCategory = bankStatementDetailsDto.getSubCategory();
            String tag = bankStatementDetailsDto.getTag();
            System.out.println("Category: " + category + " SubCategory: " + subCategory + " Tag: " + tag);
            /*for (Category categoryItem : categoryList) {
                if (categoryItem.getName().equals(category)){
                    List<SubCategory> subCategoryList = categoryItem.getSubCategory();
                    for (SubCategory subCategoryItem : subCategoryList) {
                        if (subCategoryItem.getName().equals(subCategory)){
                            List<String> tagItems = subCategoryItem.getTags();
                            for (String tagItem : tagItems){
                                if (!tag.equals(tagItem)){
                                    tagItems.add(tag);
                                    //TODO Update MongoDB
                                    System.out.println("Inserted new tag");
                                }
                            }
                        }
                        else {
                            subCategoryList.add(SubCategory.builder().name(subCategory).tags(List.of(tag)).build());
                            //TODO Update MongoDB
                            System.out.println("Inserted new Subcategory");
                        }
                    }
                }
                else{
                    categoryList.add(Category.builder().name(category).subCategory(List.of(SubCategory.builder().name(subCategory).tags(List.of(tag)).build())).build());
                    //TODO update MongoDB
                    System.out.println("Inserted new Category");
                }
            }*/
            Query query = new Query(Criteria.where("name").is(category)
                    .and("subCategory.name").is(subCategory));

            Update update = new Update().addToSet("subCategory.$.tags", tag);
            UpdateResult result = mongoTemplate.updateFirst(query, update, Category.class);

            if (result.getMatchedCount() == 0) {
                Query categoryQuery = new Query(Criteria.where("name").is(category));

                SubCategory sub = new SubCategory();
                sub.setName(subCategory);
                sub.setTags(List.of(tag));

                Update push = new Update().push("subCategory", sub);
                mongoTemplate.updateFirst(categoryQuery, push, Category.class);
            }
        }
    }

    public void insertConfigs() throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream("src/main/resources/config.properties"));

        Map<String, List<String>> topCategories = new HashMap<>();
        Map<String, List<String>> subcategoryTags = new HashMap<>();

        for (String key : props.stringPropertyNames()) {
            List<String> values = Arrays.stream(props.getProperty(key).split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();

            if (List.of("Needs", "Wants", "Investments").contains(key)) {
                topCategories.put(key, values);
            } else {
                subcategoryTags.put(key, values);
            }
        }

        for (Map.Entry<String, List<String>> entry : topCategories.entrySet()) {
            String categoryName = entry.getKey();
            List<String> subcatNames = entry.getValue();

            List<SubCategory> subcategories = new ArrayList<>();
            for (String subcat : subcatNames) {
                List<String> tags = subcategoryTags.getOrDefault(subcat, List.of(subcat));
                subcategories.add(new SubCategory(subcat, tags));
            }

            Category category = new Category();
            category.setName(categoryName);
            category.setSubCategory(subcategories);
            mongoTemplate.save(category);
        }

        System.out.println("Loaded categories from .properties into MongoDB.");
    }
}
