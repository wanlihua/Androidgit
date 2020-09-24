package com.dtl.gemini.ui.asset.beans;

import com.dtl.gemini.bean.BaseBean;
import com.dtl.gemini.ui.asset.model.AssetsCfd;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author DTL
 * @date 2020/5/15
 **/
@Getter
@Setter
public class AssetCfdBean extends BaseBean {

    /**
     * massage : 成功
     * data : [{"currency":"ETH","useableAmount":0,"frostAmount":0,"totalProfit":0,"expectedProfit":0},{"currency":"BTC","useableAmount":0,"frostAmount":0,"totalProfit":0,"expectedProfit":0}]
     */
    private List<AssetsCfd> data;
}
