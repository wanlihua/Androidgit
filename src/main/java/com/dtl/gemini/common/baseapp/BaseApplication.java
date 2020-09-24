package com.dtl.gemini.common.baseapp;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import androidx.multidex.MultiDex;
//import android.support.multidex.MultiDex;
//
//import com.blankj.utilcode.utils.Utils;
//import com.orhanobut.hawk.Hawk;

/**
 * APPLICATION
 */
public class BaseApplication extends Application {

    private static BaseApplication baseApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;

//        //init Hawk
//        Hawk.init(this).build();
//
//        //initUtil类
//        Utils.init(this);
    }

    public static Application getAppContext() {
        return baseApplication;
    }
    public static Resources getAppResources() {
        return baseApplication.getResources();
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    /**
     * 分包
     * @param base
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}