package com.dtl.gemini.ui.asset.model;

import java.io.Serializable;

import lombok.Data;

/**
 * 大图灵
 * 2019/5/18
 **/
@Data
public class TransactionRecord implements Serializable {
    private long id;
    private String createDateTime;
    private Object updateDateTime;
    /**
     * 金额
     */
    private double amount;
    /**
     * 币种
     */
    private String currency;
    /**
     * 状态 0:待处理,1:同意/完成,2:拒绝
     */
    private int status;
    /**
     * 备注
     */
    private Object extra;
    /**
     * 关联Id
     */
    private String otherId;
    /**
     * 类型  1:充币,2:提币,3:兑换,4:划转,5:后台操作
     */
    private int type;
}
