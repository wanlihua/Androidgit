package com.dtl.gemini.ui.me.model;

import lombok.Data;

/**
 * @author DTL
 * @date 2020/4/28
 **/
@Data
public class Language {
    String name;
    String nameEnglish;
    String shortName;

    public Language(String name, String nameEnglish, String shortName) {
        this.name = name;
        this.nameEnglish = nameEnglish;
        this.shortName = shortName;
    }
}
