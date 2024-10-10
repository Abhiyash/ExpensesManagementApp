package com.apm.expenses.service;

import com.apm.expenses.dao.StatementDetailsDao;
import com.apm.expenses.model.BankStatementDetails;
import com.apm.expenses.utility.ExpensesUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import com.apm.expenses.constant.Constants;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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

    public String updateStatement(String userId) throws IOException {
        /*TODO
         * 1. Go to the App directory
         * 2. Go to the user directory
         * 3. Pick up the files
         * 4. Perform Actions according to file format
         * */
        List<BankStatementDetails> bankStatementDetailsList = new ArrayList<BankStatementDetails>();
        final String filesPath = Constants.APP_FILES_PATH + "/" + userId + "/" + Constants.INPUT_FILE;
        Stream<Path> stream = Files.list(Paths.get(filesPath));
        List<String> fileNamesList = stream.filter(file -> !Files.isDirectory(file)).map(Path::getFileName).map(Path::toString).toList();
        for (String fileName : fileNamesList) {
            String completeFilePath = filesPath + "/" + fileName;
            expensesUtility.genrateUUID(fileName);
            System.out.println("Working on " + completeFilePath);
            if (fileName.contains("txt")){
                bankStatementDetailsList = fileParsingService.parseInputFilesTxt(completeFilePath);
            }
            //Add else block for parsing xlsx file

            //TODO Add a return for classify method. So only when status is success we will insert data
            if (!ObjectUtils.isEmpty(bankStatementDetailsList)){
                classificationService.classify(bankStatementDetailsList);
            }

            statementDetailsDao.save(bankStatementDetailsList);
        }
        System.out.println(fileNamesList);
        //System.out.println(mongoTemplate.getCollectionNames());
        return "SUCCESS";
    }

}
