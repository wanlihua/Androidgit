package com.dtl.gemini.ui.asset.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
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
import com.dtl.gemini.ui.asset.adapter.ExchangeRecordAdapter;
import com.dtl.gemini.ui.asset.adapter.RechargeRecordAdapter;
import com.dtl.gemini.ui.asset.adapter.WithdrawalRecordAdapter;
import com.dtl.gemini.ui.asset.beans.ExchangeBean;
import com.dtl.gemini.ui.asset.beans.RechargeBean;
import com.dtl.gemini.ui.asset.beans.WithdrawalBean;
import com.dtl.gemini.ui.asset.model.Exchange;
import com.dtl.gemini.ui.asset.model.Recharge;
import com.dtl.gemini.ui.asset.model.Record;
import com.dtl.gemini.ui.asset.model.Withdrawal;
import com.dtl.gemini.ui.other.activity.LoginActivity;
import com.dtl.gemini.utils.DataUtil;
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

/**
 * @author DTL
 * @date 2020/4/29
 * 充币/提币/兑换记录
 **/
public class AssetRecordActivity extends BaseAppActivity {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.list_mlv)
    MyListView listMlv;
    @Bind(R.id.ll_kong)
    LinearLayout llKong;
    @Bind(R.id.refersh)
    SmartRefreshLayout refersh;

    int type = 1;//1:充币2：提币3：兑换
    String currency;

    List<Recharge> rechargeList;
    List<Withdrawal> withdrawalList;
    List<Exchange> exchangeList;
    ExchangeRecordAdapter exchangeRecordAdapter;
    RechargeRecordAdapter rechargeRecordAdapter;
    WithdrawalRecordAdapter withdrawalRecordAdapter;
    User user;
    Context context;

    @Override

    public int getLayoutId() {
        return R.layout.activity_asset_record;
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
        context = this;
        DataUtil.setFocusTv(title);
        Bundle bundle = getIntent().getBundleExtra(Constant.BUNDLE);
        if (bundle != null) {
            type = bundle.getInt(Constant.TYPE);
            currency = bundle.getString(Constant.CURRENCY);
        }
        DataUtil.setRefershTheme(this, refersh);
        DataUtil.setRefershNoLoad(refersh);

        rechargeList = new ArrayList<>();
        withdrawalList = new ArrayList<>();
        exchangeList = new ArrayList<>();

        exchangeRecordAdapter = new ExchangeRecordAdapter(context);
        rechargeRecordAdapter = new RechargeRecordAdapter(context);
        withdrawalRecordAdapter = new WithdrawalRecordAdapter(context);
        refershData();
    }

    private void bindListener() {
        setBackOnClickListener();

        refersh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refershData();
            }
        });

        refersh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refershData();
            }
        });

        listMlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (DataUtil.isFastClick() && (type == 1 || type == 2)) {
                    Bundle bundle = new Bundle();
                    Record record = null;
                    if (type == 1&&rechargeList.size()>0) {
                        Recharge recharge = rechargeList.get(i);
                        record = new Record(1, recharge.getCurrency(), DataUtil.doubleFour(recharge.getAmount()), recharge.getCreateDateTime(), recharge.getAddress(), 1, null, recharge.getHash());
                    } else if (type == 2&&withdrawalList.size()>0) {
                       Withdrawal withdrawal = withdrawalList.get(i);
                        record = new Record(2, withdrawal.getCurrency(), DataUtil.doubleFour(withdrawal.getAmount()), withdrawal.getCreateDateTime(), withdrawal.getAddress(), withdrawal.getStatus(), withdrawal.getRefuseReason(), withdrawal.getHash());
                    }
                    bundle.putSerializable(Constant.MODEL, record);
                    DataUtil.startActivity(AssetRecordActivity.this, RecordDetailsActivity.class, bundle);
                }
            }
        });
    }

    private void refershData() {
        user = StoreUtils.init(this).getLoginUser();
        if (user != null) {
            refershLists();
            switch (type) {
                case 1://充币
                    setTitle(getResources().getString(R.string.recharge_record));
                    listMlv.setAdapter(rechargeRecordAdapter);
                    queryRechargeRecord();
                    break;
                case 2://提币
                    setTitle(getResources().getString(R.string.withdraw_record));
                    listMlv.setAdapter(withdrawalRecordAdapter);
                    queryWithdrawalRecord();
                    break;
                case 3://兑换
                    setTitle(getResources().getString(R.string.exchange_record));
                    listMlv.setAdapter(exchangeRecordAdapter);
                    queryExchangeRecord();
                    break;
            }
        } else {
            DataUtil.stopRefersh(refersh);
            LoginActivity.startAction(this);
        }
    }

    /**
     * 查询充币记录
     */
    private void queryRechargeRecord() {
        try {
            Api.getInstance().queryRechargeRecord(context, currency).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<RechargeBean>(context) {
                @Override
                protected void _onNext(RechargeBean bean) {
                    if (bean.getData() != null && bean.getData().size() > 0) {
                        llKong.setVisibility(View.GONE);
                        listMlv.setVisibility(View.VISIBLE);
                        rechargeList = bean.getData();
                        rechargeRecordAdapter.refersh(rechargeList);
                    } else {
                        llKong.setVisibility(View.VISIBLE);
                        listMlv.setVisibility(View.GONE);
                    }
                    DataUtil.stopRefersh(refersh);
                }

                @Override
                protected void _onError(String message) {
                    Log.e("查询充币记录", message + "");
                    DataUtil.stopRefersh(refersh);
                    llKong.setVisibility(View.VISIBLE);
                    listMlv.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询提币记录
     */
    private void queryWithdrawalRecord() {
        try {
            Api.getInstance().queryWithdrawalRecord(context, currency).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<WithdrawalBean>(context) {
                @Override
                protected void _onNext(WithdrawalBean bean) {
                    if (bean.getData() != null && bean.getData().size() > 0) {
                        llKong.setVisibility(View.GONE);
                        listMlv.setVisibility(View.VISIBLE);
                        withdrawalList = bean.getData();
                        withdrawalRecordAdapter.refersh(withdrawalList);
                    } else {
                        llKong.setVisibility(View.VISIBLE);
                        listMlv.setVisibility(View.GONE);
                    }
                    DataUtil.stopRefersh(refersh);
                }

                @Override
                protected void _onError(String message) {
                    Log.e("查询提币记录", message + "");
                    DataUtil.stopRefersh(refersh);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询兑换记录
     */
    private void queryExchangeRecord() {
        try {
            Api.getInstance().queryExchangeRecord(context, currency).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<ExchangeBean>(context) {
                @Override
                protected void _onNext(ExchangeBean bean) {
                    if (bean.getData() != null && bean.getData().size() > 0) {
                        llKong.setVisibility(View.GONE);
                        listMlv.setVisibility(View.VISIBLE);
                        exchangeList = bean.getData();
                        exchangeRecordAdapter.refersh(exchangeList);
                    } else {
                        llKong.setVisibility(View.VISIBLE);
                        listMlv.setVisibility(View.GONE);
                    }
                    DataUtil.stopRefersh(refersh);
                }

                @Override
                protected void _onError(String message) {
                    Log.e("查询兑换记录", message + "");
                    DataUtil.stopRefersh(refersh);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refershLists() {
        if (rechargeList != null) {
            rechargeList.clear();
        }
        if (withdrawalList != null) {
            withdrawalList.clear();
        }
        if (exchangeList != null) {
            exchangeList.clear();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {

    }

}
