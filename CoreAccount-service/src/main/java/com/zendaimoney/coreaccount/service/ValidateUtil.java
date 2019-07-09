package com.zendaimoney.coreaccount.service;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.entity.*;
import com.zendaimoney.coreaccount.util.PropertiesReader;
import com.zendaimoney.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

import static com.zendaimoney.coreaccount.constants.Constants.PROCESS_STATUS_FAIL;

/*
 * ValidateUtil 检查专用-单纯比较，不涉及数据的读取、组装等，数额大小检查的参数由逻辑按该有的数额大小由小到大排序。
 * 目前提供的方法有：检查分账信息，检查债权信息，检查账号冻结金额是否足够解冻，检查账号金额是否足够冻结，检查账号金额是否足够支付
 * */
public abstract class ValidateUtil {
    private static Logger logger = Logger.getLogger(ValidateUtil.class);

    /**
     * validateLedger 检查分账信息 ， 参数：被检查分账信息，分账业务类别（参数为空时表示不用检查业务类别）。 1、检查分账信息是否为空
     * 2、检查分账信息状态是否正常 3、检查分账业务类别
     */
    public static void validateLedger(Ledger ledger, String busiType) {
        if (ledger == null) {
            logger.info("分账号不存在");
            throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("account_not_exist"));
        }
        if (!ledger.getAcctStatus().equals(Constants.ACCOUNT_STATUS_REGULAR)) {
            logger.info("分账状态不正常");
            throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("ledgerLoan.create.ledger.disabled"));
        }
        if (StringUtils.isNotBlank(busiType)) {
            if (!ledger.getBusiType().equals(busiType)) {
                logger.info("分账业务类别不是" + busiType);
                throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString(busiType.equals(Constants.BUSINESS_TYPE_FINANCING) ? "ledger.businessType.notfinance" : "ledger.businessType.notdebt"));
            }
        }
    }

    public static void validateLedgerLoanNull(LedgerLoan ledgerLoan) {
        if (ledgerLoan == null) {
            logger.info("债权信息不存在");
            throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("ledgerLoan.id.not.exist"));
        }
    }

    /**
     * validateLedgerLoan 检查债权信息 ， 参数：被检查债权信息。 1、检查债权信息是否为空 2、检查债权信息状态是否正常(1)
     */
    public static void validateLedgerLoan(LedgerLoan ledgerLoan) {
        if (ledgerLoan == null) {
            logger.info("债权信息不存在");
            throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("ledgerLoan.id.not.exist"));
        }
        if (!ledgerLoan.getAcctStatus().equals(Constants.ACCOUNT_STATUS_REGULAR)) {
            logger.info("债权状态不正常");
            throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("ledgerLoan.acctstatus.disabled"));
        }
    }

    /**
     * validateLedgerLoanAll 检查债权信息 ， 参数：被检查债权信息。 1、检查债权信息是否为空
     * 2、检查债权信息状态是否正常(1,3,4)
     */
    public static void validateLedgerLoanAll(LedgerLoan ledgerLoan) {
        if (ledgerLoan == null) {
            logger.info("债权信息不存在");
            throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("ledgerLoan.id.not.exist"));
        }
        String loanStatus = ledgerLoan.getAcctStatus();
        if (!(loanStatus.equals(Constants.ACCOUNT_STATUS_REGULAR) || loanStatus.equals(Constants.ACCOUNT_STATUS_OVERDUE) || loanStatus.equals(Constants.ACCOUNT_STATUS_IDLE))) {
            logger.info("债权状态不正常");
            throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("ledgerLoan.acctstatus.disabled"));
        }
    }

    /**
     * validateUnfreezeAmount:检查账号冻结金额是否足够解冻 。 parameters:"准备解冻结的金额","账号冻结金额"。
     * 提示信息“可解冻金额不足”。 description:"准备解冻结的金额"大于"账号冻结金额"则抛出异常“可解冻金额不足”。
     */
    public static void validateUnfreezeAmount(BigDecimal unfreezeAmount, BigDecimal ledgerFrozenAmt) {
        compareAmount(unfreezeAmount, ledgerFrozenAmt, "ledger.unfreezeAmt.error");
    }

    /**
     * validateFrozen:检查账号金额是否足够冻结 。 parameters:当前账户金额,准备冻结的金额,提示信息“可冻结金额不足”。
     * description:"当前账户金额"大于"准备冻结的金额"则抛出异常“可冻结金额不足”。
     */
    public static void validateFrozenAmount(BigDecimal frozenAmount, BigDecimal ledgerAmount) {
        compareAmount(frozenAmount, ledgerAmount, "ledger.frozenAmt.error");
    }

    /**
     * validateLedgerAmount:检查金额是否足够支付 。 parameters:需要支付的金额,
     * 账号当前金额（通常是当前账户金额加上解冻金额的总和）,提示信息“账号账户余额不足”。 description:"需要支付的金额" 大于
     * "账号当前金额"则抛出异常“账号账户余额不足”。
     */
    public static void validateLedgerAmount(BigDecimal amount, BigDecimal toBeLedgerAmount) {
        compareAmount(amount, toBeLedgerAmount, "ledger.amount.less");
    }

    public static void validateLedgerAmountForId(BigDecimal amount, BigDecimal toBeLedgerAmount) {
        compareAmount(amount, toBeLedgerAmount, "customer.balance.not.enough");
    }

    /**
     * validateCompanyCashLedgerAmount:检查金额是否足够支付 。 parameters:需要支付的金额,
     * 账号当前金额（通常是当前账户金额加上解冻金额的总和）,提示信息“公司现金账号账户余额不足”。 description:"需要支付的金额" 大于
     * "账号当前金额"则抛出异常“账号账户余额不足”。
     */
    public static void validateCompanyCashLedgerAmount(BigDecimal amount, BigDecimal toBeLedgerAmount) {
        compareAmount(amount, toBeLedgerAmount, "company.cash.balance.not.enough");
    }

    /*
     * compareAmount： 私有方法，比较数据大小，并抛出异常提示信息。 parameters:小数额，大数额，异常提示信息。
     * description:当"小数额"大于"大数额"时抛出“异常提示信息”。
     */
    public static void compareAmount(BigDecimal littleAmount, BigDecimal muchAmount, String exceptionInfo) {

        if ((littleAmount.subtract(muchAmount)).compareTo(Constants.BIGDECIMAL_SCALE) >= 0) {
            logger.info(exceptionInfo);
            throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString(exceptionInfo));
        }

    }

    public static void validateBusinessInfo(BusinessInfo businessInfo, String messageSequence) {
        if (businessInfo == null || businessInfo.getId() == 0) {
            logger.info("没找到messageSequence对应的报文" + messageSequence);
            throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("messageSequence.error") + messageSequence);
        }
    }

    /**
     * 校验还款计划是否合法
     *
     * @param repaymentPlan
     */
    static public void validateRepaymentPlan(RepaymentPlan repaymentPlan) {
        if (repaymentPlan == null) {
            logger.info("该笔债权不存在对应还款计划");
            throw new BusinessException(PROCESS_STATUS_FAIL, PropertiesReader.readAsString("repayment.not.exist"));
        }
    }

    /**
     * 检验理财分户信息是否存在
     *
     * @param ledgerFinance
     */
    static public void validateLedgerFinanceNull(LedgerFinance ledgerFinance, long financeId) {
        if (ledgerFinance == null) {
            logger.info("理财分户信息不存在");
            throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("ledger.finance.not.exist") + financeId);
        }
    }

    /**
     * 检验理财分户信息是否合法
     *
     * @param ledgerFinance
     */
    static public void validateLedgerFinance(LedgerFinance ledgerFinance, long financeId) {
        if (ledgerFinance == null) {
            logger.info("理财分户信息不存在");
            throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("ledger.finance.not.exist") + financeId);
        }
        String acctStatus = ledgerFinance.getAcctStatus().intern();
        if (acctStatus != Constants.ACCOUNT_STATUS_REGULAR && acctStatus != Constants.ACCOUNT_STATUS_OVERDUE && acctStatus != Constants.ACCOUNT_STATUS_IDLE) {
            logger.info("理财分户状态不正常");
            throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("ledgerfinance.status.disabled") + financeId);
        }
    }

    /**
     * 验证分账信息
     */
    static public void validateLedger(Ledger ledger) {
        if (ledger == null) {
            logger.info("分账信息表无此分账号，无法停用！");
            throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("ledger.account.disable"));
        }
    }

    /**
     * 校验客户信息
     *
     * @param customer
     */
    static public void validateCustomer(Customer customer) {
        if (null != customer)
            throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("account.opened"));
    }

    /**
     * 账户所属客户不是同一人
     *
     * @param account1
     * @param account2
     */
    static public void ifNotSameCustomer(Ledger account1, Ledger account2) {
        if (account1.getCustomer().getId() != account2.getCustomer().getId())
            throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("ledger.customer.account.not.same"));
    }

    /**
     * 检查finance冻结比例是否足够解冻 。 提示信息“理财分户冻结比例不足,理财分户ID”。
     */
    public static void validateUnfreezeProportions(BigDecimal unfreezeProportions, BigDecimal ledgerFinanceFrozenPro, long financeId) {
        compareProportions(unfreezeProportions, ledgerFinanceFrozenPro, "ledgerfinance.frozenproportion.less", financeId);
    }

    /**
     * 检查finance持有比例是否足够冻结 或交易。 提示信息“理财分户持有比例不足,理财分户ID”。
     */
    public static void validateFrozenProportions(BigDecimal frozenProportions, BigDecimal ledgerFinanceDebtPro, long financeId) {
        compareProportions(frozenProportions, ledgerFinanceDebtPro, "ledgerfinance.proportion.less", financeId);
    }

    /*
     * compareAmount： 私有方法，比较数据大小，并抛出异常提示信息。 parameters:小数额，大数额，异常提示信息。
     * description:当"小数额"大于"大数额"时抛出“异常提示信息”。
     */
    public static void compareProportions(BigDecimal littlePro, BigDecimal muchPro, String exceptionInfo, long financeId) {

        if ((littlePro.subtract(muchPro)).compareTo(Constants.DEVIATION) >= 0) {
            logger.info(exceptionInfo);
            throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString(exceptionInfo) + financeId);
        }

    }

    /**
     * @param condition (条件表达式)
     * @param errCode   (国际化消息key)
     */
    static public void assertTrue(boolean condition, String errCode) {
        if (!condition)
            throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString(errCode));
    }

    public static void validateWorkFlow(WorkFlow workFlow) {
        if (workFlow == null) {
            logger.info("事务流水信息不存在");
            throw new BusinessException(Constants.PROCESS_STATUS_FAIL, PropertiesReader.readAsString("workflow.not.exist"));
        }
    }
}
