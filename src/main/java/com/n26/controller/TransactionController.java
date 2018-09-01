package com.n26.controller;


import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import com.n26.model.Transaction;
import com.n26.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/transactions")
public class TransactionController {

    private TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @RequestMapping(method = POST, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity createTransaction(@RequestBody @Valid Transaction transaction) {
        HttpStatus status;

        if (transactionService.isOutsideStatisticsTimeWindow(transaction)) {
            status = NO_CONTENT;
        } else {
            transactionService.addTransaction(transaction);
            status = CREATED;
        }
        return ResponseEntity.status(status).build();
    }

    @ResponseStatus(NO_CONTENT)
    @RequestMapping(method = DELETE)
    public void deleteAllTransactions() {
        transactionService.deleteAllTransactions();
    }
}
