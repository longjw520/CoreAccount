package com.zendaimoney.coreaccount.service;

import com.google.common.collect.Lists;
import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.constants.EntryNo;
import com.zendaimoney.coreaccount.constants.Subject;
import com.zendaimoney.coreaccount.dao.*;
import com.zendaimoney.coreaccount.data.redis.BasicRedisOpts;
import com.zendaimoney.coreaccount.entity.*;
import com.zendaimoney.coreaccount.rmi.vo.*;
import com.zendaimoney.coreaccount.tools.BufferedInputFile;
import com.zendaimoney.coreaccount.util.JsonHelper;
import com.zendaimoney.coreaccount.util.PropertiesReader;
import com.zendaimoney.exception.BusinessException;
import com.zendaimoney.utils.DateUtils;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.util.*;

import static com.zendaimoney.coreaccount.constants.Constants.*;
import static com.zendaimoney.coreaccount.constants.Subject.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
public class LedgerServiceTest {
    private static final String MESSAGE_SEQUENCE = "1002";

    @InjectMocks
    private LedgerService ledgerService;
    @Mock
    private AccountManagerService accountManagerService;
    @Mock
    private CustomerDao customerDao;
    @Mock
    private LedgerDao ledgerDao;
    @Mock
    private LedgerLoanDao ledgerLoanDao;
    @Mock
    private WorkFlowDao workFlowDao;
    @Mock
    private LedgerFinanceDao ledgerFinanceDao;
    @Mock
    private PvService pvService;
    @Mock
    private FlowDao flowDao;
    @Mock
    private BusinessInfoDao businessInfoDao;
    @Mock
    private SequenceDao sequenceDao;
    @Mock
    private DebtInfoDao debtDao;
    @Mock
    private SysglService sysglService;
    @Mock
    private LedgerDetailDao ledgerDetailDao;
    @Mock
    private BasicRedisOpts basicRedisOpts;
    @Mock
    BusinessInfoService businessInfoService;
    @Mock
    LedgerFinanceService ledgerFinanceService;

    // 充值
    String messageSequence = MESSAGE_SEQUENCE;
    String account = "0000000100000002000";
    BigDecimal rechargeAmount = new BigDecimal(2000);
    BigDecimal rechargeCommission = new BigDecimal(20);
    String rechargeAmountMemo = "充值备注";
    String rechargeCommissionMemo = "充值手续费备注";

    // 取现
    BigDecimal amount1 = new BigDecimal(2000);
    BigDecimal chargeAmount = new BigDecimal(20);
    String enchashmentMemo = "充值备注";
    String chargeMemo = "充值手续费备注";

    // 原始债权交易
    BigDecimal amount = new BigDecimal(2000);
    String amountMemo = "amountMemo";
    long loanId = 1L;
    BigDecimal unfreezeAmount = new BigDecimal(500);
    String unfreezeAmountMemo = "memo";
    BigDecimal debtProportion = new BigDecimal("0.3");
    String tradeMemo = "tradeMemo";

    @Test
    public void testQueryLedger_ok() {
        String datagram = BufferedInputFile.read("data/json/QueryObligationsTest_ok_request.json");
        Datagram dg = (Datagram) JsonHelper.toBean(datagram, QueryObligationsVo.class);
        QueryObligationsVo queryObligationsVo = (QueryObligationsVo) dg.getDatagramBody();
        ledgerService.queryLedger(queryObligationsVo);
        verify(ledgerDao).queryObligationsPage((QueryObligationsVo) dg.getDatagramBody());

    }

    /** 分账转账Service测试 */
    @Ignore
    public void testTransferAccount_ok() {
        TransferAccountVo vo = new TransferAccountVo();
        vo.setTransferAmount(BigDecimal.TEN);
        vo.setAccountIn("001");
        vo.setAccountOut("002");
        String messageSequence = "000121";
        Ledger in = new Ledger();
        Ledger out = new Ledger();
        out.setAmount(BigDecimal.valueOf(200));
        Customer c = new Customer();
        c.setId(2L);
        in.setCustomer(c);
        in.setAmount(BigDecimal.valueOf(5));
        out.setCustomer(c);
        in.setAcctStatus(Constants.ACCOUNT_STATUS_REGULAR);
        out.setAcctStatus(Constants.ACCOUNT_STATUS_REGULAR);
        Mockito.when(ledgerDao.loadByAccount(vo.getAccountIn())).thenReturn(in);
        Mockito.when(ledgerDao.loadByAccount(vo.getAccountOut())).thenReturn(out);
        Ledger company = new Ledger();
        company.setDetailValue(LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE, BigDecimal.valueOf(500));
        when(ledgerDao.loadByAccount(COMPANY_CASH_LEDGER_ACCOUNT)).thenReturn(company);
        when(businessInfoService.findByMessageSequence(messageSequence)).thenReturn(2L);
        ledgerService.transferAccount(vo, 0L);
        assertTrue(out.getAmount().compareTo(BigDecimal.valueOf(190)) == 0);
        assertTrue(company.getDetailValue(LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE).compareTo(BigDecimal.valueOf(500)) == 0);
        assertTrue(in.getAmount().compareTo(BigDecimal.valueOf(15)) == 0);
        verify(flowDao, times(4)).save(any(Flow.class));
    }

    /** 分账转账Service测试 */
    @Test(expected = BusinessException.class)
    public void testTransferAccount_error() {

        Customer customer = new Customer();
        customer.setId(250l);
        customer.setCustomerNo("012145454012145454012145454012");
        customer.setName("abc");
        customer.setTotalAcct("0000000100000007");
        customer.setCardId("422822168411111111");
        customer.setCardType("1");

        Customer customerSys = new Customer();
        customerSys.setId(1l);
        customerSys.setCustomerNo("1");
        customerSys.setName("系统账户");
        customerSys.setTotalAcct("1000000000000001");
        customerSys.setCardId("2433");
        customerSys.setCardType("99");

        Ledger ledgerIn = new Ledger();
        ledgerIn.setId(250);
        ledgerIn.setAccount("00000001000000020001");
        ledgerIn.setCustomer(customer);
        ledgerIn.setAcctStatus("1");
        ledgerIn.setAmount(new BigDecimal(560));
        ledgerIn.setBusiType("1");

        Ledger ledgerOut = new Ledger();
        ledgerOut.setId(250);
        ledgerOut.setAccount("00000001000000030001");
        ledgerOut.setCustomer(customer);
        ledgerOut.setAcctStatus("1");
        ledgerOut.setAmount(new BigDecimal(86000));
        ledgerOut.setBusiType("1");

        Ledger companyLedger = new Ledger();
        companyLedger.setId(5);
        companyLedger.setAccount("10000000000000010001");
        companyLedger.setCustomer(customer);
        companyLedger.setAcctStatus("1");
        companyLedger.setAmount(new BigDecimal("4000.5680000"));
        companyLedger.setBusiType("3");

        BusinessInfo businessInfo = new BusinessInfo();
        businessInfo.setId(010111303271425l);

        LedgerDetail ledgerDetail = new LedgerDetail();
        ledgerDetail.setDetailValue("12");
        ledgerDetail.setId(400);
        ledgerDetail.setLedger(ledgerIn);

        TransferAccountVo transferAccountVo = new TransferAccountVo();
        transferAccountVo.setAccountIn("00000001000000020001");
        transferAccountVo.setAccountOut("0000000");
        transferAccountVo.setTransferAmount(new BigDecimal(10));

        when(ledgerDao.loadByAccount("00000001000000020001")).thenReturn(ledgerIn);
        when(ledgerDao.loadByAccount("00000001000000030001")).thenReturn(ledgerOut);
        when(ledgerDao.loadByAccount("10000000000000010001")).thenReturn(companyLedger);
        when(businessInfoDao.findUniqueBy("messageSequence", "010111303271425")).thenReturn(businessInfo);
        when(ledgerDetailDao.getLedgerDetailById("2008", companyLedger.getId())).thenReturn(ledgerDetail);

        ledgerService.transferAccount(transferAccountVo, 0L);
        Assert.assertEquals(new BigDecimal(570), ledgerIn.getAmount());

        try {
            ledgerService.transferAccount(transferAccountVo, 0L);
            Assert.fail();
        } catch (BusinessException e) {
            Assert.assertEquals("000001", e.getCode());
            Assert.assertEquals(PropertiesReader.readAsString("company.interest.not.enough"), e.getMessage());
        }
    }

