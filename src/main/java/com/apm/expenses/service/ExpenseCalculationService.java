package com.apm.expenses.service;

import com.apm.expenses.model.BankStatementDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseCalculationService {
    double needsTotal = 0, wantedTotal = 0, investmentTotal = 0, eatingOutsideTotal = 0,vegetableGroceryTotal = 0,doctorMedicieTotal = 0,otherTotal = 0;
    public void calculateExpenses(List<BankStatementDetails> bankStatementDetailsList){
        for (BankStatementDetails bankStatement : bankStatementDetailsList) {
            String category = bankStatement.getCategory();
            switch (category){
                    case "Need":
                        needsTotal += bankStatement.getDebitAmount();
                        break;
                    case "Wants":
                        wantedTotal += bankStatement.getDebitAmount();
                        break;
                    case "Investments":
                        investmentTotal+= bankStatement.getDebitAmount();
                        break;
                default:
                    otherTotal += bankStatement.getDebitAmount();
                    break;
            }
            String subCategory = bankStatement.getSubCategory();
            switch (subCategory){
                    case "Eating":
                        eatingOutsideTotal += bankStatement.getDebitAmount();
                        break;
                    case  "VegetableGrocery":
                        vegetableGroceryTotal += bankStatement.getDebitAmount();
                        break;
                    case "DoctorMedicie":
                        doctorMedicieTotal += bankStatement.getDebitAmount();
                        break;
                default:
                    otherTotal+= bankStatement.getDebitAmount();
                    break;
            }
        }
    }
}
