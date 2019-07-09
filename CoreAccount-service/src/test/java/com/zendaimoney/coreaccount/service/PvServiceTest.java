package com.zendaimoney.coreaccount.service;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.dao.LedgerLoanDao;
import com.zendaimoney.coreaccount.data.redis.BasicRedisOpts;
import com.zendaimoney.coreaccount.rmi.vo.QueryLedgerLoanVo;
import com.zendaimoney.coreaccount.util.ThreadPoolUtil;
import com.zendaimoney.coreaccount.util.ThreadPoolUtil.ThreadPool;
import com.zendaimoney.exception.BusinessException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.math.BigDecimal;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class PvServiceTest {

	@InjectMocks
	PvService pvService;
	@Mock
	BasicRedisOpts basicRedisOpts;
	@Mock
	LedgerLoanDao ledgerLoanDao;
	@Mock
	RepaymentPlanService repaymentPlanService;
	@Mock
	ThreadPoolTaskExecutor taskExecutor;

	@Test
	public void redisPVtest() {
		Boolean deducted = false;
		long loanId = 123L;
		Date pvDate = new Date();
		Format format = new SimpleDateFormat("yyyy-MM-dd");
		String newDate = format.format(pvDate);
		BigDecimal pvValue = new BigDecimal("123435.435");
		PowerMockito.when(basicRedisOpts.getSingleResult(loanId + ":" + newDate)).thenReturn("123435.435:55841.324235");
		BigDecimal pv = pvService.redisPV(deducted, loanId, newDate);
		Assert.assertEquals(pvValue, pv);

	}

	@Test
	public void redisPV2test() {
		Boolean deducted = true;
		long loanId = 123L;
		Date pvDate = new Date();
		Format format = new SimpleDateFormat("yyyy-MM-dd");
		String newDate = format.format(pvDate);
		BigDecimal pvValue = new BigDecimal("55841.324235");
		PowerMockito.when(basicRedisOpts.getSingleResult(loanId + ":" + newDate)).thenReturn("123435.435:55841.324235");
		BigDecimal pv = pvService.redisPV(deducted, loanId, newDate);
		Assert.assertEquals(pvValue, pv);

	}

	@Test
	public void redisPV3test() {

		Boolean deducted = false;
		long loanId = 123L;
		Date pvDate = new Date();
		Format format = new SimpleDateFormat("yyyy-MM-dd");
		String newDate = format.format(pvDate);
		PowerMockito.when(basicRedisOpts.getSingleResult(loanId + ":" + newDate)).thenReturn(null);
		BigDecimal pv = pvService.redisPV(deducted, loanId, newDate);
		Assert.assertEquals(pv, null);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void redisPVErrortest() throws Exception {

		Boolean deducted = false;
		long loanId = 123L;
		Date pvDate = new Date();
		Format format = new SimpleDateFormat("yyyy-MM-dd");
		String newDate = format.format(pvDate);
		when(basicRedisOpts.getSingleResult(loanId + ":" + newDate)).thenThrow(BusinessException.class);
		BigDecimal pv = pvService.redisPV(deducted, loanId, newDate);
		Assert.assertEquals(pv, null);

	}

    @Ignore
	public void getAllLoansPVtest() throws InterruptedException {
		QueryLedgerLoanVo queryLedgerLoanVo = new QueryLedgerLoanVo();
		queryLedgerLoanVo.setId(100L);
		queryLedgerLoanVo.setAcctStatusArray(new String[] { "123", "234" });
		Date pvDate = new Date();
		long pvTime = pvDate.getTime();
		Format format = new SimpleDateFormat("yyyy-MM-dd");
		String newDate = format.format(pvDate);
		queryLedgerLoanVo.setMaxImportDate(newDate);
		Date dateMock = PowerMockito.mock(Date.class);
		try {
			PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(dateMock);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PowerMockito.when(dateMock.getTime()).thenReturn(pvTime);
		PowerMockito.mockStatic(org.apache.commons.lang3.time.DateFormatUtils.class);
		PowerMockito.when(org.apache.commons.lang3.time.DateFormatUtils.format(new Date(), Constants.DATE_FORMAT)).thenReturn(newDate);
//		PowerMockito.when(ledgerLoanDao.getPVKeys(queryLedgerLoanVo)).thenReturn(new ArrayList<Long>(Arrays.asList(123L, 234L)));
		PowerMockito.when(basicRedisOpts.getSingleResult(123L + ":" + newDate)).thenReturn("12313:3333");
		PowerMockito.when(basicRedisOpts.getSingleResult(234L + ":" + newDate)).thenReturn("12313:55555.33");
		BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 6, 1, TimeUnit.DAYS, queue);
		PowerMockito.when(taskExecutor.getThreadPoolExecutor()).thenReturn(executor);
		ThreadPool<Object> threadPool = new ThreadPool<Object>(executor);
		PowerMockito.mockStatic(ThreadPoolUtil.class);
		PowerMockito.when(ThreadPoolUtil.newCompletionService(executor)).thenReturn(threadPool);
		try {
			PowerMockito.when(ThreadPoolUtil.getResult(threadPool)).thenReturn(new BigDecimal("123"));
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		BigDecimal pv = pvService.getAllLoansPV(queryLedgerLoanVo);
		Assert.assertEquals(pv, new BigDecimal("246"));

	}
    @Ignore
	@SuppressWarnings("unchecked")
	public void getAllLoansPVErrortest() throws InterruptedException {
		QueryLedgerLoanVo queryLedgerLoanVo = new QueryLedgerLoanVo();
		queryLedgerLoanVo.setId(100L);
		queryLedgerLoanVo.setAcctStatusArray(new String[] { "123", "234" });
		Date pvDate = new Date();
		long pvTime = pvDate.getTime();
		Format format = new SimpleDateFormat("yyyy-MM-dd");
		String newDate = format.format(pvDate);
		queryLedgerLoanVo.setMaxImportDate(newDate);
		Date dateMock = PowerMockito.mock(Date.class);
		try {
			PowerMockito.whenNew(Date.class).withNoArguments().thenReturn(dateMock);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		PowerMockito.when(dateMock.getTime()).thenReturn(pvTime);
		PowerMockito.mockStatic(org.apache.commons.lang3.time.DateFormatUtils.class);
		PowerMockito.when(org.apache.commons.lang3.time.DateFormatUtils.format(new Date(), Constants.DATE_FORMAT)).thenReturn(newDate);
//		PowerMockito.when(ledgerLoanDao.getPVKeys(queryLedgerLoanVo)).thenReturn(new ArrayList<Long>(Arrays.asList(123L, 234L)));
		PowerMockito.when(basicRedisOpts.getSingleResult(123L + ":" + newDate)).thenReturn("12313:3333");
		PowerMockito.when(basicRedisOpts.getSingleResult(234L + ":" + newDate)).thenReturn("12313:55555.33");
		BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 6, 1, TimeUnit.DAYS, queue);
		PowerMockito.when(taskExecutor.getThreadPoolExecutor()).thenReturn(executor);
		ThreadPool<Object> threadPool = new ThreadPool<Object>(executor);
		PowerMockito.mockStatic(ThreadPoolUtil.class);
		PowerMockito.when(ThreadPoolUtil.newCompletionService(executor)).thenReturn(threadPool);
		try {
			PowerMockito.when(ThreadPoolUtil.getResult(threadPool)).thenThrow(Exception.class);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		BigDecimal pv = pvService.getAllLoansPV(queryLedgerLoanVo);
		Assert.assertEquals(BigDecimal.ZERO, pv);

	}

}
