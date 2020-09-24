package com.dtl.gemini.enums;

/**
 * @author DTL
 * @date 2020/7/28
 **/
public enum CfdTypeEnum {
    /**
     * 双仓合约
     */
    DOUBLE(1),

    /**
     * 自由合约
     */
    FREE(2);

    CfdTypeEnum(int grade) {
        this.grade = grade;
    }

    public int grade;
}
