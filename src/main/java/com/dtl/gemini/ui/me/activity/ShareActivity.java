package com.dtl.gemini.ui.me.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dtl.gemini.PunkApplication;
import com.dtl.gemini.R;
import com.dtl.gemini.api.Api;
import com.dtl.gemini.base.BaseAppActivity;
import com.dtl.gemini.bean.DataBean;
import com.dtl.gemini.common.baserx.RxSchedulers;
import com.dtl.gemini.common.baserx.RxSubscriber;
import com.dtl.gemini.common.commonwidget.CustomProgressDialog;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.db.StoreUtils;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.model.User;
import com.dtl.gemini.ui.me.model.UserShareInfo;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.ImageHelper;
import com.dtl.gemini.utils.ModifyPhotoUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.download.DownloadInfo;
import com.lzy.okserver.listener.DownloadListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 分享页面
 *
 * @author DTL
 * @date 2020/5/22
 **/
public class ShareActivity extends BaseAppActivity {
    @Bind(R.id.iv_bg)
    ImageView ivBg;
    @Bind(R.id.tv_down_url)
    TextView tvDownUrl;
    @Bind(R.id.iv_down_url_copy)
    ImageView ivDownUrlCopy;
    @Bind(R.id.tv_invitation_code)
    TextView tvInvitationCode;
    @Bind(R.id.iv_invitation_code_copy)
    ImageView ivInvitationCodeCopy;
    @Bind(R.id.btn_share_url)
    Button btnShareUrl;
    @Bind(R.id.btn_share_img)
    Button btnShareImg;
    @Bind(R.id.tv_direct_push_num)
    TextView tvDirectPushNum;
    @Bind(R.id.tv_team_performance)
    TextView tvTeamPerformance;

    String photoDownUrl;
    Bitmap bitmap;

    UserShareInfo userShareInfo;

    @Override
    public int getLayoutId() {
        return R.layout.activity_share;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        setTitle(getResources().getString(R.string.share_frends));

        setBackOnClickListener();
        User user = StoreUtils.init(this).getLoginUser();
        if (user != null) {
            tvInvitationCode.setText(user.getInvitationCode());
            tvDirectPushNum.setText(user.getDirectPushNum() + "");
        }
        getShareInfo(this);
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_share_qrcode);
        photoDownUrl = Environment.getExternalStorageDirectory() + Constant.FILE + Constant.IMG_FILE + "/";

        ivDownUrlCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataUtil.isFastClick()) {
                    DataUtil.copyString(ShareActivity.this, userShareInfo.getShareDownUrl());
                }
            }
        });

        ivInvitationCodeCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataUtil.isFastClick()) {
                    DataUtil.copyString(ShareActivity.this, tvInvitationCode);
                }
            }
        });

        btnShareUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataUtil.isFastClick()) {
                    DataUtil.copyString(ShareActivity.this, tvDownUrl);
                }
            }
        });

        btnShareImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataUtil.isFastClick()) {
                    CustomProgressDialog.showDialog(ShareActivity.this);
                    saveQrcode(getResources().getString(R.string.app_name) + ".png");
//                    down(ShareActivity.this, getResources().getString(R.string.app_name) + ".png");
                }
            }
        });


    }

    long statTime = System.currentTimeMillis();

    private void down(Context context, String name) {
        try {
            File file = new File(photoDownUrl, name);
            if (file.exists()) {
                file.mkdirs();
            }
            statTime = System.currentTimeMillis();
            GetRequest request = OkGo.get(userShareInfo.getShareImgUrl());
            PunkApplication.manger.addTask(userShareInfo.getShareImgUrl(), request, new DownloadListener() {
                @Override
                public void onProgress(DownloadInfo downloadInfo) {
                    if (System.currentTimeMillis() - statTime > 3000) {
                        saveQrcode(name);
                    }
                }

                @Override
                public void onFinish(DownloadInfo downloadInfo) {
                    // 发送广播，通知刷新图库的显示
                    String fileName = photoDownUrl + name;
                    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
                    CustomProgressDialog.dissmissDialog();
                    Toast.makeText(ShareActivity.this, getResources().getString(R.string.photo_down_ok_hint), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(DownloadInfo downloadInfo, String errorMsg, Exception e) {
                    saveQrcode(name);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveQrcode(String fileName) {
        CustomProgressDialog.dissmissDialog();
        if (bitmap != null) {
            try {
                PunkApplication.manger.removeTask(userShareInfo.getShareImgUrl());
                ModifyPhotoUtils.saveFile(bitmap, photoDownUrl, fileName);
                // 发送广播，通知刷新图库的显示
                String fileUrl = photoDownUrl + fileName;
                File file = new File(photoDownUrl, fileName);
                //把文件插入到系统图库
                try {
                    MediaStore.Images.Media.insertImage(getContentResolver(),
                            file.getAbsolutePath(), fileName, null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //通知图库更新
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileUrl)));

                CustomProgressDialog.dissmissDialog();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Toast.makeText(ShareActivity.this, getResources().getString(R.string.photo_down_ok_hint), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 获取邀请信息
     */
    private void getShareInfo(Context context) {
        try {
            Api.getInstance().getShareInfo(context).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<DataBean<UserShareInfo>>(context) {
                @Override
                protected void _onNext(DataBean<UserShareInfo> bean) {
                    if (bean != null && bean.getData() != null) {
                        userShareInfo = bean.getData();
                        ImageHelper.init().showImage(context, R.mipmap.icon_share_bg, userShareInfo.getShareBgImgUrl(), ivBg);
                        tvDownUrl.setText(userShareInfo.getShareDownUrl());
                        tvDirectPushNum.setText(userShareInfo.getDirectPushNum() + "");
                        tvTeamPerformance.setText(userShareInfo.getSumPerformance());
                    }
                }

                @Override
                protected void _onError(String message) {
                    Log.e("获取邀请信息", message + "");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {

    }

}
