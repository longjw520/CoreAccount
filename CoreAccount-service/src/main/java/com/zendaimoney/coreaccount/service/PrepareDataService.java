package com.zendaimoney.coreaccount.service;

import com.zendaimoney.coreaccount.constants.Constants;
import com.zendaimoney.coreaccount.dao.FlowInterestDao;
import com.zendaimoney.coreaccount.dao.LedgerDetailDao;
import com.zendaimoney.coreaccount.dao.SequenceDao;
import com.zendaimoney.coreaccount.util.Arith;
import com.zendaimoney.coreaccount.util.DateUtils;
import com.zendaimoney.coreaccount.util.SystemUtil;
import com.zendaimoney.coreaccount.vo.CalculateInterestData;
import com.zendaimoney.coreaccount.vo.EntryInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zendaimoney.coreaccount.constants.Constants.DORC_TYPE_CREDIT;
import static com.zendaimoney.coreaccount.constants.Constants.DORC_TYPE_DEBIT;
import static com.zendaimoney.coreaccount.constants.EntryNo.CALCULATE_ACCRUAL;
import static com.zendaimoney.coreaccount.constants.Subject.*;
import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * Author: kimi
 * Date: 14-11-5
 * Time: 上午10:57
 */
@Named
public class PrepareDataService {
    private static Logger logger = LoggerFactory.getLogger(PrepareDataService.class);

    private final String UPDATE_LEDGER_LOAN = "UPDATE ac_t_ledger_loan SET interest_payable=?,amount_spare=? WHERE id=?";

    private final String UPDATE_LEDGER_FINANCE = "UPDATE ac_t_ledger_finance SET interest_receivable=?,interest_deviation=?,last_modified=sysdate WHERE id=?";

    private final String UPDATE_LEDGER_DETAIL = "UPDATE ac_t_ledger_detail SET detail_value=? WHERE ledger_id=? and type=?";

    private final String INSERT_LEDGER_DETAIL = "INSERT INTO ac_t_ledger_detail(id,ledger_id,type,detail_value) values(SEQ_AC_T_LEDGER_DETAIL.NEXTVAL,?,?,?)";

    @Inject
    private LedgerLoanService ledgerLoanService;
    @Inject
    private FlowInterestDao flowInterestDao;
    @Inject
    private SequenceDao sequenceDao;
    @Inject
    private LedgerDetailDao ledgerDetailDao;


    public void calculateInterest(long businessId) {
        List<Map<String, Object>> list = queryData();
        logger.info("计息前");
        updateLoanAndFinance(list, businessId);
        logger.info("计息后");
    }

    /**
     * 拼写sql
     *
     * @return String
     * @author kimi
     * @since 2014-11-6
     */
    public String prepareSql() {
        StringBuffer sBuffer = new StringBuffer();
        sBuffer.append("select");
        sBuffer.append(" t.id as loanId,t.ledger_id as loanLedgerId,t.interest_payable as interestPayable,t.curr_num as currNum,t.outstanding as outStanding,t.rate as rate,t.next_expiry as nextExpiry,t.interest_start as interestStart,t.last_expiry as lastExpiry,");
        sBuffer.append("(select l.account  from ac_t_ledger l where l.id = t.ledger_id) as loanAccount,");
        sBuffer.append("(select d.detail_value from ac_t_ledger_detail d where d.ledger_id = t.ledger_id and d.type='2002' ) as type_2002,");
        sBuffer.append(" f.id as financeId,f.ledger_id as financeLedgerId,f.interest_receivable as interestReceivable,nvl(f.frozen_porportion, 0) + nvl(f.debt_proportion, 0) as proportionSum,f.interest_deviation as interestDeviation,");
        sBuffer.append("(select l.account   from ac_t_ledger l  where l.id = f.ledger_id) as financeAccount,");
        sBuffer.append("(select d.detail_value  from ac_t_ledger_detail d where d.ledger_id = f.ledger_id and d.type='1005' ) as type_1005,");
        sBuffer.append("(select d.detail_value  from ac_t_ledger_detail d where d.ledger_id = f.ledger_id and d.type='3034' ) as type_3034,");
        sBuffer.append(" pp.REPAY_DAY as repayDay,pp.INTEREST_AMT as interestAmt");
        sBuffer.append(" from AC_T_LEDGER_LOAN t,AC_T_LEDGER_FINANCE f,");
        sBuffer.append(" (SELECT *");
        sBuffer.append(" FROM (SELECT t1.repay_day,t1.LOAN_ID,t1.INTEREST_AMT,t1.CURR_NUM,ROW_NUMBER() OVER(PARTITION BY t1.LOAN_ID ORDER BY t1.REPAY_DAY) AS code_id");
        sBuffer.append(" FROM AC_T_REPAYMENT_PLAN t1");
        sBuffer.append(" WHERE t1.REPAY_DAY >=");
        sBuffer.append("to_date('");
        sBuffer.append(DateFormatUtils.format(new Date(), Constants.DATE_FORMAT));
        sBuffer.append("','yyyy-mm-dd')");
        sBuffer.append(")");
        sBuffer.append(" WHERE code_id = 1) pp");
        sBuffer.append(" where t.id = f.loan_id");
        sBuffer.append(" and t.id = pp.loan_id");
        sBuffer.append(" and f.acct_status in (1, 3, 4)");
        sBuffer.append(" and t.acct_status in (1, 3, 4)");
        sBuffer.append(" order by t.id, f.id");
        return sBuffer.toString();
    }
    
