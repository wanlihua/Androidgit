package com.dtl.gemini.ui.me.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户邀请页面参数
 *
 * @author DTL
 * @date 2020/7/24
 **/
@Data
public class UserShareInfo implements Serializable {

    /**
     * 邀请页面海报url
     */
    private String shareImgUrl;

    /**
     * 邀请页面背景url
     */
    private String shareBgImgUrl;

    /**
     * 下载链接
     */
    private String shareDownUrl;

    /**
     * 邀请码
     */
    private String invitationCode;

    /**
     * 总直推人数
     */
    private Integer directPushNum;

    /**
     * 团队总人数
     */
    private Integer teamUserNumber;

    /**
     * 总业绩
     */
    private BigDecimal sumPerformance;

    public String getSumPerformance() {
        return sumPerformance.stripTrailingZeros().toPlainString();
    }
}
