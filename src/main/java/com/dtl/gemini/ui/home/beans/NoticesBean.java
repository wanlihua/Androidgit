package com.dtl.gemini.ui.home.beans;

import com.dtl.gemini.bean.BaseBean;
import com.dtl.gemini.ui.home.model.Notice;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticesBean extends BaseBean implements Serializable {

    private List<Notice> data;
}
