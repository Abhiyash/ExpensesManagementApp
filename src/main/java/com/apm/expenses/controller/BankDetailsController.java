package com.apm.expenses.controller;

import com.apm.expenses.dto.BankDetailsRequest;
import com.apm.expenses.model.BankDetails;
import com.apm.expenses.service.BankDetailsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bankDetails")
public class BankDetailsController {
 
    @Autowired
    private BankDetailsService bankDetailsService;

    @PostMapping
    public ResponseEntity<BankDetails> addBankDetails(@Valid @RequestBody BankDetailsRequest bankDetailsRequest){
        return ResponseEntity.ok(bankDetailsService.addBankDetails(bankDetailsRequest));
    }

    @GetMapping
    public ResponseEntity<BankDetails> getBankDetailsByUserId(@RequestBody String userId){
        return ResponseEntity.ok(bankDetailsService.getBankDetailsByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankDetails> getBankDetailsById(@RequestBody String id){
        BankDetails bankDetails = bankDetailsService.getBankDetailsByUserId(id);
        return bankDetails != null ? ResponseEntity.ok(bankDetails) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<BankDetails> updateBankDetails(@PathVariable String id, @Valid @RequestBody BankDetailsRequest bankDetailsRequest){
        BankDetails updatedBankDetails = bankDetailsService.updateBankDetails(id, bankDetailsRequest);
        return updatedBankDetails != null ? ResponseEntity.ok(updatedBankDetails) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBankDetails(@PathVariable String id){
        int deletedCount = bankDetailsService.deleteBankDetails(id);
        if (deletedCount > 0) {
            return ResponseEntity.ok("Bank details deleted successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
