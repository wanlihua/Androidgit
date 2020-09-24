package com.dtl.gemini.ui.asset.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dtl.gemini.R;
import com.dtl.gemini.api.Api;
import com.dtl.gemini.base.BaseAppActivity;
import com.dtl.gemini.common.baserx.RxSchedulers;
import com.dtl.gemini.common.baserx.RxSubscriber;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.db.StoreUtils;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.model.User;
import com.dtl.gemini.ui.asset.adapter.TransactionRecordAdapter;
import com.dtl.gemini.ui.asset.beans.AssetWalletBean;
import com.dtl.gemini.ui.asset.beans.TransactionRecordBean;
import com.dtl.gemini.ui.asset.model.AssetWallet;
import com.dtl.gemini.ui.asset.model.Record;
import com.dtl.gemini.ui.asset.model.TransactionRecord;
import com.dtl.gemini.ui.other.activity.LoginActivity;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.MdataUtils;
import com.dtl.gemini.widget.MyListView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author DTL
 * @date 2020/4/28
 * 资产详情
 **/
public class AssetDetailsActivity extends BaseAppActivity implements View.OnClickListener {

    @Bind(R.id.logo_iv)
    ImageView logoIv;
    @Bind(R.id.currency_tv)
    TextView currencyTv;
    @Bind(R.id.usable_tv)
    TextView usableTv;
    @Bind(R.id.frost_tv)
    TextView frostTv;
    @Bind(R.id.asset_details_list)
    MyListView assetDetailsList;
    @Bind(R.id.refersh)
    SmartRefreshLayout refersh;
    @Bind(R.id.transfer_btn)
    Button transferBtn;

    TransactionRecordAdapter adapter;
    List<TransactionRecord> list, lists;

    String currency = "", asset = "";
    User user;
    int page = 1, size = 20, maxPage = 1, maxSize = 20;

    @Override
    public int getLayoutId() {
        return R.layout.activity_asset_details;
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
        DataUtil.setFocusIv(logoIv);
        DataUtil.setRefershTheme(this, refersh);
        DataUtil.setRefersh(refersh);
        Bundle bundle = getIntent().getBundleExtra(Constant.BUNDLE);
        if (bundle != null) {
            currency = bundle.getString(Constant.CURRENCY);
        }
        list = new ArrayList<>();
        lists = new ArrayList<>();
        adapter = new TransactionRecordAdapter(this, list);
        assetDetailsList.setAdapter(adapter);
        refershData();
    }

    private void bindListener() {
        setBackOnClickListener();

        refersh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                page = 1;
                refershData();
            }
        });

        refersh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                page++;
                if (page <= maxPage) {
                    queryTransactionRecord(AssetDetailsActivity.this);
                } else {
                    DataUtil.stopRefersh(refersh);
                }
            }
        });

        assetDetailsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int type = lists.get(i).getType();
                if (DataUtil.isFastClick() && (type == 1 || type == 2)) {
                    Bundle bundle = new Bundle();
                    Record record = new Record(lists.get(i).getOtherId(), lists.get(i).getType());
                    bundle.putSerializable(Constant.MODEL, record);
                    DataUtil.startActivity(AssetDetailsActivity.this, RecordDetailsActivity.class, bundle);
                }
            }
        });
    }

    private void refershData() {
        user = StoreUtils.init(this).getLoginUser();
        if (currency != null && !currency.equals("")) {
            setTitle(currency);
            DataUtil.setTokenIcon(logoIv, currency);
            currencyTv.setText(currency);
        }
        transferBtn.setVisibility(View.GONE);
        if (currency != null && DataUtil.toUpper(currency).equals("USDT")) {
            transferBtn.setVisibility(View.VISIBLE);
        }
        if (user != null) {
            queryAssets(this);
            queryTransactionRecord(this);
        } else {
            usableTv.setText("---");
            frostTv.setText("---");
            LoginActivity.startAction(this);
            DataUtil.stopRefersh(refersh);
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
                            if (wallet.getCurrency().equals(currency)) {
                                usableTv.setText(DataUtil.doubleFour(wallet.getUsableAmount()));
                                frostTv.setText(DataUtil.doubleFour(wallet.getFrostAmount()));
                            }
                        }
                    }
                    DataUtil.stopRefersh(refersh);
                }

                @Override
                protected void _onError(String message) {
                    Log.e("查询账户资产", message + "");
                    usableTv.setText("---");
                    frostTv.setText("---");
                    DataUtil.stopRefersh(refersh);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询交易记录
     */
    private void queryTransactionRecord(Context context) {
        try {
            Api.getInstance().queryTransactionRecord(context, currency, page, size).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<TransactionRecordBean>(context) {
                @Override
                protected void _onNext(TransactionRecordBean bean) {
                    if (bean != null && bean.getData() != null && bean.getData().getList().size() > 0) {
                        maxPage = bean.getData().getTotalPage();
                        maxSize = bean.getData().getTotalRow();
                        lists.clear();
                        if (page == 1) {
                            list.clear();
                        }
                        for (TransactionRecord record : bean.getData().getList()) {
                            list.add(record);
                        }
                        runOnUiThread(() -> {
                            for (TransactionRecord model : list) {
                                lists.add(model);
                            }
                            adapter.refersh(lists);
                        });
                    }
                    DataUtil.stopRefersh(refersh);
                }

                @Override
                protected void _onError(String message) {
                    Log.e("查询交易记录", message + "");
                    DataUtil.stopRefersh(refersh);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {
        switch (mData.type) {
            case MdataUtils.ASSET_WITHDRAW:
            case MdataUtils.ASSET_EXCHANGE:
            case MdataUtils.ASSET_TRANSFER:
                refershData();
                break;
        }
    }

//    public static void ListSort(List<Record> list) {
//        Collections.sort(list, new Comparator<Record>() {
//            @Override
//            public int compare(Record o1, Record o2) {
//                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//                try {
//                    Date dt1 = format.parse(o1.getDatetime());
//                    Date dt2 = format.parse(o2.getDatetime());
//                    if (dt1.getTime() < dt2.getTime()) {
//                        return 1;
//                    } else if (dt1.getTime() > dt2.getTime()) {
//                        return -1;
//                    } else {
//                        return 0;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return 0;
//            }
//        });
//    }

    @OnClick({R.id.recharge_btn, R.id.withdraw_btn, R.id.exchange_btn, R.id.transfer_btn})
    @Override
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.CURRENCY, currency);
        bundle.putString(Constant.ASSET, "wallet");
        switch (view.getId()) {
            case R.id.recharge_btn://充币
                if (DataUtil.isFastClick()) {
                    DataUtil.startActivity(this, RechargeActivity.class, bundle);
                }
                break;
            case R.id.withdraw_btn://提现
                if (DataUtil.isFastClick()) {
                    DataUtil.startActivity(this, WithdrawalActivity.class, bundle);
                }
                break;
            case R.id.exchange_btn://兑换
                if (DataUtil.isFastClick()) {
                    DataUtil.startActivity(this, ExchangeActivity.class, bundle);
                }
                break;
            case R.id.transfer_btn://划转
                if (DataUtil.isFastClick()) {
                    DataUtil.startActivity(this, TransferActivity.class, bundle);
                }
                break;
        }
    }
}
