package com.apm.expenses.controller;

import com.apm.expenses.dto.FileData;
import com.apm.expenses.service.StatementService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static com.apm.expenses.constant.Constants.APP_FILES_PATH;
import static com.apm.expenses.constant.Constants.INPUT_FILE;

@Controller
public class StatementController {

    @Autowired
    StatementService statementService;

    @PostMapping("/bankstatement")
    public @ResponseBody String updatebankstatement(@RequestParam String userId, @RequestParam String bankAccountNumber
                                                    ) throws IOException {
        System.out.println("userId = " + userId);
        statementService.updateStatement(userId,bankAccountNumber);
        //TODO What will be the values when from and to are not passed.


        return "SUCCESS";
    }

    @GetMapping("/bankstatement")
    public @ResponseBody String getbankstatement(@RequestParam String userId,
                                                 @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                 @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) throws IOException {
        statementService.getAndExportStatement(userId, from, to);
        return "SUCCESS";
    }

    @PostMapping("/insertConfigs")
    public @ResponseBody String insertConfigs(){
        return statementService.insertConfigs();
    }

    @PostMapping("/bankstatement/files")
    public @ResponseBody String readStatementsFromFile(@RequestParam String userId,
                                                       @RequestPart("data") String relatedData,
                                                       @RequestPart("files") List<MultipartFile> files) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        //List<FileData> fileData = objectMapper.readValue(relatedData, new TypeReference<List<FileData>>() {});
        //System.out.println(fileData);
        System.out.println(relatedData);
        for(MultipartFile file : files) {
            System.out.println(file.getOriginalFilename());
        }
        return "SUCCESS";
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String UPLOAD_DIR = APP_FILES_PATH + "/" + INPUT_FILE;
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please select a file to upload.");
        }

        try {
            // Ensure the upload directory exists
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Save the file locally
            Path filePath = uploadPath.resolve(file.getOriginalFilename());
            file.transferTo(filePath.toFile());

            // You can add additional logic here: e.g., trigger expense calculation, logging, database update

            return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed: " + e.getMessage());
        }
    }
}
