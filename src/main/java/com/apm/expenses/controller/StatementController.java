package com.apm.expenses.controller;

import com.apm.expenses.service.StatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;

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
}
