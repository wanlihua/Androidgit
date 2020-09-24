package com.dtl.gemini.ui.other.model;

import lombok.Data;

/**
 * @author DTL
 * @date 2020/4/11
 **/
@Data
public class Version {

    private int id;

    /**
     * 版本号
     */
    private int versionCode;

    /**
     * 版本名称
     */
    private String versionName;

    /**
     * 更新信息
     */
    private String content;

    /**
     * 更新链接
     */
    private String url;

    /**
     * 下载网页链接
     */
    private String urlDown;

    /**
     * 是否强制 1强制，0不强制
     */
    private int isForce;

    private String platform;

    private String createDateTime;

    private String updateDateTime;
}
