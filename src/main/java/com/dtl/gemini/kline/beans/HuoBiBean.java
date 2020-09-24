package com.dtl.gemini.kline.beans;

import com.dtl.gemini.kline.model.HuoBi;

import java.util.List;

import lombok.Data;

/**
 * 大图灵
 * 2020/3/3
 **/
@Data
public class HuoBiBean {

    /**
     * status : ok
     * ch : market.btcusdt.kline.1day
     * ts : 1585551740308
     * data : [{"amount":34389.648329281066,"open":6131.31,"close":6238,"high":6248,"id":1585497600,"count":271191,"low":5860,"vol":2.073332298329726E8}]
     * tick : {"amount":50848.62143241147,"open":6096.71,"close":6256.86,"high":6330.21,"id":211280594689,"count":414998,"low":5860,"version":211280594689,"vol":3.090319899384789E8}
     */

    private String status;
    private String ch;
    private long ts;
    private List<HuoBi> data;
    private HuoBi tick;
}
