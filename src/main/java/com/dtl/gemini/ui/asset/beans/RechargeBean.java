package com.dtl.gemini.ui.asset.beans;

import com.dtl.gemini.bean.BaseBean;
import com.dtl.gemini.ui.asset.model.Recharge;
import com.dtl.gemini.ui.asset.model.Withdrawal;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author DTL
 * @date 2020/5/11
 **/
@Getter
@Setter
public class RechargeBean extends BaseBean {

    private List<Recharge> data;
}
