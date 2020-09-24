package com.dtl.gemini.ui.asset.model;

import java.io.Serializable;

import lombok.Data;

/**
 * @author DTL
 * @date 2020/4/29
 **/
@Data
public class Recharge implements Serializable {
    private String id;
    private String appUserId;
    private String currency;
    private String address;
    private String hash;
    private double amount;
    private String createDateTime;
}
