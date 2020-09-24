package com.dtl.gemini.ui.other.beans;

import com.dtl.gemini.bean.BaseBean;
import com.dtl.gemini.ui.other.model.AreaCode;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author DTL
 * @date 2020/5/7
 **/
@Getter
@Setter
public class AreaCodeBean extends BaseBean {

    private List<AreaCode> data;
}
