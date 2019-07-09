package com.zendaimoney.coreaccount.rmi.vo;

import java.io.Serializable;

/**
 * 返回查询回款金额和资产价值
 *
 * @author HaoChen
 * @version $Id$
 */
public class ReturnValueVo extends DatagramBody implements Serializable {

    private static final long serialVersionUID = 5064645994902274029L;
    private Double repayAmount;
    private Double assetValue;
    private Double payAmt;

    public Double getRepayAmount() {
        return repayAmount;
    }

    public void setRepayAmount(Double repayAmount) {
        this.repayAmount = repayAmount;
    }

    public Double getAssetValue() {
        return assetValue;
    }

    public void setAssetValue(Double assetValue) {
        this.assetValue = assetValue;
    }

    public Double getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(Double payAmt) {
        this.payAmt = payAmt;
    }
}
