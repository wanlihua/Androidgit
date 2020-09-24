package com.dtl.gemini.ui.cfd.model;

import lombok.Data;

/**
 * @author DTL
 * @date 2020/5/15
 **/
@Data
public class OrderCreate {
    private String id;
    /**
     * 1,"委托中" 2,"已撤单" 3,"持仓中" 4,"平仓委托中"（订单已
     * 经没有可用数量了，全部都计划平仓，还未成交，如果有平仓委托撤销，订单状态又变为持仓中） 5,"已平
     * 仓" 6,"已爆仓"
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
    private double balance;
    /**
     * 买入数量
     */
    private double amount;
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
     * 撤销、全部平仓、爆仓时间
     */
    private Object finishTime;

    /**
     * 收益
     */
    private double predictIncome;

}
