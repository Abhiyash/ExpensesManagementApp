package com.apm.expenses.service;

import com.apm.expenses.config.MongoConfig;
import com.apm.expenses.model.BankStatementDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

@Service
public class ClassificationService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public ClassificationService(){
        MongoConfig mongoConfig = new MongoConfig();
        mongoTemplate = mongoConfig.mongoTemplate();
    }
    public void classify(List<BankStatementDetails> bankStatementDetailsList){
        /*TODO
        * 1. Read the properties file and load all the categories and subcategories
        * 2. Classify the entries in list
        *
        * */
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
        }
    }

    private void assignSubCategory(BankStatementDetails bankStatement, HashMap<String,List<String>> subCategoryMap) {
        for(String subCategory : subCategoryMap.keySet()){
            for(String eachSubCategory : subCategoryMap.get(subCategory)){
                if(bankStatement.getDescription().toLowerCase().contains(eachSubCategory.toLowerCase())){
                    bankStatement.setSubCategory(subCategory);
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
}
