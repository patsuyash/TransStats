package com.suyash586.TransStats.presentation.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suyash586.TransStats.business.service.TransactionService;
import com.suyash586.TransStats.exception.TransactionExpiredException;
import com.suyash586.TransStats.persistence.entity.Transaction;
import com.suyash586.TransStats.presentation.json.TransactionPostJson;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
	
	@Autowired
	private TransactionService transactionService;
	
	@PostMapping
	public ResponseEntity<Transaction> post(@RequestBody @Valid TransactionPostJson bodyJson) {

		try {
			Transaction transaction = this.transactionService.process(bodyJson);
			
			return new ResponseEntity<Transaction>(transaction, HttpStatus.CREATED); 
		} 
		catch (TransactionExpiredException e) {
			return new ResponseEntity<Transaction>(HttpStatus.NO_CONTENT);
		}
	}

}
