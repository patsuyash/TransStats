package com.suyash586.TransStats.business.service.impl;

import org.springframework.stereotype.Service;

import com.suyash586.TransStats.business.service.TransactionService;
import com.suyash586.TransStats.business.util.DateUtil;
import com.suyash586.TransStats.exception.TransactionExpiredException;
import com.suyash586.TransStats.persistence.entity.Transaction;
import com.suyash586.TransStats.presentation.json.TransactionPostJson;

@Service
public class TransactionServiceImpl implements TransactionService {

	@Override
	public Transaction process(TransactionPostJson json) throws TransactionExpiredException {

		Transaction transaction = new Transaction();
		transaction.setAmount(json.getAmount());
		transaction.setDate(DateUtil.epochToDate(json.getTimestamp()));
		return transaction;

	}

}