    /** 解冻_ok */
    @Test
    public void testUnfreeze() {
        Ledger ledger = new Ledger();
        ledger.setAmount(BigDecimal.valueOf(1100d));
        ledger.setFrozenAmt(BigDecimal.valueOf(1000d));
        BigDecimal unfreezeAmt = BigDecimal.valueOf(100d);
        BusinessInfo businessInfo = this.getBusinessInfo();

        when(businessInfoDao.findUniqueBy("messageSequence", businessInfo.getMessageSequence())).thenReturn(businessInfo);
        when(sequenceDao.nextFlowNO()).thenReturn("1");
        when(sequenceDao.nextFlowGroupNo()).thenReturn(2L);

        ledgerService.unfreeze(businessInfo.getId(), ledger, unfreezeAmt, null, "");
        verify(flowDao, times(2)).save(any(Flow.class));

        Assert.assertEquals(BigDecimal.valueOf(1200d), ledger.getAmount());
        Assert.assertEquals(BigDecimal.valueOf(900d), ledger.getFrozenAmt());

    }

    /** 解冻_zero */
    @Test
    public void testUnfreeze_zero() {
        Ledger ledger = new Ledger();
        ledger.setAmount(BigDecimal.valueOf(1100d));
        ledger.setFrozenAmt(BigDecimal.valueOf(1000d));
        BigDecimal unfreezeAmt = BigDecimal.valueOf(0);
        ledgerService.unfreeze(null, ledger, unfreezeAmt, null, "");
        verify(flowDao, times(2)).save(any(Flow.class));
        Assert.assertEquals(BigDecimal.valueOf(1100d), ledger.getAmount());
        Assert.assertEquals(BigDecimal.valueOf(1000d), ledger.getFrozenAmt());

    }

    /** 冻结_ok */
    @Test
    public void testFrozen() {
        Ledger ledger = this.getLedger(1L, account, Constants.ACCOUNT_STATUS_REGULAR, Constants.BUSINESS_TYPE_FINANCING);
        ledger.setAmount(BigDecimal.valueOf(10000d));
        ledger.setFrozenAmt(BigDecimal.valueOf(100d));
        BigDecimal amt = BigDecimal.valueOf(500d);

        BusinessInfo businessInfo = this.getBusinessInfo();

        when(businessInfoDao.findUniqueBy("messageSequence", businessInfo.getMessageSequence())).thenReturn(businessInfo);
        when(sequenceDao.nextFlowNO()).thenReturn("1");
        when(sequenceDao.nextFlowGroupNo()).thenReturn(2L);

        ledgerService.frozen(businessInfo.getId(), ledger, amt, null, null);
        verify(flowDao, times(2)).save(any(Flow.class));
        Assert.assertEquals(BigDecimal.valueOf(9500d), ledger.getAmount());
        Assert.assertEquals(BigDecimal.valueOf(600d), ledger.getFrozenAmt());
    }

    /** 冻结_zero */
    @Test
    public void testFrozen_zero() {
        Ledger ledger = this.getLedger(1L, account, Constants.ACCOUNT_STATUS_REGULAR, Constants.BUSINESS_TYPE_FINANCING);
        ledger.setAmount(BigDecimal.valueOf(10000d));
        ledger.setFrozenAmt(BigDecimal.valueOf(100d));
        BigDecimal amt = BigDecimal.valueOf(0);

        ledgerService.frozen(null, ledger, amt, null, null);
        verify(flowDao, times(2)).save(any(Flow.class));
        Assert.assertEquals(BigDecimal.valueOf(10000d), ledger.getAmount());
        Assert.assertEquals(BigDecimal.valueOf(100d), ledger.getFrozenAmt());
    }

    @Test
    public void testPayInterestLate_nothing() {
        LatePaymentInterestVo latePaymentInterestVo = new LatePaymentInterestVo();
        String exDate = "2013-10-10";
        latePaymentInterestVo.setExDate(exDate);
        Mockito.when(debtDao.findForPayment(exDate)).thenReturn(new ArrayList<Debt>());
        try {
            ledgerService.payInterestLate(latePaymentInterestVo, 0L);
        } catch (Throwable e) {
            fail(e.getMessage());
        }
    }

