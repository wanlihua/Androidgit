package com.dtl.gemini.ui.home.beans;

import com.dtl.gemini.bean.BaseBean;
import com.dtl.gemini.ui.home.model.LunBo;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author DTL
 * @date 2020/4/11
 **/
@Getter
@Setter
public class LunBoBean extends BaseBean implements Serializable {

    private List<LunBo> data;
    private String getData;

}
