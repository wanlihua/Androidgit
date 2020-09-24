package com.dtl.gemini.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class User implements Serializable {
    private String username;
    private boolean enabled;
    private Object headUrl;
    private String createDateTime;
    private String lastLoginDateTime;
    private String token;
    private String phone;
    protected String grade;
    protected String gradeText;
    protected Integer directPushNum;
    protected String invitationCode;
    protected String registerInvitationCode;

    /**
     * 真实姓名
     */
    protected String realName;

    /**
     * 身份证号
     */
    protected String idNumber;

    /**
     * 身份证正面照路径
     */
    protected String idPositiveUrl;

    /**
     * 身份证背面照路径
     */
    protected String idBackUrl;

    /**
     * 审核状态
     */
    protected Integer status;

    /**
     * 审核原因
     */
    protected String extra;
}