    /** 逾期付款付息_ok */
    @Ignore
    public void testPayInterestLate() {
        LatePaymentInterestVo latePaymentInterestVo = new LatePaymentInterestVo();
        String exDate = "2013-10-10";
        latePaymentInterestVo.setExDate(exDate);
        String messageSequence = "1122333";
        Debt debt = new Debt();
        debt.setAccount("account1");
        debt.setApposedAcct("account2");
        debt.setAmount(new BigDecimal(100));
        when(debtDao.findForPayment(exDate)).thenReturn(Arrays.asList(debt));
        Ledger buyer = new Ledger();
        buyer.setAcctStatus(ACCOUNT_STATUS_REGULAR);
        Set<LedgerDetail> lds = new HashSet<LedgerDetail>();
        LedgerDetail ld1 = new LedgerDetail();
        ld1.setDetailValue(BigDecimal.valueOf(110));
        ld1.setType(OVERDUEINTEREST_PAYABLE);

        LedgerDetail ld2 = new LedgerDetail();
        ld2.setDetailValue(BigDecimal.valueOf(2));
        ld2.setType(INTEREST_INCOME);
        lds.add(ld2);
        lds.add(ld1);

        buyer.setLedgerDetails(lds);
        buyer.setAmount(new BigDecimal(300));
        Ledger seller = new Ledger();

        Set<LedgerDetail> lds1 = new HashSet<LedgerDetail>();
        LedgerDetail ld3 = new LedgerDetail();
        ld3.setDetailValue(BigDecimal.ZERO);
        ld3.setType(OVERDUEINTEREST_PAYABLE);

        LedgerDetail ld4 = new LedgerDetail();
        ld4.setDetailValue(BigDecimal.valueOf(1));
        ld4.setType(INTEREST_INCOME);

        lds1.add(ld3);
        lds1.add(ld4);

        seller.setLedgerDetails(lds1);
        seller.setAcctStatus(ACCOUNT_STATUS_REGULAR);
        seller.setAmount(new BigDecimal(10));
        when(ledgerDao.loadByAccount(debt.getAccount())).thenReturn(buyer);
        when(businessInfoService.findByMessageSequence(messageSequence)).thenReturn(11122L);
        when(ledgerDao.loadByAccount(debt.getApposedAcct())).thenReturn(seller);
        ledgerService.payInterestLate(latePaymentInterestVo, 0L);
        assertEquals(EXECTUED, debt.getStatus());
        assertTrue(buyer.getDetailValue(OVERDUEINTEREST_PAYABLE).compareTo(BigDecimal.TEN) == 0);
        assertTrue(seller.getDetailValue(INTEREST_INCOME).compareTo(BigDecimal.valueOf(101)) == 0);

        assertTrue(buyer.getAmount().compareTo(BigDecimal.valueOf(200)) == 0);
        debt.setExDate(new Date());
        debt.setAmount(BigDecimal.valueOf(1000));
        ledgerService.payInterestLate(latePaymentInterestVo, 0L);
        assertTrue(seller.getAmount().compareTo(BigDecimal.valueOf(310)) == 0);
    }

    /** 充值_ok */
    @Ignore
    public void testRecharge_ok() {
        Ledger customerLedger = this.getLedger(1L, account, Constants.ACCOUNT_STATUS_REGULAR, Constants.BUSINESS_TYPE_FINANCING);
        Ledger companyCashLedger = this.getCompanyCashLedger(2L);
        Ledger companyLedger = this.getCompanyLedger(3L);
        RechargeVo rechargeVo = new RechargeVo();
        rechargeVo.setAccount(account);
        rechargeVo.setRechargeAmount(rechargeAmount);
        rechargeVo.setRechargeCommission(rechargeCommission);
        rechargeVo.setRechargeAmountMemo(rechargeAmountMemo);
        rechargeVo.setRechargeCommissionMemo(rechargeCommissionMemo);
        when(businessInfoService.findByMessageSequence(messageSequence)).thenReturn(this.getBusinessInfo().getId());
        when(ledgerDao.loadByAccount(customerLedger.getAccount())).thenReturn(customerLedger);
        when(ledgerDao.loadByAccount(Constants.COMPANY_CASH_LEDGER_ACCOUNT)).thenReturn(companyCashLedger);
        when(ledgerDao.loadByAccount(Constants.COMPANY_LEDGER_ACCOUNT)).thenReturn(companyLedger);

        when(sequenceDao.nextFlowNO()).thenReturn("1");
        when(sequenceDao.nextFlowGroupNo()).thenReturn(1L);

        ledgerService.recharge(rechargeVo, 0L);

        BigDecimal endAmount = rechargeAmount.subtract(rechargeCommission);

        Assert.assertEquals(endAmount, customerLedger.getAmount());

        Assert.assertEquals(rechargeCommission, customerLedger.getDetailValue(Subject.LEDGER_DETAIL_TYPE_RECHARGECOMMISSION_EXPENSENSE));

        Assert.assertEquals(endAmount, companyCashLedger.getDetailValue(Subject.LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE));

        Assert.assertEquals(rechargeCommission, companyLedger.getDetailValue(Subject.LEDGER_DETAIL_TYPE_RECHARGECOMMISSION_INCOME));
        verify(flowDao, times(6)).save(any(Flow.class));
    }

    /** 取现_ok */
    @Ignore
    public void testWithdrawCash_ok() {
        Ledger customerLedger = this.getLedger(1L, account, Constants.ACCOUNT_STATUS_REGULAR, Constants.BUSINESS_TYPE_FINANCING);
        customerLedger.setAmount(new BigDecimal("2020"));
        Ledger companyCashLedger = this.getCompanyCashLedger(2L);
        LedgerDetail ledgerDetail = new LedgerDetail();
        ledgerDetail.setType(Subject.LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE);
        ledgerDetail.setDetailValue(new BigDecimal("2020"));
        companyCashLedger.setLedgerDetails(new HashSet<LedgerDetail>(Arrays.asList(ledgerDetail)));
        Ledger companyLedger = this.getCompanyLedger(3L);
        EnchashmentVo enchashmentVo = new EnchashmentVo();
        enchashmentVo.setAccount(account);
        enchashmentVo.setAmount(amount1);
        enchashmentVo.setChargeAmount(chargeAmount);
        enchashmentVo.setChargeMemo(chargeMemo);
        enchashmentVo.setEnchashmentMemo(enchashmentMemo);
        when(businessInfoService.findByMessageSequence(messageSequence)).thenReturn(this.getBusinessInfo().getId());
        when(ledgerDao.loadByAccount(customerLedger.getAccount())).thenReturn(customerLedger);
        when(ledgerDao.loadByAccount(Constants.COMPANY_CASH_LEDGER_ACCOUNT)).thenReturn(companyCashLedger);
        when(ledgerDao.loadByAccount(Constants.COMPANY_LEDGER_ACCOUNT)).thenReturn(companyLedger);

        when(sequenceDao.nextFlowNO()).thenReturn("1");
        when(sequenceDao.nextFlowGroupNo()).thenReturn(1L);

        ledgerService.withdrawCash(enchashmentVo, 0L);

        BigDecimal endAmount = new BigDecimal("2020").subtract(amount1).subtract(chargeAmount);

        Assert.assertEquals(endAmount, customerLedger.getAmount());

        Assert.assertEquals(rechargeCommission, customerLedger.getDetailValue(Subject.WITHDRAWAL_CHARGE_PAY));

        Assert.assertEquals(endAmount, companyCashLedger.getDetailValue(Subject.LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE));

        Assert.assertEquals(rechargeCommission, companyLedger.getDetailValue(Subject.WITHDRAWAL_CHARGE_INCOME));
        verify(flowDao, times(6)).save(any(Flow.class));
    }

