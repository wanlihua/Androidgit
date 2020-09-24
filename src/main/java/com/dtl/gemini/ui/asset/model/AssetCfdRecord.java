package com.dtl.gemini.ui.asset.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author DTL
 * @date 2020/7/29
 **/
@Getter
@Setter
public class AssetCfdRecord {
    private int type;
    private String currency;
    private String reason;
    private double beforeUseable;
    private double afterUseable;
    private double beforeFrost;
    private double afterFrost;
    private double beforeTotalProfit;
    private double afterTotalProfit;
    private String createTime;
}
