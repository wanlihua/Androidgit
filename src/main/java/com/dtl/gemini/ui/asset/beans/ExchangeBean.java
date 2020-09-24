package com.dtl.gemini.ui.asset.beans;

import com.dtl.gemini.bean.BaseBean;
import com.dtl.gemini.ui.asset.model.Exchange;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author DTL
 * @date 2020/5/12
 **/
@Setter
@Getter
public class ExchangeBean extends BaseBean {

    private List<Exchange> data;
}
