package com.dtl.gemini.ui.asset.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dtl.gemini.R;
import com.dtl.gemini.api.Api;
import com.dtl.gemini.base.BaseAppActivity;
import com.dtl.gemini.bean.BaseBean;
import com.dtl.gemini.bean.DataBean;
import com.dtl.gemini.common.baserx.RxSchedulers;
import com.dtl.gemini.common.baserx.RxSubscriber;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.ui.asset.beans.RechargeBean;
import com.dtl.gemini.ui.asset.model.Recharge;
import com.dtl.gemini.ui.asset.model.Record;
import com.dtl.gemini.ui.asset.model.Withdrawal;
import com.dtl.gemini.utils.DataUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;

/**
 * 交易记录详情
 *
 * @author DTL
 * @date 2020/5/26
 **/
public class RecordDetailsActivity extends BaseAppActivity {
    @Bind(R.id.record_details_amount)
    TextView recordDetailsAmount;
    @Bind(R.id.record_details_type)
    TextView recordDetailsType;
    @Bind(R.id.tv2)
    TextView tv2;
    @Bind(R.id.record_details_address)
    TextView recordDetailsAddress;
    @Bind(R.id.address_copy)
    ImageView addressCopy;
    @Bind(R.id.record_details_status)
    TextView recordDetailsStatus;
    @Bind(R.id.record_details_date)
    TextView recordDetailsDate;
    @Bind(R.id.tv5)
    TextView tv5;
    @Bind(R.id.record_with_reason)
    TextView recordWithReason;

    Record record;

    @Override
    public int getLayoutId() {
        return R.layout.activity_record_details;
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
            record = (Record) bundle.getSerializable(Constant.MODEL);
            if (record != null) {
                if (record.getAdress() != null) {
                    setData();
                } else {
                    if (record.getType() == 1) {
                        queryRechargeRecordById(this, record.getId());
                    } else if (record.getType() == 2) {
                        queryWithdrawalRecordById(this, record.getId());
                    }
                }
            }
        }
    }

    private void setData() {
        if (record.getType() == 1) {//转入
            recordDetailsAmount.setText("+ " + record.getAmount() + " " + record.getCurrency());
            recordDetailsType.setText(getResources().getString(R.string.recharge));
            tv2.setText(getResources().getString(R.string.transaction_id));
            recordDetailsAddress.setText(record.getHash().toString());
        } else {
            recordDetailsAmount.setText("- " + record.getAmount() + " " + record.getCurrency());
            recordDetailsType.setText(getResources().getString(R.string.withdraw));
            tv2.setText(getResources().getString(R.string.withdraw_address));
            recordDetailsAddress.setText(record.getAdress() + "");
        }
        if (record.getStatus() == 0) {
            recordDetailsStatus.setText(getResources().getString(R.string.under_review));
        } else if (record.getStatus() == 1) {
            if (record.getType() == 2) {
                recordDetailsStatus.setText(getResources().getString(R.string.deposited));
            } else {
                recordDetailsStatus.setText(getResources().getString(R.string.completed));
            }
        } else if (record.getStatus() == 2) {
            recordDetailsStatus.setText(getResources().getString(R.string.rejected));
        }

        recordDetailsDate.setText(record.getDatetime());
        if (record.getReason() != null && !record.getReason().toString().equals("")) {
            tv5.setVisibility(View.VISIBLE);
            recordWithReason.setVisibility(View.VISIBLE);
        } else {
            tv5.setVisibility(View.GONE);
            recordWithReason.setVisibility(View.GONE);
        }
        recordWithReason.setText(record.getReason() + "");
    }

    /**
     * 提币id获取用户的提币记录
     *
     * @param context
     */
    private void queryWithdrawalRecordById(Context context, String otherId) {
        try {
            Api.getInstance().queryWithdrawalRecordById(context, otherId).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<DataBean<Withdrawal>>(context) {
                @Override
                protected void _onNext(DataBean<Withdrawal> bean) {
                    if (bean != null && bean.getData() != null) {
                        Withdrawal data = bean.getData();
                        record = new Record(2, data.getCurrency(), DataUtil.doubleFour(data.getAmount()), data.getCreateDateTime(), data.getAddress(), data.getStatus(), data.getRefuseReason(), data.getHash());
                        setData();
                    }
                }

                @Override
                protected void _onError(String message) {
                    Log.e("提币id获取用户的提币记录", message + "");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 充值id获取用户的充值记录
     *
     * @param context
     */
    private void queryRechargeRecordById(Context context, String otherId) {
        try {
            Api.getInstance().queryRechargeRecordById(context, otherId).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<DataBean<Recharge>>(context) {
                @Override
                protected void _onNext(DataBean<Recharge> bean) {
                    if (bean != null) {
                        Recharge data = bean.getData();
                        record = new Record(1, data.getCurrency(), DataUtil.doubleFour(data.getAmount()), data.getCreateDateTime(), data.getAddress(), 1, null, data.getHash());
                        setData();
                    }
                }

                @Override
                protected void _onError(String message) {
                    Log.e("充值id获取用户的充值记录", message + "");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void bindListener() {
        setBackOnClickListener();
        recordDetailsAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataUtil.isFastClick()) {
                    // 将文本内容放到系统剪贴板里。
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(recordDetailsAddress.getText().toString().trim());
                    Toast.makeText(RecordDetailsActivity.this, getResources().getString(R.string.copy_ok), Toast.LENGTH_SHORT).show();
                }
            }
        });
        addressCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataUtil.isFastClick()) {
                    // 将文本内容放到系统剪贴板里。
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(recordDetailsAddress.getText().toString().trim());
                    Toast.makeText(RecordDetailsActivity.this, getResources().getString(R.string.copy_ok), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {

    }

}
