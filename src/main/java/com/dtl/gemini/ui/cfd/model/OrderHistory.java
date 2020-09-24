package com.dtl.gemini.ui.cfd.model;

import lombok.Data;

/**
 * @author DTL
 * @date 2020/5/15
 **/
@Data
public class OrderHistory {
    private String id;
    /**
     * 1,"委托中，没有平仓" 2,"已撤单" 3,"已平仓",4,"已爆仓",5：止盈6：止损
     */
    private int status;
    /**
     * 1、开多   2、开空
     */
    private int trend;
    /**
     * 倍数
     */
    private int multiple;
    /**
     * 币种
     */
    private String currency;
    /**
     * 保证金
     */
    private double orderBalance;
    /**
     * 买入数量
     */
    private double orderAmount;
    /**
     * 1:极速开仓,2:计划开仓
     */
    private int startType;
    /**
     * 无用参数
     */
    private double startUnitPrice;
    /**
     * 计划价格
     */
    private Object planPrice;
    /**
     * 止盈价格
     */
    private double stopProfit;
    /**
     * 止损价格
     */
    private double stopLoss;
    /**
     * 开仓价格
     */
    private double startPrice;
    /**
     * 可用保证金
     */
    private double useableBalance;
    /**
     * 可用数量
     */
    private double useableAmount;
    /**
     * 开仓时间
     */
    private String createTime;
    /**
     * 买入时间
     */
    private String startTime;
    /**
     * 平仓完成时间
     */
    private Object finishTime;
    /**
     * 收益
     */
    private double income;
    /**
     * 1,"急速平仓" 2,"计划平仓"
     */
    private int finish_type;
    /**
     * 平仓价格
     */
    private double finishPrice;
    /**
     * 计划平仓价格
     */
    private double finishPlanPrice;
    /**
     * 平仓数量
     */
    private double finishAmount;
    /**
     * 平仓时市场价格
     */
    private double finishMarketPrice;

}
