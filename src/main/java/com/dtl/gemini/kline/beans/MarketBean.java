package com.dtl.gemini.kline.beans;

import lombok.Data;

/**
 * @author DTL
 * @date 2020/4/11
 **/
@Data
public class MarketBean {
    private int id;
    private String currencyId;
    private String currency;
    private String price;
    private String gains;
    private int type;
    private double gain;
    private String hight;
    private String low;
    private String vol;
    private int check;//0：未选择，1：已选择

    public MarketBean(int id, String currencyId, String currency, int type) {
        this.id = id;
        this.currencyId = currencyId;
        this.currency = currency;
        this.type = type;
    }

    public MarketBean(int id, String currency, String currencyId, String price, String gains) {
        this.id = id;
        this.currencyId = currencyId;
        this.currency = currency;
        this.price = price;
        this.gains = gains;
    }

    public MarketBean(String currency, String currencyId, String price, String gains, double gain, String hight, String low, String vol) {
        this.currencyId = currencyId;
        this.currency = currency;
        this.price = price;
        this.gain = gain;
        this.gains = gains;
        this.hight = hight;
        this.low = low;
        this.vol = vol;
    }
}
