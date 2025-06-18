package com.apm.expenses.service;

import com.apm.expenses.dao.ClassificationDao;
import com.apm.expenses.dao.StatementDetailsDao;
import com.apm.expenses.dto.BankStatementDetailsDto;
import com.apm.expenses.model.BankStatementDetails;
import com.apm.expenses.utility.ExpensesUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import com.apm.expenses.constant.Constants;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatementService {

    @Autowired
    private ExpensesUtility expensesUtility;

    @Autowired
    private FileService fileService;

    @Autowired
    private ClassificationService classificationService;

    @Autowired
    private StatementDetailsDao statementDetailsDao;

    @Autowired
    private ClassificationDao classificationDao;

    public String updateStatement(String userId, String bankAccountNumber) throws IOException {
        final String filesPath = Constants.APP_FILES_PATH + "/" + userId + "/" + Constants.INPUT_FILE;
        List<String> fileNamesList = fileService.getFiles(filesPath);
        for (String fileName : fileNamesList) {
            String completeFilePath = filesPath + "/" + fileName;
            expensesUtility.genrateUUID(fileName);
            System.out.println("Working on " + completeFilePath);
            if (fileName.contains("txt")){
                List<BankStatementDetails> bankStatementDetailsList = fileService.parseInputFilesTxt(completeFilePath);
                if (!ObjectUtils.isEmpty(bankStatementDetailsList)){
                    classificationService.classify(bankStatementDetailsList);
                }
                //TODO Add a return for classify method. So only when status is success we will insert data
                Set<String> refNumbers = bankStatementDetailsList.stream()
                        .map(BankStatementDetails::getRefNumber)
                        .collect(Collectors.toSet());

                Query query = new Query(Criteria.where("refNumber").in(refNumbers).and("bankAccountNumber").is(bankAccountNumber).and("userId").is(userId));
                List<String> existingRefNumbers = statementDetailsDao.fetchStatements(query)
                        .stream()
                        .map(BankStatementDetails::getRefNumber)
                        .collect(Collectors.toList());

                List<BankStatementDetails> newBankStatements = bankStatementDetailsList.stream()
                        .filter(bankStatement -> !existingRefNumbers.contains(bankStatement.getRefNumber()))
                        .peek(bankStatement -> {
                            // Set additional metadata for new records
                            bankStatement.setUserId(userId);
                            bankStatement.setBankAccountNumber(bankAccountNumber);
                            bankStatement.setCreatedOn(LocalDateTime.now());
                            bankStatement.setCreatedBy("System");
                            bankStatement.setModifiedOn(LocalDateTime.now());
                            bankStatement.setModifiedBy("System");
                        })
                        .collect(Collectors.toList());
                if (!newBankStatements.isEmpty()) {
                    statementDetailsDao.insertStatements(newBankStatements);
                }
            }
            else{
                List<BankStatementDetailsDto> bankStatementDetailsDtoList = fileService.parseInputFilesXlsx(completeFilePath);
                statementDetailsDao.updateStatements(bankStatementDetailsDtoList);
                classificationDao.updateConfigsForClassification(bankStatementDetailsDtoList);
            }
            fileService.renameFile(completeFilePath);
        }
        System.out.println(fileNamesList);
        return "SUCCESS";
    }

    public List<BankStatementDetailsDto> getStatement(String userId,LocalDate from, LocalDate to) throws IOException {
        if (ObjectUtils.isEmpty(from))
        {
            from = LocalDate.now().minusYears(1);
        }
        if (ObjectUtils.isEmpty(to))
        {
            to = LocalDate.now();
        }
        System.out.println("From :: " + from);
        System.out.println("To :: " + to);
        Query query = new Query();

        Criteria criteria = Criteria.where("userId").is(userId).and("transactionDate").gte(from).lte(to);
        query.addCriteria(criteria);
        query.fields().include("id").include("transactionDate").include("description").include("category").include("subCategory").include("debitAmount").include("creditAmount").include("bankAccountNumber").include("tag").include("expenseMonth");
        return statementDetailsDao.getStatements(query);
    }

    public String getAndExportStatement(String userId,LocalDate from, LocalDate to) throws IOException {
        List<BankStatementDetailsDto> bankStatementDetailsDtoList = getStatement(userId,from,to);
        System.out.println("bankStatementDetailsDtoList :: " +bankStatementDetailsDtoList);
        fileService.exportData(bankStatementDetailsDtoList,userId);
        return "SUCCESS";
    }

    public String insertConfigs(){
        try {
            classificationDao.insertConfigs();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "SUCCESS";
    }
}
