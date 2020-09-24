package com.dtl.gemini.ui.home.model;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * @author DTL
 * @date 2020/4/11
 **/
@Data
public class LunBo {
    private long id;
    private String title;
    private String imgUrl;
    private Object url;

    private String getData;
    private String language;
    @SerializedName("status")
    private int statusX;
    private String createDateTime;
    private String updateDateTime;

    public String getImgUrl() {
        return imgUrl.trim();
    }

    public Object getUrl() {
        if (url != null) {
            url = url.toString().trim();
        }
        return url;
    }
}
