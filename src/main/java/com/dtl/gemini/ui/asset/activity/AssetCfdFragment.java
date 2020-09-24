package com.dtl.gemini.ui.asset.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dtl.gemini.R;
import com.dtl.gemini.base.BaseAppFragment;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.db.StoreUtils;
import com.dtl.gemini.enums.CfdTypeEnum;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.model.User;
import com.dtl.gemini.ui.asset.model.AssetsCfd;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.MdataUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.math.RoundingMode;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 合约账户
 *
 * @author DTL
 * @date 2020/4/15
 **/
public class AssetCfdFragment extends BaseAppFragment implements View.OnClickListener {
    @Bind(R.id.rb_double_cfd)//
    RadioButton rbDoubleCfd;
    @Bind(R.id.rb_free_cfd)
    RadioButton rbFreeCfd;
    @Bind(R.id.rg_cfd)
    RadioGroup rgCfd;
    @Bind(R.id.tv_amount_sum)
    TextView tvAmountSum;
    @Bind(R.id.usable_tv)
    TextView usableTv;
    @Bind(R.id.frost_tv)
    TextView frostTv;
    @Bind(R.id.profit_unrealized_tv)
    TextView profitUnrealizedTv;
    @Bind(R.id.profit_realized_tv)
    TextView profitRealizedTv;
    @Bind(R.id.tv_frost_profit)
    TextView tvFrostProfit;

    String usableAmount = "0.0000", frostAmount = "0.0000", profitUnrealizedAmount = "0.0000", profitRealizedAmount = "0.0000", frostProfitAmount = "0.0000";
    User user;

    BigDecimal cfdSum = new BigDecimal("0.0000");
    public static CfdTypeEnum cfdTypeEnum = CfdTypeEnum.DOUBLE;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_asset_cfd;
    }

    @Override
    public void initPresenter() {
    }

    @Override
    public void initView() {
        refershWallet();

        rgCfd.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                setTab();
//                MData mData = new MData();
//                mData.type = MdataUtils.ASSET_CFD_REFERSH;
//                EventBus.getDefault().post(mData);
            }
        });
    }

    private void refershWallet() {
        user = StoreUtils.init(getActivity()).getLoginUser();
        if (user != null) {
            setTab();
        } else {
            noLogin();
        }
    }

    private void noLogin() {
        if (usableTv != null)
            usableTv.setText("---");
        if (frostTv != null)
            frostTv.setText("---");
        if (profitUnrealizedTv != null)
            profitUnrealizedTv.setText("---");
        if (profitRealizedTv != null)
            profitRealizedTv.setText("---");
    }

    private void setTab() {
        if (rbDoubleCfd.isChecked()) {
            cfdTypeEnum = CfdTypeEnum.DOUBLE;
        } else if (rbFreeCfd.isChecked()) {
            cfdTypeEnum = CfdTypeEnum.FREE;
        }
        setAssets();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {
        switch (mData.type) {
            case MdataUtils.ASSET_REFERSH:
                setAssets();
                break;
            case MdataUtils.ASSET_SEE:
                setAsset();
                break;
        }
    }

    /**
     * 设置账户资产
     */
    private void setAssets() {
        try {
            AssetsCfd assetsCfd = null;
            if (cfdTypeEnum == CfdTypeEnum.DOUBLE) {
                assetsCfd = AssetFragment.assetsDoubleCfd;
            } else {
                assetsCfd = AssetFragment.assetsFreeCfd;
            }
            if (assetsCfd != null) {
                //可用
                if (assetsCfd.getUseableAmount() != null) {
                    usableAmount = DataUtil.doubleFour(assetsCfd.getUseableAmount().doubleValue());
                }
                //冻结
                if (assetsCfd.getFrostAmount() != null) {
                    frostAmount = DataUtil.doubleFour(assetsCfd.getFrostAmount().doubleValue());
                }
                //未结算
                if (assetsCfd.getExpectedProfit() != null) {
                    profitUnrealizedAmount = DataUtil.doubleFour(assetsCfd.getExpectedProfit().doubleValue());
                }
                //已结算
                if (assetsCfd.getTotalProfit() != null) {
                    profitRealizedAmount = DataUtil.doubleFour(assetsCfd.getTotalProfit().doubleValue());
                }
                //冻结收益
                if (assetsCfd.getFrostProfit() != null) {
                    frostProfitAmount = DataUtil.doubleFour(assetsCfd.getFrostProfit().doubleValue());
                }
            }
            cfdSum = assetsCfd.getUseableAmount().add(assetsCfd.getFrostAmount()).add(assetsCfd.getFrostProfit()).setScale(4, RoundingMode.DOWN);
            setAsset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAsset() {
        if (StoreUtils.init(getActivity()).getLoginUser() != null) {
            if (AssetFragment.see) {
                usableTv.setText(usableAmount);
                frostTv.setText(frostAmount);
                profitUnrealizedTv.setText(profitUnrealizedAmount);
                profitRealizedTv.setText(profitRealizedAmount);
                tvFrostProfit.setText(frostProfitAmount);
                tvAmountSum.setText("= " + cfdSum.toPlainString());
            } else {
                usableTv.setText("******");
                frostTv.setText("******");
                profitUnrealizedTv.setText("******");
                profitRealizedTv.setText("******");
                tvFrostProfit.setText("******");
                tvAmountSum.setText("******");
            }
        } else {
            noLogin();
        }

    }

    @OnClick({R.id.cfd_transfer, R.id.ll_income})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cfd_transfer:
                toActivity(TransferActivity.class);
                break;
            case R.id.ll_income:
                toActivity(AssetCfdRecordActivity.class);
                break;
        }
    }

    private void toActivity(Class activity) {
        if (DataUtil.isFastClick()) {
            Bundle bundle = new Bundle();
            bundle.putString(Constant.CURRENCY, "USDT");
            bundle.putString(Constant.ASSET, "cfd");
            bundle.putString(Constant.TYPE, cfdTypeEnum.grade + "");
            DataUtil.startActivity(getActivity(), activity, bundle);
        }
    }
}
