package com.apm.expenses.controller;

import com.apm.expenses.service.ExpensesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.time.LocalDate;

@Controller
public class ExpensesController {

    @Autowired
    private ExpensesService expensesService;

    @GetMapping("/expenses")
    public @ResponseBody String calculateExpenses(@RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                           @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) throws IOException {
        expensesService.calculateExpenses(from, to);
        return "SUCCESS";
    }
}
