package com.dtl.gemini.ui.asset.model;

import lombok.Data;

/**
 * @author DTL
 * @date 2020/4/29
 **/
@Data
public class Exchange {
    private String id;
    private String appUserId;
    /**
     * 汇率
     */
    private double rate;
    /**
     * 手续费率
     */
    private double fee;
    /**
     * 手续费
     */
    private double feeAmount;
    /**
     * 兑换的币种
     */
    private String exchangeCurrency;
    /**
     * 获得的币种
     */
    private String receiveCurrency;
    /**
     * 交易对 例：BTCUSDT
     */
    private String symbol;
    /**
     * 兑换的币种数量
     */
    private double exchangeAmount;
    /**
     * 获得的币种数量
     */
    private double receiveAmount;
    private String createDateTime;
}
