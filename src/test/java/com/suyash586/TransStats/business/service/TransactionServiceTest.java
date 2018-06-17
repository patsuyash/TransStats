package com.suyash586.TransStats.business.service;

import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.suyash586.TransStats.business.util.DateUtil;
import com.suyash586.TransStats.exception.TransactionExpiredException;
import com.suyash586.TransStats.persistence.entity.Transaction;
import com.suyash586.TransStats.presentation.json.TransactionPostJson;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TransactionServiceTest {

	@Autowired
	private TransactionService transactionService;

	@Value("${timeSpanInMs}")
	private Long windowInMs;

	@Test(expected = TransactionExpiredException.class)
	public void processExpired() throws TransactionExpiredException {

		TransactionPostJson json = new TransactionPostJson();
		json.setAmount(6.0);
		json.setTimestamp(DateUtil.dateToEpochMs(LocalDateTime.now()) - windowInMs - 1000);

		this.transactionService.process(json);
	}

	@Test
	public void processOk() throws TransactionExpiredException {

		TransactionPostJson json = new TransactionPostJson();
		json.setAmount(6.0);
		json.setTimestamp(DateUtil.dateToEpochMs(LocalDateTime.now()) - windowInMs);

		Transaction transaction = this.transactionService.process(json);

		Assert.assertEquals(json.getAmount(), transaction.getAmount());
		Assert.assertEquals(DateUtil.epochToDate(json.getTimestamp()), transaction.getDate());
	}

}
