package com.apm.expenses.service;

import com.apm.expenses.config.MongoConfig;
import com.apm.expenses.dao.ClassificationDao;
import com.apm.expenses.dto.TagInfo;
import com.apm.expenses.model.BankStatementDetails;
import com.apm.expenses.model.Category;
import com.apm.expenses.model.SubCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.swing.text.html.HTML;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Service
public class ClassificationService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ClassificationDao classificationDao;

    public ClassificationService(){
        MongoConfig mongoConfig = new MongoConfig();
        mongoTemplate = mongoConfig.mongoTemplate();
    }
    public void classify(List<BankStatementDetails> bankStatementDetailsList) throws IOException {
        /*TODO
        * 1. Read the properties file and load all the categories and subcategories
        * 2. Classify the entries in list
        *
        *
        Properties properties = new Properties();
        HashMap<String,List<String>> categoryMap = new HashMap<>();
        HashMap<String,List<String>> subCategoryMap = new HashMap<>();
        try {
            properties.load(new FileInputStream("src/main/resources/config.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String wants = properties.getProperty("Wants");
        String[] wantsArr = wants.split(",");
        List<String> wantsList = Arrays.stream(wantsArr).toList();
        categoryMap.put("Wants", wantsList);

        String needs = properties.getProperty("Needs");
        String[] needsArr = needs.split(",");
        List<String> needsList = Arrays.stream(needsArr).toList();
        categoryMap.put("Needs", needsList);

        String investments = properties.getProperty("Investments");
        String[] investmentsArr = investments.split(",");
        List<String> investmentsList = Arrays.stream(investmentsArr).toList();
        categoryMap.put("Investments", investmentsList);

        String eatingOutside = properties.getProperty("Eating_Outside");
        String[] eatingOutsideArr = eatingOutside.split(",");
        List<String> eatingOutsideList = Arrays.stream(eatingOutsideArr).toList();
        subCategoryMap.put("Eating_Outside", eatingOutsideList);

        String vegetableGroceries = properties.getProperty("Vegetable_And_Groceries");
        String[] vegetableGroceriesArr = vegetableGroceries.split(",");
        List<String> vegetableGroceriesList = Arrays.stream(vegetableGroceriesArr).toList();
        subCategoryMap.put("Vegetable_And_Groceries", vegetableGroceriesList);

        String doctorMedicine = properties.getProperty("Doctor_Medicine");
        String[] doctorMedicineArr = doctorMedicine.split(",");
        List<String> doctorMedicineList = Arrays.stream(doctorMedicineArr).toList();
        subCategoryMap.put("Doctor_Medicine", doctorMedicineList);

        String movie = properties.getProperty("Movie");
        List<String> movieList = Arrays.asList(movie);

        String wifi = properties.getProperty("Wifi");
        List<String> wifiList = Arrays.asList(wifi);
        subCategoryMap.put("movie",movieList);
        subCategoryMap.put("wifi",wifiList);

        String petrol = properties.getProperty("Petrol");
        List<String> petrolList = Arrays.asList(petrol);
        subCategoryMap.put("petrol",petrolList);

        String otherNeeds = properties.getProperty("other_needs");
        String[] otherNeedsArr = otherNeeds.split(",");
        List<String> otherNeedsList = Arrays.stream(otherNeedsArr).toList();
        subCategoryMap.put("other_needs",otherNeedsList);

        String mom = properties.getProperty("Mom");
        List<String> momList = Arrays.asList(mom);
        subCategoryMap.put("mom",momList);

        String rent = properties.getProperty("Rent");
        List<String> rentList = Arrays.asList(rent);
        subCategoryMap.put("rent",rentList);

        for(BankStatementDetails bankStatement : bankStatementDetailsList){
            assignSubCategory(bankStatement, subCategoryMap);
            assignCategory(bankStatement,categoryMap);
        }*/
        List<Category> categoryList = classificationDao.loadConfigsForClassification();
        Map<String, TagInfo> tagInfoMap = buildTagMap(categoryList);
        for(BankStatementDetails bankStatementDetails : bankStatementDetailsList){
            String description = bankStatementDetails.getDescription().toLowerCase();
            for(Map.Entry<String, TagInfo> entry : tagInfoMap.entrySet()){
                if (description.contains(entry.getKey().toLowerCase())){
                    TagInfo tagInfo = entry.getValue();
                    bankStatementDetails.setCategory(tagInfo.getCategory());
                    bankStatementDetails.setSubCategory(tagInfo.getSubCategory());
                    bankStatementDetails.setTag(entry.getKey());
                    break;
                }
            }
        }

        //insertConfigs();
    }

    private void insertConfigs() throws IOException {
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

    private void assignSubCategory(BankStatementDetails bankStatement, HashMap<String,List<String>> subCategoryMap) {
        for(String subCategory : subCategoryMap.keySet()){
            for(String eachSubCategory : subCategoryMap.get(subCategory)){
                if(bankStatement.getDescription().toLowerCase().contains(eachSubCategory.toLowerCase())){
                    bankStatement.setSubCategory(subCategory);
                    bankStatement.setTag(eachSubCategory);
                }
            }
        }

    }

    private void assignCategory(BankStatementDetails bankStatement, HashMap<String,List<String>> categoryMap ) {
        for (String category : categoryMap.keySet()) {
            for (String eachCategory : categoryMap.get(category)) {
                if (bankStatement.getDescription().toLowerCase().contains(eachCategory.toLowerCase()) || bankStatement.getSubCategory().contains(eachCategory)) {
                    bankStatement.setCategory(category);
                    if (category.equals("Investments")){
                        bankStatement.setSubCategory(category);
                    }
                }
            }
        }
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
