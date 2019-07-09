package com.zendaimoney.coreaccount.rmi.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 取现020005
 */
public class EnchashmentVo extends DatagramBody implements Serializable {

    private static final long serialVersionUID = 1L;
    /** 分账号 */
    @NotBlank
    @Size(max = 20)
    private String account;
    /** 取现金额 */
    @NotNull
    @Digits(integer = 15, fraction = 7)
    @Min(0)
    private BigDecimal amount;
    /** 手续费金额 */
    @NotNull
    @Digits(integer = 15, fraction = 7)
    @Min(0)
    private BigDecimal chargeAmount;
    /** 取现金额备注 */
    @Size(max = 50)
    private String enchashmentMemo;
    /** 取现手续费备注 */
    @Size(max = 50)
    private String chargeMemo;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public String getEnchashmentMemo() {
        return enchashmentMemo;
    }

    public void setEnchashmentMemo(String enchashmentMemo) {
        this.enchashmentMemo = enchashmentMemo;
    }

    public String getChargeMemo() {
        return chargeMemo;
    }

    public void setChargeMemo(String chargeMemo) {
        this.chargeMemo = chargeMemo;
    }

}
