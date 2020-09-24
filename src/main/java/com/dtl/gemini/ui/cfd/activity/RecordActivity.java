package com.dtl.gemini.ui.cfd.activity;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dtl.gemini.R;
import com.dtl.gemini.api.Api;
import com.dtl.gemini.base.BaseAppActivity;
import com.dtl.gemini.bean.BaseBean;
import com.dtl.gemini.common.baserx.RxSchedulers;
import com.dtl.gemini.common.baserx.RxSubscriber;
import com.dtl.gemini.common.commonwidget.CustomProgressDialog;
import com.dtl.gemini.enums.CfdTypeEnum;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.ui.cfd.adapter.CloseRecordAdapter;
import com.dtl.gemini.ui.cfd.adapter.ClosedRecordAdapter;
import com.dtl.gemini.ui.cfd.beans.OrderHistoryBean;
import com.dtl.gemini.ui.cfd.model.OrderHistory;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.DialogUtils;
import com.dtl.gemini.widget.MyListView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;

/**
 * 交易记录
 *
 * @author DTL
 * @date 2020/5/15
 **/
public class RecordActivity extends BaseAppActivity {

    @Bind(R.id.close_order_record)
    RadioButton closeOrderRecord;
    @Bind(R.id.unclose_order_record)
    RadioButton uncloseOrderRecord;
    @Bind(R.id.cfd_record_rg)
    RadioGroup cfdRecordRg;
    @Bind(R.id.list_mlv)
    MyListView list;
    @Bind(R.id.ll_kong)
    LinearLayout llKong;
    @Bind(R.id.refersh)
    SmartRefreshLayout refersh;

    CloseRecordAdapter closeRecordAdapter;
    ClosedRecordAdapter closedRecordAdapter;

    List<OrderHistory> orderCreateList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_record;
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
        if (CfdFragment.cfdTypeEnum == CfdTypeEnum.DOUBLE) {
            setTitle(getResources().getString(R.string.double_cfd));
        } else {
            setTitle(getResources().getString(R.string.free_cfd));
        }
        DataUtil.setRefershTheme(this, refersh);
        DataUtil.setRefershNoLoad(refersh);
        closeRecordAdapter = new CloseRecordAdapter(this);
        closedRecordAdapter = new ClosedRecordAdapter(this);
        setTab();
    }

    private void bindListener() {
        setBackOnClickListener();

        cfdRecordRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setTab();
            }
        });

        refersh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                setTab();
            }
        });

        closeRecordAdapter.setCancelListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (DataUtil.isFastClick()) {
                    DialogUtils.showConfirmDialog(RecordActivity.this, null, getResources().getString(R.string.hint), getResources().getString(R.string.cancel_order_hint));
                    DialogUtils.btn(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (DataUtil.isFastClick()) {
                                DialogUtils.dismissDialog();
                                cfdOrderCancelClose(RecordActivity.this, orderCreateList.get(i).getId());
                            }
                        }
                    });
                }
            }
        });
    }

    private void setTab() {
        if (orderCreateList != null) {
            orderCreateList.clear();
        }
        if (closeOrderRecord.isChecked()) {
            list.setAdapter(closeRecordAdapter);
            queryFinishOrder(this, 1);
        } else if (uncloseOrderRecord.isChecked()) {
            list.setAdapter(closedRecordAdapter);
            queryFinishOrder(this, 2);
        }
    }

    /**
     * 1:平仓委托单记录 2：已平仓记录
     *
     * @param context
     */
    private void queryFinishOrder(Context context, int type) {
        try {
            Api.getInstance().queryFinishOrder(context, type, CfdFragment.cfdTypeEnum).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<OrderHistoryBean>(context) {
                @Override
                protected void _onNext(OrderHistoryBean bean) {
                    if (bean.getData() != null && bean.getData().size() > 0) {
                        llKong.setVisibility(View.GONE);
                        list.setVisibility(View.VISIBLE);
                        orderCreateList = bean.getData();
                        if (type == 1) {
                            closeRecordAdapter.refersh(orderCreateList);
                        } else {
                            closedRecordAdapter.refersh(orderCreateList);
                        }
                    } else {
                        llKong.setVisibility(View.VISIBLE);
                        list.setVisibility(View.GONE);
                    }
                    DataUtil.stopRefersh(refersh);
                    CustomProgressDialog.dissmissDialog();
                }

                @Override
                protected void _onError(String message) {
                    CustomProgressDialog.dissmissDialog();
                    Log.e("查询平仓记录", message + "");
                    llKong.setVisibility(View.VISIBLE);
                    list.setVisibility(View.GONE);
                    DataUtil.stopRefersh(refersh);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cfdOrderCancelClose(Context context, String finishOrderId) {
        try {
            CustomProgressDialog.showDialog(context);
            Api.getInstance().cfdOrderCancelClose(context, finishOrderId, CfdFragment.cfdTypeEnum).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<BaseBean>(context) {
                @Override
                protected void _onNext(BaseBean bean) {
                    if (bean != null) {
                        Toast.makeText(context, getResources().getString(R.string.cancel_order_ok), Toast.LENGTH_SHORT).show();
                        setTab();
                    }
                }

                @Override
                protected void _onError(String message) {
                    Toast.makeText(context, getResources().getString(R.string.cancel_order_error), Toast.LENGTH_SHORT).show();
                    CustomProgressDialog.dissmissDialog();
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
