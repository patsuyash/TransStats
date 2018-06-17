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
import com.suyash586.TransStats.persistence.entity.Statistic;
import com.suyash586.TransStats.persistence.entity.Transaction;

@SpringBootTest
@RunWith(SpringRunner.class)
public class StatisticServiceTest {

	@Autowired
	private StatisticService statisticService;

	@Value("${timeSpanInMs}")
	private Long windowInMs;

	@Test(expected = TransactionExpiredException.class)
	public void addExpired() throws TransactionExpiredException {

		Transaction transaction = new Transaction();
		transaction.setAmount(5.0);
		transaction.setDate(LocalDateTime.now().minusSeconds(windowInMs / 1000).minusSeconds(1l));

		this.statisticService.add(transaction);
	}

	@Test
	public void findCurrentEmpty() {

		Long currentTimestamp = DateUtil.dateToEpochMs(LocalDateTime.now());
		LocalDateTime currentDate = DateUtil.epochToDate(currentTimestamp);

		Statistic statistic = this.statisticService.findCurrent();
		Assert.assertEquals(currentDate, statistic.getDate());
		Assert.assertEquals(Long.valueOf(0), statistic.getCount());
	}

	@Test
	public void findCurrent() throws TransactionExpiredException {

		Transaction tr1 = new Transaction();
		tr1.setAmount(5.0);
		tr1.setDate(LocalDateTime.now());
		this.statisticService.add(tr1);

		Transaction tr2 = new Transaction();
		tr2.setAmount(10.0);
		tr2.setDate(LocalDateTime.now());
		this.statisticService.add(tr2);

		Transaction tr3 = new Transaction();
		tr3.setAmount(20.0);
		tr3.setDate(LocalDateTime.now());
		this.statisticService.add(tr3);

		Transaction tr4 = new Transaction();
		tr4.setAmount(50.0);
		tr4.setDate(LocalDateTime.now());
		this.statisticService.add(tr4);

		Long currentTimestamp = DateUtil.dateToEpochMs(LocalDateTime.now());
		LocalDateTime currentDate = DateUtil.epochToDate(currentTimestamp);

		Statistic statistic = this.statisticService.findCurrent();
		Assert.assertEquals(currentDate, statistic.getDate());
		Assert.assertEquals(Long.valueOf(4), statistic.getCount());
		Assert.assertEquals(Double.valueOf(5.0), statistic.getMin());
		Assert.assertEquals(Double.valueOf(50.0), statistic.getMax());
		Assert.assertEquals(Double.valueOf(85.0), statistic.getSum());
		Assert.assertEquals(Double.valueOf(21.25), statistic.getAvg());
	}

}
