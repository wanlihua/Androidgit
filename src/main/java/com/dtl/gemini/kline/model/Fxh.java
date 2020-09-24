package com.dtl.gemini.kline.model;

import java.util.List;

import lombok.Data;

/**
 * 大图灵
 * 2020/3/3
 **/
@Data
public class Fxh {
    /**
     * current_price : 61971.55
     * current_price_usd : 8900.89
     * code : bitcoin
     * name : BTC
     * fullname : 比特币
     * logo : https://s1.bqiapp.com/coin/20181030_72_webp/bitcoin_200_200.webp?v=20
     * change_percent : 3.04
     * market_value : 1.130831524361E12
     * vol : 9.169329915E10
     * supply : 1.8247575E7
     * rank : 1
     * star_level : 1
     * kline_data : 9596.25,9547.81,9355.38,9390.94,9183.83,9194.58,8796.26,8840.90,8727.82,8842.62,8962.17,8812.49,8836.99,8649.09,8635.18,8785.44,8768.68,8667.44,8643.77,8682.65,8600.96,8596.49,8518.06,8579.09,8678.79,8760.64,8893.92,8903.95
     * market_value_usd : 1.62419786907E11
     * vol_usd : 1.3169783286E10
     * marketcap : 1.62419786907E11
     * high_price : 20089.0
     * drop_ath : -55.69
     * low_price : 65.526
     * high_time : 2017-12-17
     * low_time : 2013-07-05
     * isifo : 0
     * ismineable : 1
     * ads : ["MXC;https://www.mxc.io/;https://s1.bqiapp.com/image/20190308/mexc_mid.png","A网;https://aofex.com/;https://s1.bqiapp.com/image/20190611/aofex_mid.png"]
     * adpairs : ["",""]
     * turnoverrate : 8.11
     * changerate_utc : 3.85
     * changerate_utc8 : 0.58
     */

    private double current_price;//价格-人民币
    private double current_price_usd;//价格-美元
    private String code;
    private String name;
    private String fullname;
    private String logo;
    private double change_percent;
    private double market_value;//24H涨幅
    private double vol;
    private double supply;
    private int rank;
    private int star_level;
    private String kline_data;
    private double market_value_usd;
    private double vol_usd;
    private double marketcap;
    private double high_price;
    private double drop_ath;
    private double low_price;
    private String high_time;
    private String low_time;
    private int isifo;
    private int ismineable;
    private double turnoverrate;
    private double changerate_utc;//国际时间今日涨幅
    private double changerate_utc8;//UTC-8涨幅今日
    private List<String> ads;
    private List<String> adpairs;
}
