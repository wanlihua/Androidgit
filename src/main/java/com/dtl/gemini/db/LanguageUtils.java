package com.dtl.gemini.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

/***
 **时间：2018/11/20
 **来源：飞亚中盛
 * 语言
 ***/
public class LanguageUtils {
    private static LanguageUtils utils;

    Context context;
    SharedPreferences preferences;

    private LanguageUtils(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("me_language", Context.MODE_PRIVATE);
    }

    public static LanguageUtils init(Context context) {
        if (utils == null) {
            utils = new LanguageUtils(context);
        }
        return utils;
    }

    public void setParameter(String value) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("language", value);
        edit.commit();
    }

    public String getParameter() {
        return preferences.getString("language", null);
    }

    public void delParameter() {
        SharedPreferences.Editor edit = preferences.edit();
        edit.clear();
        edit.commit();
    }

    @SuppressLint("NewApi")
    public static Configuration returnConfigration(Context context, String shortName) {
        Configuration configuration = context.getResources().getConfiguration();
        if (shortName.equals("zh")) {
            configuration.setLocale(Locale.CHINESE);
        } else if (shortName.equals("en")) {
            configuration.setLocale(Locale.US);
        } else if (shortName.equals("it")) {
            configuration.setLocale(Locale.ITALIAN);
        } else if (shortName.equals("es")) {
            configuration.setLocale(new Locale("es"));
        } else if (shortName.equals("de")) {
            configuration.setLocale(Locale.GERMAN);
        } else if (shortName.equals("nl")) {
            configuration.setLocale(new Locale("nl"));
        } else if (shortName.equals("ar")) {
            configuration.setLocale(new Locale("ar"));
        } else if (shortName.equals("pt_br")) {
            configuration.setLocale(new Locale("pt"));
        } else if (shortName.equals("ko")) {
            configuration.setLocale(Locale.KOREAN);
        } else if (shortName.equals("fr")) {
            configuration.setLocale(new Locale("fr"));
        } else if (shortName.equals("ru")) {
            configuration.setLocale(new Locale("ru"));
        }
        return configuration;
    }
}
