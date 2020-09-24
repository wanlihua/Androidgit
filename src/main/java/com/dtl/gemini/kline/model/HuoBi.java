package com.dtl.gemini.kline.model;

import lombok.Data;

/**
 * 大图灵
 * 2020/3/3
 **/
@Data
public class HuoBi {

    /**
     * amount : 38320.049084367834
     * open : 6131.31
     * close : 6240.31
     * high : 6330.21
     * id : 1585497600
     * count : 299855
     * low : 5860.0
     * vol : 2.3200622804198223E8
     * symbol : "btcusdt"
     */

    private double amount;
    private double open;
    private double close;
    private double high;
    private long id;
    private long count;
    private double low;
    private double vol;
    private String symbol;
}
