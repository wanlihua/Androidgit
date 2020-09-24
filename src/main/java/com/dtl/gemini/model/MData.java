package com.dtl.gemini.model;

import java.io.Serializable;

/**
 * 数据交互的模型
 */
public class MData<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public String id;
    public int type;
    public T data;//多种类型数据，一般是List集合，比如获取所有员工列表
}
