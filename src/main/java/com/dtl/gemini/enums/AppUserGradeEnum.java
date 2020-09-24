package com.dtl.gemini.enums;


/**
 * app用户等级
 */
public enum AppUserGradeEnum {
    /**
     * 普通
     */
    STAR_ZERO("普通"),
    /**
     * S1
     */
    STAR_ONE("S1"),
    /**
     * S2
     */
    STAR_TWO("S2"),
    /**
     * S3
     */
    STAR_THREE("S3"),
    /**
     * S4
     */
    STAR_FOUR("S4"),
    /**
     * S5
     */
    STAR_FIVE("S5"),
    /**
     * S6
     */
    STAR_SIX("S6");

    AppUserGradeEnum(String grade) {
        this.grade = grade;
    }

    public String grade;
}
