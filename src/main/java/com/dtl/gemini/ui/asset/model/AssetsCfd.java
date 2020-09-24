package com.dtl.gemini.ui.asset.model;

import com.dtl.gemini.constants.Constant;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.Data;

/**
 * 资产（合约账户）
 *
 * @author DTL
 **/
@Data
public class AssetsCfd {
    /**
     * 可用
     */
    private BigDecimal useableAmount;
    /**
     * 冻结
     */
    private BigDecimal frostAmount;
    /**
     * 冻结收益
     */
    private BigDecimal frostProfit;
    /**
     * 已实现
     */
    private BigDecimal totalProfit;
    /**
     * 未到账收益/浮动收益
     */
    private BigDecimal expectedProfit;

    /**
     *
     */
    private String currency;

    public BigDecimal getUseableAmount() {
        return useableAmount.setScale(Constant.coinScale, RoundingMode.DOWN);
    }

    public BigDecimal getFrostAmount() {
        return frostAmount.setScale(Constant.coinScale, RoundingMode.DOWN);
    }

    public BigDecimal getTotalProfit() {
        return totalProfit.setScale(Constant.coinScale, RoundingMode.DOWN);
    }

    public BigDecimal getExpectedProfit() {
        return expectedProfit.setScale(Constant.coinScale, RoundingMode.DOWN);
    }

    public BigDecimal getFrostProfit() {
        return frostProfit.setScale(Constant.coinScale, RoundingMode.DOWN);
    }

}
