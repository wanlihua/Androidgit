package com.dtl.gemini.ui.cfd.beans;

import com.dtl.gemini.bean.BaseBean;

import java.util.List;

/**
 * @author DTL
 * @date 2020/5/15
 **/
public class MultipleBean extends BaseBean {

    /**
     * massage : 成功
     * data : ["10","20","30","50","100"]
     */

    private List<String> data;

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
