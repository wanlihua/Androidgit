package com.dtl.gemini.ui.asset.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
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
import com.dtl.gemini.ui.asset.beans.AssetWalletBean;
import com.dtl.gemini.ui.asset.beans.WithdrawalSysBean;
import com.dtl.gemini.ui.asset.model.AssetWallet;
import com.dtl.gemini.ui.asset.model.WithdrawalSys;
import com.dtl.gemini.ui.me.activity.UpdateAssetPwdActivity;
import com.dtl.gemini.ui.other.activity.LoginActivity;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.DialogUtils;
import com.dtl.gemini.utils.MdataUtils;
import com.dtl.gemini.widget.AutoWheelChoicePopup;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author DTL
 * @date 2020/4/29
 * 提币
 **/
public class WithdrawalActivity extends BaseAppActivity implements View.OnClickListener {

    @Bind(R.id.tokentype_tv)
    TextView tokentypeTv;
    @Bind(R.id.address_et)
    EditText addressEt;
    @Bind(R.id.withdrawal_count)
    EditText withdrawalCount;
    @Bind(R.id.useable_count)
    TextView useableCount;
    @Bind(R.id.withdrawal_fee)
    TextView withdrawalFee;
    @Bind(R.id.tokentype_tv2)
    TextView tokentypeTv2;
    @Bind(R.id.the_account_tv)
    TextView theAccountTv;
    @Bind(R.id.tokentype_tv3)
    TextView tokentypeTv3;
    @Bind(R.id.tv_fee_rate)
    TextView tvFeeRate;

    Context context;
    AutoWheelChoicePopup popup;
    List<String> currencyList = new ArrayList<>();

    public static List<WithdrawalSys> withdrawalSysList = new ArrayList<>();
    String currency, address;
    double usableAmount, minAmount = 0.00, feeAmount = 0.00, fee = 5;

