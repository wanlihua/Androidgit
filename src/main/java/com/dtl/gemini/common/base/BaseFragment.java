package com.dtl.gemini.common.base;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.dtl.gemini.base.permission.OnBooleanListener;
import com.dtl.gemini.R;
import com.dtl.gemini.common.baserx.RxManager;
import com.dtl.gemini.common.commonutils.TUtil;
import com.dtl.gemini.common.commonutils.ToastUitl;
import com.dtl.gemini.common.commonwidget.CustomProgressDialog;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.DialogUtils;
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
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;

/**
 * des:基类fragment
 * Created by xsf
 * on 2016.07.12:38
 */

/***************使用例子*********************/
//1.mvp模式
//public class SampleFragment extends BaseFragment<NewsChanelPresenter, NewsChannelModel>implements NewsChannelContract.View {
//    @Override
//    public int getLayoutId() {
//        return R.layout.activity_news_channel;
//    }
//
//    @Override
//    public void initPresenter() {
//        mPresenter.setVM(this, mModel);
//    }
//
//    @Override
//    public void initView() {
//    }
//}
//2.普通模式
//public class SampleFragment extends BaseFragment {
//    @Override
//    public int getLayoutResource() {
//        return R.layout.activity_news_channel;
//    }
//
//    @Override
//    public void initPresenter() {
//    }
//
//    @Override
//    public void initView() {
//    }
//}
public abstract class BaseFragment<T extends BasePresenter, E extends BaseModel> extends Fragment {
    private OnBooleanListener onPermissionListener;
    protected ShareAction shareAction;
    protected ShareBoardConfig shareConfig;
    protected View rootView;
    public T mPresenter;
    public E mModel;
    public RxManager mRxManager;
    private CustomProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null)
            rootView = inflater.inflate(getLayoutResource(), container, false);
        mRxManager = new RxManager();
        ButterKnife.bind(this, rootView);
        EventBus.getDefault().register(this);//注册事件总线EventBus
        UMShareInit();
        mPresenter = TUtil.getT(this, 0);
        mModel = TUtil.getT(this, 1);
        if (mPresenter != null) {
            mPresenter.mContext = this.getActivity();
        }
        initPresenter();
        initView();
        return rootView;
    }

    //获取布局文件
    protected abstract int getLayoutResource();

    //简单页面无需mvp就不用管此方法即可,完美兼容各种实际场景的变通
    public abstract void initPresenter();

    //初始化view
    protected abstract void initView();


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
        intent.setClass(getActivity(), cls);
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
        intent.setClass(getActivity(), cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }


    /**
     * 开启加载进度条
     */
    public void startProgressDialog() {
        startProgressDialog("");
    }

    /**
     * 开启加载进度条
     *
     * @param msg
     */
    public void startProgressDialog(String msg) {
        if (dialog == null) {
            dialog = CustomProgressDialog.createDialog(getActivity());
        }
        dialog.setMessage(msg);
        dialog.show();
    }

    /**
     * 停止加载进度条
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
            if (text.equals("服务器数据异常"))
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity(), text + "", Toast.LENGTH_SHORT).show();
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


    public void showToastWithImg(String text, int res) {
        ToastUitl.showToastWithImg(text, res);
    }

    /**
     * 网络访问错误提醒
     */
    public void showNetErrorTip() {
        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
    }

    public void showNetErrorTip(String error) {
        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (mPresenter != null)
            mPresenter.onDestroy();
        mRxManager.clear();
        UMShareAPI.get(getActivity()).release();//防止内存泄漏
        EventBus.getDefault().unregister(this);//取消注册事件总线EventBus
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//取消注册事件总线EventBus
    }


    public void onPermissionRequests(String permission, OnBooleanListener onBooleanListener) {
        onPermissionListener = onBooleanListener;
        if (ContextCompat.checkSelfPermission(getActivity(),
                permission)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {
                //权限已有
                onPermissionListener.onClick(true);
            } else {
                //没有权限，申请一下
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{permission},
                        1);
            }
        } else {
            onPermissionListener.onClick(true);
        }
    }


    /**
     * 事件总线EventBus，使子类处理消息
     */
    public abstract void event(MData mData);
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        MobclickAgent.onPageStart("BaseFragment");
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        MobclickAgent.onPageEnd("BaseFragment");
//    }

    private void UMShareInit() {
        shareAction = new ShareAction(getActivity());
        PlatformName.WEIXIN = getActivity().getResources().getString(R.string.umeng_share_weixin);
        PlatformName.WEIXIN_CIRCLE = getActivity().getResources().getString(R.string.umeng_share_weixinq);
        PlatformName.QZONE = getActivity().getResources().getString(R.string.umeng_share_qqc);

        shareAction.setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE);
        shareConfig = new ShareBoardConfig();
        shareConfig.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_BOTTOM);
        shareConfig.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);
        shareConfig.setCancelButtonVisibility(true);
        shareConfig.setTitleText(getActivity().getResources().getString(R.string.share));
        shareConfig.setCancelButtonText(getActivity().getResources().getString(R.string.umeng_share_cancel));
        shareConfig.setTitleVisibility(true);
        shareConfig.setShareboardBackgroundColor(0xffffffff);
        shareConfig.setIndicatorVisibility(false);
        shareAction.setCallback(shareListener);
    }

    //友盟分享回调
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(getActivity()).onActivityResult(requestCode, resultCode, data);
    }

    private UMShareListener shareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.start_share), Toast.LENGTH_SHORT).show();
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
                if (!DataUtil.isWeixinAvilible(getActivity())) {
                    DialogUtils.showErrorDialog(getActivity(), getActivity().getResources().getString(R.string.hint), getActivity().getResources().getString(R.string.share_no_wx));
                } else {
                    DialogUtils.showErrorDialog(getActivity(), getActivity().getResources().getString(R.string.hint), t.getMessage());
                }
            } else if (platform == SHARE_MEDIA.QQ || platform == SHARE_MEDIA.QZONE) {
                if (!DataUtil.isQQClientAvailable(getActivity())) {
                    DialogUtils.showErrorDialog(getActivity(), getActivity().getResources().getString(R.string.hint), getActivity().getResources().getString(R.string.share_no_qq));
                } else {
                    DialogUtils.showErrorDialog(getActivity(), getActivity().getResources().getString(R.string.hint), t.getMessage());
                }
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
        }
    };


    // 分享图片
    public void shareImg(Bitmap bitmap) {
        UMImage image = new UMImage(getActivity(), bitmap);//本地文件
        UMImage thumb = new UMImage(getActivity(), R.mipmap.ic_launcher);
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
        UMImage image = new UMImage(getActivity(), file);//本地文件
        UMImage thumb = new UMImage(getActivity(), R.mipmap.ic_launcher);
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
        UMImage thumb = new UMImage(getActivity(), imgUrl);
        web.setThumb(thumb);
        //描述
        web.setDescription(text);
        shareAction.withMedia(web);
        shareAction.withText(text);
        //分享
        shareAction.open(shareConfig);
    }

}
