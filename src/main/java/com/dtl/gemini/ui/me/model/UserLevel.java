package com.dtl.gemini.ui.me.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 用户等级参数
 *
 * @author DTL
 * @date 2020/7/24
 **/
@Data
public class UserLevel implements Serializable {

    /**
     * 昵称
     */
    private String userName;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 等级
     */
    private String grade;

    /**
     * 总直推人数
     */
    private Integer directPushNum;

    /**
     * 团队总人数
     */
    private Integer teamUserNumber;

    /**
     * 团队有效人数
     */
    private Integer teamValidUserNumber;

    /**
     * 已购买等级数量
     */
    private BigDecimal consumeAmount;

    /**
     * 下一等级
     */
    private String nextGrade;

    private String nextGradeText;

    /**
     * 下一等级所需有效人数
     */
    private Integer nextTeamValidUserNumber;

    /**
     * 下一等级所需购买等级数量
     */
    private BigDecimal nextConsumeAmount;
}
