package com.dtl.gemini.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * @author DTL
 * @date 2020/5/11
 **/
@Getter
@Setter
public class DataBean<T> extends BaseBean {
    private T data;
}
