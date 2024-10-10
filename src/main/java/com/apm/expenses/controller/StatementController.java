package com.apm.expenses.controller;

import com.apm.expenses.service.StatementService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class StatementController {

    @Autowired
    StatementService statementService;

    @PostMapping("/bankstatement")
    public @ResponseBody String bankstatement(@RequestParam String userId) throws IOException {
        statementService.updateStatement(userId);
        return "SUCCESS";
    }
}
