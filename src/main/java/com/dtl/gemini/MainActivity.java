package com.dtl.gemini;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dtl.gemini.api.Api;
import com.dtl.gemini.common.baseapp.AppManager;
import com.dtl.gemini.common.baserx.RxSchedulers;
import com.dtl.gemini.common.baserx.RxSubscriber;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.db.StoreUtils;
import com.dtl.gemini.kline.KlineApi;
import com.dtl.gemini.kline.KlineApiImpl;
import com.dtl.gemini.kline.beans.HuoBiBean;
import com.dtl.gemini.kline.beans.MarketBean;
import com.dtl.gemini.kline.model.HuoBi;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.ui.asset.activity.AssetFragment;
import com.dtl.gemini.ui.cfd.activity.CfdFragment;
import com.dtl.gemini.ui.home.activity.HomeFragment;
import com.dtl.gemini.ui.me.activity.MeFragment;
import com.dtl.gemini.ui.other.beans.VersionBean;
import com.dtl.gemini.ui.other.model.Version;
import com.dtl.gemini.utils.AndroidUtil;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.DialogUtils;
import com.dtl.gemini.utils.PermissionUtil;
import com.dtl.gemini.utils.StatusBarUtil;
import com.dtl.gemini.utils.UpdateAppDialog;
import com.google.gson.Gson;
import com.lzy.okgo.callback.FileCallback;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.PlatformName;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.ShareBoardConfig;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTabHost;

import okhttp3.Call;
import okhttp3.Response;

import static java.lang.Math.floor;

/**
 * @author DTL
 * @date 2020/4/11
 **/
public class MainActivity extends FragmentActivity {
    private final String TAG = "MainActivity";

    protected ShareAction shareAction;
    protected ShareBoardConfig shareConfig;
    FragmentTabHost fragmentTabHost;
    private boolean ispay = true;
    public static double usd = 6.80, krw = 1175.77, jpy = 108.81;
    public static Map<String, String> mapPrice = new HashMap<>();
    String names[];
    int btBgs[];
    Class fragments[];

    private final int INSTALL_PERMISSION_REQUEST_CODE = 10082;
    public static String apkFilePath, apkFileName;

    @Override
    public void setEnterSharedElementCallback(SharedElementCallback callback) {
        super.setEnterSharedElementCallback(callback);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.setStatusBarColor2(this);
        UMShareInit();
        init();
    }

    private void init() {
        AppManager.getAppManager().finishAllActivity();
        AppManager.getAppManager().addActivity(MainActivity.this);
        String home = getResources().getString(R.string.main_tab_home);
        String cfd = getResources().getString(R.string.main_tab_cfd);
        String asset = getResources().getString(R.string.main_tab_asset);
        String me = getResources().getString(R.string.main_tab_me);
        //中文
        btBgs = new int[]{R.drawable.main_tab_home_bg, R.drawable.main_tab_cfd_bg, R.drawable.main_tab_asset_bg, R.drawable.main_tab_me_bg};
        fragments = new Class[]{HomeFragment.class, CfdFragment.class, AssetFragment.class, MeFragment.class};
        names = new String[]{home, cfd, asset, me};
        fragmentTabHost = findViewById(android.R.id.tabhost);
        fragmentTabHost.setup(this, getSupportFragmentManager(), R.id.menu_content);
        for (int i = 0; i < names.length; i++) {
            TabHost.TabSpec spec = fragmentTabHost.newTabSpec(names[i]).setIndicator(getView(i));
            fragmentTabHost.addTab(spec, fragments[i], null);
            fragmentTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tab_bg);
        }
        fragmentTabHost.getTabWidget().setDividerDrawable(null);

