package com.dtl.gemini.common.base;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import com.dtl.gemini.base.permission.OnBooleanListener;
import com.dtl.gemini.R;
import com.dtl.gemini.common.baseapp.AppManager;
import com.dtl.gemini.common.baserx.RxManager;
import com.dtl.gemini.common.commonutils.ChangeModeController;
import com.dtl.gemini.common.commonutils.TUtil;
import com.dtl.gemini.common.commonutils.ToastUitl;
import com.dtl.gemini.common.commonwidget.CustomProgressDialog;
import com.dtl.gemini.common.commonwidget.StatusBarCompat;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.DialogUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.PlatformName;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.ShareBoardConfig;
import org.greenrobot.eventbus.EventBus;
import java.io.File;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.ButterKnife;
/**
 * 基类
 */
public abstract class BaseActivity<T extends BasePresenter, E extends BaseModel> extends AppCompatActivity {
    private OnBooleanListener onPermissionListener;
    protected ShareAction shareAction;
    protected ShareBoardConfig shareConfig;
    public T mPresenter;
    public E mModel;
    public Context mContext;
    public RxManager mRxManager;
    private CustomProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mRxManager = new RxManager();
        mPresenter = TUtil.getT(this, 0);
        mModel = TUtil.getT(this, 1);
        if (mPresenter != null) {
            mPresenter.mContext = this;
        }
        doBeforeSetcontentView();
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);//注册事件总线EventBus
        UMShareInit();
        this.initPresenter();
        this.initView();
    }

    /**
     * 设置layout前配置
     */
    protected void doBeforeSetcontentView() {
        //设置昼夜主题
        initTheme();
        // 把actvity放到application栈中管理
        AppManager.getAppManager().addActivity(this);
        // 无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置竖屏
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 默认着色状态栏
        SetStatusBarColor();
    }

    /*********************子类实现*****************************/
    //获取布局文件
    public abstract int getLayoutId();

    //简单页面无需mvp就不用管此方法即可,完美兼容各种实际场景的变通
    public abstract void initPresenter();

    //初始化view
    public abstract void initView();


    /**
     * 设置主题
     */
    protected void initTheme() {
        ChangeModeController.setTheme(this, R.style.DayTheme, R.style.NightTheme);
    }

    /**
     * 着色状态栏（5.0以上系统有效）
     */
    protected void SetStatusBarColor() {
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white));
    }

    /**
     * 通过Class跳转界面
     **/
    public void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /**
     * 通过Class跳转界面
     **/
    public void startActivityForResult(Class<?> cls, int requestCode) {
        startActivityForResult(cls, null, requestCode);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivityForResult(Class<?> cls, Bundle bundle,
                                       int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 开启浮动加载进度条
     */
    public void startProgressDialog() {
        startProgressDialog("");
    }

    /**
     * 开启浮动加载进度条
     *
     * @param msg
     */
    public void startProgressDialog(String msg) {
        if (dialog == null) {
            dialog = CustomProgressDialog.createDialog(this);
        }
        dialog.setMessage(msg);
        dialog.show();
    }

    /**
     * 停止浮动加载进度条
     */
    public void stopProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * 短暂显示Toast提示(来自String)
     **/
    public void showShortToast(String text) {
        if (text != null && !text.equals("")) {
            if (text.equals("服务器数据异常")) {
                DialogUtils.dismissDialog(this);
                DialogUtils.showErrorDialog(this, mContext.getResources().getString(R.string.hint), getResources().getString(R.string.server_error));
            } else if (text.indexOf("Failed to connect to /") != -1) {
                DialogUtils.dismissDialog(this);
                DialogUtils.showErrorDialog(this, mContext.getResources().getString(R.string.hint), getResources().getString(R.string.server_error));
            } else {
                DialogUtils.dismissDialog(this);
                DialogUtils.showErrorDialog(this, mContext.getResources().getString(R.string.hint), text);
            }
        }
    }

    /**
     * 短暂显示Toast提示(id)
     **/
    public void showShortToast(int resId) {
        ToastUitl.showShort(resId);
    }

    /**
     * 长时间显示Toast提示(来自res)
     **/
    public void showLongToast(int resId) {
        ToastUitl.showLong(resId);
    }

    /**
     * 长时间显示Toast提示(来自String)
     **/
    public void showLongToast(String text) {
        ToastUitl.showLong(text);
    }

    /**
     * 带图片的toast
     *
     * @param text
     * @param res
     */
    public void showToastWithImg(String text, int res) {
        ToastUitl.showToastWithImg(text, res);
    }

    /**
     * 网络访问错误提醒
     */
    public void showNetErrorTip() {
        Toast.makeText(this, mContext.getResources().getString(R.string.no_net), Toast.LENGTH_SHORT).show();
    }

    public void showNetErrorTip(String error) {
        Toast.makeText(this, mContext.getResources().getString(R.string.no_net), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //debug版本不统计crash
//        if (!BuildConfig.LOG_DEBUG) {
//            //友盟统计
//        }
        MobclickAgent.onPageStart("BaseActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //debug版本不统计crash
//        if (!BuildConfig.LOG_DEBUG) {
//            //友盟统计
//        }
        MobclickAgent.onPageEnd("BaseActivity");
        MobclickAgent.onPause(this);
    }

    private void UMShareInit() {
        shareAction = new ShareAction(this);
        PlatformName.WEIXIN = mContext.getResources().getString(R.string.umeng_share_weixin);
        PlatformName.WEIXIN_CIRCLE = mContext.getResources().getString(R.string.umeng_share_weixinq);
        PlatformName.QZONE = mContext.getResources().getString(R.string.umeng_share_qqc);
        shareAction.setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.KAKAO);
        shareConfig = new ShareBoardConfig();
        shareConfig.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_BOTTOM);
        shareConfig.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);
        shareConfig.setCancelButtonVisibility(true);
        shareConfig.setTitleText(mContext.getResources().getString(R.string.share));
        shareConfig.setCancelButtonText(mContext.getResources().getString(R.string.umeng_share_cancel));
        shareConfig.setTitleVisibility(true);
        shareConfig.setShareboardBackgroundColor(0xffffffff);
        shareConfig.setIndicatorVisibility(false);
        shareAction.setCallback(shareListener);
    }

    public void onPermissionRequests(String permission, OnBooleanListener onBooleanListener) {
        onPermissionListener = onBooleanListener;
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                //权限已有
                onPermissionListener.onClick(true);
            } else {
                //没有权限，申请一下
                ActivityCompat.requestPermissions(this,
                        new String[]{permission},
                        1);
            }
        } else {
            onPermissionListener.onClick(true);
        }
    }


    //友盟分享回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private UMShareListener shareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.start_share), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            if (platform == SHARE_MEDIA.WEIXIN) {//微信

            } else if (platform == SHARE_MEDIA.WEIXIN_CIRCLE) {//朋友圈

            } else if (platform == SHARE_MEDIA.QQ) {//qq

            } else if (platform == SHARE_MEDIA.QZONE) {//qq空间

            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if (platform == SHARE_MEDIA.WEIXIN || platform == SHARE_MEDIA.WEIXIN_CIRCLE) {
                if (!DataUtil.isWeixinAvilible(mContext)) {
                    DialogUtils.showErrorDialog(mContext, mContext.getResources().getString(R.string.hint), mContext.getResources().getString(R.string.share_no_wx));
                } else {
                    DialogUtils.showErrorDialog(mContext, mContext.getResources().getString(R.string.hint), t.getMessage());
                }
            } else if (platform == SHARE_MEDIA.QQ || platform == SHARE_MEDIA.QZONE) {
                if (!DataUtil.isQQClientAvailable(mContext)) {
                    DialogUtils.showErrorDialog(mContext, mContext.getResources().getString(R.string.hint), mContext.getResources().getString(R.string.share_no_qq));
                } else {
                    DialogUtils.showErrorDialog(mContext, mContext.getResources().getString(R.string.hint), t.getMessage());
                }
            } else {
                DialogUtils.showErrorDialog(mContext, mContext.getResources().getString(R.string.hint), t.getMessage());
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
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.onDestroy();
        mRxManager.clear();
        ButterKnife.unbind(this);
        AppManager.getAppManager().finishActivity(this);
        UMShareAPI.get(this).release();//防止内存泄漏
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);//取消注册事件总线EventBus
        }

    }

    protected String getWidgetContent(TextView view) {
        return view.getText().toString().replaceAll(" ", "").trim();
    }


    /**
     * 事件总线EventBus，使子类处理消息
     */
    public abstract void event(MData mData);

}