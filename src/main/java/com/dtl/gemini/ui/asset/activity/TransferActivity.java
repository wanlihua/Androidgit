package com.dtl.gemini.ui.asset.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dtl.gemini.R;
import com.dtl.gemini.api.Api;
import com.dtl.gemini.base.BaseAppActivity;
import com.dtl.gemini.bean.BaseBean;
import com.dtl.gemini.common.baserx.RxSchedulers;
import com.dtl.gemini.common.baserx.RxSubscriber;
import com.dtl.gemini.common.commonwidget.CustomProgressDialog;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.db.StoreUtils;
import com.dtl.gemini.enums.CfdTypeEnum;
import com.dtl.gemini.enums.TokenEnum;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.model.User;
import com.dtl.gemini.ui.asset.beans.AssetCfdBean;
import com.dtl.gemini.ui.asset.beans.AssetWalletBean;
import com.dtl.gemini.ui.asset.model.AssetWallet;
import com.dtl.gemini.ui.asset.model.AssetsCfd;
import com.dtl.gemini.ui.other.activity.LoginActivity;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.MdataUtils;
import com.dtl.gemini.widget.AutoWheelChoicePopup;

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
 * 划转
 **/
public class TransferActivity extends BaseAppActivity implements View.OnClickListener {

    @Bind(R.id.left_btn)
    TextView leftBtn;
    @Bind(R.id.right_btn)
    TextView rightBtn;
    @Bind(R.id.transfer_select_iv)
    ImageView transferSelectIv;
    @Bind(R.id.ll_select_cfd)
    LinearLayout llSelectCfd;
    @Bind(R.id.transfer_select_iv2)
    ImageView transferSelectIv2;
    @Bind(R.id.ll_select_cfd2)
    LinearLayout llSelectCfd2;
    @Bind(R.id.transfer_count_et)
    EditText transferCountEt;
    @Bind(R.id.transfer_usable)
    TextView transferUsable;

    AutoWheelChoicePopup popup;
    List<String> list = new ArrayList<>();
    User user;
    String asset = "";
    double usableAmount = 0.00;//可用
    String currency = "";
    String walletAsset, cfdAsset, assetDouble, assetFree;
    String amounts = "0.00";

    CfdTypeEnum cfdTypeEnum = CfdTypeEnum.DOUBLE;

    @Override
    public int getLayoutId() {
        return R.layout.activity_transfer;
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
        setTitle(getResources().getString(R.string.transfer));
        assetDouble = getResources().getString(R.string.asset_double_cfd);
        assetFree = getResources().getString(R.string.asset_free_cfd);
        list.add(assetDouble);
        list.add(assetFree);
        walletAsset = getResources().getString(R.string.asset_wallet);
        Bundle bundle = getIntent().getBundleExtra(Constant.BUNDLE);
        if (bundle != null) {
            currency = bundle.getString(Constant.CURRENCY);
            asset = bundle.getString(Constant.ASSET);
            String type = bundle.getString(Constant.TYPE);
            cfdAsset = assetDouble;
            cfdTypeEnum = CfdTypeEnum.DOUBLE;
            if (type != null && !type.equals("") && Integer.parseInt(type) == CfdTypeEnum.FREE.grade) {
                cfdAsset = assetFree;
                cfdTypeEnum = CfdTypeEnum.FREE;
            }
        }
        if (asset.equals("cfd")) {
            leftBtn.setText(cfdAsset);
            rightBtn.setText(walletAsset);
        } else if (asset.equals("wallet")) {
            leftBtn.setText(walletAsset);
            rightBtn.setText(cfdAsset);
        }
        setEnbleDefault();
        refershDta();
        initPopup();
    }