    /** 放款_ok */
    @Ignore
    public void testMakeLoan_ok() {
        Ledger customerLedger = this.getLedger(1L, account, Constants.ACCOUNT_STATUS_REGULAR, Constants.BUSINESS_TYPE_FINANCING);
        customerLedger.setAmount(new BigDecimal("2020"));
        Ledger companyCashLedger = this.getCompanyCashLedger(2L);
        LedgerDetail ledgerDetail = new LedgerDetail();
        ledgerDetail.setType(Subject.LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE);
        ledgerDetail.setDetailValue(new BigDecimal("2020"));
        companyCashLedger.setLedgerDetails(new HashSet<LedgerDetail>(Arrays.asList(ledgerDetail)));

        GrantLoanVo grantLoanVo = new GrantLoanVo();
        grantLoanVo.setAccount(account);
        grantLoanVo.setAmount(amount1);
        grantLoanVo.setAmountMemo(amountMemo);
        when(businessInfoService.findByMessageSequence(messageSequence)).thenReturn(this.getBusinessInfo().getId());
        when(ledgerDao.loadByAccount(customerLedger.getAccount())).thenReturn(customerLedger);
        when(ledgerDao.loadByAccount(Constants.COMPANY_CASH_LEDGER_ACCOUNT)).thenReturn(companyCashLedger);

        when(sequenceDao.nextFlowNO()).thenReturn("1");
        when(sequenceDao.nextFlowGroupNo()).thenReturn(1L);

        ledgerService.makeLoan(grantLoanVo, 0L);

        BigDecimal endAmount = new BigDecimal("2020").subtract(amount1);

        Assert.assertEquals(endAmount, customerLedger.getAmount());

        Assert.assertEquals(endAmount, companyCashLedger.getDetailValue(Subject.LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE));

        verify(flowDao, times(2)).save(any(Flow.class));
    }

    /** 放款_分账号不存在 */
    @Test
    public void testMakeLoan_ledgernull() {
        Ledger customerLedger = this.getLedger(1L, account, Constants.ACCOUNT_STATUS_REGULAR, Constants.BUSINESS_TYPE_FINANCING);
        customerLedger.setAmount(new BigDecimal("2020"));
        Ledger companyCashLedger = this.getCompanyCashLedger(2L);
        LedgerDetail ledgerDetail = new LedgerDetail();
        ledgerDetail.setType(Subject.LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE);
        ledgerDetail.setDetailValue(new BigDecimal("2020"));
        companyCashLedger.setLedgerDetails(new HashSet<LedgerDetail>(Arrays.asList(ledgerDetail)));

        GrantLoanVo grantLoanVo = new GrantLoanVo();
        grantLoanVo.setAccount(account);
        grantLoanVo.setAmount(amount1);
        grantLoanVo.setAmountMemo(amountMemo);
        when(businessInfoService.findByMessageSequence(messageSequence)).thenReturn(this.getBusinessInfo().getId());
        when(ledgerDao.loadByAccount(customerLedger.getAccount())).thenReturn(null);
        when(ledgerDao.loadByAccount(Constants.COMPANY_CASH_LEDGER_ACCOUNT)).thenReturn(companyCashLedger);

        when(sequenceDao.nextFlowNO()).thenReturn("1");
        when(sequenceDao.nextFlowGroupNo()).thenReturn(1L);

        try {
            ledgerService.makeLoan(grantLoanVo, 0L);
            fail();
        } catch (BusinessException e) {
            Assert.assertTrue(true);
        }
    }

    /** 放款_金额不足 */
    @Test
    public void testMakeLoan_amounterror() {
        Ledger customerLedger = this.getLedger(1L, account, Constants.ACCOUNT_STATUS_REGULAR, Constants.BUSINESS_TYPE_FINANCING);
        customerLedger.setAmount(new BigDecimal("1999.99"));
        Ledger companyCashLedger = this.getCompanyCashLedger(2L);
        LedgerDetail ledgerDetail = new LedgerDetail();
        ledgerDetail.setType(Subject.LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE);
        ledgerDetail.setDetailValue(new BigDecimal("2020"));
        companyCashLedger.setLedgerDetails(new HashSet<LedgerDetail>(Arrays.asList(ledgerDetail)));

        GrantLoanVo grantLoanVo = new GrantLoanVo();
        grantLoanVo.setAccount(account);
        grantLoanVo.setAmount(amount1);
        grantLoanVo.setAmountMemo(amountMemo);
        when(businessInfoService.findByMessageSequence(messageSequence)).thenReturn(this.getBusinessInfo().getId());
        when(ledgerDao.loadByAccount(customerLedger.getAccount())).thenReturn(customerLedger);
        when(ledgerDao.loadByAccount(Constants.COMPANY_CASH_LEDGER_ACCOUNT)).thenReturn(companyCashLedger);

        when(sequenceDao.nextFlowNO()).thenReturn("1");
        when(sequenceDao.nextFlowGroupNo()).thenReturn(1L);

        try {
            ledgerService.makeLoan(grantLoanVo, 0L);
            fail();
        } catch (BusinessException e) {
            Assert.assertTrue(true);
        }
    }

    /** 原始债权交易_ok */
    @Ignore
    public void testOriginalLoanTrade_ok() {
        Ledger customerLedger = this.getLedger(1L, account, Constants.ACCOUNT_STATUS_REGULAR, Constants.BUSINESS_TYPE_FINANCING);
        BigDecimal initCusAmount = BigDecimal.valueOf(10000);
        customerLedger.setAmount(initCusAmount);
        customerLedger.setFrozenAmt(BigDecimal.valueOf(10000));
        Ledger creditLedger = this.getLedger(2L, "account", Constants.ACCOUNT_STATUS_REGULAR, Constants.BUSINESS_TYPE_FINANCING);
        LedgerLoan loan = this.getLoan(loanId);
        loan.setLedger(creditLedger);

        when(ledgerDao.loadByAccount(customerLedger.getAccount())).thenReturn(customerLedger);
        when(ledgerLoanDao.getById(loanId)).thenReturn(loan);
        when(businessInfoDao.findUniqueBy("messageSequence", messageSequence)).thenReturn(this.getBusinessInfo());
        when(sequenceDao.nextFlowNO()).thenReturn("1");
        when(sequenceDao.nextFlowGroupNo()).thenReturn(1L);

        OriginalLoanTradeVo vo = new OriginalLoanTradeVo();
        vo.setAccount(account);
        vo.setLoanId(loanId);
        vo.setAmount(amount);
        vo.setAmountMemo(amountMemo);
        vo.setUnfreezeAmount(unfreezeAmount);
        vo.setUnfreezeAmountMemo(unfreezeAmountMemo);
        vo.setDebtProportion(debtProportion);
        vo.setTradeMemo(tradeMemo);
        ledgerService.originalLoanTrade(vo,  0L);
        verify(flowDao, times(6)).save(any(Flow.class));
        Assert.assertEquals(customerLedger.getAmount(), initCusAmount.add(unfreezeAmount).subtract(amount));
        Assert.assertEquals(customerLedger.getDetailValue(Subject.INVESTMENT_AMOUNT), amount);

        Assert.assertEquals(creditLedger.getAmount(), amount);
        Assert.assertEquals(creditLedger.getDetailValue(Subject.LEDGER_DETAIL_TYPE_DEBT_BALANCE), amount);
    }

