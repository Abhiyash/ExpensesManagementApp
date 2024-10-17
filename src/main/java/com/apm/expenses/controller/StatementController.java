package com.apm.expenses.controller;

import com.apm.expenses.service.StatementService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
public class StatementController {

    @Autowired
    StatementService statementService;

    @PostMapping("/bankstatement")
    public @ResponseBody String updatebankstatement(@RequestParam String userId) throws IOException {
        statementService.updateStatement(userId);
        return "SUCCESS";
    }

    @GetMapping("/bankstatement")
    public @ResponseBody String getbankstatement(@RequestParam String userId) throws IOException {
        statementService.getStatement(userId);
        return "SUCCESS";
    }
}