        PermissionUtil.initPermission(this);
        if (ispay) {
            if (StoreUtils.init(this).getParameter("usd") != null)
                usd = Double.parseDouble(StoreUtils.init(this).getParameter("usd"));
            if (StoreUtils.init(this).getParameter("krw") != null)
                krw = Double.parseDouble(StoreUtils.init(this).getParameter("krw"));
            if (StoreUtils.init(this).getParameter("jpy") != null)
                jpy = Double.parseDouble(StoreUtils.init(this).getParameter("jpy"));
            setCurrencyPrice();
            setPrice("USDT");
            queryExchangeRate(this, "usd", "美元人民币", "usdcny");
            queryExchangeRate(this, "krw", "美元韩元", "usdkrw");
            queryExchangeRate(this, "jpy", "美元日元", "usdjpy");
        }
        String activity = StoreUtils.init(this).getParameter(Constant.MODEL);
        if (activity != null && activity.equals("cfd")) {
            fragmentTabHost.setCurrentTab(1);
        }
    }

    private View getView(int i) {
        View view = View.inflate(this, R.layout.main_tabcontent, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.tab_iamge);
        TextView textView = (TextView) view.findViewById(R.id.tab_text);
        imageView.setImageResource(btBgs[i]);
        textView.setText(names[i]);
        return view;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        doChceckVersion(this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    long keyBackTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - keyBackTime < 5000) {
                finish();
            } else {
                Toast.makeText(this, "" + getResources().getString(R.string.key_download), Toast.LENGTH_SHORT).show();
                keyBackTime = System.currentTimeMillis();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 检测版本
     */
    private void doChceckVersion(Context context) {
        int versionCode = AndroidUtil.getVersionCode(PunkApplication.getAppContext());
        apkFilePath = Environment.getExternalStorageDirectory() + Constant.FILE + "/";
        apkFileName = getResources().getString(R.string.app_name) + "_" + versionCode + ".apk";
        try {
            Api.getInstance().getAppVersion(context).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<VersionBean>(context) {
                @Override
                protected void _onNext(VersionBean bean) {
                    if (bean != null && bean.getStatus() == Constant.SUCCESS) {
                        if (versionCode < bean.getData().getVersionCode())
                            judgeNewVersion(bean.getData());
                    }
                }

                @Override
                protected void _onError(String message) {
                    Log.e("检查更新", message + "");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    /**
     * 有新版本
     */
    private void judgeNewVersion(Version bean) {
        apkFileName = getResources().getString(R.string.app_name) + "_" + bean.getVersionCode() + ".apk";
        UpdateAppDialog.dismissDialog(MainActivity.this);
        UpdateAppDialog.showDialog(this, getResources().getString(R.string.version_update), bean.getContent(), bean.getIsForce());
        UpdateAppDialog.btnOk(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataUtil.initPermission(MainActivity.this, permissions);
                File file = new File(apkFilePath, apkFileName);
                if (file.exists()) {// 如果已经存在,
                    file.delete();
                }
                if (bean.getIsForce() == 0) {
//                    doDownLoad(bean.getUrlDown());
                    UpdateAppDialog.dismissDialog(MainActivity.this);
                }
//                else {
//                    doDownLoads(bean.getUrlDown());
//                }
                if (bean != null) {
                    Intent intent = new Intent();
                    intent.setData(Uri.parse(bean.getUrl()));//Url 就是你要打开的网址
                    intent.setAction(Intent.ACTION_VIEW);
                    startActivity(intent); //启动浏览器
                }
            }
        });
    }

    /**
     * 判断是否有安装权限
     */
    public void checkInstallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!getPackageManager().canRequestPackageInstalls()) {
                //没有权限让调到设置页面进行开启权限；
                Uri packageURI = Uri.parse("package:" + getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                startActivityForResult(intent, INSTALL_PERMISSION_REQUEST_CODE);
            }
        }
    }

    /**
     * 下载
     */
    float progress = 0;
    NotificationManager manager;

    @SuppressLint("NewApi")
    private void doDownLoad(String downLink) {
        String hint = getResources().getString(R.string.is_downing) + "," + getResources().getString(R.string.is_downing1);
        Toast.makeText(this, hint, Toast.LENGTH_SHORT).show();
        if (manager != null) {
            return;
        }
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (manager.getNotificationChannel(Constant.PUSH_CHANNEL_ID) != null) {
                manager.deleteNotificationChannel(Constant.PUSH_CHANNEL_ID);
            }
//            IMPORTANCE_MIN//倒数第二
            NotificationChannel channel = new NotificationChannel(Constant.PUSH_CHANNEL_ID, Constant.PUSH_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);//是否在桌面icon右上角展示小红点
            channel.setLightColor(Color.RED);//小红点颜色
            channel.setShowBadge(false); //是否在久按桌面图标时显示此渠道的通知
            channel.enableVibration(false);
            channel.enableLights(true);
            channel.setSound(null, null);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        String appName = getResources().getString(R.string.app_name);
        builder.setContentTitle(appName + ".apk");
        builder.setContentText("" + getResources().getString(R.string.is_downing));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(Constant.PUSH_CHANNEL_ID);
        }
        //设置重要程度： PRIORITY_DEFAULT （表示默认）PRIORITY_MIN(表示最低) PRIORITY_LOW （较低）PRIORITY_HIGH （较高）PRIORITY_MAX（最高）
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setSound(null);
        manager.notify(Constant.NOTIFY_ID, builder.build());
        builder.setProgress(100, 0, false);
        Api.getInstance().downLoadFile(this, downLink, new FileCallback(apkFilePath, apkFileName) {

            @Override
            public void downloadProgress(long currentSize, long totalSize, float progres, long networkSpeed) {
                super.downloadProgress(currentSize, totalSize, progres, networkSpeed);
                Random r = new Random();
                if (floor(progres * (r.nextInt() % 20)) > floor(progress * (r.nextInt() % 20))) {
                    builder.setProgress(100, (int) (progress * 100), false);
                    manager.notify(Constant.NOTIFY_ID, builder.build());
                    progress = progres;
//                        //下载进度提示
                    builder.setContentText("" + getResources().getString(R.string.download) + (int) (progress * 100) + "%");
                }
            }

            @Override
            public void onSuccess(File file, Call call, Response response) {
                manager.cancel(Constant.NOTIFY_ID);//设置关闭通知栏
                try {
                    DataUtil.installApk(MainActivity.this, apkFilePath, apkFileName);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Toast.makeText(MainActivity.this, e.toString() + "" + getResources().getString(R.string.download_error), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void doDownLoads(String downLink) {
        UpdateAppDialog.setLlBottom(2);
        UpdateAppDialog.setTitle(getResources().getString(R.string.is_downing));
        UpdateAppDialog.setProMax(100);
        Api.getInstance().downLoadFile(this, downLink, new FileCallback(apkFilePath, apkFileName) {

            @Override
            public void downloadProgress(long currentSize, long totalSize, float progres, long networkSpeed) {
                super.downloadProgress(currentSize, totalSize, progres, networkSpeed);
                Random r = new Random();
                if (floor(progres * (r.nextInt() % 20)) > floor(progress * (r.nextInt() % 20))) {
                    progress = progres;
                    double again = 1024 * 1024.00;
                    UpdateAppDialog.setPro((int) (progress * 100), DataUtil.numberTwo(currentSize / again), DataUtil.numberTwo(totalSize / again), DataUtil.numberTwo(progress * 100));
                }
            }

            @Override
            public void onSuccess(File file, Call call, Response response) {
                UpdateAppDialog.dismissDialog(MainActivity.this);
                try {
                    DataUtil.installApk(MainActivity.this, apkFilePath, apkFileName);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                UpdateAppDialog.dismissDialog(MainActivity.this);
                String hint = getResources().getString(R.string.download_error) + "," + getResources().getString(R.string.download_error1);
                Toast.makeText(MainActivity.this, hint, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 获取美元人民币汇率
     */
    private void queryExchangeRate(Context context, String name, String goodName, String id) {
        try {
            KlineApi.queryPrice(context, name, id, 4, new KlineApiImpl() {
                @Override
                public void onSuccess(Object str) {
                    MarketBean marketBeans = (MarketBean) str;
                    if (name.equals("usd")) {
                        usd = Double.parseDouble(marketBeans.getPrice());
                        mapPrice.put("USDT", usd + "");
                        queryCurrencyPrice();
                        StoreUtils.init(context).setParameter("usd", usd + "");
                    } else if (name.equals("krw")) {
                        krw = Double.parseDouble(marketBeans.getPrice());
                        StoreUtils.init(context).setParameter("krw", krw + "");
                    } else if (name.equals("jpy")) {
                        jpy = Double.parseDouble(marketBeans.getPrice());
                        StoreUtils.init(context).setParameter("jpy", jpy + "");
                    }
                }

                @Override
                public void onError(Object str) {
                    Log.e("获取" + goodName + "汇率", str + "");
                    if (name.equals("usd")) {
                        if (StoreUtils.init(context).getParameter("usd") != null)
                            usd = Double.parseDouble(StoreUtils.init(context).getParameter("usd"));
                        mapPrice.put("USDT", usd + "");
                        queryCurrencyPrice();
                    } else if (name.equals("krw")) {
                        if (StoreUtils.init(context).getParameter("krw") != null)
                            krw = Double.parseDouble(StoreUtils.init(context).getParameter("krw"));
                    } else if (name.equals("jpy")) {
                        if (StoreUtils.init(context).getParameter("jpy") != null)
                            krw = Double.parseDouble(StoreUtils.init(context).getParameter("jpy"));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String[] array = new String[]{"BTC", "ETH", "EOS", "XRP", "LTC"};

    private void queryCurrencyPrice() {
        for (String currency : array) {
            queryPrice(this, currency);
        }
    }

    /**
     * 获取加密货币-价格
     */
//    private void queryPrice(Context context) {
//        try {
//            KlineApi.queryFxhPrice(context, new KlineApiImpl() {
//                @Override
//                public void onSuccess(Object str) {
//                    FxhBean fxhBean = new Gson().fromJson(str + "", FxhBean.class);
//                    if (fxhBean.getData() != null) {
//                        for (Fxh fxh : fxhBean.getData()) {
//                            for (String currency : array) {
//                                if (fxh.getName().contains(currency)) {
//                                    mapPrice.put(currency, DataUtil.numberTwo(fxh.getCurrent_price()));
//                                    StoreUtils.init(context).setParameter(currency, DataUtil.numberTwo(fxh.getCurrent_price()));
//                                }
//                            }
//                        }
//                    } else {
//                        setCurrencyPrice();
//                    }
//                }
//
//                @Override
//                public void onError(Object str) {
//                    Log.e("获取实时价格:", str + "");
//                    setCurrencyPrice();
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    private void queryPrice(Context context, String currency) {
        try {
            KlineApi.queryBiAnKlineOne(context, currency, "1day", new KlineApiImpl() {
                @Override
                public void onSuccess(Object str) {
                    JSONArray array = JSON.parseArray(str.toString());
                    if (array.size() > 0) {
                        JSONArray kline = (JSONArray) array.get(0);
                        double close = Double.parseDouble((String) kline.get(4));
                        mapPrice.put(currency, DataUtil.numberTwo(close * usd));
                        StoreUtils.init(context).setParameter(currency, DataUtil.numberTwo(close));

                    } else {
                        setCurrencyPrice();
                    }
                }

                @Override
                public void onError(Object str) {
                    setCurrencyPrice();
                }
            });
        } catch (
                Exception e) {
            e.printStackTrace();
        }

    }

    private void queryHuoBiPrice(Context context) {
        try {
            KlineApi.queryHuoBiPrice(context, new KlineApiImpl() {
                @Override
                public void onSuccess(Object str) {
                    HuoBiBean huoBiBean = new Gson().fromJson(str + "", HuoBiBean.class);
                    if (huoBiBean.getData() != null && huoBiBean.getData().size() > 0) {
                        for (HuoBi huoBi : huoBiBean.getData()) {
                            for (String currency : array) {
                                if (huoBi.getSymbol().contains((DataUtil.toLower(currency) + "usdt"))) {
                                    mapPrice.put(currency, DataUtil.numberTwo(huoBi.getClose() * usd));
                                    StoreUtils.init(context).setParameter(currency, DataUtil.numberTwo(huoBi.getClose() * usd));
                                }
                            }
                        }
                    } else {
                        setCurrencyPrice();
                    }
                }

                @Override
                public void onError(Object str) {
                    Log.e("获取实时价格:", str + "");
                    setCurrencyPrice();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCurrencyPrice() {
        setPrice("BTC");
        setPrice("ETH");
        setPrice("LTC");
        setPrice("XRP");
        setPrice("EOS");
    }

    private void setPrice(String token) {
        if (StoreUtils.init(this).getParameter(token) != null)
            mapPrice.put(token, StoreUtils.init(this).getParameter(token));
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }

    private void UMShareInit() {
        shareAction = new ShareAction(this);
        PlatformName.WEIXIN = getResources().getString(R.string.umeng_share_weixin);
        PlatformName.WEIXIN_CIRCLE = getResources().getString(R.string.umeng_share_weixinq);
        PlatformName.QZONE = getResources().getString(R.string.umeng_share_qqc);

        shareAction.setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE);

        shareConfig = new ShareBoardConfig();
        shareConfig.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_BOTTOM);
        shareConfig.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);
        shareConfig.setCancelButtonVisibility(true);
        shareConfig.setTitleText(getResources().getString(R.string.share));
        shareConfig.setCancelButtonText(getResources().getString(R.string.umeng_share_cancel));
        shareConfig.setTitleVisibility(true);
        shareConfig.setShareboardBackgroundColor(0xffffffff);
        shareConfig.setIndicatorVisibility(false);
        shareAction.setCallback(shareListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == INSTALL_PERMISSION_REQUEST_CODE) {
            //安装权限返回
        }
    }

    /**
     * 友盟分享回调
     */
    private UMShareListener shareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.start_share), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            MData mData = new MData();
            if (platform == SHARE_MEDIA.WEIXIN) {//微信

            } else if (platform == SHARE_MEDIA.WEIXIN_CIRCLE) {//朋友圈

            } else if (platform == SHARE_MEDIA.QQ) {//qq

            } else if (platform == SHARE_MEDIA.QZONE) {//qq空间

            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if (platform == SHARE_MEDIA.WEIXIN || platform == SHARE_MEDIA.WEIXIN_CIRCLE) {
                if (!DataUtil.isWeixinAvilible(MainActivity.this)) {
                    DialogUtils.showErrorDialog(MainActivity.this, getResources().getString(R.string.hint), getResources().getString(R.string.share_no_wx));
                } else {
                    DialogUtils.showErrorDialog(MainActivity.this, getResources().getString(R.string.hint), t.getMessage());
                }
            } else if (platform == SHARE_MEDIA.QQ || platform == SHARE_MEDIA.QZONE) {
                if (!DataUtil.isQQClientAvailable(MainActivity.this)) {
                    DialogUtils.showErrorDialog(MainActivity.this, getResources().getString(R.string.hint), getResources().getString(R.string.share_no_qq));
                } else {
                    DialogUtils.showErrorDialog(MainActivity.this, getResources().getString(R.string.hint), t.getMessage());
                }
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
        }
    };

    // 分享图片
    public void shareImg(Bitmap bitmap) {
        UMImage image = new UMImage(this, bitmap);//本地文件
        UMImage thumb = new UMImage(this, R.mipmap.ic_launcher);
        image.setThumb(thumb);
        image.compressStyle = UMImage.CompressStyle.SCALE;//大小压缩，默认为大小压缩，适合普通很大的图
        //   new ShareAction(this).withMedia(image).share();
        shareAction.withMedia(image);
        shareAction.withText(getResources().getString(R.string.app_name));
        shareAction.open(shareConfig);
    }

    // 分享图片
    public void shareImg(String path) {
        File file = new File(path);
        UMImage image = new UMImage(this, file);//本地文件
        UMImage thumb = new UMImage(this, R.mipmap.ic_launcher);
        image.setThumb(thumb);
        image.compressStyle = UMImage.CompressStyle.SCALE;//大小压缩，默认为大小压缩，适合普通很大的图
        //   new ShareAction(this).withMedia(image).share();
        shareAction.withMedia(image);
        shareAction.withText(getResources().getString(R.string.app_name));
        shareAction.open(shareConfig);
    }

    //分享自定义信息
    protected void shareCustom(String url, String title, String imgUrl, String text) {
        UMWeb web = new UMWeb(url);
        //标题
        web.setTitle(title);
        //缩略图
        UMImage thumb = new UMImage(this, imgUrl);
        web.setThumb(thumb);
        //描述
        web.setDescription(text);
        shareAction.withMedia(web);
        shareAction.withText(text);
        //分享
        shareAction.open(shareConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
//        super.onSaveInstanceState(outState, outPersistentState);
//        invokeFragmentManagerNoteStateNotSaved();
    }

    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();//防止内存泄漏
        ispay = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MenuActivity");
        MobclickAgent.onPause(this);
    }


    private Method noteStateNotSavedMethod;
    private Object fragmentMgr;

    private void invokeFragmentManagerNoteStateNotSaved() {
        //java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return;
        }
        try {
            if (noteStateNotSavedMethod != null && fragmentMgr != null) {
                noteStateNotSavedMethod.invoke(fragmentMgr);
                return;
            }
            Class cls = getClass();
            do {
                cls = cls.getSuperclass();
            } while (!(names[0].equals(cls.getSimpleName())
                    || names[1].equals(cls.getSimpleName())));

            Field fragmentMgrField = prepareField(cls, "mFragments");
            if (fragmentMgrField != null) {
                fragmentMgr = fragmentMgrField.get(this);
                noteStateNotSavedMethod = getDeclaredMethod(fragmentMgr, "noteStateNotSaved");
                if (noteStateNotSavedMethod != null) {
                    noteStateNotSavedMethod.invoke(fragmentMgr);
                }
            }

        } catch (Exception ex) {
        }
    }

    private Field prepareField(Class<?> c, String fieldName) throws NoSuchFieldException {
        while (c != null) {
            try {
                Field f = c.getDeclaredField(fieldName);
                f.setAccessible(true);
                return f;
            } finally {
                c = c.getSuperclass();
            }
        }
        throw new NoSuchFieldException();
    }

    private Method getDeclaredMethod(Object object, String methodName, Class<?>... parameterTypes) {
        Method method = null;
        for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                method = clazz.getDeclaredMethod(methodName, parameterTypes);
                return method;
            } catch (Exception e) {
            }
        }
        return null;
    }

}