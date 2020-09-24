package com.dtl.gemini.ui.cfd.model;

import lombok.Data;

/**
 * @author DTL
 * @date 2020/5/5
 **/
@Data
public class Delegate {
    private String id;
    /**
     * 类型
     */
    private int type;
    /**
     * 倍数
     */
    private int multiple;
    /**
     * 状态
     */
    private int status;

    /**
     * 持仓量
     */
    private double positionNumber;

    /**
     * 开仓价格
     */
    private double openPrice;

    /**
     * 交易量(保证金)
     */
    private double transactionNumber;


    /**
     * 平仓数量
     */
    private double closeNumer;

    /**
     * 平仓价格
     */
    private double closePrice;

    /**
     * 收益
     */
    private double profit;

    /**
     * 创建时间
     */
    private String date;
}
