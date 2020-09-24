package com.dtl.gemini.ui.asset.model;

import lombok.Data;

/**
 * @author DTL
 * @date 2020/5/11
 **/
@Data
public class WithdrawalSys {
    /**
     * 提币手续费
     */
    private double withdrawalFee;
    /**
     * 最小提币数量
     */
    private double minimumWithdrawalAmount;
    /**
     * 最大提币数量
     */
    private double maximumWithdrawalAmount;
    /**
     * 每日提币额度
     */
    private double dailyWithdrawalLimit;
    /**
     * 每日提币次数
     */
    private int numberWithdrawalsPerDay;
    /**
     * 币种
     */
    private String currency;
}
