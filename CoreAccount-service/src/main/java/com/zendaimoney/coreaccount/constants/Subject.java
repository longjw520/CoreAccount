package com.zendaimoney.coreaccount.constants;

/** 科目号 */
public class Subject {

	/** 取现手续费收入 */
	public final static String WITHDRAWAL_CHARGE_INCOME = "3017";
	/** 取现手续费支出 */
	public final static String WITHDRAWAL_CHARGE_PAY = "3038";

	/** 应付利息 */
	public final static String LEDGER_DETAIL_TYPE_PAYABLE = "2002";
	/** 误差处理 */
	public final static String LEDGER_DETAIL_TYPE_DEVIATION = "3034";
	/** 预收款项 */
	public final static String LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE = "2008";

	/** 充值手续费支出 */
	public final static String LEDGER_DETAIL_TYPE_RECHARGECOMMISSION_EXPENSENSE = "3039";

	/** 现金 */
	public final static String ACCT_TITLE_CASH = "1001";

	/** 充值手续费收入 */
	public final static String LEDGER_DETAIL_TYPE_RECHARGECOMMISSION_INCOME = "3018";

	/** 债权资产 */
	public final static String LEDGER_DETAIL_TYPE_CREDIT_ASSERT = "1002";

	/** 借款金额 */
	public final static String LEDGER_DETAIL_TYPE_DEBT_BALANCE = "2007";

	/** 应付逾期利息 */
	public final static String OVERDUEINTEREST_PAYABLE = "2006";

	/** 应收逾期利息 */
	public final static String OVERDUEINTEREST_RECEIVABLE = "1007";

	/** 应收利息 */
	public final static String OVERDUEINTEREST_INTERESTRECEIVABLE = "1005";

	/** 利息收入 */
	public final static String INTEREST_INCOME = "3001";
	/** 管理费支出 */
	public final static String MANAGEMENT_FEE_EXPENSENSE = "3023";

	/** 管理费收入 */
	public final static String MANAGEMENT_FEE_INCOME = "3005";

	/** 利息支出 */
	public final static String INTEREST_PAY_OUT = "3036";

	/** 转让服务费支出 */
	public final static String TRANSFERABLE_SERVICE_CHARGE_FEE_EXPENSENSE = "3025";

	/** 转让服务费收入 */
	public final static String TRANSFERABLE_SERVICE_CHARGE_FEE_INCOME = "3007";

	/** 紧急转让服务费支出 */
	public final static String URGE_TRANSFERABLE_SERVICE_CHARGE_FEE_EXPENSENSE = "3030";

	/** 紧急转让服务费收入 */
	public final static String URGE_TRANSFERABLE_SERVICE_CHARGE_FEE_INCOME = "3008";

	/** 冻结类现金 */
	public final static String FROZEN_CASH = "1010";

	/** 投资金额 */
	public final static String INVESTMENT_AMOUNT = "1011";
}