    /** 债权转让_ok */
    @SuppressWarnings("unchecked")
    @Test
    public void testDebtAssignment_ok() {
        Ledger companyCashLedger = this.getCompanyCashLedger(1L);
        Ledger companyLedger = this.getCompanyLedger(2L);

        // 借款人
        Ledger creditLedger = this.getLedger(3L, "account", Constants.ACCOUNT_STATUS_REGULAR, Constants.BUSINESS_TYPE_FINANCING);
        LedgerLoan loan = this.getLoan(loanId);
        loan.setLedger(creditLedger);

        // 买方
        Ledger buyLedger = this.getLedger(4L, account, Constants.ACCOUNT_STATUS_REGULAR, Constants.BUSINESS_TYPE_FINANCING);
        BigDecimal initBuyAmount = BigDecimal.valueOf(10000);
        buyLedger.setAmount(initBuyAmount);
        buyLedger.setFrozenAmt(BigDecimal.valueOf(10000));

        // 卖方
        Ledger sellLedger = this.getLedger(5L, account, Constants.ACCOUNT_STATUS_REGULAR, Constants.BUSINESS_TYPE_FINANCING);
        BigDecimal initSellAmount = BigDecimal.valueOf(10000);
        sellLedger.setAmount(initSellAmount);
        sellLedger.setFrozenAmt(BigDecimal.valueOf(10000));

        LedgerFinance finance = new LedgerFinance();
        finance.setLedgerLoan(loan);
        finance.setId(1L);
        finance.setAcctStatus(Constants.ACCOUNT_STATUS_REGULAR);
        finance.setDebtAmount(loan.getLoan());
        finance.setDebtProportion(BigDecimal.valueOf(0.5));
        finance.setFrozenPorportion(BigDecimal.valueOf(0.5));
        finance.setInterestDeviation(BigDecimal.valueOf(20));
        finance.setInterestReceivable(BigDecimal.valueOf(10));
        finance.setLedger(sellLedger);
        sellLedger.getLedgerFinances().add(finance);

        DebtAssignmentsVo debtAssignmentsVo = new DebtAssignmentsVo();

        DebtAssignmentVo debtAssignmentVo = new DebtAssignmentVo();
        debtAssignmentVo.setFinanceId(finance.getId());
        debtAssignmentVo.setContractDeliveryDatePV(BigDecimal.valueOf(1100));
        debtAssignmentVo.setDeliveryDatePV(BigDecimal.valueOf(1200));
        debtAssignmentVo.setDebtProportion(new BigDecimal("0.1"));
        debtAssignmentVo.setUnfreezeProportion(new BigDecimal("0.1"));

        BigDecimal frozenAmount = BigDecimal.valueOf(20);
        String frozenAmountMemo = "frozenAmountMemo";
        BigDecimal unfreezeAmount = new BigDecimal(30);
        String unfreezeAmountMemo = "unfreezeAmountMemo";
        BigDecimal managementFee = new BigDecimal(21);
        String managementFeeMemo = "managementFeeMemo";
        BigDecimal fixProduTranSerFee = new BigDecimal(40);
        String fixProduTranSerFeeMemo = "fixProduTranSerFeeMemo";
        BigDecimal urgSerFee = new BigDecimal(50);
        String urgSerFeeMemo = "urgSerFeeMemo";

        debtAssignmentsVo.setBuyerAccount(buyLedger.getAccount());
        debtAssignmentsVo.setFrozenAmount(frozenAmount);
        debtAssignmentsVo.setFrozenAmountMemo(frozenAmountMemo);
        debtAssignmentsVo.setUnfreezeAmount(unfreezeAmount);
        debtAssignmentsVo.setUnfreezeAmountMemo(unfreezeAmountMemo);
        debtAssignmentsVo.setManagementFee(managementFee);
        debtAssignmentsVo.setManagementFeeMemo(managementFeeMemo);
        debtAssignmentsVo.setFixProduTranSerFee(fixProduTranSerFee);
        debtAssignmentsVo.setFixProduTranSerFeeMemo(fixProduTranSerFeeMemo);
        debtAssignmentsVo.setUrgSerFee(urgSerFee);
        debtAssignmentsVo.setUrgSerFeeMemo(urgSerFeeMemo);
        debtAssignmentsVo.setDebtAssignments(Lists.newArrayList(debtAssignmentVo));

        when(ledgerFinanceDao.getById(finance.getId())).thenReturn(finance);
        when(ledgerDao.loadByAccount(buyLedger.getAccount())).thenReturn(buyLedger);
        when(ledgerDao.loadByAccount(finance.getLedger().getAccount())).thenReturn(sellLedger);
        when(businessInfoDao.findUniqueBy("messageSequence", messageSequence)).thenReturn(this.getBusinessInfo());
        when(sequenceDao.nextFlowNO()).thenReturn("1");
        when(sequenceDao.nextFlowGroupNo()).thenReturn(1L);
        LedgerFinance ledgerFinance = new LedgerFinance();
        when(ledgerFinanceService.tradeFinance(any(LedgerFinance.class), any(Ledger.class), any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class), any(BigDecimal.class), (List<LedgerFinance>) any(List.class), any(BusinessInfo.class))).thenReturn(ledgerFinance);

        when(ledgerDao.loadByAccount(companyCashLedger.getAccount())).thenReturn(companyCashLedger);
        when(ledgerDao.loadByAccount(companyLedger.getAccount())).thenReturn(companyLedger);