    private void bindListener() {
        setBackOnClickListener();

        transferCountEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                DataUtil.checkEditInputNumber(transferCountEt);
                if (s.toString() != null && !s.toString().equals("") && DataUtil.isNumeric(s.toString()))
                    amounts = DataUtil.numberFour(Double.parseDouble(s.toString()));
            }
        });

    }

    private void initPopup() {
        if (list == null || list.size() == 0)
            return;
        popup = new AutoWheelChoicePopup(this, list);
        popup.setBtnOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cfdAsset = popup.getSelecedData();
                if (asset.equals("cfd")) {
                    leftBtn.setText(cfdAsset);
                } else if (asset.equals("wallet")) {
                    rightBtn.setText(cfdAsset);
                }
                if (cfdAsset.equals(assetDouble)) {
                    cfdTypeEnum = CfdTypeEnum.DOUBLE;
                } else if (cfdAsset.equals(assetFree)) {
                    cfdTypeEnum = CfdTypeEnum.FREE;
                }
                popup.dismiss();
                if (asset.equals("cfd")) {
                    queryCfdAsset(TransferActivity.this);
                }
            }
        });
    }

    private void refershDta() {
        user = StoreUtils.init(this).getLoginUser();
        if (user != null) {
            if (asset.equals("wallet")) {
                queryWalletAssets(this);
            } else {
                queryCfdAsset(this);
            }
        } else {
            LoginActivity.startAction(this);
        }
    }

    private void setAssetSelect() {
        String lefts = leftBtn.getText().toString();
        String rights = rightBtn.getText().toString();
        if ((lefts.equals(walletAsset) && rights.equals(cfdAsset))) {//数字到合约
            asset = "cfd";
            leftBtn.setText(cfdAsset);
            rightBtn.setText(walletAsset);//换为合约到数字
        } else if ((lefts.equals(cfdAsset) && rights.equals(walletAsset))) {//合约到数字
            asset = "wallet";
            leftBtn.setText(walletAsset);
            rightBtn.setText(cfdAsset);//换为数字到合约
        }
        setEnbleDefault();
        refershDta();
    }

    private void setEnbleDefault() {
        if (asset.equals("cfd")) {
            llSelectCfd.setClickable(true);
            llSelectCfd.setEnabled(true);
            llSelectCfd2.setClickable(false);
            llSelectCfd2.setEnabled(false);
            transferSelectIv.setVisibility(View.VISIBLE);
            transferSelectIv2.setVisibility(View.GONE);
        } else {
            llSelectCfd.setClickable(false);
            llSelectCfd.setEnabled(false);
            llSelectCfd2.setClickable(true);
            llSelectCfd2.setEnabled(true);
            transferSelectIv.setVisibility(View.GONE);
            transferSelectIv2.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {

    }

    @OnClick({R.id.ll_select_cfd, R.id.ll_select_cfd2, R.id.transfer_select, R.id.transfer_all, R.id.transfer_confirm})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_select_cfd:
            case R.id.ll_select_cfd2:
                if (DataUtil.isFastClick()) {
                    DataUtil.hideJianPan(this, transferCountEt);
                    if (popup != null && list != null && list.size() > 0)
                        popup.showAtLocation(findViewById(R.id.transfer_view), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
                break;
            case R.id.transfer_select:
                if (DataUtil.isFastClick()) {
                    setAssetSelect();
                }
                break;
            case R.id.transfer_all:
                if (DataUtil.isFastClick()) {
                    transferCountEt.setText(DataUtil.numberFour(usableAmount));
                    amounts = DataUtil.numberSix(usableAmount) + "";
                }
                break;
            case R.id.transfer_confirm:
                if (DataUtil.isFastClick()) {
                    if (transferCountEt.getText().toString().trim().equals(""))
                        return;
                    double amount = Double.parseDouble(amounts);
                    if (amount == 0.00)
                        return;
                    if (amount > usableAmount) {
                        Toast.makeText(this, getResources().getString(R.string.usable_number_no), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String lefts = leftBtn.getText().toString();
                    String rights = rightBtn.getText().toString();
                    CustomProgressDialog.showDialog(this);
                    assetTransfer(this, amount + "");
                }
                break;
        }
    }

    /**
     * 查询账户资产
     */
    private void queryWalletAssets(Context context) {
        try {
            Api.getInstance().queryWalletAssets(context).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<AssetWalletBean>(context) {
                @Override
                protected void _onNext(AssetWalletBean bean) {
                    if (bean.getData() != null && bean.getData().size() > 0) {
                        for (AssetWallet wallet : bean.getData()) {
                            if (wallet.getCurrency().equals(currency)) {
                                usableAmount = wallet.getUsableAmount();
                            }
                        }
                    }
                    transferUsable.setText(getResources().getString(R.string.usable) + DataUtil.numberFour(usableAmount) + " " + currency);
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

    /**
     * 查询合约账户资产
     */
    private void queryCfdAsset(Context context) {
        try {
            Api.getInstance().queryCfdAsset(context, cfdTypeEnum).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<AssetCfdBean>(context) {
                @Override
                protected void _onNext(AssetCfdBean bean) {
                    if (bean != null && bean.getData().size() > 0) {
                        for (AssetsCfd cfd : bean.getData()) {
                            if (cfd.getCurrency().equals(TokenEnum.USDT.name())) {
                                usableAmount = cfd.getUseableAmount().doubleValue();
                            }
                        }
                    }
                    transferUsable.setText(getResources().getString(R.string.usable) + DataUtil.numberFour(usableAmount) + " " + currency);
                }

                @Override
                protected void _onError(String message) {
                    Log.e("查询合约账户资产", message + "");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 资产划转
     */
    private void assetTransfer(Context context, String amount) {
        try {
            Api.getInstance().assetTransfer(context, amount, asset, cfdTypeEnum).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<BaseBean>(context) {
                @Override
                protected void _onNext(BaseBean bean) {
                    CustomProgressDialog.dissmissDialog();
                    if (bean != null) {
                        Toast.makeText(context, getResources().getString(R.string.transfer_ok), Toast.LENGTH_SHORT).show();
                        refershDta();
                        MData mData = new MData();
                        mData.type = MdataUtils.ASSET_TRANSFER;
                        EventBus.getDefault().post(mData);
                        transferCountEt.setText("");
                    }
                }

                @Override
                protected void _onError(String message) {
                    CustomProgressDialog.dissmissDialog();
                    Toast.makeText(context, message + "", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override

    public void finish() {
        DataUtil.hideJianPan(this, transferCountEt);
        super.finish();
    }

}
