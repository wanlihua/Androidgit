package com.dtl.gemini.ui.asset.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dtl.gemini.R;
import com.dtl.gemini.api.Api;
import com.dtl.gemini.base.BaseAppActivity;
import com.dtl.gemini.bean.DataBean;
import com.dtl.gemini.common.baserx.RxSchedulers;
import com.dtl.gemini.common.baserx.RxSubscriber;
import com.dtl.gemini.common.commonwidget.CustomProgressDialog;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.ui.me.activity.ShareActivity;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.EWMUtils;
import com.dtl.gemini.utils.ModifyPhotoUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author DTL
 * @date 2020/4/29
 * 充币
 **/
public class RechargeActivity extends BaseAppActivity implements View.OnClickListener {

    @Bind(R.id.receive_address_code)
    ImageView receiveAddressCode;
    @Bind(R.id.receive_address)
    TextView receiveAddress;

    String currency;
    Bitmap codeBitmap;

    @Override
    public int getLayoutId() {
        return R.layout.activity_recharge;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        init();
        bindListener();
    }

    private void init() {
        Bundle bundle = getIntent().getBundleExtra(Constant.BUNDLE);
        if (bundle != null) {
            currency = bundle.getString(Constant.CURRENCY);
        }
        if (currency != null) {
            setTitle(currency + getResources().getString(R.string.recharge));
        }
        setRightTile(getResources().getString(R.string.recharge_record));
        queryAddress(this);
    }

    private void bindListener() {
        setBackOnClickListener();

        setRightTileClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataUtil.isFastClick()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constant.TYPE, 1);
                    bundle.putString(Constant.CURRENCY, currency);
                    DataUtil.startActivity(RechargeActivity.this, AssetRecordActivity.class, bundle);
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {

    }

    @OnClick({R.id.receive_save_code, R.id.receive_address_copy})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.receive_save_code:
                if (DataUtil.isFastClick()) {
                    if (codeBitmap != null) {
                        try {
                            String photoDownUrl = Environment.getExternalStorageDirectory() + Constant.FILE + Constant.IMG_FILE + "/";
                            String name = getResources().getString(R.string.app_name) + "_" + currency + ".png";
                            ModifyPhotoUtils.saveFile(codeBitmap, photoDownUrl, name);
                            // 发送广播，通知刷新图库的显示
                            String fileName = photoDownUrl + name;
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
                            CustomProgressDialog.dissmissDialog();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        Toast.makeText(RechargeActivity.this, getResources().getString(R.string.photo_down_ok_hint), Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.receive_address_copy:
                if (DataUtil.isFastClick()) {
                    // 将文本内容放到系统剪贴板里。
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(receiveAddress.getText().toString().trim());
                    Toast.makeText(this, getResources().getString(R.string.copy_ok), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void queryAddress(Context context) {
        try {
            Api.getInstance().queryAddress(context, currency).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<DataBean<String>>(context) {
                @Override
                protected void _onNext(DataBean<String> bean) {
                    if (bean != null) {
                        codeBitmap = EWMUtils.createQRImage(bean.getData(), (int) getResources().getDimension(R.dimen.ewm_with), (int) getResources().getDimension(R.dimen.ewm_height));
                        receiveAddressCode.setImageBitmap(codeBitmap);
                        receiveAddress.setText(bean.getData());
                    } else {
                        Toast.makeText(context, getResources().getString(R.string.query_recharge_error), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                protected void _onError(String message) {
                    Toast.makeText(context, getResources().getString(R.string.query_recharge_error) + message, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