    boolean isRunning = false, pwdSee = false;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                if (msg.what == 0) {
                    if (DialogUtils.time != null) {
                        DialogUtils.time.setText(getResources().getText(R.string.send));
                    }
                    setGetCodeEnd(true);
                } else {
                    if (DialogUtils.time != null) {
                        DialogUtils.time.setText(msg.what + "s");
                    }
                }
            } catch (Exception e) {
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_withdrawal;
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
        tvFeeRate.setText(getResources().getString(R.string.withdrawal_fee_rate) + DataUtil.numberFourUp(fee));
        context = this;
        Bundle bundle = getIntent().getBundleExtra(Constant.BUNDLE);
        if (bundle != null) {
            currency = bundle.getString(Constant.CURRENCY);
        }
        setRightTile(getResources().getString(R.string.withdraw_record));
        queryWithdrawalSys(this);
    }

    private void refershData() {
        if (currency != null) {
            setTitle(currency + getResources().getString(R.string.withdraw));
            tokentypeTv.setText(currency);
            tokentypeTv2.setText(currency);
            tokentypeTv3.setText(currency);
        }
        queryAssets(this);
        for (WithdrawalSys sys : withdrawalSysList) {
            if (currency.equals(sys.getCurrency())) {
                fee = sys.getWithdrawalFee();
                minAmount = sys.getMinimumWithdrawalAmount();
            }
        }
        tvFeeRate.setText(getResources().getString(R.string.withdrawal_fee_rate) + DataUtil.numberFourUp(fee));
        withdrawalCount.setHint(getResources().getString(R.string.min_withdrawal_number) + " " + minAmount + currency);
        setFeeAmount();
    }

    private void setFeeAmount() {
        feeAmount = fee;
        withdrawalFee.setText(DataUtil.doubleFour(feeAmount));
        double theAmount = returnWithdrawAmount() - feeAmount;
        if (theAmount < 0) {
            theAmount = 0;
        }
        theAccountTv.setText(DataUtil.doubleFour(theAmount));
    }

    private void bindListener() {
        setBackOnClickListener();
        setRightTileClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataUtil.isFastClick()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constant.TYPE, 2);
                    bundle.putString(Constant.CURRENCY, currency);
                    DataUtil.startActivity(WithdrawalActivity.this, AssetRecordActivity.class, bundle);
                }
            }
        });

        withdrawalCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                DataUtil.checkEditInputNumber(withdrawalCount);
                setFeeAmount();
            }
        });
    }

    private void initPopup() {
        if (currencyList == null || currencyList.size() == 0)
            return;
        popup = new AutoWheelChoicePopup(this, currencyList, currency);
        popup.setBtnOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currency = popup.getSelecedData();
                refershData();
                popup.dismiss();
            }
        });
    }

    private double returnWithdrawAmount() {
        double amount = 0;
        String amounts = withdrawalCount.getText().toString().trim();
        if (amounts != null && !amounts.equals("") && DataUtil.isNumeric(amounts)) {
            amount = Double.parseDouble(amounts);
        }
        return amount;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MyCaptureActivity.REQUEST_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        if (null != data) {
                            Bundle bundle = data.getExtras();
                            if (bundle == null) {
                                return;
                            }
                            if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                                String result = bundle.getString(CodeUtils.RESULT_STRING);
                                addressEt.setText(result);
                            }
                        }
                        break;
                }
                break;
        }
    }

    /**
     * 获取提币设置
     */
    private void queryWithdrawalSys(Context context) {
        try {
            Api.getInstance().queryWithdrawalSys(context).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<WithdrawalSysBean>(context) {
                @Override
                protected void _onNext(WithdrawalSysBean bean) {
                    if (bean != null && withdrawalSysList != null) {
                        withdrawalSysList = bean.getData();
                        if (currencyList != null)
                            currencyList.clear();
                        for (WithdrawalSys sys : withdrawalSysList) {
                            currencyList.add(sys.getCurrency());
                        }
                        initPopup();
                    }
                    refershData();
                }

                @Override
                protected void _onError(String message) {
                    Log.e("获取提币设置", message + "");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.asset_type_rl, R.id.scan_qrcode, R.id.withdrawal_all, R.id.confirm_btn})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.asset_type_rl:
                if (DataUtil.isFastClick()) {
                    DataUtil.hideJianPan(this, addressEt);
                    DataUtil.hideJianPan(this, withdrawalCount);
                    if (popup != null && currencyList != null && currencyList.size() > 0)
                        popup.showAtLocation(findViewById(R.id.withdrawal_view), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
                break;
            case R.id.scan_qrcode:
                if (DataUtil.isFastClick()) {
                    if (ContextCompat.checkSelfPermission(WithdrawalActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        //请求权限
                        ActivityCompat.requestPermissions(WithdrawalActivity.this, new String[]{Manifest.permission.CAMERA}, 99);
                    else
                        startCaptureActivityForResult();
                }
                break;
            case R.id.withdrawal_all:
                if (DataUtil.isFastClick()) {
                    withdrawalCount.setText(DataUtil.doubleFour(usableAmount));
                }
                break;
            case R.id.confirm_btn:
                if (DataUtil.isFastClick()) {
                    if (StoreUtils.init(this).getLoginUser() != null) {
                        try {
                            if (returnWithdrawAmount() < minAmount) {
                                Toast.makeText(mContext, getResources().getString(R.string.min_withdrawal_number) + " " + minAmount + " " + currency, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (returnWithdrawAmount() > usableAmount) {
                                Toast.makeText(mContext, getResources().getString(R.string.usable_number_no), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (Exception e) {
                            Toast.makeText(mContext, getResources().getString(R.string.input_number_error), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        address = addressEt.getText().toString().trim();
                        if (address == null || address.length() < 5) {
                            Toast.makeText(mContext, getResources().getString(R.string.withdrawal_address_error), Toast.LENGTH_SHORT).show();
                            return;
                        }
//                        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
//                        if (pattern.matcher(address).matches()) {
//                            Toast.makeText(mContext, getResources().getString(R.string.withdrawal_hint06), Toast.LENGTH_SHORT).show();
//                            return;
//                        }
                        withdrawalConfirm();
                    } else {
                        LoginActivity.startAction(WithdrawalActivity.this);
                    }
                }
                break;
        }

    }

    private void withdrawalConfirm() {
        DialogUtils.confirmWithdrawal(this, null, null, null, null);
        DialogUtils.setTimeOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataUtil.isFastClick()) {
                    getCode(context);
                }
            }
        });
        DialogUtils.setToOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataUtil.isFastClick()) {
                    DataUtil.startActivity(context, UpdateAssetPwdActivity.class);
                }
            }
        });
        DialogUtils.btn(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataUtil.isFastClick()) {
                    String code = DialogUtils.getEt1String();
                    String pwd = DialogUtils.getEt2String();
                    if (code.length() < 6) {
                        Toast.makeText(context, getResources().getString(R.string.code_formar_error), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (pwd.equals("")) {
                        Toast.makeText(context, DataUtil.returnNoNullHint(context, getResources().getString(R.string.asset_pwd)), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    CustomProgressDialog.showDialog(context);
                    withdrawal(context, code, pwd);
                }
            }
        });
    }

    private void startCaptureActivityForResult() {
        Intent intent = new Intent(WithdrawalActivity.this, MyCaptureActivity.class);
//        Bundle bundle = new Bundle();
//        intent.putExtra(CaptureActivity.EXTRA_SETTING_BUNDLE, bundle);
        startActivityForResult(intent, MyCaptureActivity.REQUEST_CODE);
    }

    private void setGetCodeEnd(Boolean status) {
        if (DialogUtils.time != null)
            if (status) {
                DialogUtils.time.setEnabled(true);
                DialogUtils.time.setClickable(true);
            } else {
                DialogUtils.time.setEnabled(false);
                DialogUtils.time.setClickable(false);
            }
    }

    /**
     * 获取验证码
     *
     * @param context
     */
    private void getCode(Context context) {
        try {
            Api.getInstance().sendWithdrawalSmsCode(context, currency, returnWithdrawAmount()).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<BaseBean>(context) {
                @Override
                protected void _onNext(BaseBean bean) {
                    if (bean != null) {
                        Toast.makeText(context, getResources().getString(R.string.send_ok), Toast.LENGTH_SHORT).show();
                        setGetCodeEnd(false);
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    isRunning = true;
                                    int i = 60;
                                    while (isRunning && (i > 1)) {
                                        i--;
                                        Thread.sleep(1000);
                                        handler.sendEmptyMessage(i);
                                    }
                                    handler.sendEmptyMessage(0);
                                } catch (Exception e) {
                                    handler.sendEmptyMessage(0);
                                }
                            }
                        }.start();
                    }
                }

                @Override
                protected void _onError(String message) {
                    Toast.makeText(context, message + "", Toast.LENGTH_SHORT).show();
                    setGetCodeEnd(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提币
     */
    private void withdrawal(Context context, String code, String payPassword) {
        try {
            Api.getInstance().withdrawal(context, currency, returnWithdrawAmount(), address, code, payPassword).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<BaseBean>(context) {
                @Override
                protected void _onNext(BaseBean bean) {
                    CustomProgressDialog.dissmissDialog();
                    if (bean != null) {
                        Toast.makeText(context, getResources().getString(R.string.with_submit_ok), Toast.LENGTH_SHORT).show();
                        addressEt.setText("");
                        withdrawalCount.setText("");
                        theAccountTv.setText("0.00");
                        refershData();
                        DialogUtils.dismissDialog();
                        MData mData = new MData();
                        mData.type = MdataUtils.ASSET_WITHDRAW;
                        EventBus.getDefault().post(mData);
                    }
                }

                @Override
                protected void _onError(String msg) {
                    CustomProgressDialog.dissmissDialog();
                    Toast.makeText(context, msg + "", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询账户资产
     */
    private void queryAssets(Context context) {
        try {
            Api.getInstance().queryWalletAssets(context).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<AssetWalletBean>(context) {
                @Override
                protected void _onNext(AssetWalletBean bean) {
                    if (bean != null) {
                        for (AssetWallet wallet : bean.getData()) {
                            if (currency.equals(wallet.getCurrency())) {
                                usableAmount = wallet.getUsableAmount();
                                useableCount.setText(DataUtil.doubleFour(usableAmount) + " " + currency);
                            }
                        }
                    }
                }

                @Override
                protected void _onError(String message) {
                    Log.e("查询账户资产", message + "");
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
