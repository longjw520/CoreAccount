package com.zendaimoney.coreaccount.service;

import com.zendaimoney.coreaccount.dao.DebtInfoDao;
import com.zendaimoney.coreaccount.dao.LedgerFinanceDao;
import com.zendaimoney.coreaccount.dao.RepaymentPlanDao;
import com.zendaimoney.coreaccount.entity.Debt;
import com.zendaimoney.coreaccount.entity.LedgerFinance;
import com.zendaimoney.coreaccount.entity.LedgerLoan;
import com.zendaimoney.coreaccount.entity.RepaymentPlan;
import com.zendaimoney.coreaccount.rmi.vo.QueryAccountsReceivableAndPayableVo;
import com.zendaimoney.coreaccount.rmi.vo.ReturnValueVo;
import com.zendaimoney.coreaccount.util.Arith;
import org.springside.modules.orm.Page;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.List;

import static com.zendaimoney.coreaccount.constants.Constants.ACCOUNT_STATUS_OVERDUE;
import static com.zendaimoney.coreaccount.constants.Constants.ACCOUNT_STATUS_REGULAR;

/**
 * 查询回款金额和资产价值
 * 
 * @author larry
 * 
 */
@Named
public class QueryReturnService {
	@Inject
	private LedgerFinanceDao ledgerFinanceDao;
	@Inject
	private RepaymentPlanDao repaymentPlanDao;
	@Inject
	private DebtInfoDao debtInfoDao;
	@Inject
	private LedgerLoanService ledgerLoanService;

	public ReturnValueVo getReturnValue(String account, String repayDay) {
		List<LedgerFinance> ledgerFinances = ledgerFinanceDao.queryBy(new String[] { ACCOUNT_STATUS_REGULAR, ACCOUNT_STATUS_OVERDUE }, account);
		ReturnValueVo returnValueVo = new ReturnValueVo();
		if(ledgerFinances.size() == 0){
			return returnValueVo;
		}
		BigDecimal repayAmount, assetValue, totalProportion, payAmt;
		repayAmount = assetValue = totalProportion = payAmt = BigDecimal.ZERO;
		RepaymentPlan repaymentPlan;
		for (final LedgerFinance finance : ledgerFinances) {
			LedgerLoan loan = finance.getLedgerLoan();
			repaymentPlan = repaymentPlanDao.queryByLedgerLoanAndRepayDay(loan.getId(), repayDay);
			if (repaymentPlan != null) {
//				totalProportion = finance.getDebtProportion().add(finance.getFrozenPorportion());
				totalProportion = finance.getDebtProportion();
				repayAmount = repayAmount.add(totalProportion.multiply(repaymentPlan.getAmt()));// 回款金额
				BigDecimal pv = ledgerLoanService.calculatePV(Boolean.FALSE, loan.getRate(), loan.getId(), repayDay);
				assetValue = assetValue.add(totalProportion.multiply(pv));
			}
		}
		QueryAccountsReceivableAndPayableVo queryVo = new QueryAccountsReceivableAndPayableVo();
		queryVo.setAccount(account);
		queryVo.setStatus("1");
		queryVo.setPageSize(Integer.MAX_VALUE);
		Page<Debt> debts = debtInfoDao.queryAccountsReceivableAndPayablePage(queryVo);
		for (Debt debt : debts) {
			payAmt = Arith.add(payAmt, debt.getAmount());
		}
		returnValueVo.setAssetValue(assetValue.doubleValue());
		returnValueVo.setRepayAmount(repayAmount.doubleValue());
		returnValueVo.setPayAmt(payAmt.doubleValue());
		return returnValueVo;
	}
}
