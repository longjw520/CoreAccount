package com.zendaimoney.coreaccount.service;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.dao.SequenceDao;
import com.zendaimoney.coreaccount.dao.WorkFlowDao;
import com.zendaimoney.coreaccount.email.SimpleMailService;
import com.zendaimoney.coreaccount.entity.LedgerFinance;
import com.zendaimoney.coreaccount.util.PropertiesReader;

@Named
@Transactional
public class MergeFinanceService {

	/*
	 * static ApplicationContext ctx = new
	 * ClassPathXmlApplicationContext("applicationContext.xml",
	 * "applicationContext-redis.xml", "applicationContext-jms.xml",
	 * "email.xml"); static SessionFactory sessionFactory = (SessionFactory)
	 * ctx.getBean("sessionFactory");
	 */

	@Inject
	private SessionFactory sessionFactory;
	@Inject
	private WorkFlowDao workFlowDao;
	@Inject
	private SequenceDao sequenceDao;
	@Inject
	private SimpleMailService simpleMailService;

	private static InetAddress inetAddress;
	static {
		try {
			inetAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private static String ipAddr = inetAddress != null ? inetAddress.getHostAddress() : "Unknown address";

	private Logger logger = Logger.getLogger(getClass());

	@SuppressWarnings("unchecked")
	public void mergerFinance() {

		logger.info("合并存量债权开始!");

		try {
			Query query = sessionFactory.getCurrentSession().createQuery(
					"select ff.ledger.id,ff.ledgerLoan.id  from LedgerFinance ff   where ff.acctStatus in (1,3,4) and ff.frozenPorportion =0  and not exists ( from Transaction t where t.holdId = ff ) group by ff.ledger.id,ff.ledgerLoan.id having count(*) > 1");

			List<Object[]> list = query.list();
			ArrayList<LedgerFinance> ledgerFinances = new ArrayList<LedgerFinance>();
			for (int i = 0; i < list.size(); i++) {
				Object obj[] = (Object[]) list.get(i);
				long ledgerId = (Long) obj[0];
				long loanId = (Long) obj[1];
				Query query1 = sessionFactory.getCurrentSession().createQuery(
						"from LedgerFinance ff where ff.ledger.id =" + ledgerId + "and ff.ledgerLoan.id =" + loanId + "and ff.acctStatus in (1,3,4) and ff.frozenPorportion =0 and not exists ( from Transaction t where t.holdId = ff ) order by id asc");

				ledgerFinances = (ArrayList<LedgerFinance>) query1.list();
				LedgerFinance mergerLedgerFinance = new LedgerFinance();
				boolean isFirst = true;
				for (LedgerFinance ledgerFinance : ledgerFinances) {
					if (isFirst) {
						mergerLedgerFinance = ledgerFinance;
						isFirst = false;
						continue;
					} else {
						String oldDebtAmount = String.valueOf(mergerLedgerFinance.getDebtAmount());
						String oldDebtProportion = String.valueOf(mergerLedgerFinance.getDebtProportion());
						String oldInterestReceivable = String.valueOf(mergerLedgerFinance.getInterestReceivable());
						String oldInterestDeviation = String.valueOf(mergerLedgerFinance.getInterestDeviation());

						String oldSubDebtAmount = String.valueOf(ledgerFinance.getDebtAmount());
						String oldSubDebtProportion = String.valueOf(ledgerFinance.getDebtProportion());
						String oldSubInterestReceivable = String.valueOf(ledgerFinance.getInterestReceivable());
						String oldSubInterestDeviation = String.valueOf(ledgerFinance.getInterestDeviation());
						String oldSubAcctStatus = ledgerFinance.getAcctStatus();

						String oldTradeDebtAmount = String.valueOf(ledgerFinance.getDebtAmount().negate());
						String oldTradeDebtProportion = String.valueOf(ledgerFinance.getDebtProportion().negate());
						String oldTradeInterestReceivable = String.valueOf(ledgerFinance.getInterestReceivable().negate());
						String oldTradeInterestDeviation = String.valueOf(ledgerFinance.getInterestDeviation().negate());

						mergerLedgerFinance.setDebtAmount(mergerLedgerFinance.getDebtAmount().add(ledgerFinance.getDebtAmount()));
						mergerLedgerFinance.setDebtProportion(mergerLedgerFinance.getDebtProportion().add(ledgerFinance.getDebtProportion()));
						mergerLedgerFinance.setInterestReceivable(mergerLedgerFinance.getInterestReceivable().add(ledgerFinance.getInterestReceivable()));
						mergerLedgerFinance.setInterestDeviation(mergerLedgerFinance.getInterestDeviation().add(ledgerFinance.getInterestDeviation()));
						workFlowDao.workFlowSave(9999999999999L, oldSubDebtAmount, mergerLedgerFinance.getId(), oldDebtAmount, null, String.valueOf(mergerLedgerFinance.getDebtAmount()), sequenceDao.nextWorkFlowNo(), "DEBT_AMOUNT", 14L);
						workFlowDao.workFlowSave(9999999999999L, oldSubDebtProportion, mergerLedgerFinance.getId(), oldDebtProportion, null, String.valueOf(mergerLedgerFinance.getDebtProportion()), sequenceDao.nextWorkFlowNo(), "DEBT_PROPORTION", 14L);
						workFlowDao.workFlowSave(9999999999999L, oldSubInterestReceivable, mergerLedgerFinance.getId(), oldInterestReceivable, null, String.valueOf(mergerLedgerFinance.getInterestReceivable()), sequenceDao.nextWorkFlowNo(), "INTEREST_RECEIVABLE", 14L);
						workFlowDao.workFlowSave(9999999999999L, oldSubInterestDeviation, mergerLedgerFinance.getId(), oldInterestDeviation, null, String.valueOf(mergerLedgerFinance.getInterestDeviation()), sequenceDao.nextWorkFlowNo(), "INTEREST_DEVIATION", 14L);

						ledgerFinance.setDebtAmount(BigDecimal.ZERO);
						ledgerFinance.setDebtProportion(BigDecimal.ZERO);
						ledgerFinance.setInterestReceivable(BigDecimal.ZERO);
						ledgerFinance.setInterestDeviation(BigDecimal.ZERO);
						ledgerFinance.setAcctStatus(Constants.ACCOUNT_STATUS_DISABLE);

						workFlowDao.workFlowSave(9999999999999L, null, ledgerFinance.getId(), oldSubAcctStatus, null, Constants.ACCOUNT_STATUS_DISABLE, sequenceDao.nextWorkFlowNo(), Constants.LEDGER_FINANCE, 14L);
						workFlowDao.workFlowSave(9999999999999L, oldTradeDebtAmount, ledgerFinance.getId(), oldSubDebtAmount, null, "0", sequenceDao.nextWorkFlowNo(), "DEBT_AMOUNT", 14L);
						workFlowDao.workFlowSave(9999999999999L, oldTradeDebtProportion, ledgerFinance.getId(), oldSubDebtProportion, null, "0", sequenceDao.nextWorkFlowNo(), "DEBT_PROPORTION", 14L);
						workFlowDao.workFlowSave(9999999999999L, oldTradeInterestReceivable, ledgerFinance.getId(), oldSubInterestReceivable, null, "0", sequenceDao.nextWorkFlowNo(), "INTEREST_RECEIVABLE", 14L);
						workFlowDao.workFlowSave(9999999999999L, oldTradeInterestDeviation, ledgerFinance.getId(), oldSubInterestDeviation, null, "0", sequenceDao.nextWorkFlowNo(), "INTEREST_DEVIATION", 14L);

					}
				}
			}
		} catch (Exception e) {
			String errorInfo = StringUtils.isNotBlank(ExceptionUtils.getStackTrace(e)) ? ExceptionUtils.getStackTrace(e) : "";
			simpleMailService.sendMail(PropertiesReader.readAsString("mergee.finance.error") + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n message from " + ipAddr, null, "合并存量债权失败!" + errorInfo);
			throw new RuntimeException(e.getMessage());
		}
		logger.info("合并存量债权成功!");
		simpleMailService.sendMail(PropertiesReader.readAsString("mergee.finance.ok") + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n message from " + ipAddr, null, "合并存量债权成功!");

	}

	/*
	 * public static void main(String[] args) {
	 * 
	 * ((MergeFinanceService) ctx.getBean("mergeFinance")).mergerFinance(); }
	 */
}
