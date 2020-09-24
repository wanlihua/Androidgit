package com.dtl.gemini.ui.me.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dtl.gemini.R;
import com.dtl.gemini.api.Api;
import com.dtl.gemini.base.BaseAppActivity;
import com.dtl.gemini.bean.BaseBean;
import com.dtl.gemini.bean.DataBean;
import com.dtl.gemini.common.baserx.RxSchedulers;
import com.dtl.gemini.common.baserx.RxSubscriber;
import com.dtl.gemini.common.commonwidget.CustomProgressDialog;
import com.dtl.gemini.db.StoreUtils;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.model.User;
import com.dtl.gemini.ui.me.model.UserLevel;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.ImageHelper;
import com.dtl.gemini.utils.StatusBarUtil;
import com.dtl.gemini.widget.CircleImageView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 等级信息
 *
 * @author DTL
 * @date 2020/7/27
 **/
public class PurchaseLevelActivity extends BaseAppActivity {

    @Bind(R.id.tv_username)
    TextView tvUsername;
    @Bind(R.id.iv_grade)
    ImageView ivGrade;
    @Bind(R.id.tv_valid_user_num)
    TextView tvValidUserNum;
    @Bind(R.id.tv_pay_num)
    TextView tvPayNum;
    @Bind(R.id.tv_pay_status)
    TextView tvPayStatus;
    @Bind(R.id.ll_pay)
    LinearLayout llPay;
    @Bind(R.id.btn_confirm)
    Button btnConfirm;
    @Bind(R.id.refersh)
    SwipeRefreshLayout refersh;
    @Bind(R.id.civ_head)
    CircleImageView civHead;
    @Bind(R.id.tv_hint)
    TextView tvHint;

    UserLevel userLevel;


    @Override
    public int getLayoutId() {
        return R.layout.activity_purchase_level;
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
        StatusBarUtil.setStatusBarColor2(this);
        setTitle(getResources().getString(R.string.purchase_level_title));
        User user = StoreUtils.init(this).getLoginUser();
        if (user != null && user.getHeadUrl() != null && !user.getHeadUrl().equals("")) {
            ImageHelper.init().showHeadImage(user.getHeadUrl().toString(), civHead);
        }
        getUserLevelByLevel(PurchaseLevelActivity.this);
    }

    private void bindListener() {
        setBackOnClickListener();

        refersh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserLevelByLevel(PurchaseLevelActivity.this);
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataUtil.isFastClick()) {
                    if (userLevel != null && userLevel.getNextConsumeAmount().compareTo(userLevel.getConsumeAmount()) > 0) {
                        CustomProgressDialog.showDialog(PurchaseLevelActivity.this);
                        purchaseLevel(PurchaseLevelActivity.this);
                    }
                }
            }
        });
    }

    private void setData() {
        if (userLevel != null) {
            tvUsername.setText(userLevel.getUserName());
            DataUtil.setUserGrade(userLevel.getGrade(), ivGrade);
            tvValidUserNum.setText("(" + userLevel.getTeamValidUserNumber() + "/" + userLevel.getNextTeamValidUserNumber() + ")");
            if (userLevel.getTeamValidUserNumber() >= userLevel.getNextTeamValidUserNumber()) {
                tvValidUserNum.setTextColor(getResources().getColor(R.color.asset_green));
            } else {
                tvValidUserNum.setTextColor(getResources().getColor(R.color.asset_red));
            }
            tvPayNum.setText(getResources().getString(R.string.pay) + " " + userLevel.getNextConsumeAmount().stripTrailingZeros().toPlainString() + " USDT/GEN");
            tvPayStatus.setText(userLevel.getConsumeAmount().stripTrailingZeros().toPlainString() + "/" + userLevel.getNextConsumeAmount().stripTrailingZeros().toPlainString());
            if (userLevel.getConsumeAmount().compareTo(userLevel.getNextConsumeAmount()) >= 0) {
                setBtnEnble(false);
                tvPayStatus.setTextColor(getResources().getColor(R.color.asset_green));
            } else {
                tvPayStatus.setTextColor(getResources().getColor(R.color.asset_red));
                setBtnEnble(true);
            }
        }
    }

    private void setBtnEnble(Boolean enble) {
        if (enble) {
            btnConfirm.setBackgroundResource(R.drawable.dialogutils_btn);
        } else {
            btnConfirm.setBackgroundResource(R.drawable.item_coin_asset_gray);
        }
        btnConfirm.setEnabled(enble);
        btnConfirm.setClickable(enble);
    }

    private void getUserLevelByLevel(Context context) {
        try {
            Api.getInstance().getUserLevelByLevel(context).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<DataBean<UserLevel>>(context) {
                @Override
                protected void _onNext(DataBean<UserLevel> bean) {
                    if (bean != null && bean.getData() != null) {
                        userLevel = bean.getData();
                    }
                    setData();
                    DataUtil.stopRefersh(refersh);
                }

                @Override
                protected void _onError(String message) {
                    Log.e("查询用户等级信息", message + "");
                    DataUtil.stopRefersh(refersh);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 购买下一等级扣除USDT
     *
     * @param context
     */
    private void purchaseLevel(Context context) {
        try {
            Api.getInstance().purchaseLevel(context).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<BaseBean>(context) {
                @Override
                protected void _onNext(BaseBean bean) {
                    Toast.makeText(context, bean.getMessage(), Toast.LENGTH_SHORT).show();
                    getUserLevelByLevel(context);
                    CustomProgressDialog.dissmissDialog();
                }

                @Override
                protected void _onError(String message) {
                    Log.e("购买下一等级扣除USDT", message + "");
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
