package com.dtl.gemini.ui.asset.model;

import lombok.Data;

/**
 * @author DTL
 * @date 2020/5/12
 **/
@Data
public class ExchangeSys {
    /**
     * 兑换汇率
     */
    private double exchangeRate;
    /**
     * 兑换手续费比例
     */
    private double exchangeFeeRatio;
    /**
     * 最小兑换数量
     */
    private double minExchangeAmount;
    /**
     * 每日兑换额度
     */
    private double dailyRedemptionQuota;
    /**
     * 每日兑换次数
     */
    private int numberRedemptionsPerDay;
    /**
     * 兑换交易对
     */
    private String symbol;
}
