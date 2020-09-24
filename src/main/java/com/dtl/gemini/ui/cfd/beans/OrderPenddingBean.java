package com.dtl.gemini.ui.cfd.beans;

import com.dtl.gemini.bean.BaseBean;
import com.dtl.gemini.ui.cfd.model.OrderCreate;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author DTL
 * @date 2020/5/15
 **/
@Getter
@Setter
public class OrderPenddingBean extends BaseBean {
    private List<OrderCreate> data;
}
