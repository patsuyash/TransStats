package com.suyash586.TransStats.business.service.impl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.suyash586.TransStats.business.service.StatisticService;
import com.suyash586.TransStats.business.util.DateUtil;
import com.suyash586.TransStats.exception.TransactionExpiredException;
import com.suyash586.TransStats.persistence.entity.Statistic;
import com.suyash586.TransStats.persistence.entity.Transaction;

/**
 * 
 * Statistics Service
 * 
 * @author Suyash Patil
 *
 */
@Service
@Aspect
public class StatisticServiceImpl implements StatisticService {

	// Lock for ADD / REMOVE
	private Object LCK = new Object();

	// Space Complexity: O(${timeSpanInMs} / 1000 + ${removeExpiredStatisticsInMs} /
	// 1000) -> O(1)
	private Map<Long, Statistic> statisticHistory;

	// Space Complexity: O(${timeSpanInMs} / 1000 + ${removeExpiredStatisticsInMs} /
	// 1000) -> O(1)
	private Queue<Long> statisticTimestamps;

	@Value("${timeSpanInMs}")
	private Long timeSpanInMs;

	public StatisticServiceImpl() {
		this.statisticHistory = new ConcurrentHashMap<Long, Statistic>();
		this.statisticTimestamps = new PriorityBlockingQueue<Long>();
	}

	/**
	 * 
	 * Create init statistic
	 * 
	 * Time complexity O(1)
	 * 
	 * @return Statistic with init values
	 */
	private Statistic createInitStatistic(Long timestamp) {
		Statistic statistic = new Statistic();
		statistic.setDate(DateUtil.epochToDate(timestamp));
		statistic.setMax(Double.MIN_VALUE);
		statistic.setMin(Double.MAX_VALUE);
		statistic.setSum(0.0);
		statistic.setCount(0l);
		return statistic;
	}

	/**
	 * 
	 * Remove expired statistics
	 * 
	 * Time complexity: O(statisticTimestamps.size() * log
	 * statisticTimestamps.size()) -> O(1)
	 * 
	 */
	@Scheduled(fixedDelayString = "${clearTimeSpanInMs}")
	private void removeExpiredStatistics() {

		Long currentTimestamp = DateUtil.dateToEpochMs(LocalDateTime.now());

		if (this.statisticTimestamps.isEmpty() || this.statisticTimestamps.peek() >= currentTimestamp)
			return;

		synchronized (LCK) {

			// O(n) - Where n = statisticTimestamps.size()
			while (!this.statisticTimestamps.isEmpty() && this.statisticTimestamps.peek() < currentTimestamp) {

				// O(log n) - Where n = statisticTimestamps.size()
				Long key = this.statisticTimestamps.poll();

				// O(1)
				this.statisticHistory.remove(key);
			}
		}
	}

	/**
	 * 
	 * Add statistic from transaction
	 * 
	 * Time complexity: O(${timeSpanInMs}/1000 * log statisticTimestamps.size()) ->
	 * O(1)
	 * 
	 * @throws TransactionOutOfFutureWindow
	 * 
	 */
	@Override
	@AfterReturning(pointcut = "execution(* com.suyash586.TransStats.business.service.TransactionService.process(..))", returning = "transaction")
	public void add(Transaction transaction) throws TransactionExpiredException {

		Long currentTimestamp = DateUtil.dateToEpochMs(LocalDateTime.now());
		Long transactionTimestamp = DateUtil.dateToEpochMs(transaction.getDate());

		if ((transactionTimestamp + timeSpanInMs) < currentTimestamp)
			throw new TransactionExpiredException();
		synchronized (LCK) {

			// O(n) - Where n = ${timeSpanInMs}/1000
			for (Long i = currentTimestamp; i < transactionTimestamp + timeSpanInMs; i += 1000) {

				// O(1)
				Statistic statistic = this.statisticHistory.get(i);

				if (statistic == null) {

					// O(1)
					statistic = this.createInitStatistic(i);

					// O(1)
					this.statisticHistory.put(i, statistic);

					// O(log n) - Where n = statisticTimestamps.size()
					this.statisticTimestamps.add(i);
				}

				if (transaction.getAmount() > statistic.getMax())
					statistic.setMax(transaction.getAmount());
				if (transaction.getAmount() < statistic.getMin())
					statistic.setMin(transaction.getAmount());

				statistic.setSum(statistic.getSum() + transaction.getAmount());
				statistic.setCount(statistic.getCount() + 1);
				statistic.setAvg(statistic.getSum() / statistic.getCount());
			}

		}
	}

	/**
	 * 
	 * Get current statistic
	 * 
	 * Time complexity: O(1)
	 * 
	 */
	@Override
	public Statistic findCurrent() {

		Long currentTimestamp = DateUtil.dateToEpochMs(LocalDateTime.now());

		// O(1)
		Statistic statistic = this.statisticHistory.get(currentTimestamp);

		if (statistic == null)
			statistic = this.createInitStatistic(currentTimestamp);

		return statistic;
	}

}