    public static void main(String[] args) {
		System.out.println(new PrepareDataService().prepareSql());
	}

    public List<Map<String, Object>> queryData() {
        return flowInterestDao.getSession().createSQLQuery(prepareSql()).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
    }

    /**
     * 更新贷款分户和理财分户
     *
     * @param list
     * @param businessId
     * @author kimi
     * @since 2014-11-20
     */
    public void updateLoanAndFinance(List<Map<String, Object>> list, long businessId) {
        if (CollectionUtils.isNotEmpty(list)) {
            logger.info("计息开始。。。。。。");
            Long tempLoanId = 0L;
            BigDecimal nowPv = null;
            int batchSize = 0;
            CalculateInterestData data;
            Map<Long, Map<String, BigDecimal>> mapDetail = new HashMap<Long, Map<String, BigDecimal>>();
            for (Map map : list) {
                batchSize++;
                data = buildCalculateInterestData(map);
                if (data.getLoanId() != tempLoanId) {
                    nowPv = ledgerLoanService.calculatePV(false, data.getRate(), data.getLoanId(), DateFormatUtils.format(new Date(), Constants.DATE_FORMAT));
                    //更新债权
                    flowInterestDao.batchSQLExecute(UPDATE_LEDGER_LOAN, interestPayable(data), nowPv, data.getLoanId());
                }
                tempLoanId = data.getLoanId();
                postProcessCalculateInterestData(generateTodayInterestDetail(data, nowPv), data);
                //更新理财
                flowInterestDao.batchSQLExecute(UPDATE_LEDGER_FINANCE, interestReceivable(data, nowPv), data.getInterestDeviation().add(data.getInterestDayDeviation()), data.getFinanceId());
                //写流水
                processFlows(businessId, data);
                fillDetailMap(mapDetail, data.getFinanceLedgerId(), OVERDUEINTEREST_INTERESTRECEIVABLE,
                        data.getType_1005(), data.getCreditorInterestDay());
                fillDetailMap(mapDetail, data.getLoanLedgerId(), LEDGER_DETAIL_TYPE_PAYABLE,
                        data.getType_2002(), data.getBorrowerInterestDayDetail());
                fillDetailMap(mapDetail, data.getFinanceLedgerId(), LEDGER_DETAIL_TYPE_DEVIATION,
                        data.getType_3034(), data.getInterestDayDeviation());

                batchFlushAndClear(batchSize, 200, "【流水】");
            }
            logger.info("开始更新明细。。。");
            updateOrSaveLedgerDetails(mapDetail);
            logger.info("计息结束。。。。。。");
            logger.info("一共执行了：" + batchSize + "次");
        }
    }

