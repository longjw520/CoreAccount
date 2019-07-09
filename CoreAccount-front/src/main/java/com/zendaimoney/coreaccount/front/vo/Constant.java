package com.zendaimoney.coreaccount.front.vo;


/**
 * 存储常量
 * 
 * @author liubin
 * 
 */
public class Constant {

	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/** 用于分割线程ID和报文内容的分隔符 */
	public final static String IDENTIFIER = "#";

	/** 科目号 */
	/** 待付款 */
	public final static String SUBJECTS_OBLIGATION = "002001";

	/** 科目号 end */
	// --start 报文请求的各种状态/
	/** 初始状态 */
	public final static String REQUEST_INITIAL_STATUS = "000";

	/** 通过 */
	public final static String REQUEST_STATUS_PASS = "001";

	/** 处理中 */
	public final static String REQUEST_STATUS_PENDING = "002";

	/** 已处理 */
	public final static String REQUEST_STATUS_PROCESSED = "003";

	/** 已返回 */
	public final static String REQUEST_STATUS_RETURNED = "004";

	/** 请求重复 */
	public final static String REQUEST_STATUS_REPEATED = "101";

	/** 报文格式错误 */
	public final static String REQUEST_STATUS_MESSAGE_ERROR = "102";

	/** 用户不存在 */
	public final static String REQUEST_STATUS_USER_NOT_EXIST = "103";

	/** 密码错误 */
	public final static String REQUEST_STATUS_PWD_ERROR = "104";

	/** IP错误 */
	public final static String REQUEST_STATUS_IP_ERROR = "105";

	/** 授权错误 */
	public final static String REQUEST_STATUS_AUTHORIZATION_ERROR = "106";

	// end----/

	// --start处理队列/
	/** 处理队列ID */
	public final static String HANDLE_QUEUE_ID = "0101";

	/** 回调队列ID */
	public final static String CALLBACK_QUEUE_ID = "0201";
	// --end/

	// ---start报文类型/
	/** 账户事务 */
	public final static String MESSAGE_TYPE_ACCOUNT = "01";

	/** 账户分录 */
	public final static String MESSAGE_TYPE_ACCOUNT_RECORD = "02";

	/** 查询 */
	public final static String MESSAGE_TYPE_QUERY = "03";
	// ---end/

	// --start队列类型/
	/** 缓冲 */
	public final static String QUEUE_TYPE_BUFFERING = "01";

	/** 待处理 */
	public final static String QUEUE_TYPE_PENDING = "02";
	/** 已处理 */
	public final static String QUEUE_TYPE_PROCESSED = "03";
	// ----end//

	// ---start请求系统类型/
	/** fortune */
	public final static String REQUEST_TYPE_FORTUNE = "2003";
	// --end/

	// --start报文相关的属性/
	/** 普通的文本格式 */
	public final static String MESSAGE_CONTENT_TYPE_TXT = "TXT";

	/** xml格式 */
	public final static String MESSAGE_CONTENT_TYPE_XML = "XML";

	/** Json格式 */
	public final static String MESSAGE_CONTENT_TYPE_JSON = "JSON";

	/** 优先级 */
	public final static String PRIORITY_03 = "03";

	/** 报文发送系统fortune */
	public final static String SYSTEM_FORTUNE = "200300";

	/** 报文接收系统 */
	public final static String RECEIVE_SYSTEM = "200700";

	// ---start报文类型定义//
	/** 查询操作报文类型 */
	public final static String MESSAGE_BUSINESS_TYPE_QUERY_TYPE = "99";
	/** 内部发起 */
	public final static String MESSAGE_BUSINESS_TYPE_INNER = "00";
	/** 计息 */
	public final static String COUNT_ACCRUAL = "000003";

