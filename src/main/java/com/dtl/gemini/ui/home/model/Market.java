package com.dtl.gemini.ui.home.model;

import lombok.Data;

@Data
public class Market {

    int tokenId;
    /**
     * 币种
     */
    String currency;
    /**
     * 成交量
     */
    String vol;
    /**
     * 美元价格
     */
    String us;
    /**
     * 人民币价格
     */
    String cny;
    /**
     * 涨跌额
     */
    double gain;
    /**
     * 涨跌幅
     */
    String gains;

    /**
     * 1：双仓，2：自由
     */
    private int mode;

    public Market() {
    }

    public Market(String currency, String vol, String us, String cny, double gain, String gains) {
        this.currency = currency;
        this.vol = vol;
        this.cny = cny;
        this.us = us;
        this.gain = gain;
        this.gains = gains;
    }
}
