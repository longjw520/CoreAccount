package com.zendaimoney.coreaccount.constants;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class Constants {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final int SEQ_INCREMENT = 2000;
    /**
     * 公司现金账户分账号
     */
    public static final String COMPANY_CASH_LEDGER_ACCOUNT = "10000000000000010001";
    /**
     * 公司账户分账号
     */
    public static final String COMPANY_LEDGER_ACCOUNT = "10000000000000010002";
    // --start请求状态/
    /**
     * 操作码(成功)
     */
    public final static String PROCESS_STATUS_OK = "000000";

    /**
     * 操作码(失败)
     */
    public final static String PROCESS_STATUS_FAIL = "000001";
    // --end/

    /**
     * BigDecimal精度位数
     */
    public final static BigDecimal BIGDECIMAL_SCALE = new BigDecimal("0.001");
    public final static BigDecimal DEVIATION = new BigDecimal("0.00000000000001");// 冻结或持有比例的误差

    /**
     * 借贷别 D-借
     */
    public final static String DORC_TYPE_DEBIT = "D";// 借
    /**
     * 借贷别 C-贷
     */
    public final static String DORC_TYPE_CREDIT = "C";// 贷

    /**
     * 计息(每天批处理)
     */
    public final static String MESSAGE_BUSINESS_TYPE_CALCULATE_ACCRUAL = "000003";

    /**
     * 客户开户
     */
    public final static String MESSAGE_BUSINESS_TYPE_OPEN_ACCOUNT = "010001";
    /**
     * 新建理财分账
     */
    public final static String MESSAGE_BUSINESS_TYPE_FINANCING = "010002";
    /**
     * 新建贷款分账010003
     */
    public final static String MESSAGE_BUSINESS_TYPE_LOAN_LEDGER = "010003";
    /**
     * 新建贷款分户
     */
    public final static String MESSAGE_BUSINESS_TYPE_LOAN_HOUSEHOLD = "010004";
    /**
     * 分账停用
     */
    public final static String MESSAGE_BUSINESS_TYPE_LEDGER_DISABLE = "010005";
    /**
     * 分户停用
     */
    public final static String MESSAGE_BUSINESS_TYPE_ACCOUNT_STA_UPDATE = "010006";

    /**
     * 充值
     */
    public final static String MESSAGE_BUSINESS_TYPE_RECHARGE = "020001";
    /**
     * 冻结解冻现金
     */
    public final static String MESSAGE_BUSINESS_TYPE_FROZEN_AMOUNT = "020002";
    /**
     * 回款
     */
    public final static String MESSAGE_BUSINESS_TYPE_REPAYMENT = "020041";
    /**
     * 取现
     */
    public final static String MESSAGE_BUSINESS_TYPE_ENCHASHMENT = "020005";
    /**
     * 放款
     */
    public final static String MESSAGE_BUSINESS_TYPE_GRANT_LOAN = "020042";
    /**
     * 分账转账
     */
    public final static String MESSAGE_BUSINESS_TYPE_LEDGER_TRANSFER_ACCOUNT = "020043";
    /**
     * 收取管理费
     */
    public final static String MESSAGE_BUSINESS_TYPE_CHARGE_MANAGEMENT_COST = "020044";
    /**
     * 逾期付款利息
     */
    public final static String MESSAGE_BUSINESS_TYPE_LATE_PAYMENT_INTEREST = "020045";

    /**
     * 债权转让
     */
    public final static String MESSAGE_BUSINESS_TYPE_DEBTASSIGNMENT = "030003";
    /**
     * 原始债权交易
     */
    public final static String MESSAGE_BUSINESS_TYPE_ORIGINAL_LOAN_TRADE = "030005";
    /**
     * 冻结解冻持有比例
     */
    public final static String MESSAGE_BUSINESS_TYPE_FROZEN_PORPORTION = "030006";
    /**
     * 外部债权初始化
     */
    public final static String MESSAGE_BUSINESS_TYPE_EXTERNAL_LOAN_INIT = "030007";

    /**
     * 查询债权接口
     */
    public final static String MESSAGE_BUSINESS_TYPE_QUERY_LEDGER_LOAN = "990001";
    /**
     * 查询投资明细接口
     */
    public final static String MESSAGE_BUSINESS_TYPE_QUERY_LEDGER_FINANCE = "990002";
    /**
     * 查询应收应付
     */
    public final static String MESSAGE_BUSINESS_TYPE_QUERY_ACCOUNTSRECIEVABLEANDPAYABLE = "990003";
    /**
     * 查询待付款
     */
    public final static String MESSAGE_BUSINESS_TYPE_QUERY_OBLIGATIONS = "990004";
    /**
     * 查询PV
     */
    public final static String MESSAGE_BUSINESS_TYPE_QUERY_PV = "990005";

    /**
     * 查询还款计划
     */
    public final static String MESSAGE_BUSINESS_TYPE_QUERY_REPAYMENT_PLAN = "990006";

    /**
     * 冲正
     */
    public final static String MESSAGE_BUSINESS_TYPE_REVERSE = "060001";

    /** 结息 */
    public final static String MESSAGE_BUSINESS_TYPE_EXPIRY= "020046";

    /** 查询回款金额和资产价值 */
    public final static String MESSAGE_BUSINESS_TYPE_QUERY_RETURN_VALUE = "990007";

    // --start--业务类别/
    /**
     * 1--理财
     */
    public final static String BUSINESS_TYPE_FINANCING = "1";
    /**
     * 2--贷款
     */
    public final static String BUSINESS_TYPE_LOAN = "2";
    /**
     * 3--系统收款
     */
    public final static String BUSINESS_TYPE_RECEIVABLES = "3";
    
    /*-----状态 start-------*/
    /**正常 */
    public final static String ACCOUNT_STATUS_REGULAR = "1";
    /*** 停用*/
    public final static String ACCOUNT_STATUS_DISABLE = "2";
    /*** 逾期*/
    public final static String ACCOUNT_STATUS_OVERDUE = "3";
    /*** 呆滞*/
    public final static String ACCOUNT_STATUS_IDLE = "4";
    /*** 提前结清*/
    public final static String ACCOUNT_STATUS_EARLYSTL = "5";
    /*** 异常债权*/
    public final static String ACCOUNT_STATUS_EXCEPT = "6";
    /*-----状态 end-------*/
    
    /**冻结状态：1 冻结*/
    public final static String LEDGERFINANCE_STATUS_FRO = "1";
    /**冻结状态：2 解冻*/
    public final static String LEDGERFINANCE_STATUS_UNFRE = "2";

    public final static String DEBT_STATUS_INVALID = "9";

    // ----end/

    // 操作对象的类型
    public final static String LEDGER_FINANCE = "F";
    public final static String LEDGER_LOAN = "L";
    public final static String DEBT = "D";

    /**
     * 分页常量相关数据
     */
    public final static Integer PAGE_SIZE = 10;
    public final static Integer PAGE_NO = 1;
    static public final String UNEXECTUED = "1";// 未执行
    static public final String EXECTUED = "2";// 已执行

    public static final String FLOW_FILE_NAME = File.separator + "flow.ser";
    
    /**债权转让：债权合并账号*/
    public static final Set<String> loanMergeAccounts = new HashSet<String>();
    static{
    	loanMergeAccounts.add("00010001000000010001");//普通居间人1号
    	loanMergeAccounts.add("00010001000000010002");//普通居间人16号
    	loanMergeAccounts.add("00010001000000010007");//回购居间人1号
    	loanMergeAccounts.add("00010001000000010008");//回购居间人16号
    }
}