	/** 客户开户 */
	public final static String MESSAGE_BUSINESS_TYPE_OPEN_ACCOUNT = "010001";
	/** 新建理财分账 */
	public final static String MESSAGE_BUSINESS_TYPE_FINANCING = "010002";
	/** 新建贷款分账010003 */
	public final static String MESSAGE_BUSINESS_TYPE_LOAN_LEDGER = "010003";
	/** 新建贷款分户010004 */
	public final static String MESSAGE_BUSINESS_TYPE_LOAN_HOUSEHOLD = "010004";
	/** 分账停用010005 */
	public final static String MESSAGE_BUSINESS_TYPE_LEDGER_DISABLED = "010005";// LedgerDisableVo
	/** 贷款分户停用010006 */
	public final static String MESSAGE_BUSINESS_TYPE_ACCOUNT_STA_UPDATE = "010006";

	/** 充值 */
	public final static String MESSAGE_BUSINESS_TYPE_RECHARGE = "020001";
	/** 冻结解冻现金 */
	public final static String MESSAGE_BUSINESS_TYPE_FROZEN_AMOUNT = "020002";
	/** 回款 */
	public final static String MESSAGE_BUSINESS_TYPE_REPAYMENT = "020041";
	/** 取现 */
	public final static String MESSAGE_BUSINESS_TYPE_ENCHASHMENT = "020005";
	/** 放款 */
	public final static String MESSAGE_BUSINESS_TYPE_GRANT_LOAN = "020042";
	/** 分账转账 */
	public final static String MESSAGE_BUSINESS_TYPE_LEDGER_TRANSFER_ACCOUNT = "020043";
	/** 收取管理费 */
	public final static String MESSAGE_BUSINESS_TYPE_CHARGE_MANAGEMENT_COST = "020044";
	/** 逾期付款利息 */
	public final static String MESSAGE_BUSINESS_TYPE_LATE_PAYMENT_INTEREST = "020045";

	/** 债权转让 */
	public final static String MESSAGE_BUSINESS_TYPE_DEBTASSIGNMENT = "030003";
	/** 原始债权交易 */
	public final static String MESSAGE_BUSINESS_TYPE_ORIGINAL_LOAN_TRADE = "030005";
	/** 冻结解冻持有比例 */
	public final static String MESSAGE_BUSINESS_TYPE_FROZEN_PORPORTION = "030006";
	/** 外部债权初始化 */
	public final static String MESSAGE_BUSINESS_TYPE_EXTERNAL_LOAN_INIT  = "030007";
	
	/** 查询债权接口 */
	public final static String MESSAGE_BUSINESS_TYPE_QUERY = "990001";
	/** 查询投资明细 */
	public final static String MESSAGE_BUSINESS_TYPE_QUERY_LEDGER_FINANCE = "990002";
	/** 查询应收应付 */
	public final static String MESSAGE_BUSINESS_TYPE_QUERY_ACCOUNTSRECIEVABLEANDPAYABLE = "990003";
	/** 查询分账 */
	public final static String MESSAGE_BUSINESS_TYPE_QUERY_OBLIGATIONS = "990004";
	/** 查询PV */
	public final static String MESSAGE_BUSINESS_TYPE_QUERY_PV = "990005";

	/** 查询还款计划 */
	public final static String MESSAGE_BUSINESS_TYPE_QUERY_REPAYMENT_PLAN = "990006";

	/** 结息 */
	public final static String MESSAGE_BUSINESS_TYPE_EXPIRY= "020046";

	/** 查询回款金额和资产价值 */
	public final static String MESSAGE_BUSINESS_TYPE_QUERY_RETURN_VALUE = "990007";

	// ----end//

	public static final String HEADER_START_TAG = "{H:";
	public static final String BODY_START_TAG = "{D:";
	public static final String END_TAG = "}";
	public static final String VERSION = "1.00"; // 报文的版本号
	public static final String SEPARATOR = ","; // 报文内容分割符,

	/** Front项目下类中的字符串常量 */
	public static final String DATAGRAM_NAME_IN_SESSION = "datagram";
	public static final String DATAGRAM_VO_ENTITY_IN_SESSION = "content";
	public static final String DATAGRAM_VO_LOG_IN_SESSION = "messageLog";
	public static final String CLIENT_HOST_IN_SESSION = "clientHost";

}
