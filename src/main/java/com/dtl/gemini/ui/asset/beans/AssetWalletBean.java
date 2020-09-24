package com.dtl.gemini.ui.asset.beans;

import com.dtl.gemini.bean.BaseBean;
import com.dtl.gemini.ui.asset.model.AssetWallet;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author DTL
 * @date 2020/5/11
 **/
@Setter
@Getter
public class AssetWalletBean extends BaseBean {

    private List<AssetWallet> data;

}
