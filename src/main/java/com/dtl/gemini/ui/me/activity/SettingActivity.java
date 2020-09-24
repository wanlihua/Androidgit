package com.dtl.gemini.ui.me.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dtl.gemini.R;
import com.dtl.gemini.api.Api;
import com.dtl.gemini.base.BaseAppActivity;
import com.dtl.gemini.bean.BaseBean;
import com.dtl.gemini.common.baserx.RxSchedulers;
import com.dtl.gemini.common.baserx.RxSubscriber;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.db.StoreUtils;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.model.User;
import com.dtl.gemini.ui.me.beans.UploadBean;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.DialogUtils;
import com.dtl.gemini.utils.ImageHelper;
import com.dtl.gemini.utils.ModifyPhotoUtils;
import com.dtl.gemini.widget.CircleImageView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author DTL
 * @date 2020/4/28
 * 系统设置
 **/
public class SettingActivity extends BaseAppActivity implements View.OnClickListener {

    @Bind(R.id.user_head)
    CircleImageView userHead;
    @Bind(R.id.update_username_tv)
    TextView updateUsernameTv;
    @Bind(R.id.language_tv)
    TextView languageTv;

    User user;

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        setBackOnClickListener();
        user = StoreUtils.init(this).getLoginUser();
        if (user.getHeadUrl() != null)
            ImageHelper.init().showHeadImage(user.getHeadUrl().toString(), userHead);
        updateUsernameTv.setText(user.getUsername());
        languageTv.setText(DataUtil.returnLanguuageName(this));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {

    }

    @OnClick({R.id.update_head_img, R.id.update_username, R.id.update_pwd, R.id.update_asset_pwd,
            R.id.update_language})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_head_img:
                if (DataUtil.isFastClick()) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        //请求权限
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 99);
                    } else {
                        ModifyPhotoUtils.modifyUserPhoto(this);
                    }
                }
                break;
            case R.id.update_username:
                if (DataUtil.isFastClick()) {
                    DialogUtils.showInputDialog(this, getResources().getString(R.string.update_username), updateUsernameTv.getText().toString().trim());
                    DialogUtils.btn(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String s = DialogUtils.getEt1String();
                            if (s.equals("")) {
                                Toast.makeText(SettingActivity.this, DataUtil.returnNoNullHint(SettingActivity.this, getResources().getString(R.string.username)), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            DataUtil.hideJianPan(SettingActivity.this, DialogUtils.et1);
                            updateUsername(SettingActivity.this, s);
                        }
                    });
                }
                break;
            case R.id.update_pwd:
                if (DataUtil.isFastClick()) {
                    DataUtil.startActivity(this, UpdatePwdActivity.class);
                }
                break;
            case R.id.update_asset_pwd:
                if (DataUtil.isFastClick()) {
                    DataUtil.startActivity(this, UpdateAssetPwdActivity.class);
                }
                break;
            case R.id.update_language:
                if (DataUtil.isFastClick()) {
                    startActivity(new Intent(this, LanguageActivity.class));
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case ModifyPhotoUtils.GET_IMAGE_BY_CAMERA_U:
                    /*
                     * 这里我做了一下调用系统切图，高版本也有需要注意的地方
                     * */
                    if (ModifyPhotoUtils.imageUriFromCamera != null) {
                        ModifyPhotoUtils.cropImage(this, ModifyPhotoUtils.imageUriFromCamera, 1, 400, 400, ModifyPhotoUtils.CROP_IMAGE_U);
                        break;
                    }
                    break;
                case ModifyPhotoUtils.GET_IMAGE_BY_GALLERY_U:
                    if (data != null) {
                        ModifyPhotoUtils.cropImage(this, data.getData(), 1, 400, 400, ModifyPhotoUtils.CROP_IMAGE_U);// 裁剪图片
                        break;
                    }
                    break;
                case ModifyPhotoUtils.CROP_IMAGE_U:
                    final String s = getExternalCacheDir() + "/" + ModifyPhotoUtils.USER_CROP_IMAGE_NAME;
                    Bitmap imageBitmap = ModifyPhotoUtils.GetBitmap(s, 400, 400);
                    if (imageBitmap != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        imageBitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
                        uploadFile(this, imageBitmap);
                    }
                    break;
            }
        }
    }

    /**
     * 上传照片
     */
    private void uploadFile(Context context, Bitmap bitmap) {
        try {
            File file = ModifyPhotoUtils.saveFile(bitmap, context.getExternalCacheDir() + Constant.IMG_FILE, "head.jpg");
            Api.getInstance().uploadFile(context, file,Constant.FILE_HEAD).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<UploadBean>(context) {
                @Override
                protected void _onNext(UploadBean bean) {
                    if (bean.getData() != null && bean.getData().size() > 0) {
                        updateHeadImg(context, bean.getData().get(0).getUrl());
                    }
                }

                @Override
                protected void _onError(String msg) {
                    Toast.makeText(context, getResources().getString(R.string.upload_error), Toast.LENGTH_SHORT).show();
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置头像
     */
    private void updateHeadImg(Context context, String url) {
        try {
            Api.getInstance().updateHeadImg(context, url).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<BaseBean>(context) {
                @Override
                protected void _onNext(BaseBean bean) {
                    if (bean != null) {
                        if (userHead != null) {
                            ImageHelper.init().showHeadImage(url, userHead);
                            StoreUtils.init(context).storeUserHead(url);
                            Toast.makeText(context, getResources().getString(R.string.upload_ok), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                protected void _onError(String msg) {
                    Toast.makeText(context, getResources().getString(R.string.upload_error), Toast.LENGTH_SHORT).show();
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改昵称
     */
    private void updateUsername(Context context, String username) {
        try {
            Api.getInstance().updateUserNickname(context, username).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<BaseBean>(context) {
                @Override
                protected void _onNext(BaseBean bean) {
                    if (bean.getStatus() == Constant.SUCCESS) {
                        Toast.makeText(context, getResources().getString(R.string.update_ok), Toast.LENGTH_SHORT).show();
                        StoreUtils.init(context).storeUsername(username);
                        user = StoreUtils.init(context).getLoginUser();
                        updateUsernameTv.setText(user.getUsername());
                        DialogUtils.dismissDialog();
                    }
                }

                @Override
                protected void _onError(String msg) {
                    Toast.makeText(context, msg + "", Toast.LENGTH_SHORT).show();
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
