package com.apm.expenses.service;

import com.apm.expenses.model.BankStatementDetails;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class FileParsingService {
    private List<BankStatementDetails> bankStatementDetailsList = new ArrayList<BankStatementDetails>();

    public List<BankStatementDetails> parseInputFiles() {
        List<BankStatementDetails> bankStatementDetailsList = new ArrayList<BankStatementDetails>();
        try(Reader reader = new FileReader("src/main/resources/115329700_1718992304079.txt");) {
            String[] HEADERS = { "Date", "Narration","Value Dat","Debit Amount","Credit Amount","Chq/Ref Number","Closing Balance"};
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yy", Locale.ENGLISH);
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setDelimiter(',').setHeader(HEADERS).setSkipHeaderRecord(true).build();
            Iterable<CSVRecord> records = csvFormat.parse(reader);

            for (CSVRecord record : records) {
                bankStatementDetailsList.add(new BankStatementDetails(LocalDate.parse(record.get(0).trim(),dateTimeFormatter),record.get(1).trim(),"","",Double.parseDouble(record.get(3).trim()),Double.parseDouble(record.get(4).trim()),Double.parseDouble(record.get(6).trim())));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bankStatementDetailsList;
    }
}
