package com.dtl.gemini.ui.asset.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * @author DTL
 * @date 2020/5/26
 **/
@Getter
@Setter
public class Record implements Serializable {
    String id;
    /**
     * 1:转入，2：转出
     */
    int type;
    String currency;
    String amount;
    String datetime;
    Object adress;
    Object hash;
    /**
     * 状态 0:待处理,1:同意/完成,2:拒绝
     */
    int status;
    Object reason;

    public Record(String id, int type) {
        this.id = id;
        this.type = type;
    }

    public Record(int type, String currency, String amount, String datetime, Object adress, int status, Object reason, Object hash) {
        this.type = type;
        this.currency = currency;
        this.amount = amount;
        this.datetime = datetime;
        this.adress = adress;
        this.status = status;
        this.reason = reason;
        this.hash = hash;
    }
}
