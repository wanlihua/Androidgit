package com.dtl.gemini.ui.other.model;

import lombok.Data;

/**
 * @author DTL
 * @date 2020/5/7
 **/
@Data
public class AreaCode {
    /**
     * id : 986
     * name : 鸿煊.林
     * areaCode : 657
     * language : p05is0
     */

    private int id;
    private String name;
    private int areaCode;
    private String language;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(int areaCode) {
        this.areaCode = areaCode;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
