package com.dtl.gemini.kline.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author DTL
 * @date 2020/5/19
 **/
@Getter
@Setter
public class Contract {
    /**
     * symbol : BTC
     * index_price : 9809.78
     * index_ts : 1589877924014
     */
    /**
     * 指数代码
     */
    private String symbol;
    /**
     * 指数价格
     */
    private double index_price;
    /**
     * 响应生成时间点 单位：毫秒
     */
    private long index_ts;
}
