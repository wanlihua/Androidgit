package com.dtl.gemini.ui.home.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 合约支持币种
 *
 * @author DTL
 * @date 2020/7/28
 **/
@Getter
@Setter
public class CfdToken {
    private String currency;
    /**
     * 1：双仓，2：自由
     */
    private int mode;
    /**
     * 1：只能买涨 2：只能买跌
     */
    private int trend;
}
