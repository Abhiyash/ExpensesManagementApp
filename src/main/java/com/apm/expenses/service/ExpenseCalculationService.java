package com.apm.expenses.service;

import com.apm.expenses.model.BankStatementDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class ExpenseCalculationService {
    double needsTotal = 0, wantedTotal = 0, investmentTotal = 0, eatingOutsideTotal = 0,vegetableGroceryTotal = 0,doctorMedicieTotal = 0,otherTotal = 0;
    public void calculateExpenses(List<BankStatementDetails> bankStatementDetailsList){
        for (BankStatementDetails bankStatement : bankStatementDetailsList) {
            String category = bankStatement.getCategory();
            switch (category){
                    case "Needs":
                        needsTotal += bankStatement.getDebitAmount();
                        break;
                    case "Wants":
                        wantedTotal += bankStatement.getDebitAmount();
                        break;
                    case "Investments":
                        investmentTotal+= bankStatement.getDebitAmount();
                        break;
                default:
                    if (ObjectUtils.isEmpty(bankStatement.getSubCategory())){
                        otherTotal += bankStatement.getDebitAmount();
                    }
                    break;
            }
            String subCategory = bankStatement.getSubCategory();
            switch (subCategory){
                    case "Eating_Outside":
                        eatingOutsideTotal += bankStatement.getDebitAmount();
                        break;
                    case  "Vegetable_And_Groceries":
                        vegetableGroceryTotal += bankStatement.getDebitAmount();
                        break;
                    case "Doctor_Medicine":
                        doctorMedicieTotal += bankStatement.getDebitAmount();
                        break;
                default:
                    if(ObjectUtils.isEmpty(bankStatement.getCategory())){
                        otherTotal+= bankStatement.getDebitAmount();
                    }
                    break;
            }
        }
        System.out.println("needsTotal : " + needsTotal);
        System.out.println("wantedTotal : " + wantedTotal);
        System.out.println("investmentTotal : " + investmentTotal);
        System.out.println("eatingOutsideTotal : " + eatingOutsideTotal);
        System.out.println("vegetableGroceryTotal : " + vegetableGroceryTotal);
        System.out.println("doctorMedicieTotal : " + doctorMedicieTotal);
        System.out.println("otherTotal : " + otherTotal);
    }
}
