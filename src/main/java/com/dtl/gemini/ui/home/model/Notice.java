package com.dtl.gemini.ui.home.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 大图灵
 * 2019/9/4
 **/
@Getter
@Setter
public class Notice implements Serializable {
    private int id;
    private String title;
    private String content;
    private String author;
    private String language;
    @SerializedName("status")
    private int statusX;
    private String createDateTime;
    private String updateDateTime;
}
