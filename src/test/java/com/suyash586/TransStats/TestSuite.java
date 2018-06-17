package com.suyash586.TransStats;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.suyash586.TransStats.business.service.StatisticServiceTest;
import com.suyash586.TransStats.business.service.TransactionServiceTest;
import com.suyash586.TransStats.business.util.DateUtilTest;
import com.suyash586.TransStats.presentation.controller.StatisticControllerTest;
import com.suyash586.TransStats.presentation.controller.TransactionControllerTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ DateUtilTest.class, StatisticServiceTest.class, TransactionServiceTest.class, TransactionControllerTest.class, StatisticControllerTest.class })
public class TestSuite {

}
