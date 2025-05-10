package com.apm.expenses.service;

import com.apm.expenses.dao.StatementDetailsDao;
import com.apm.expenses.dto.BankStatementDetailsDto;
import com.apm.expenses.model.BankStatementDetails;
import com.apm.expenses.utility.ExpensesUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import com.apm.expenses.constant.Constants;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class StatementService {

    @Autowired
    private ExpensesUtility expensesUtility;

    @Autowired
    private FileParsingService fileParsingService;

    @Autowired
    private ClassificationService classificationService;

    @Autowired
    private StatementDetailsDao statementDetailsDao;

    public String updateStatement(String userId, String bankAccountNumber) throws IOException {
        /*TODO
         * 1. Go to the App directory
         * 2. Go to the user directory
         * 3. Pick up the files
         * 4. Perform Actions according to file format
         * */

        final String filesPath = Constants.APP_FILES_PATH + "/" + userId + "/" + Constants.INPUT_FILE;
        Stream<Path> stream = Files.list(Paths.get(filesPath));
        List<String> fileNamesList = stream.filter(file -> !Files.isDirectory(file)).map(Path::getFileName).map(Path::toString).toList();
        for (String fileName : fileNamesList) {
            String completeFilePath = filesPath + "/" + fileName;
            expensesUtility.genrateUUID(fileName);
            System.out.println("Working on " + completeFilePath);
            if (fileName.contains("txt")){
                List<BankStatementDetails> bankStatementDetailsList = fileParsingService.parseInputFilesTxt(completeFilePath);
                if (!ObjectUtils.isEmpty(bankStatementDetailsList)){
                    classificationService.classify(bankStatementDetailsList);
                }
                //TODO Add a return for classify method. So only when status is success we will insert data
                /*TODO
                * 1. Fetch data from Mongo using the query parameters
                * 2. Check if the data is duplicate
                * 3. Only save new data
                * */
                Set<String> refNumbers = bankStatementDetailsList.stream()
                        .map(BankStatementDetails::getRefNumber)
                        .collect(Collectors.toSet());
                //statementDetailsDao.save(bankStatementDetailsList);

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
                System.out.println("newbankStatements :: "+newBankStatements);
                if (!newBankStatements.isEmpty()) {
                    statementDetailsDao.insertStatements(newBankStatements);
                }
            }
            else{
                List<BankStatementDetailsDto> bankStatementDetailsDtoList = fileParsingService.parseInputFilesXlsx(completeFilePath);
                //TODO Update the records in MongoDB

            }





        }
        System.out.println(fileNamesList);
        //System.out.println(mongoTemplate.getCollectionNames());
        return "SUCCESS";
    }

    public String getStatement(String userId,LocalDate from, LocalDate to) throws IOException {
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
        query.fields().include("id").include("transactionDate").include("description").include("category").include("subCategory").include("debitAmount").include("creditAmount").include("bankAccountNumber").include("tag");
        List<BankStatementDetailsDto> bankStatementDetailsDtoList = statementDetailsDao.getStatements(query);
        System.out.println("bankStatementDetailsDtoList :: " +bankStatementDetailsDtoList);
        fileParsingService.exportData(bankStatementDetailsDtoList,userId);
        return "SUCCESS";
    }
}
