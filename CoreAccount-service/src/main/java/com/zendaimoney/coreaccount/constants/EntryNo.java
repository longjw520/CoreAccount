package com.zendaimoney.coreaccount.constants;

/** 流水分录号 ： 报文操作码 + 2位分录顺序码 */
public class EntryNo {
	/** 充值-充值(02000101) */
	public final static String RECHARGE_AMOUNT = "02000101";
	/** 充值-手续费(02000102) */
	public final static String RECHARGE_COMMISSION = "02000102";

	/** 取现-取现(02000501) */
	public final static String WITHDRAWAL = "02000501";
	/** 取现-手续费(02000502) */
	public final static String WITHDRAWAL_COMMISSION = "02000502";
	/** 债权转让-解冻(03000301) */
	public final static String DEBTASSIGNMENT_UNFREEZE = "03000301";
	/** 债权转让-本金(03000302) */
	public final static String DEBTASSIGNMENT_DEBT_AMOUNT = "03000302";
	/** 债权转让-逾期付款利息 (03000303) */
	public final static String DEBTASSIGNMENT_OVERDUE_INTEREST = "03000303";
	/** 债权转让-应收利息 (03000304) */
	public final static String DEBTASSIGNMENT_INTEREST_RECEIVABLE = "03000304";
	/** 债权转让-利息(03000305) */
	public final static String DEBTASSIGNMENT_INTEREST = "03000305";
	/** 债权转让-管理费(03000306) */
	public final static String DEBTASSIGNMENT_MANAGEMENT_FEE = "03000306";
	/** 债权转让-固定产品转让服务费(03000307) */
	public final static String DEBTASSIGNMENT_FIXPRODUCTTRADESERVICE_FEE = "03000307";
	/** 债权转让-紧急转让服务费 (03000308) */
	public final static String DEBTASSIGNMENT_URGENT_SERVICE_FEE = "03000308";
	/** 债权转让-冻结(03000309) */
	public final static String DEBTASSIGNMENT_FROZEN = "03000309";
	/** 原始债权转让-解冻(03000501) */
	public final static String ORIGINALLOANTRADE_UNFREEZE = "03000501";
	/** 原始债权转让-交易(03000502) */
	public final static String ORIGINALLOANTRADE_TRADE = "03000502";
	/** 计息（每天批处理）(00000301) */
	public final static String CALCULATE_ACCRUAL = "00000301";
	/** 收取管理费(02004401) */
	public final static String CHARGE_MANAGEMENT_COST = "02004401";
	/** 回款结息(02004601) */
	public final static String REPAYMENT_INTEREST = "02004601";
	/** 还本金(02004602) */
	public final static String PAY_PRINCIPAL = "02004602";
	/** 还利息 (02004603) */
	public final static String PAY_INTEREST = "02004603";
	/** 息转本(02004604) */
	public final static String INTEREST_SWITCH_PRINCIPAL = "02004604";

	/** 逾期付款利息（02004501） */
	public final static String LATE_PAYMENT_INTEREST = "02004501";

	/** 放款（02004201） */
	public final static String MAKE_LOAN = "02004201";

	/** 分账转账-取现（02004301） */
	public final static String TRANSFERACCOUNT_ENCHASHMENT = "02004301";
	/** 分账转账-充值（02004302） */
	public final static String TRANSFERACCOUNT_RECHARGE = "02004302";

	/* 冻结、解冻现金-冻结（02000201） */
	public final static String FROZEN_AMOUNT = "02000201";
	/* 冻结、解冻现金-解冻（02000202） */
	public final static String UNFREEZE_AMOUNT = "02000202";

	// 初始化(03000701)
	public final static String LOAN_INITIALIZATION = "03000701";

	/**回归债权交易(03000801) -解冻*/
	public final static String REGRESSLOANTRADE_UNFREEZE = "03000801";
	/**回归债权交易(03000802) -本金*/
	public final static String REGRESSLOANTRADE_DEBT_AMOUNT = "03000802";
	/**回归债权交易（03000803） -利息*/
	public final static String REGRESSLOANTRADE_INTEREST = "03000803";
	/**回归债权交易（03000804） -现金*/
	public final static String REGRESSLOANTRADE_CASH = "03000804";
	
	/**提前结清（02004701） -充值*/
	public final static String EARLYSETTLE_CASH = "02004701";
	/**提前结清（02004702） -还款*/
	public static final String EARLYSETTLE_REPAYMENT = "02004702";
	/**提前结清（02004703） -逾期应收应付*/
	public static final String EARLYSETTLE_LATE_PAYMENT_INTEREST = "02004703";
}
