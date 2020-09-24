package com.dtl.gemini.ui.me.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dtl.gemini.MainActivity;
import com.dtl.gemini.PunkApplication;
import com.dtl.gemini.R;
import com.dtl.gemini.api.Api;
import com.dtl.gemini.base.BaseAppFragment;
import com.dtl.gemini.bean.BaseBean;
import com.dtl.gemini.common.baserx.RxSchedulers;
import com.dtl.gemini.common.baserx.RxSubscriber;
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
import com.dtl.gemini.ui.other.beans.LoginBean;
import com.dtl.gemini.ui.other.beans.VersionBean;
import com.dtl.gemini.utils.AndroidUtil;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.DialogUtils;
import com.dtl.gemini.utils.ImageHelper;
import com.dtl.gemini.utils.StatusBarUtil;
import com.dtl.gemini.widget.CircleImageView;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.math.RoundingMode;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author DTL
 * @date 2020/4/11
 **/
public class MeFragment extends BaseAppFragment implements View.OnClickListener {

    @Bind(R.id.me_head_civ)
    CircleImageView meHeadCiv;
    @Bind(R.id.me_name_tv)
    TextView meNameTv;
    @Bind(R.id.me_refersh)
    SwipeRefreshLayout meRefersh;
    @Bind(R.id.iv_grade)
    ImageView ivGrade;
    @Bind(R.id.tv_phone)
    TextView tvPhone;
    @Bind(R.id.profit_unrealized_type)
    TextView profitUnrealizedType;
    @Bind(R.id.profit_unrealized)
    TextView profitUnrealized;
    @Bind(R.id.profit_realized_type)
    TextView profitRealizedType;
    @Bind(R.id.profit_realized)
    TextView profitRealized;
    @Bind(R.id.version_new_tv)
    TextView versionNewTv;
    @Bind(R.id.tv_invitation_code)
    TextView tvInvitationCode;

    User user;

    VersionBean versionBean;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_me;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    protected void initView() {
        init();
        bindListener();
    }

    private void init() {
    }

    private void bindListener() {
        meRefersh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doChceckVersion(getActivity());
                if (StoreUtils.init(getActivity()).getLoginUser() != null) {
                    queryUserInfo(getActivity());
                } else {
                    DataUtil.stopRefersh(meRefersh);
                    LoginActivity.startAction(getActivity());
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {

    }

    /*
     *退出登录
     */
    private void logout(Context context) {
        try {
            Api.getInstance().logout(context).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<BaseBean>(context) {
                @Override
                protected void _onNext(BaseBean bean) {

                }

                @Override
                protected void _onError(String message) {
                    Log.e("退出登录", message + "");
                }
            });
            MobclickAgent.onProfileSignOff();
            StoreUtils.init(getActivity()).logout();
            getActivity().finish();
            LoginActivity.startAction(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void queryUserInfo(Context context) {
        try {
            Api.getInstance().queryUserInfo(context).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<LoginBean>(context) {
                @Override
                protected void _onNext(LoginBean bean) {
                    if (bean != null && bean.getData() != null) {
                        user = bean.getData();
                        StoreUtils.init(context).storeUser(user);
                        if (user != null) {
                            if (user.getHeadUrl() != null)
                                ImageHelper.init().showHeadImage(user.getHeadUrl().toString(), meHeadCiv);
                            meNameTv.setText(getResources().getString(R.string.username) + "：" + user.getUsername());
                            tvInvitationCode.setText(user.getInvitationCode());
                            tvPhone.setText(getResources().getString(R.string.phone) + "：" + user.getPhone());
                            DataUtil.setUserGrade(user.getGrade(), ivGrade);
                        }
                    }
                    DataUtil.stopRefersh(meRefersh);
                }

                @Override
                protected void _onError(String message) {
                    Log.e("获取当前用户信息", message + "");
                    DataUtil.stopRefersh(meRefersh);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 检测版本
     */
    private void doChceckVersion(Context context) {
        versionNewTv.setVisibility(View.GONE);
        int versionCode = AndroidUtil.getVersionCode(PunkApplication.getAppContext());
        try {
            Api.getInstance().getAppVersion(context).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<VersionBean>(context) {
                @Override
                protected void _onNext(VersionBean bean) {
                    versionBean = bean;
                    if (bean != null && bean.getStatus() == Constant.SUCCESS) {
                        if (versionCode < bean.getData().getVersionCode())
                            versionNewTv.setVisibility(View.VISIBLE);
                    }
                    DataUtil.stopRefersh(meRefersh);
                }

                @Override
                protected void _onError(String message) {
                    Log.e("检查更新", message + "");
                    DataUtil.stopRefersh(meRefersh);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.me_head_civ, R.id.me_name_tv, R.id.ll_purchase_level, R.id.iv_invitation_code_copy,
            R.id.profit_unrealized_cl, R.id.profit_realized_cl, R.id.real_name_ll, R.id.down_url_ll,
            R.id.sys_setting_ll, R.id.version_update_ll, R.id.logout_ll, R.id.kefu_ll})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.me_head_civ:
                if (DataUtil.isFastClick()) {
                    if (user == null) {
                        LoginActivity.startAction(getActivity());
                    }
                }
                break;
            case R.id.me_name_tv:
                if (DataUtil.isFastClick()) {
                    if (user == null) {
                        LoginActivity.startAction(getActivity());
                    }
                }
                break;
            case R.id.ll_purchase_level:
                if (DataUtil.isFastClick()) {
                    DataUtil.startActivity(getActivity(), PurchaseLevelActivity.class);
                }
                break;
            case R.id.iv_invitation_code_copy:
                if (DataUtil.isFastClick()) {
                    DataUtil.copyString(getActivity(), tvInvitationCode);
                }
                break;
            case R.id.profit_unrealized_cl:
                if (DataUtil.isFastClick()) {

                }
                break;
            case R.id.real_name_ll:
                if (DataUtil.isFastClick()) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", user);
                    DataUtil.startActivity(getActivity(), RealNameActivity.class, bundle);
                }
                break;
            case R.id.profit_realized_cl:
                if (DataUtil.isFastClick()) {

                }
            case R.id.down_url_ll:
                if (DataUtil.isFastClick()) {
                    startActivity(new Intent(getActivity(), ShareActivity.class));
                }
                break;
            case R.id.sys_setting_ll:
                if (DataUtil.isFastClick()) {
                    DataUtil.startActivity(getActivity(), SettingActivity.class);
                }
                break;
            case R.id.version_update_ll:
                if (DataUtil.isFastClick()) {
                    if (versionBean != null && AndroidUtil.getVersionCode(PunkApplication.getAppContext()) < versionBean.getData().getVersionCode()) {
                        Intent intent = new Intent();
                        intent.setData(Uri.parse(versionBean.getData().getUrl()));//Url 就是你要打开的网址
                        intent.setAction(Intent.ACTION_VIEW);
                        getActivity().startActivity(intent); //启动浏览器
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.is_version_new_hide), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.logout_ll:
                if (DataUtil.isFastClick()) {
                    DialogUtils.showExitDialog(getActivity(), getResources().getString(R.string.log_out));
                    DialogUtils.btn((v) -> {
                        DialogUtils.dismissDialog();
                        logout(getActivity());
                    });
                }
                break;
            case R.id.kefu_ll:
                if (DataUtil.isFastClick()) {
                    DataUtil.startActivity(getActivity(), UserChildrenActivity.class);
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        StatusBarUtil.setStatusBarColor2(getActivity());
        doChceckVersion(getActivity());
        queryUserInfo(getActivity());
    }
}
