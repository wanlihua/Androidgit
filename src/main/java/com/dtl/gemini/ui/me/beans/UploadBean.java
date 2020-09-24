package com.dtl.gemini.ui.me.beans;

import com.dtl.gemini.bean.BaseBean;
import com.dtl.gemini.ui.me.model.Upload;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 大图灵
 * 2019/8/12
 **/
@Data
@EqualsAndHashCode(callSuper = false)
public class UploadBean extends BaseBean {
    private List<Upload> data;
}
