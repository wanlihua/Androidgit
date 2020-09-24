package com.dtl.gemini;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;

import com.dtl.gemini.common.baseapp.BaseApplication;
import com.dtl.gemini.common.commonwidget.CustomProgressDialog;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.db.StoreUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okserver.download.DownloadManager;
import com.lzy.okserver.download.DownloadService;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.io.File;
import java.util.logging.Level;

import androidx.multidex.MultiDex;

public class PunkApplication extends BaseApplication {

    public static String mSDCardPath;

    public static DownloadManager manger = null;

    public static PunkApplication getInstance() {
        return (PunkApplication) getAppContext();
    }

    @SuppressLint("StaticFieldLeak")
    public static volatile Context applicationContext;
    public static volatile Handler applicationHandler;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initOkGo();
        manger = DownloadService.getDownloadManager();
        manger.getThreadPool().setCorePoolSize(9);
        initDirs();
        X5Init();
        initUmeng();
        UMShareInit();
        //适配安卓8.0以上的相机
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        initImageLoader(this);

        //telegram
        applicationContext = getApplicationContext();

        applicationHandler = new Handler(applicationContext.getMainLooper());

        String token = StoreUtils.init(this).getRongToken();
        if (token != null && token.length() > 5 && StoreUtils.init(this).getLoginUser() != null) {
        }
//        FirebaseApp.initializeApp(this);
        ZXingLibrary.initDisplayOpinion(this);
    }

    private static CustomProgressDialog dialog;

    public static CustomProgressDialog showDialog(Context mContext, String msg) {
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
            dialog = null;
        } catch (Exception e) {
        }

        dialog = CustomProgressDialog.createDialog(mContext);
        dialog.setMessage(msg);
//        dialog.show();
        return dialog;
    }

    public static void closeDialog() {
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
            dialog = null;
        } catch (Exception e) {
        }
    }

    public static void initImageLoader(Context context) {
        //缓存文件的目录
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, "imageloader/Cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
                .threadPoolSize(3) //线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()) //将保存的时候的URI名称用MD5 加密
                .memoryCache(new UsingFreqLimitedMemoryCache(5 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                .memoryCacheSize(5 * 1024 * 1024) // 内存缓存的最大值
                .diskCacheSize(50 * 1024 * 1024)  // 50 Mb sd卡(本地)缓存的最大值
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                // 由原先的discCache -> diskCache
                .diskCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs() // Remove for release app
                .build();
        //全局初始化此配置
        ImageLoader.getInstance().init(config);
    }

    private void initOkGo() {
        //初始化OKGO
        OkGo.init(com.dtl.gemini.PunkApplication.getAppContext());
        //以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
        //好处是全局参数统一,特定请求可以特别定制参数
        try {
            //以下都不是必须的，根据需要自行选择,一般来说只需要 debug,缓存相关,cookie相关的 就可以了
            OkGo.getInstance()
                    //打开该调试开关,控制台会使用 红色error 级别打印log,并不是错误,是为了显眼,不需要就不要加入该行
                    .debug("OkGo", Level.INFO, Constant.isDebug)
                    //如果使用默认的 60秒,以下三行也不需要传
                    .setConnectTimeout(5 * 1000)  //全局的连接超时时间
                    .setReadTimeOut(Constant.DEFAULT_MILLISECONDS)     //全局的读取超时时间
                    .setWriteTimeOut(Constant.DEFAULT_MILLISECONDS)    //全局的写入超时时间
                    //可以全局统一设置缓存模式,默认是不使用缓存,可以不传,具体其他模式看 github 介绍 https://github.com/jeasonlzy/
                    .setCacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)
                    //可以全局统一设置缓存时间,默认永不过期,具体使用方法看 github 介绍
                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
                    //如果不想让框架管理cookie,以下不需要
                    .setCookieStore(new MemoryCookieStore())                //cookie使用内存缓存（app退出后，cookie消失）
                    //可以设置https的证书,以下几种方案根据需要自己设置,不需要不用设置
                    .setCertificates();
//                    .getOkHttpClientBuilder().proxy(Proxy.NO_PROXY);
//            .getOkHttpClientBuilder().proxy(Proxy.NO_PROXY)//防抓包
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean initDirs() {
        mSDCardPath = Environment.getExternalStorageDirectory().toString();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, Constant.APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    public void X5Init() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.e("X5", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                Log.e("X5", " onCoreInitFinished   @@@@@@@@@@");
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }


    private void UMShareInit() {
        PlatformConfig.setWeixin("wxa53cf854429142cb", "9dff17cb7bd8d22e1b9f7ee1f7696646");
        PlatformConfig.setKakao("e4f60e065048eb031e235c806b31c70f");
    }

    private void initUmeng() {
        /**
         * 初始化common库
         * 参数1:上下文，不能为空
         * 参数2:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
         * 参数3:Push推送业务的secret
         */
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        UMConfigure.setLogEnabled(false);
        MobclickAgent.setDebugMode(true);// 打开调试模式
//        MobclickAgent.setCatchUncaughtExceptions(true);// 关闭错误统计
//        Config.DEBUG = true;//可以弹出对话框告诉我们什么地方出错了，不写这句话的话，要费时间找bug的
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}