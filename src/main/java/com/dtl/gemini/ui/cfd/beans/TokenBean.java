package com.dtl.gemini.ui.cfd.beans;

import com.dtl.gemini.bean.BaseBean;
import com.dtl.gemini.ui.home.model.CfdToken;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author DTL
 * @date 2020/5/15
 **/
@Getter
@Setter
public class TokenBean extends BaseBean {

    private List<CfdToken> data;

}
