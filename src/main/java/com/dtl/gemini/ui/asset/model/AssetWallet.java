package com.dtl.gemini.ui.asset.model;

import java.io.Serializable;

import lombok.Data;

/**
 * 资产(钱包账户)
 *
 * @author DTL
 * @date 2020/4/15
 **/
@Data
public class AssetWallet implements Serializable {
    private String id;
    private Object updateUserId;
    private double usableAmount;
    private double frostAmount;
    private Object appUserId;
    private String currency;
    private String address;
    private String platform;
    private int enable;
    private int priority;
    private String createDateTime;
    private String updateDateTime;
}