    /**
     * 填充明细表的会计科目
     *
     * @param mapDetail
     * @param ledgerId
     * @param type
     * @param origin
     * @param tradeAmount
     * @author czb
     * @since 2014-11-24
     */
    private void fillDetailMap(Map<Long, Map<String, BigDecimal>> mapDetail, Long ledgerId, String type, BigDecimal origin, BigDecimal tradeAmount) {
        if (mapDetail.containsKey(ledgerId)) {
            Map<String, BigDecimal> tempMap = mapDetail.get(ledgerId);
            if (tempMap.containsKey(type)) {
                tempMap.put(type, tempMap.get(type).add(tradeAmount));
            } else {
                tempMap.put(type, origin.add(tradeAmount));
            }
        } else {
            Map<String, BigDecimal> tempMap = new HashMap<String, BigDecimal>();
            tempMap.put(type, origin.add(tradeAmount));
            mapDetail.put(ledgerId, tempMap);
        }
    }

    /**
     * 算出当天增量的利息后加工CalculateInterestData
     *
     * @param results
     * @param data
     * @author kimi
     * @since 2014-11-13
     */
    private void postProcessCalculateInterestData(BigDecimal[] results, CalculateInterestData data) {
        data.setCreditorInterestDay(results[0]);
        data.setBorrowerInterestDayDetail(results[1]);
        data.setInterestDayDeviation(results[2]);
    }

    /**
     * 组装CalculateInterestData
     *
     * @return CalculateInterestData
     */
    private CalculateInterestData buildCalculateInterestData(Map<String, Object> map) {
        CalculateInterestData data = new CalculateInterestData();
        data.setLoanId(((BigDecimal) map.get("LOANID")).longValue());
        data.setLoanLedgerId(((BigDecimal) map.get("LOANLEDGERID")).longValue());
        data.setInterestPayable((BigDecimal) map.get("INTERESTPAYABLE"));
        data.setCurrNum(((BigDecimal) map.get("CURRNUM")).longValue());
        data.setOutStanding((BigDecimal) map.get("OUTSTANDING"));
        data.setRate((BigDecimal) map.get("RATE"));
        data.setNextExpiry((Date) map.get("NEXTEXPIRY"));
        data.setInterestStart((Date) map.get("INTERESTSTART"));
        data.setLastExpiry((Date) map.get("LASTEXPIRY"));
        data.setLoanAccount((String) map.get("LOANACCOUNT"));
        data.setType_2002(map.get("TYPE_2002") == null ? BigDecimal.ZERO : new BigDecimal((String) map.get("TYPE_2002")));
        data.setFinanceId(((BigDecimal) map.get("FINANCEID")).longValue());
        data.setFinanceLedgerId(((BigDecimal) map.get("FINANCELEDGERID")).longValue());
        data.setInterestReceivable((BigDecimal) map.get("INTERESTRECEIVABLE"));
        data.setProportionSum((BigDecimal) map.get("PROPORTIONSUM"));
        data.setInterestDeviation((BigDecimal) map.get("INTERESTDEVIATION"));
        data.setFinanceAccount((String) map.get("FINANCEACCOUNT"));
        data.setType_1005(map.get("TYPE_1005") == null ? BigDecimal.ZERO : new BigDecimal((String) map.get("TYPE_1005")));
        data.setType_3034(map.get("TYPE_3034") == null ? BigDecimal.ZERO : new BigDecimal((String) map.get("TYPE_3034")));
        data.setRepayDay((Date) map.get("REPAYDAY"));
        data.setInterestAmt((BigDecimal) map.get("INTERESTAMT"));
        return data;
    }

    /**
     * @param data
     * @param nowPv
     * @return 日利息信息
     * @author kimi
     * @since 2014-11-10
     */
    private BigDecimal[] generateTodayInterestDetail(CalculateInterestData data, BigDecimal nowPv) {
        BigDecimal[] results = new BigDecimal[3];
        BigDecimal creditorInterestDay = interestReceivable(data, nowPv).subtract(data.getInterestReceivable()).setScale(7, ROUND_HALF_UP);
        BigDecimal borrowerInterestDayDetail = (interestPayable(data).subtract(data.getInterestPayable())).multiply(data.getProportionSum()).setScale(7, ROUND_HALF_UP);
        BigDecimal interestDayDeviation = borrowerInterestDayDetail.subtract(creditorInterestDay).setScale(7, ROUND_HALF_UP);
        results[0] = creditorInterestDay;
        results[1] = borrowerInterestDayDetail;
        results[2] = interestDayDeviation;
        return results;
    }

