package com.zendaimoney.coreaccount.rmi.vo;

import com.zendaimoney.coreaccount.rmi.annotation.DateTimeFormat;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Copyright (c) 2014 ZENDAI. All Rights Reserved. This software is published
 * under the terms of the ZENDAI Software
 *
 * @author chen.hao
 * @mail haoc@zendaimoney.com
 * @date: 2014/12/16 20:56
 */
public class ExpiryInterestVo extends DatagramBody implements Serializable {
    /** 还款日期 */
    @NotBlank
    @Size(max = 10)
    @DateTimeFormat
    private String payDate;

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }
}
