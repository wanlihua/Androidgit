package com.dtl.gemini.ui.me.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.dtl.gemini.common.commonwidget.CustomProgressDialog;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.db.StoreUtils;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.model.User;
import com.dtl.gemini.ui.me.beans.UploadBean;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.ImageHelper;
import com.dtl.gemini.utils.ModifyPhotoUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 实名认证
 *
 * @author DTL
 * @date 2020/8/6
 **/
public class RealNameActivity extends BaseAppActivity implements View.OnClickListener {

    @Bind(R.id.real_name)
    EditText realName;
    @Bind(R.id.real_name_id)
    EditText realNameId;
    @Bind(R.id.real_name_upload_1_iv)
    ImageView realNameUpload1Iv;
    @Bind(R.id.real_name_upload_2_iv)
    ImageView realNameUpload2Iv;
    @Bind(R.id.real_name_error)
    TextView realNameError;
    @Bind(R.id.real_name_bt)
    Button realNameBt;

    private User user;

    private String idName, idNumber, url1, url2;
    private int currImageType = 1;

    @Override
    public int getLayoutId() {
        return R.layout.activity_real_name;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        setTitle(getResources().getString(R.string.real_name));
        setBackOnClickListener();
        Bundle bundle = getIntent().getBundleExtra(Constant.BUNDLE);
        if (bundle != null) {
            user = (User) bundle.getSerializable("user");
        }
        if (user != null && user.getStatus() != null) {
            if (user.getRealName() != null)
                realName.setText(user.getRealName());
            if (user.getIdNumber() != null)
                realNameId.setText(user.getIdNumber());
            if (user.getIdPositiveUrl() != null)
                ImageHelper.init().showImage(this, R.mipmap.icon_img_idcard_1, user.getIdPositiveUrl(), realNameUpload1Iv);
            if (user.getIdBackUrl() != null)
                ImageHelper.init().showImage(this, R.mipmap.icon_img_idcard_2, user.getIdBackUrl(), realNameUpload2Iv);
            if (user.getStatus() == 2) {
                realNameError.setVisibility(View.VISIBLE);
                realNameError.setText(user.getExtra());
//                realNameBt.setText(getResources().getString(R.string.status2));
            } else {
                realNameBt.setBackgroundResource(R.drawable.item_coin_asset_gray);
                realName.setEnabled(false);
                realName.setClickable(false);
                realNameId.setEnabled(false);
                realNameId.setClickable(false);
                realNameUpload1Iv.setEnabled(false);
                realNameUpload1Iv.setClickable(false);
                realNameUpload2Iv.setEnabled(false);
                realNameUpload2Iv.setClickable(false);
                realNameBt.setEnabled(false);
                realNameBt.setClickable(false);
                if (user.getStatus() == 0) {
                    realNameBt.setText(getResources().getString(R.string.status0));
                } else {
                    realNameBt.setText(getResources().getString(R.string.status1));
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case ModifyPhotoUtils.GET_IMAGE_BY_CAMERA_U:
                    if (ModifyPhotoUtils.imageUriFromCamera != null)
                        ModifyPhotoUtils.cropImage(this, ModifyPhotoUtils.imageUriFromCamera, 0.1, 500, 500, ModifyPhotoUtils.CROP_IMAGE_U);
                    break;
                case ModifyPhotoUtils.GET_IMAGE_BY_GALLERY_U:
                    if (data != null)
                        ModifyPhotoUtils.cropImage(this, data.getData(), 0.1, 550, 550, ModifyPhotoUtils.CROP_IMAGE_U);// 裁剪图片
                    break;
                case ModifyPhotoUtils.CROP_IMAGE_U:
                    String s = getExternalCacheDir() + "/" + ModifyPhotoUtils.USER_CROP_IMAGE_NAME;
                    Bitmap imageBitmap = ModifyPhotoUtils.GetBitmap(s, 550, 550);
                    if (imageBitmap != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        imageBitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
                        dealBitmap(imageBitmap);
                    }
                    break;
            }
        }
    }

    /**
     * 上传照片
     */
    private void dealBitmap(Bitmap bitmap) {
        try {
            String filename = "";
            if (currImageType == 1) {
                filename = "realname1.png";
            } else if (currImageType == 2) {
                realNameUpload2Iv.setImageBitmap(bitmap);
                filename = "realname2.png";
            }
            uploadFile(this, bitmap, filename);
        } catch (Exception e) {

        }
    }


    /**
     * 上传照片
     */
    private void uploadFile(Context context, Bitmap bitmap, String filename) {
        try {
            File file = ModifyPhotoUtils.saveFile(bitmap, context.getExternalCacheDir() + Constant.IMG_FILE, filename);
            Api.getInstance().uploadFile(context, file, Constant.FILE_REAL_NAME).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<UploadBean>(context) {
                @Override
                protected void _onNext(UploadBean bean) {
                    if (bean.getData() != null && bean.getData().size() > 0) {
                        if (currImageType == 1) {
                            realNameUpload1Iv.setImageBitmap(bitmap);
                            url1 = bean.getData().get(0).getUrl();
                        } else if (currImageType == 2) {
                            realNameUpload2Iv.setImageBitmap(bitmap);
                            url2 = bean.getData().get(0).getUrl();
                        }
                    }
                    CustomProgressDialog.dissmissDialog();
                }

                @Override
                protected void _onError(String msg) {
                    Toast.makeText(context, getResources().getString(R.string.upload_error), Toast.LENGTH_SHORT).show();
                    CustomProgressDialog.dissmissDialog();
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提交信息
     */
    private void sumbitUserVerified(Context context) {
        try {
            Api.getInstance().sumbitUserVerified(context, idName, idNumber, url1, url2).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<BaseBean>(context) {
                @Override
                protected void _onNext(BaseBean bean) {
                    if (bean != null && bean.getStatus() == 200) {
                        Toast.makeText(context, bean.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                protected void _onError(String msg) {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
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

    @OnClick({R.id.real_name_upload_1_iv, R.id.real_name_upload_2_iv, R.id.real_name_bt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.real_name_upload_1_iv:
                currImageType = 1;
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //请求权限
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 99);
                } else {
                    ModifyPhotoUtils.modifyUserPhoto(this);
                }
                break;
            case R.id.real_name_upload_2_iv:
                currImageType = 2;
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //请求权限
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 99);
                } else {
                    ModifyPhotoUtils.modifyUserPhoto(this);
                }
                break;
            case R.id.real_name_bt:
                if (DataUtil.isFastClick()) {
                    idName = realName.getText().toString().trim();
                    idNumber = realNameId.getText().toString().trim();
                    if (idName == null || idName.equals("")) {
                        Toast.makeText(mContext, getResources().getString(R.string.rz1_hint5), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (idNumber == null || idNumber.equals("")) {
                        Toast.makeText(mContext, getResources().getString(R.string.rz1_hint7), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (url1 == null || url1.equals("")) {
                        Toast.makeText(mContext, getResources().getString(R.string.rz2_hint1), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (url2 == null || url2.equals("")) {
                        Toast.makeText(mContext, getResources().getString(R.string.rz2_hint2), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    sumbitUserVerified(this);
                }
                break;
        }
    }

}