        ledgerService.debtAssignment(debtAssignmentsVo, new BusinessInfo());
        verify(flowDao, times(30)).save(any(Flow.class));
    }

    /**
     * 外部债权初始化_ok shi
     */
    @Ignore
    public void testExternalLoanInitialization_ok() {
        // 债权人
        Ledger customerLedger = this.getLedger(1L, account, Constants.ACCOUNT_STATUS_REGULAR, Constants.BUSINESS_TYPE_FINANCING);

        // 借款人
        Ledger creditLedger = this.getLedger(2L, "account", Constants.ACCOUNT_STATUS_REGULAR, Constants.BUSINESS_TYPE_FINANCING);
        LedgerLoan thisLoan = this.getLoan(loanId);
        thisLoan.setCurrNum(0L);
        thisLoan.setNextExpiry(DateUtils.nullSafeParseDate("2013-4-1", Constants.DATE_FORMAT));
        thisLoan.setInterestStart(DateUtils.nullSafeParseDate("2013-3-12", Constants.DATE_FORMAT));
        thisLoan.setLoan(new BigDecimal(8000));

        RepaymentPlan repaymentPlan = new RepaymentPlan();
        repaymentPlan.setId(1L);
        repaymentPlan.setInterestAmt(new BigDecimal(1000));
        repaymentPlan.setCurrNum(0L);
        thisLoan.getRepaymentPlans().add(repaymentPlan);
        thisLoan.setLedger(creditLedger);

        String messageSequence = this.getBusinessInfo().getMessageSequence();
        String account = customerLedger.getAccount();
        long loanId = thisLoan.getId();
        BigDecimal amount = new BigDecimal(10000);
        BigDecimal debtProportion = new BigDecimal(1);
        String tradeMemo = "tradeMemo";

        when(ledgerDao.loadByAccount(account)).thenReturn(customerLedger);
        when(ledgerLoanDao.getById(loanId)).thenReturn(thisLoan);
        when(businessInfoDao.findUniqueBy("messageSequence", messageSequence)).thenReturn(this.getBusinessInfo());
        when(sequenceDao.nextFlowGroupNo()).thenReturn(1L);
        when(sequenceDao.nextFlowNO()).thenReturn("1");

        ExternalLoanInitializationVo vo = new ExternalLoanInitializationVo();
        vo.setAccount(account);
        vo.setLoanId(loanId);
        vo.setAmount(amount);
        vo.setDebtProportion(debtProportion);
        vo.setTradeMemo(tradeMemo);
        ledgerService.externalLoanInitialization(vo,  0L);
        verify(flowDao, times(5)).save(any(Flow.class));

    }

    /**
     * 外部债权初始化_ok2 shi
     */
    @Ignore
    public void testExternalLoanInitialization_ok2() {
        // 债权人
        Ledger customerLedger = this.getLedger(1L, account, Constants.ACCOUNT_STATUS_REGULAR, Constants.BUSINESS_TYPE_FINANCING);

        // 借款人
        Ledger creditLedger = this.getLedger(2L, "account", Constants.ACCOUNT_STATUS_REGULAR, Constants.BUSINESS_TYPE_FINANCING);
        LedgerLoan thisLoan = this.getLoan(loanId);
        thisLoan.setCurrNum(1L);
        thisLoan.setNextExpiry(DateUtils.nullSafeParseDate("2013-4-1", Constants.DATE_FORMAT));
        thisLoan.setLastExpiry(DateUtils.nullSafeParseDate("2013-3-1", Constants.DATE_FORMAT));
        thisLoan.setLoan(new BigDecimal(8000));

        RepaymentPlan repaymentPlan = new RepaymentPlan();
        repaymentPlan.setId(1L);
        repaymentPlan.setInterestAmt(new BigDecimal(1000));
        repaymentPlan.setCurrNum(1L);
        thisLoan.getRepaymentPlans().add(repaymentPlan);

        RepaymentPlan repaymentPlan1 = new RepaymentPlan();
        repaymentPlan1.setId(2L);
        repaymentPlan1.setCurrNum(2L);
        repaymentPlan1.setOutstanding(new BigDecimal(1000));
        thisLoan.getRepaymentPlans().add(repaymentPlan1);

        thisLoan.setLedger(creditLedger);

        String messageSequence = this.getBusinessInfo().getMessageSequence();
        String lastExpiryStr = "2013-03-01";
        String account = customerLedger.getAccount();
        long loanId = thisLoan.getId();
        LedgerLoan externalLoan = thisLoan;
        BigDecimal amount = new BigDecimal(10000);
        BigDecimal debtProportion = new BigDecimal(1);
        String tradeMemo = "tradeMemo";

        when(ledgerDao.loadByAccount(account)).thenReturn(customerLedger);
        when(ledgerLoanDao.getById(loanId)).thenReturn(thisLoan);
        when(businessInfoService.findByMessageSequence(messageSequence)).thenReturn(this.getBusinessInfo().getId());
        when(sequenceDao.nextFlowGroupNo()).thenReturn(1L);
        when(sequenceDao.nextFlowNO()).thenReturn("1");
        when(basicRedisOpts.getSingleResult(externalLoan.getId() + ":" + lastExpiryStr)).thenReturn("100:100");

        when(pvService.getPvAlways(thisLoan, Boolean.TRUE)).thenReturn(BigDecimal.ZERO);
        ExternalLoanInitializationVo vo = new ExternalLoanInitializationVo();
        vo.setAccount(account);
        vo.setLoanId(loanId);
        vo.setAmount(amount);
        vo.setDebtProportion(debtProportion);
        vo.setTradeMemo(tradeMemo);
        ledgerService.externalLoanInitialization(vo,  0L);
        verify(flowDao, times(5)).save(any(Flow.class));
    }

    /** 冻结/解冻金额_冻结ok */
    @Ignore
    public void testFrozenOrUnfreezeAmount_frozen() {
        Ledger ledger = this.getLedger(1L, account, Constants.ACCOUNT_STATUS_REGULAR, Constants.BUSINESS_TYPE_FINANCING);
        ledger.setAmount(BigDecimal.valueOf(10000d));
        ledger.setFrozenAmt(BigDecimal.valueOf(1000d));
        BusinessInfo businessInfo = this.getBusinessInfo();
        BigDecimal fAMT = BigDecimal.valueOf(500d);
        Boolean flag = true;
        FrozenOrUnfreezeAmountVo frozenOrUnfreezeAmountVo = new FrozenOrUnfreezeAmountVo();
        frozenOrUnfreezeAmountVo.setAccount(account);
        frozenOrUnfreezeAmountVo.setAmount(fAMT);
        frozenOrUnfreezeAmountVo.setAmountMemo(amountMemo);
        frozenOrUnfreezeAmountVo.setFlag(flag);
        when(ledgerDao.loadByAccount(account)).thenReturn(ledger);
        when(businessInfoDao.findUniqueBy("messageSequence", businessInfo.getMessageSequence())).thenReturn(businessInfo);
        when(sequenceDao.nextFlowNO()).thenReturn("1");
        when(sequenceDao.nextFlowGroupNo()).thenReturn(2L);
        ledgerService.frozenOrUnfreezeAmount(frozenOrUnfreezeAmountVo,  0L);
        verify(flowDao, times(2)).save(any(Flow.class));
        Assert.assertEquals(BigDecimal.valueOf(9500d), ledger.getAmount());
        Assert.assertEquals(BigDecimal.valueOf(1500d), ledger.getFrozenAmt());

    }

    /** 冻结/解冻金额_解冻ok */
    @Ignore
    public void testFrozenOrUnfreezeAmount_unfreeze() {
        Ledger ledger = this.getLedger(1L, account, Constants.ACCOUNT_STATUS_REGULAR, Constants.BUSINESS_TYPE_FINANCING);
        ledger.setAmount(BigDecimal.valueOf(10000d));
        ledger.setFrozenAmt(BigDecimal.valueOf(1000d));
        BusinessInfo businessInfo = this.getBusinessInfo();
        BigDecimal fAMT = BigDecimal.valueOf(500d);
        Boolean flag = false;
        FrozenOrUnfreezeAmountVo frozenOrUnfreezeAmountVo = new FrozenOrUnfreezeAmountVo();
        frozenOrUnfreezeAmountVo.setAccount(account);
        frozenOrUnfreezeAmountVo.setAmount(fAMT);
        frozenOrUnfreezeAmountVo.setAmountMemo(amountMemo);
        frozenOrUnfreezeAmountVo.setFlag(flag);
        when(ledgerDao.loadByAccount(account)).thenReturn(ledger);
        when(businessInfoDao.findUniqueBy("messageSequence", businessInfo.getMessageSequence())).thenReturn(businessInfo);
        when(sequenceDao.nextFlowNO()).thenReturn("1");
        when(sequenceDao.nextFlowGroupNo()).thenReturn(2L);
        ledgerService.frozenOrUnfreezeAmount(frozenOrUnfreezeAmountVo,  0L);
        verify(flowDao, times(2)).save(any(Flow.class));
        Assert.assertEquals(BigDecimal.valueOf(10500d), ledger.getAmount());
        Assert.assertEquals(BigDecimal.valueOf(500d), ledger.getFrozenAmt());

    }

    private Ledger getLedger(Long id, String account, String status, String busiType) {
        Ledger ledger = new Ledger();
        ledger.setId(id);
        ledger.setAmount(BigDecimal.ZERO);
        ledger.setAccount(account);
        ledger.setBusiType(busiType);
        ledger.setAcctStatus(status);
        return ledger;
    }

    private LedgerLoan getLoan(Long id) {
        LedgerLoan loan = new LedgerLoan();
        loan.setId(id);
        loan.setAcctStatus(Constants.ACCOUNT_STATUS_REGULAR);
        loan.setLoan(BigDecimal.valueOf(1000));
        loan.setOutstanding(loan.getLoan());
        loan.setNextExpiry(new Date());
        return loan;
    }

    private Ledger getCompanyCashLedger(Long id) {
        Ledger ledger = new Ledger();
        ledger.setId(id);
        ledger.setAmount(BigDecimal.ZERO);
        ledger.setAccount(Constants.COMPANY_CASH_LEDGER_ACCOUNT);
        ledger.setBusiType(Constants.BUSINESS_TYPE_RECEIVABLES);
        ledger.setAcctStatus(Constants.ACCOUNT_STATUS_REGULAR);
        return ledger;
    }

    private Ledger getCompanyLedger(Long id) {
        Ledger ledger = new Ledger();
        ledger.setId(id);
        ledger.setAmount(BigDecimal.ZERO);
        ledger.setAccount(Constants.COMPANY_LEDGER_ACCOUNT);
        ledger.setBusiType(Constants.BUSINESS_TYPE_RECEIVABLES);
        ledger.setAcctStatus(Constants.ACCOUNT_STATUS_REGULAR);
        return ledger;
    }

    private BusinessInfo getBusinessInfo() {
        BusinessInfo businessInfo = new BusinessInfo();
        businessInfo.setId(1L);
        businessInfo.setMessageSequence(messageSequence);
        return businessInfo;
    }

    @Test
    public void testGenerateAccount() {
        accountManagerService = new AccountManagerService();
        String totalAccount = "1000000000000001";
        int nextSeqNo = 1;
        Set<String> existAcct = new HashSet<String>(Arrays.asList("10000000000000010001", "10000000000000010002"));
        String result = accountManagerService.generateAccount(totalAccount, existAcct, nextSeqNo);
        assertEquals("10000000000000010003", result);
    }

    @Test
    public void testCreateAccount_fail() {
        Datagram datagram = preData();
        PowerMockito.when(customerDao.findUniqueBy("totalAcct", ((LedgerVo) datagram.getDatagramBody()).getTotalAccountId())).thenReturn(null);
        ledgerService.createAccount(datagram);
        assertEquals(Constants.PROCESS_STATUS_FAIL, datagram.getDatagramBody().getOperateCode());
        assertThat(datagram.getDatagramBody().getOperateCode(), notNullValue());
        assertEquals(PropertiesReader.readAsString("customer.finance.not.open"), datagram.getDatagramBody().getMemo());
    }

    @Test
    public void testCreateAccount() throws Exception {
        Datagram datagram = preData();
        LedgerVo ledgerVo = (LedgerVo) datagram.getDatagramBody();
        Customer customer = new Customer();
        PowerMockito.when(customerDao.findUniqueBy("totalAcct", ledgerVo.getTotalAccountId())).thenReturn(customer);
        Ledger ledger = PowerMockito.mock(Ledger.class);
        PowerMockito.whenNew(Ledger.class).withNoArguments().thenReturn(ledger);
        int i = 1;
        PowerMockito.when(ledgerDao.getRowCountBy(ledgerVo.getTotalAccountId())).thenReturn(i);
        Set<String> sets = new HashSet<String>(Arrays.asList("11"));
        PowerMockito.when(ledgerDao.queryLedgerAccountBy(ledgerVo.getTotalAccountId())).thenReturn(sets);
        PowerMockito.when(accountManagerService.generateAccount(ledgerVo.getTotalAccountId(), sets, i + 1)).thenReturn("account000");
        PowerMockito.doNothing().when(sysglService).maintainMainAccount();
        ledgerService.createAccount(datagram);
        assertEquals(Constants.PROCESS_STATUS_OK, datagram.getDatagramBody().getOperateCode());
    }

    @Test(expected = BusinessException.class)
    public void testDisableLedger_fail() {
        Datagram datagram = new Datagram();
        LedgerDisableVo vo = new LedgerDisableVo();
        vo.setAccount("4234234324324");
        datagram.setDatagramBody(vo);
        PowerMockito.when(ledgerDao.findUniqueBy("account", vo.getAccount())).thenReturn(null);
        try {
            ledgerService.disableLedger(datagram);
        } catch (BusinessException e) {
            assertEquals(Constants.PROCESS_STATUS_FAIL, e.getCode());
            throw e;
        }
    }

    @Test
    public void testDisableLedger() {
        Datagram datagram = new Datagram();
        LedgerDisableVo vo = new LedgerDisableVo();
        vo.setAccount("4234234324324");
        datagram.setDatagramBody(vo);
        Ledger ledger = PowerMockito.mock(Ledger.class);
        PowerMockito.when(ledgerDao.findUniqueBy("account", vo.getAccount())).thenReturn(ledger);
        ledgerService.disableLedger(datagram);
        assertEquals(Constants.PROCESS_STATUS_OK, vo.getOperateCode());
    }

    private static Datagram preData() {
        Datagram datagram = new Datagram();
        DatagramHeader header = new DatagramHeader();
        header.setMessageCode(Constants.MESSAGE_BUSINESS_TYPE_FINANCING);
        LedgerVo ledgerVo = new LedgerVo();
        ledgerVo.setTotalAccountId("13434");
        datagram.setDatagramBody(ledgerVo);
        datagram.setDatagramHeader(header);
        return datagram;
    }

    @Test
    public void testChargeFee() {
        Ledger companyCashLedger = this.getCompanyCashLedger(2L);
        companyCashLedger.setDetailValue(Subject.LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE, new BigDecimal("200"));
        Ledger companyLedger = this.getCompanyLedger(2L);
        companyLedger.setDetailValue(Subject.LEDGER_DETAIL_TYPE_RECHARGECOMMISSION_INCOME, new BigDecimal("100"));

        Long businessId = 123L;
        String debitSubject = Subject.LEDGER_DETAIL_TYPE_RECHARGECOMMISSION_EXPENSENSE;
        String creditSubject = Subject.LEDGER_DETAIL_TYPE_RECHARGECOMMISSION_INCOME;
        String entryNo = EntryNo.RECHARGE_AMOUNT;
        BigDecimal fee = new BigDecimal("144");
        Ledger ledger = new Ledger();
        ledger.setId(250);
        ledger.setAccount("00000001000000020001");
        // ledger.setCustomer(customer);
        ledger.setAcctStatus("1");
        ledger.setAmount(new BigDecimal(560));
        ledger.setBusiType("1");
        when(sequenceDao.nextFlowGroupNo()).thenReturn(1L);
        ledgerService.chargeFee(companyCashLedger, companyLedger, businessId, fee, null, ledger, debitSubject, creditSubject, entryNo);
        Assert.assertEquals(new BigDecimal("56"), companyCashLedger.getDetailValue(Subject.LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE));
        Assert.assertEquals(new BigDecimal("416"), ledger.getAmount());
        Assert.assertEquals(new BigDecimal("144"), ledger.getDetailValue(Subject.LEDGER_DETAIL_TYPE_RECHARGECOMMISSION_EXPENSENSE));
        Assert.assertEquals(new BigDecimal("244"), companyLedger.getDetailValue(Subject.LEDGER_DETAIL_TYPE_RECHARGECOMMISSION_INCOME));

    }

    @Test
    public void testChargeFee_null() {
        Ledger companyCashLedger = this.getCompanyCashLedger(2L);
        companyCashLedger.setDetailValue(Subject.LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE, new BigDecimal("200"));
        Ledger companyLedger = this.getCompanyLedger(2L);
        companyLedger.setDetailValue(Subject.LEDGER_DETAIL_TYPE_RECHARGECOMMISSION_INCOME, new BigDecimal("100"));
        Long businessId = 123L;
        String debitSubject = Subject.LEDGER_DETAIL_TYPE_RECHARGECOMMISSION_EXPENSENSE;
        String creditSubject = Subject.LEDGER_DETAIL_TYPE_RECHARGECOMMISSION_INCOME;
        String entryNo = EntryNo.RECHARGE_AMOUNT;
        BigDecimal fee = new BigDecimal("144");
        Ledger ledger = new Ledger();
        ledger.setId(250);
        ledger.setAccount("00000001000000020001");
        ledger.setAcctStatus("1");
        ledger.setAmount(new BigDecimal(560));
        ledger.setBusiType("1");
        when(sequenceDao.nextFlowGroupNo()).thenReturn(1L);
        when(ledgerDao.loadByAccount(Constants.COMPANY_LEDGER_ACCOUNT)).thenReturn(companyLedger);
        when(ledgerDao.loadByAccount(Constants.COMPANY_CASH_LEDGER_ACCOUNT)).thenReturn(companyCashLedger);
        ledgerService.chargeFee(null, null, businessId, fee, null, ledger, debitSubject, creditSubject, entryNo);
        Assert.assertEquals(new BigDecimal("56"), companyCashLedger.getDetailValue(Subject.LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE));
        Assert.assertEquals(new BigDecimal("416"), ledger.getAmount());
        Assert.assertEquals(new BigDecimal("144"), ledger.getDetailValue(Subject.LEDGER_DETAIL_TYPE_RECHARGECOMMISSION_EXPENSENSE));
        Assert.assertEquals(new BigDecimal("244"), companyLedger.getDetailValue(Subject.LEDGER_DETAIL_TYPE_RECHARGECOMMISSION_INCOME));

    }

    @Test(expected = BusinessException.class)
    public void testChargeManagementCost_fail() {
        ChargeManagementCostVo vo = new ChargeManagementCostVo();
        vo.setAccount("001");
        vo.setManagementCostAmount(BigDecimal.ONE);
        Ledger ledger = new Ledger();
        ledger.setAcctStatus(ACCOUNT_STATUS_REGULAR);
        ledger.setAmount(BigDecimal.valueOf(100));

        Ledger companyCashLedger = new Ledger();
        companyCashLedger.setDetailValue(LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE, BigDecimal.ZERO);
        Mockito.when(ledgerDao.findUniqueBy("account", vo.getAccount())).thenReturn(ledger);
        Mockito.when(ledgerDao.loadByAccount(COMPANY_CASH_LEDGER_ACCOUNT)).thenReturn(companyCashLedger);
        Mockito.when(businessInfoService.findByMessageSequence(messageSequence)).thenReturn(1L);
        try {
            ledgerService.chargeManagementCost(vo,  0L);
            Assert.fail();
        } catch (BusinessException e) {
            assertEquals(PROCESS_STATUS_FAIL, e.getCode());
            throw e;
        }
    }

    @Test
    public void testChargeManagementCost() {
        ChargeManagementCostVo vo = new ChargeManagementCostVo();
        vo.setAccount("001");
        vo.setManagementCostAmount(BigDecimal.ONE);
        Ledger ledger = new Ledger();
        ledger.setAcctStatus(ACCOUNT_STATUS_REGULAR);
        ledger.setAmount(BigDecimal.valueOf(100));
        ledger.setDetailValue(MANAGEMENT_FEE_EXPENSENSE, BigDecimal.valueOf(2));

        Ledger companyCashLedger = new Ledger();
        companyCashLedger.setDetailValue(LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE, BigDecimal.valueOf(100));

        Ledger companyLedger = new Ledger();
        Mockito.when(ledgerDao.findUniqueBy("account", vo.getAccount())).thenReturn(ledger);
        Mockito.when(ledgerDao.loadByAccount(COMPANY_CASH_LEDGER_ACCOUNT)).thenReturn(companyCashLedger);
        Mockito.when(businessInfoService.findByMessageSequence(messageSequence)).thenReturn(1L);
        Mockito.when(ledgerDao.loadByAccount(COMPANY_LEDGER_ACCOUNT)).thenReturn(companyLedger);
        ledgerService.chargeManagementCost(vo,  0L);
        assertTrue(companyCashLedger.getDetailValue(LEDGER_DETAIL_TYPE_ITEMRECEIVEDADVANCE).compareTo(BigDecimal.valueOf(99)) == 0);
        assertTrue(ledger.getAmount().compareTo(BigDecimal.valueOf(99)) == 0);
        assertTrue(ledger.getDetailValue(MANAGEMENT_FEE_EXPENSENSE).compareTo(BigDecimal.valueOf(3)) == 0);
        assertTrue(companyLedger.getDetailValue(MANAGEMENT_FEE_INCOME).compareTo(BigDecimal.valueOf(1)) == 0);
        verify(flowDao, times(4)).save(any(Flow.class));

    }
}
