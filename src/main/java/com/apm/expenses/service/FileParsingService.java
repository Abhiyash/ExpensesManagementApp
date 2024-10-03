package com.apm.expenses.service;

import com.apm.expenses.model.BankStatementDetails;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
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
        //115329700_1727870796358
        // Abhiyash 115329700_1727870760267
        try(Reader reader = new FileReader("src/main/resources/115329700_1727870796358.txt");) {
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
    public void exportData(List<BankStatementDetails> bankStatementDetailsList) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bank Statements");

        Row header = sheet.createRow(0);
        Cell headercell = header.createCell(0);
        headercell.setCellValue("Narration");

        headercell = header.createCell(1);
        headercell.setCellValue("Category");

        headercell = header.createCell(2);
        headercell.setCellValue("subCategory");

        headercell = header.createCell(3);
        headercell.setCellValue("Amount");

        int rowCount = 1;

        for (BankStatementDetails bankStatementDetails : bankStatementDetailsList) {
            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(bankStatementDetails.getDescription());
            row.createCell(1).setCellValue(bankStatementDetails.getCategory());
            row.createCell(2).setCellValue(bankStatementDetails.getSubCategory());
            row.createCell(3).setCellValue(bankStatementDetails.getDebitAmount());
        }

        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";

        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        workbook.write(outputStream);
        workbook.close();

    }
}