    /**
     * 处理流水
     *
     * @param businessId
     * @param data
     * @author kimi
     */
    private void processFlows(long businessId, CalculateInterestData data) {
        String financeIdStr = String.valueOf(data.getFinanceId());
        long flowGroupNo = sequenceDao.nextFlowGroupNo();
        flowInterestDao.save(new EntryInfo(data.getFinanceAccount(), OVERDUEINTEREST_INTERESTRECEIVABLE, data.getCreditorInterestDay(), CALCULATE_ACCRUAL, data.getType_1005()).calculateEndBalance(EntryInfo.OPERATOR.ADD).init(businessId, financeIdStr, getFlowNo(), flowGroupNo, DORC_TYPE_DEBIT));
        flowInterestDao.save(new EntryInfo(data.getLoanAccount(), LEDGER_DETAIL_TYPE_PAYABLE, data.getBorrowerInterestDayDetail(), CALCULATE_ACCRUAL, data.getType_2002()).calculateEndBalance(EntryInfo.OPERATOR.ADD).init(businessId, financeIdStr, getFlowNo(), flowGroupNo, DORC_TYPE_CREDIT));
        flowInterestDao.save(new EntryInfo(data.getFinanceAccount(), LEDGER_DETAIL_TYPE_DEVIATION, data.getInterestDayDeviation(), CALCULATE_ACCRUAL, data.getType_3034()).calculateEndBalance(EntryInfo.OPERATOR.ADD).init(businessId, financeIdStr, getFlowNo(), flowGroupNo, DORC_TYPE_DEBIT));
    }


    private void batchFlushAndClear(int currentNum, int batchSize, String type) {
        if (currentNum % batchSize == 0) {
        	logger.info(type + "当前执行了：" + currentNum + "次");
            flowInterestDao.flush();
            flowInterestDao.clear();
        }
    }

    /**
     * 根据ledgerId组装明细科目
     *
     * @param mapDetail
     * @author czb
     * @since 2014-11-24
     */
    private void updateOrSaveLedgerDetails(Map<Long, Map<String, BigDecimal>> mapDetail) {
        int batchSize = 0;
        for (Long ledgerId : mapDetail.keySet()) {
            for (String type : mapDetail.get(ledgerId).keySet()) {
                BigDecimal detailValue = mapDetail.get(ledgerId).get(type);
                if (0 == ledgerDetailDao.batchSQLExecute(UPDATE_LEDGER_DETAIL, detailValue, ledgerId, type)) {
                    ledgerDetailDao.batchSQLExecute(INSERT_LEDGER_DETAIL, ledgerId, type, detailValue);
                }
                batchSize++;
                batchFlushAndClear(batchSize, 300, "【明细科目】");
            }
        }
    }

    /**
     * @param data
     * @return 这笔投资的应收利息
     */
    private BigDecimal interestReceivable(CalculateInterestData data, BigDecimal pv) {
        return (pv.subtract(data.getOutStanding())).multiply(data.getProportionSum());
    }


    /**
     * @param data
     * @return 借款人应付利息
     */
    private BigDecimal interestPayable(CalculateInterestData data) {
        BigDecimal newInterestPayable;
        Date appDate = SystemUtil.currentDate();
        if (data.getCurrNum() == 0) {
            newInterestPayable = Arith.mul(Arith.div(data.getInterestAmt(), new BigDecimal(DateUtils.getDayCount(data.getInterestStart(), data.getNextExpiry()))), new BigDecimal(DateUtils.getDayCount(data.getInterestStart(), appDate)));
        } else {
            newInterestPayable = Arith.mul(Arith.div(data.getInterestAmt(), new BigDecimal(DateUtils.getDayCount(data.getLastExpiry(), data.getNextExpiry()))), new BigDecimal(DateUtils.getDayCount(data.getLastExpiry(), appDate)));
        }
        return newInterestPayable;
    }

    /**
     * 生成流水号
     */
    private String getFlowNo() {
        return sequenceDao.nextFlowNO();
    }
}
