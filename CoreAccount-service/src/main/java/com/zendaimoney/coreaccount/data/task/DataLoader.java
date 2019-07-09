package com.zendaimoney.coreaccount.data.task;

import com.zendaimoney.coreaccount.data.redis.BasicRedisOpts;
import com.zendaimoney.coreaccount.email.SimpleMailService;
import com.zendaimoney.coreaccount.service.LedgerLoanService;
import com.zendaimoney.coreaccount.service.RepaymentPlanService;
import com.zendaimoney.coreaccount.util.Arith;
import com.zendaimoney.coreaccount.util.NPresentValueUtil;
import com.zendaimoney.coreaccount.util.PropertiesReader;
import com.zendaimoney.coreaccount.util.SystemUtil;
import com.zendaimoney.exception.BusinessException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.zendaimoney.coreaccount.constants.Constants.*;
import static com.zendaimoney.coreaccount.util.DateUtils.getDayCount;
import static java.math.BigDecimal.ZERO;
import static org.apache.commons.lang3.time.DateFormatUtils.format;

/**
 * 缓存同步redis和本地数据库数据
 * 
 * @author binliu
 * 
 */
@Named
public class DataLoader {

	private static Logger logger = Logger.getLogger(DataLoader.class);
	@Inject
	private LedgerLoanService ledgerLoanService;
	@Inject
	private BasicRedisOpts basicRedisOpts;
	@Inject
	private RepaymentPlanService repaymentPlanService;
	@Inject
	private SimpleMailService simpleMailService;
	private AtomicInteger failedTask;// 失败的任务数量
	private CountDownLatch cdl;
	private List<Object[]> errInfos;// 保存失败的任务的loanId和appDate
	@Value("${max.thread.count}")
	private int nThreads;
	private int cpu = Runtime.getRuntime().availableProcessors();// cpu个数
	private double blockCoefficient = 0.75f;// 阻塞系数
	private int recommended = (int) (cpu / (1 - blockCoefficient));// 推荐的线程池大小

	public void load() {
		logger.info("startup task of write data to redis.");
		Object[] ledgerLoans = ledgerLoanService.getAllLoanIdByStatus(ACCOUNT_STATUS_REGULAR, ACCOUNT_STATUS_OVERDUE, ACCOUNT_STATUS_IDLE);
//		ExecutorService es = new ThreadPoolExecutor(nThreads, Math.max(recommended, nThreads), 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		ExecutorService es = new ThreadPoolExecutor(nThreads, 8, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		int loops = ledgerLoans.length >> 1;
		logger.info("缓存PV loops=" + loops);
		failedTask = new AtomicInteger();
		cdl = new CountDownLatch(loops);
		errInfos = Collections.synchronizedList(new ArrayList<Object[]>());
		for (int j = 0, idx = 0; j < loops; ++j, idx++) {
			es.execute(new CalcPVJob((Long) ledgerLoans[idx], (BigDecimal) ledgerLoans[++idx]));
		}
		es.shutdown();
		try {
			logger.info("缓存PV 线程执行中...");
			cdl.await();
			logger.info("缓存PV 线程执行完成...");
			report();
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
			Thread.currentThread().interrupt();
		} finally {
			ledgerLoans = null;// let vm do gc
			logger.info(" task of load data is over");

		}
	}

	/**
	 * 组合key和value作为redis的一个条目
	 * 
	 * @param rate
	 * @param id
	 * @param date
	 * @return
	 * @throws Exception
	 */
	private String[] combineKV(BigDecimal rate, Long id, Calendar date) throws Exception {
		BigDecimal[] cashFlows = repaymentPlanService.getCashFLow(id, date.getTime());
		BigDecimal pv1 = NPresentValueUtil.pV(rate, cashFlows);
		BigDecimal pv2 = Arith.sub(pv1, cashFlows[0]);
		if (pv2.compareTo(ZERO) != 1) {
			pv2 = ZERO;
		}
		cashFlows = null;// let vm do gc
		return new String[] { id + ":" + format(date, DATE_FORMAT), pv1.toPlainString() + ":" + pv2.toPlainString() };
	}

	/**
	 * 计算PV
	 * 
	 */
	private class CalcPVJob implements Runnable {
		private Long loanId;
		private BigDecimal rate;

		CalcPVJob(Long loanId, BigDecimal rate) {
			this.loanId = loanId;
			this.rate = rate;
		}

		@Override
		public void run() {
			Calendar now = Calendar.getInstance();
			Calendar ago1Mon = (Calendar) now.clone();
			ago1Mon.add(Calendar.MONTH, -1);
			Calendar aft1Mon = (Calendar) now.clone();
			aft1Mon.add(Calendar.MONTH, 1);
			int period = getDayCount(ago1Mon, aft1Mon);
			String date = format(now, DATE_FORMAT);
			Date reset = ((Calendar) ago1Mon.clone()).getTime();
			String[] entry = { "null:null" };
			Calendar liveTime;
			try {
				if (basicRedisOpts.exists(loanId + ":" + date)) {
					entry = combineKV(rate, loanId, aft1Mon);
					liveTime = (Calendar) aft1Mon.clone();
					liveTime.add(Calendar.MONTH, 1);
					liveTime.add(Calendar.DAY_OF_MONTH, 1);
					basicRedisOpts.persist(entry[0], entry[1], liveTime.getTime());
				} else {
					for (int i = 0; i <= period; i++) {
						entry = combineKV(rate, loanId, ago1Mon);
						liveTime = (Calendar) ago1Mon.clone();
						liveTime.add(Calendar.MONTH, 1);
						liveTime.add(Calendar.DAY_OF_MONTH, 1);
						basicRedisOpts.persist(entry[0], entry[1], liveTime.getTime());
						ago1Mon.add(Calendar.DAY_OF_MONTH, 1);
					}
					ago1Mon.setTime(reset);
				}
			} catch (Exception e) {
				failedTask.incrementAndGet();
				Object[] errs = { loanId, entry[0].split(":")[1] };
				errInfos.add(errs);
				throw new BusinessException(e);
			} finally {
				long count = cdl.getCount();
				if(count % 1000 == 0){
					logger.info("缓存PV次数：" + count);
				}
				cdl.countDown();
			}
		}
	}

	/**
	 * 邮件通知任务完成情况
	 */
	private void report() {
		String subject = PropertiesReader.readAsString("mail.data.loader.title");
		subject = MessageFormat.format(subject, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), SystemUtil.getIpAddr());
		if (failedTask.intValue() > 0) {
			StringBuilder content = new StringBuilder("详细信息如下：\n");
			for (Object[] err : errInfos) {
				content.append("loanId: ").append(err[0]).append("\t\t\tappDate: ").append(err[1]).append("\n");
			}
			logger.info(content.toString());
			simpleMailService.sendMail(subject, null, content.toString() + "失败" + failedTask.intValue() + "笔");
			return;
		}
		simpleMailService.sendMail(subject, null, "向redis缓存PV成功！");
	}
}
