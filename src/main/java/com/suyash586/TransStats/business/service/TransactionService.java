package com.suyash586.TransStats.business.service;

import com.suyash586.TransStats.exception.TransactionExpiredException;
import com.suyash586.TransStats.persistence.entity.Transaction;
import com.suyash586.TransStats.presentation.json.TransactionPostJson;

public interface TransactionService {
	
	public Transaction process(TransactionPostJson json) throws TransactionExpiredException;

}
