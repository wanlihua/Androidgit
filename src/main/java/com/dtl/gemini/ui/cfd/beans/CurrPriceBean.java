package com.dtl.gemini.ui.cfd.beans;

import com.dtl.gemini.bean.BaseBean;

import java.math.BigDecimal;

/**
 * @author DTL
 * @date 2020/5/15
 **/
public class CurrPriceBean extends BaseBean {

    /**
     * massage : 成功
     * data : 66534.23
     */

    private BigDecimal data;

    public BigDecimal getData() {
        return data;
    }

    public void setData(BigDecimal data) {
        this.data = data;
    }
}
