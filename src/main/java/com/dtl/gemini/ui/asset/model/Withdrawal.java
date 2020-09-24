package com.dtl.gemini.ui.asset.model;

import java.io.Serializable;

import lombok.Data;

/**
 * @author DTL
 **/
@Data
public class Withdrawal implements Serializable {
    private String id;
    /**
     * 提币地址
     */
    private String address;
    /**
     * 提币数量
     */
    private double amount;
    /**
     * 手续费
     */
    private double feeAmount;
    /**
     * 到账数量
     */
    private double recevieAmount;
    /**
     * 状态
     */
    private int status;
    /**
     * 拒绝原因
     */
    private Object refuseReason;
    private String currency;
    private Object hash;
    private String appUserId;
    private String createDateTime;
    private Object updateDateTime;
    private Object updateUserId;
}
