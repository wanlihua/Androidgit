package com.dtl.gemini.kline.beans;

import com.dtl.gemini.kline.model.Contract;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 合约指数信息
 *
 * @author DTL
 * @date 2020/5/19
 **/
@Getter
@Setter
public class ContractBean {
    /**
     * status : ok
     * data : [{"symbol":"BTC","index_price":9809.78,"index_ts":1589877924014}]
     * ts : 1589877932783
     */

    private String status;
    private long ts;
    private List<Contract> data;
}
