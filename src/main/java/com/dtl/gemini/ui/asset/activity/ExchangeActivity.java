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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import com.dtl.gemini.ui.asset.beans.ExchangeSysOneBean;
import com.dtl.gemini.ui.asset.model.AssetWallet;
import com.dtl.gemini.ui.other.activity.LoginActivity;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.MdataUtils;
import com.dtl.gemini.widget.AutoWheelChoicePopup;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author DTL
 * @date 2020/4/29
 * 兑换
 **/
public class ExchangeActivity extends BaseAppActivity implements View.OnClickListener {
    @Bind(R.id.refersh)
    SmartRefreshLayout refersh;
    @Bind(R.id.left_img)
    ImageView leftImg;
    @Bind(R.id.left_btn)
    TextView leftBtn;
    @Bind(R.id.right_img)
    ImageView rightImg;
    @Bind(R.id.right_btn)
    TextView rightBtn;
    @Bind(R.id.exchange_select_iv)
    ImageView exchangeSelectIv;
    @Bind(R.id.transfer_count_et)
    EditText transferCountEt;
    @Bind(R.id.exchange_usable)
    TextView exchangeUsable;
    @Bind(R.id.transfer_count)
    TextView transferCount;
    @Bind(R.id.exchange_rate)
    TextView exchangeRate;
    @Bind(R.id.exchange_fee)
    TextView exchangeFee;
    @Bind(R.id.min_amount_tv)
    TextView minAmountTv;

    double usableAmount = 0.00, fee = 0.001, rate = 0.00, minAmount = 0.001;//可用余额，手续费率，汇率，最小兑换数量
    String currency1, currency2 = "ETH", amounts = "0.00";
    AssetWallet assetWallet;
    Context context;

    AutoWheelChoicePopup popup;
    List<String> currencyList = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_exchange;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        init();
        bindListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {
        context = this;
        DataUtil.setRefershTheme(this, refersh);
        DataUtil.setRefershNoLoad(refersh);
        Bundle bundle = getIntent().getBundleExtra(Constant.BUNDLE);
        if (bundle != null) {
            currency1 = bundle.getString(Constant.CURRENCY);
        }
        setTitle(getResources().getString(R.string.exchange));
        setRightTile(getResources().getString(R.string.exchange_record));
        for (String s : getResources().getStringArray(R.array.currencys)) {
            currencyList.add(s);
        }
        refershData();
    }

