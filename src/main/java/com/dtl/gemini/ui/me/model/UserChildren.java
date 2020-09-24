package com.dtl.gemini.ui.me.model;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author DTL
 * @date 2020/6/16
 * 用户团队
 **/
@Data
@ToString(callSuper = true)
public class UserChildren {

    /**
     * 父级手机号
     */
    private String parentPhone;

    /**
     * 父级昵称
     */
    private String parentUserName;

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

    private String gradeText;

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
     * 总业绩
     */
    private BigDecimal sumPerformance;

    /**
     * 是否有下级
     */
    private Boolean hasChildren;

    /**
     * 下级
     */
    private List<UserChildren> childrenList;

    public String getSumPerformance() {
        return sumPerformance.stripTrailingZeros().toPlainString();
    }
}
