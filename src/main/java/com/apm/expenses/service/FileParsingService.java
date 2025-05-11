package com.apm.expenses.service;

import com.apm.expenses.constant.Constants;
import com.apm.expenses.dto.BankStatementDetailsDto;
import com.apm.expenses.model.BankStatementDetails;

import com.apm.expenses.utility.ExpensesUtility;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.apm.expenses.constant.Constants.HEADERS;

@Service
public class FileParsingService {

    @Autowired
    ExpensesUtility expensesUtility;

    public List<BankStatementDetails> parseInputFilesTxt(String fileName) {
        List<BankStatementDetails> bankStatementDetailsList = new ArrayList<BankStatementDetails>();
        //115329700_1727870796358
        // Abhiyash 115329700_1727870760267

        try(Reader reader = new FileReader(fileName);) {

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yy", Locale.ENGLISH);
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder().setDelimiter(',').setHeader(HEADERS).setSkipHeaderRecord(true).build();
            Iterable<CSVRecord> records = csvFormat.parse(reader);

            for (CSVRecord record : records) {
                BankStatementDetails bankStatementDetails = new BankStatementDetails();
                bankStatementDetails.setTransactionDate(LocalDate.parse(record.get(0).trim(),dateTimeFormatter));
                bankStatementDetails.setDescription(record.get(1).trim());
                bankStatementDetails.setDebitAmount(Double.parseDouble(record.get(3).trim()));
                bankStatementDetails.setCreditAmount(Double.parseDouble(record.get(4).trim()));
                bankStatementDetails.setRefNumber(record.get(5).trim());
                bankStatementDetails.setClosingBalance(Double.parseDouble(record.get(6).trim()));
                bankStatementDetails.setSubCategory("");
                bankStatementDetails.setCategory("");
                bankStatementDetailsList.add(bankStatementDetails);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bankStatementDetailsList;
    }

    public List<BankStatementDetailsDto> parseInputFilesXlsx(String fileName) {
        List<BankStatementDetailsDto> bankStatementDetailsDtoList = new ArrayList<BankStatementDetailsDto>();
        try(FileInputStream file = new FileInputStream(new File(fileName))){
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // Assuming first row is header
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row == null) continue;

                BankStatementDetailsDto bankStatementDetailsDto = new BankStatementDetailsDto();
                bankStatementDetailsDto.setId(row.getCell(0).getStringCellValue());
                bankStatementDetailsDto.setTransactionDate(DateUtil.getLocalDateTime(row.getCell(1).getNumericCellValue()).toLocalDate());
                bankStatementDetailsDto.setBankAccountNumber(row.getCell(2).getStringCellValue());
                bankStatementDetailsDto.setDescription(row.getCell(3).getStringCellValue());
                bankStatementDetailsDto.setCategory(row.getCell(4) == null ? "UNKNOWN" : row.getCell(4).getStringCellValue());
                bankStatementDetailsDto.setSubCategory(row.getCell(5) == null ? "UNKNOWN" : row.getCell(5).getStringCellValue());
                bankStatementDetailsDto.setTag(row.getCell(6) == null ? "UNKNOWN" : row.getCell(6).getStringCellValue());
                bankStatementDetailsDto.setDebitAmount(row.getCell(7).getNumericCellValue());
                bankStatementDetailsDto.setCreditAmount(row.getCell(8).getNumericCellValue());

                bankStatementDetailsDtoList.add(bankStatementDetailsDto);
            }
            workbook.close();
                //TODO Rename the files by adding processed suffix and moving to processed folder
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        return bankStatementDetailsDtoList;
    }

    public void exportData(List<BankStatementDetailsDto> bankStatementDetailsDtoList, String userId) throws IOException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bank Statements");

        Row header = sheet.createRow(0);
        Cell headercell = header.createCell(0);
        headercell.setCellValue("Id");

        headercell = header.createCell(1);
        headercell.setCellValue("Transaction Date");

        headercell = header.createCell(2);
        headercell.setCellValue("Account Number");

        headercell = header.createCell(3);
        headercell.setCellValue("Description");

        headercell = header.createCell(4);
        headercell.setCellValue("Category");

        headercell = header.createCell(5);
        headercell.setCellValue("subCategory");

        headercell = header.createCell(6);
        headercell.setCellValue("tag");

        headercell = header.createCell(7);
        headercell.setCellValue("Debit Amount");

        headercell = header.createCell(8);
        headercell.setCellValue("Credit Amount");
        int rowCount = 1;

        for (BankStatementDetailsDto bankStatementDetailsDto : bankStatementDetailsDtoList) {
            Row row = sheet.createRow(rowCount++);

            row.createCell(0).setCellValue(bankStatementDetailsDto.getId());
            row.createCell(1).setCellValue(bankStatementDetailsDto.getTransactionDate());
            row.createCell(2).setCellValue(bankStatementDetailsDto.getBankAccountNumber());
            row.createCell(3).setCellValue(bankStatementDetailsDto.getDescription());
            row.createCell(4).setCellValue(bankStatementDetailsDto.getCategory());
            row.createCell(5).setCellValue(bankStatementDetailsDto.getSubCategory());
            row.createCell(6).setCellValue(bankStatementDetailsDto.getTag());
            row.createCell(7).setCellValue(bankStatementDetailsDto.getDebitAmount());
            row.createCell(8).setCellValue(bankStatementDetailsDto.getCreditAmount());
        }

        String directory = Constants.APP_FILES_PATH + "/" + userId + "/" + Constants.OUTPUT_FILE;
        String fileName = userId + "_" + expensesUtility.currentTimeStamp() +".xlsx";
        Path path = Paths.get(directory,fileName);
        //TODO Add proper exception handling
        FileOutputStream outputStream = new FileOutputStream(path.toFile());
        workbook.write(outputStream);
        workbook.close();
    }
}
