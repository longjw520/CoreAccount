package com.zendaimoney.coreaccount.service;

import com.zendaimoney.coreaccount.entity.LedgerLoan;
import com.zendaimoney.coreaccount.rmi.vo.ExpiryInterestVo;
import com.zendaimoney.coreaccount.rmi.vo.QueryLedgerLoanVo;
import com.zendaimoney.coreaccount.rmi.vo.RepaymentVo;
import org.springside.modules.orm.Page;

import javax.inject.Inject;
import javax.inject.Named;

import static com.zendaimoney.coreaccount.constants.Constants.*;

/**
 * Copyright (c) 2014 ZENDAI. All Rights Reserved. This software is published
 * under the terms of the ZENDAI Software
 *
 * @author chen.hao
 * @mail haoc@zendaimoney.com
 * @date: 2014/12/16 20:56
 */
@Named
public class ExpiryInterestService {
    @Inject
    private LedgerLoanService ledgerLoanService;
    @Inject
    private RepaymentPlanService repaymentPlanService;

    public void expiry(ExpiryInterestVo expiryInterestVo, long businessId) {
        String[] accountStatus = new String[] { ACCOUNT_STATUS_REGULAR, ACCOUNT_STATUS_OVERDUE, ACCOUNT_STATUS_IDLE};
        QueryLedgerLoanVo queryLedgerLoanVo = new QueryLedgerLoanVo();
        queryLedgerLoanVo.setAcctStatusArray(accountStatus);
        queryLedgerLoanVo.setPageSize(Integer.MAX_VALUE);
        queryLedgerLoanVo.setNextExpiry(expiryInterestVo.getPayDate());
        Page<LedgerLoan> page = ledgerLoanService.queryBy(queryLedgerLoanVo, false);
        for (LedgerLoan loan : page) {
            RepaymentVo repaymentVo = new RepaymentVo();
            repaymentVo.setLoanId(loan.getId());
            repaymentVo.setPayDate(expiryInterestVo.getPayDate());
            repaymentPlanService.repayment(repaymentVo, businessId);
        }
    }
}
