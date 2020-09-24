package com.dtl.gemini.kline.beans;

import java.math.BigDecimal;

import lombok.Data;

/**
 * zll
 * 2020/3/30
 * 币安K线
 **/
@Data
public class BiAnBean {
//    [
//         1585533900000,时间戳
//        "5954.57000000",开盘
//        "5954.94000000",最高
//        "5947.46000000",最低
//        "5948.68000000",收盘
//        "92.21047000",成交
//        1585533959999,
//                "548787.05972513",
//                399,
//                "47.63135100",
//                "283487.80238568",
//                "0"
//                ]

    private Long time;
    private BigDecimal open;
    private BigDecimal hight;
    private BigDecimal low;
    private BigDecimal close;
    private BigDecimal vol;
    private Long times;

}
