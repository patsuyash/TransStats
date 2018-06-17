package com.suyash586.TransStats.business.service;

import com.suyash586.TransStats.exception.TransactionExpiredException;
import com.suyash586.TransStats.persistence.entity.Statistic;
import com.suyash586.TransStats.persistence.entity.Transaction;

public interface StatisticService {

	public Statistic findCurrent();

	public void add(Transaction transaction) throws TransactionExpiredException;

}