    private void bindListener() {
        setBackOnClickListener();

        initPopup();

        setRightTileClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataUtil.isFastClick()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constant.TYPE, 3);
                    bundle.putString(Constant.CURRENCY, currency1);
                    DataUtil.startActivity(context, AssetRecordActivity.class, bundle);
                }
            }
        });

        refersh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {

            }
        });

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
                    amounts = DataUtil.doubleFour(Double.parseDouble(s.toString()));
                double countTv = (Double.parseDouble(amounts) - Double.parseDouble(amounts) * fee) * returnRate();
                transferCount.setText(DataUtil.doubleFour(countTv) + " " + currency2);
            }
        });
    }

    private void initPopup() {
        if (currencyList == null || currencyList.size() == 0)
            return;
        popup = new AutoWheelChoicePopup(this, currencyList, currency2);
        popup.setBtnOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currency2 = popup.getSelecedData();
                refershData();
                popup.dismiss();
            }
        });
    }

    private void refershData() {
        if (currency1 != null && !currency1.equals("USDT")) {
            currency2 = "USDT";
            exchangeSelectIv.setVisibility(View.GONE);
        } else {
            exchangeSelectIv.setVisibility(View.VISIBLE);
        }
        DataUtil.setTokenIcon(leftImg, currency1);
        DataUtil.setTokenIcon(rightImg, currency2);
        leftBtn.setText(currency1);
        rightBtn.setText(currency2);
        queryExchangeSymbol(this);
    }

    private double returnRate() {
        double rates = rate;
        try {
            if (currency1 != null && currency1.equals("USDT")) {
                rates = Double.parseDouble(DataUtil.doubleEight(1 / rate));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rates;
    }

    /**
     * 获取单个交易对兑换设置
     */
    private void queryExchangeSymbol(Context context) {
        try {
            Api.getInstance().queryExchangeSymbol(context, currency1 + currency2).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<ExchangeSysOneBean>(context) {
                @Override
                protected void _onNext(ExchangeSysOneBean bean) {
                    if (bean != null && bean.getData() != null) {
                        rate = bean.getData().getExchangeRate();
                        fee = bean.getData().getExchangeFeeRatio();
                        minAmount = bean.getData().getMinExchangeAmount();
                        minAmountTv.setText(getResources().getString(R.string.min_exchange_number) + ":" + minAmount + " " + currency1);
                        exchangeFee.setText(getResources().getString(R.string.fee) + ":" + (fee * 100) + "%");
                        exchangeRate.setText("1 " + currency1 + " : " + DataUtil.numberSix(returnRate()) + " " + currency2);
                    }
                    queryAssets(context);
                }

                @Override
                protected void _onError(String message) {
                    Log.e("获取单个交易对兑换设置", message + "");
                    queryAssets(context);
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
                    if (bean.getData() != null && bean.getData().size() > 0) {
                        for (AssetWallet wallet : bean.getData()) {
                            if (wallet.getCurrency().equals(currency1)) {
                                assetWallet = wallet;
                                usableAmount = assetWallet.getUsableAmount();
                            }
                        }
                        exchangeUsable.setText(getResources().getString(R.string.usable) + DataUtil.doubleFour(usableAmount) + " " + currency1);
                    }
                    DataUtil.stopRefersh(refersh);
                }

                @Override
                protected void _onError(String message) {
                    Log.e("查询账户资产", message + "");
                    exchangeUsable.setText(getResources().getString(R.string.usable) + "---");
                    DataUtil.stopRefersh(refersh);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 兑换
     */
    private void exchange(Context context, double amount) {
        try {
            Api.getInstance().exchange(context, currency1, currency2, amount).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<BaseBean>(context) {
                @Override
                protected void _onNext(BaseBean bean) {
                    CustomProgressDialog.dissmissDialog();
                    if (bean != null) {
                        transferCountEt.setText("");
                        transferCount.setText("");
                        Toast.makeText(context, getResources().getString(R.string.exchange_ok), Toast.LENGTH_SHORT).show();
                        MData mData = new MData();
                        mData.type = MdataUtils.ASSET_EXCHANGE;
                        EventBus.getDefault().post(mData);
                        queryAssets(context);
                    }
                }

                @Override
                protected void _onError(String message) {
                    CustomProgressDialog.dissmissDialog();
                    DataUtil.noLogin(context, message);
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

    @OnClick({R.id.select_img, R.id.transfer_select, R.id.right_ll, R.id.transfer_all, R.id.transfer_confirm})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_img:
            case R.id.transfer_select:
                if (DataUtil.isFastClick()) {
                    transferCountEt.setText("");
                    transferCount.setText("0.00");
                    String currency = currency1;
                    currency1 = currency2;
                    currency2 = currency;
                    refershData();
                }
                break;
            case R.id.right_ll:
                if (DataUtil.isFastClick()) {
                    DataUtil.hideJianPan(this, transferCountEt);
                    if (popup != null && currencyList != null && currencyList.size() > 0)
                        popup.showAtLocation(findViewById(R.id.asset_exchange_view), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
                break;
            case R.id.transfer_all:
                if (DataUtil.isFastClick()) {
                    transferCountEt.setText(DataUtil.doubleFour(usableAmount));
                    amounts = DataUtil.numberSix(usableAmount) + "";
                }
                break;
            case R.id.transfer_confirm:
                if (DataUtil.isFastClick()) {
                    if (StoreUtils.init(ExchangeActivity.this).getLoginUser() != null) {
                        if (transferCountEt.getText().toString().trim().equals(""))
                            return;
                        double amount = Double.parseDouble(amounts);
                        if (amount > usableAmount || usableAmount == 0.00) {
                            Toast.makeText(context, getResources().getString(R.string.usable_number_no), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (amount == 0.00) {
                            return;
                        }
                        if (amount < minAmount) {
                            Toast.makeText(context, getResources().getString(R.string.min_exchange_number) + ":" + minAmount, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        exchange(ExchangeActivity.this, amount);
                    } else {
                        LoginActivity.startAction(ExchangeActivity.this);
                    }
                }
                break;
        }
    }
}
