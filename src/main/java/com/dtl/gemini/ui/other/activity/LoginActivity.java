package com.dtl.gemini.ui.other.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dtl.gemini.MainActivity;
import com.dtl.gemini.R;
import com.dtl.gemini.api.Api;
import com.dtl.gemini.base.BaseAppActivity;
import com.dtl.gemini.bean.BaseBean;
import com.dtl.gemini.common.baseapp.AppManager;
import com.dtl.gemini.common.baserx.RxSchedulers;
import com.dtl.gemini.common.baserx.RxSubscriber;
import com.dtl.gemini.common.commonwidget.CustomProgressDialog;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.db.StoreUtils;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.model.User;
import com.dtl.gemini.ui.cfd.beans.CurrPriceBean;
import com.dtl.gemini.ui.other.beans.AreaCodeBean;
import com.dtl.gemini.ui.other.beans.LoginBean;
import com.dtl.gemini.ui.other.model.AreaCode;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.DialogUtils;
import com.dtl.gemini.widget.AutoWheelChoicePopup;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * @author DTL
 * @date 2020/4/14
 * 登录
 **/
public class LoginActivity extends BaseAppActivity implements View.OnClickListener {
    @Bind(R.id.phone_area_tv)
    TextView phoneAreaTv;
    @Bind(R.id.phone_rl)
    RelativeLayout phoneRl;
    @Bind(R.id.login_pwd_or_code)
    TextView loginPwdOrCode;
    @Bind(R.id.login_phone_et)
    EditText loginPhoneEt;
    @Bind(R.id.login_password_et)//密码
    EditText loginPasswordEt;
    @Bind(R.id.see_pwd_iv)
    ImageView seePwdIv;
    @Bind(R.id.pwd_login_ll)
    LinearLayout pwdLoginLl;
    @Bind(R.id.login_code_et)
    EditText loginCodeEt;
    @Bind(R.id.login_getcode_tv)
    TextView loginGetcodeTv;
    @Bind(R.id.code_login_ll)
    LinearLayout codeLoginLl;

    AutoWheelChoicePopup popup;
    List<String> list = new ArrayList<>();

    boolean pwdSee = false, isPwdLogin = true;

    private String phone = "", pwd = "", area = "86";

    boolean isRunning = false;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                if (msg.what == 0) {
                    loginGetcodeTv.setText(getResources().getText(R.string.send));
                    setGetCodeEnd(true);
                    loginPhoneEt.setEnabled(true);
                    loginPhoneEt.setClickable(true);
                } else {
                    loginGetcodeTv.setText(msg.what + "s");
                }
            } catch (Exception e) {
            }
        }
    };

    public static void startAction(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    public static void startAction(Context context, int status) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("status", status);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
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
        int status = getIntent().getIntExtra("status", 0);
        if (status == Constant.FAILURE) {
            String hint = getResources().getString(R.string.login_failure);
            Toast.makeText(this, hint, Toast.LENGTH_SHORT).show();
        }
        loginPhoneEt.setHint(DataUtil.returnNoNullHint(this, getResources().getString(R.string.phone)));
        loginPasswordEt.setHint(DataUtil.returnNoNullHint(this, getResources().getString(R.string.pwd)));
        loginCodeEt.setHint(DataUtil.returnNoNullHint(this, getResources().getString(R.string.code)));
        qureyAreaCode(this);
    }

    private void bindListener() {

    }

    private void initPopup() {
        if (list == null || list.size() == 0)
            return;
        popup = new AutoWheelChoicePopup(this, list);
        popup.setBtnOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                area = popup.getSelecedData();
                phoneAreaTv.setText(getString(R.string.area, area));
                popup.dismiss();
            }
        });
    }

    private void setGetCodeEnd(Boolean status) {
        if (status) {
            loginGetcodeTv.setEnabled(true);
            loginGetcodeTv.setClickable(true);
        } else {
            loginGetcodeTv.setEnabled(false);
            loginGetcodeTv.setClickable(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {

    }

    @OnClick({R.id.phone_rl, R.id.see_pwd_iv, R.id.login_getcode_tv, R.id.login_pwd_or_code,
            R.id.login_btn, R.id.register_btn, R.id.login_findpwd_tv})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.phone_rl://+86
                if (DataUtil.isFastClick()) {
                    DataUtil.hideJianPan(this, loginPhoneEt);
                    DataUtil.hideJianPan(this, loginPasswordEt);
                    DataUtil.hideJianPan(this, loginCodeEt);
                    if (popup != null && list != null && list.size() > 0)
                        popup.showAtLocation(findViewById(R.id.login_view), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
                break;
            case R.id.see_pwd_iv:
                if (DataUtil.isFastClick()) {
                    if (pwdSee) {
                        loginPasswordEt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                        seePwdIv.setImageResource(R.mipmap.icon_see_on);
                    } else {
                        loginPasswordEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        seePwdIv.setImageResource(R.mipmap.icon_see_off);
                    }
                    pwdSee = !pwdSee;
                }
                break;
            case R.id.login_getcode_tv://发送
                if (DataUtil.isFastClick()) {
                    phone = loginPhoneEt.getText().toString().trim();
                    if (phone.length() < 7) {
                        Toast.makeText(this, getResources().getString(R.string.phone_formar_error), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    getCode(this);
                    setGetCodeEnd(false);
                }
                break;
            case R.id.login_pwd_or_code://验证码登入
                if (DataUtil.isFastClick()) {
                    if (isPwdLogin) {
                        codeLoginLl.setVisibility(View.VISIBLE);
                        pwdLoginLl.setVisibility(View.GONE);
                        loginPwdOrCode.setText(getResources().getString(R.string.pwd_login));
                    } else {
                        codeLoginLl.setVisibility(View.GONE);
                        pwdLoginLl.setVisibility(View.VISIBLE);
                        loginPwdOrCode.setText(getResources().getString(R.string.code_login));
                    }
                    isPwdLogin = !isPwdLogin;
                }
                break;
            case R.id.login_btn://登入  login_btn登录

//                Toast toast = Toast.makeText(LoginActivity.this,'要',Toast.LENGTH_SHORT);

                Toast.makeText(LoginActivity.this, "111我是通过makeText方法创建的消息提示框", Toast.LENGTH_SHORT).show();

                if (DataUtil.isFastClick()) {
                    phone = loginPhoneEt.getText().toString().trim();
                    if (phone.length() < 7) {
                        Toast.makeText(this, getResources().getString(R.string.phone_formar_error), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!isPwdLogin) {
                        pwd = loginCodeEt.getText().toString().trim();
                        if (pwd.length() < 6) {
                            Toast.makeText(this, getResources().getString(R.string.code_formar_error), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        pwd = loginPasswordEt.getText().toString().trim();
                        if (pwd.length() < 8) {
                            Toast.makeText(this, getResources().getString(R.string.pwd_formar_error), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    CustomProgressDialog.showDialog(LoginActivity.this);
                    if (isPwdLogin) {
                        login(this, 1);
                    } else {
                        login(this, 2);
                    }
                }
                break;
            case R.id.register_btn://注册
                if (DataUtil.isFastClick()) {
                    startActivity(new Intent(this, RegisterActivity.class));
                }
                break;
            case R.id.login_findpwd_tv:
                if (DataUtil.isFastClick()) {
                    startActivity(new Intent(this, FindPwdActivity.class));
                }
                break;
        }
    }

    /**
     * 获取区号
     *
     * @param context
     */
    private void qureyAreaCode(Context context) {
        try {
            Api.getInstance().qureyAreaCode(context).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<AreaCodeBean>(context) {
                @Override
                protected void _onNext(AreaCodeBean bean) {
                    if (bean.getData() != null && bean.getData().size() > 0) {
                        for (AreaCode areaCode : bean.getData()) {
                            list.add(areaCode.getAreaCode() + "");
                        }
                        initPopup();
                    }
                }

                @Override
                protected void _onError(String message) {
                    Log.e("获取区号", message + "");
                    String[] areas = getResources().getStringArray(R.array.areas);
                    for (String area : areas) {
                        list.add(area.replaceAll("a", "").trim());
                    }
                    initPopup();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取验证码
     *
     * @param context
     */
    private void getCode(Context context) {
        try {
            Api.getInstance().sendLoginSmsCode(context, area + phone).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<BaseBean>(context) {
                @Override
                protected void _onNext(BaseBean bean) {
                    if (bean != null && bean.getStatus() == Constant.SUCCESS) {
                        loginPhoneEt.setEnabled(false);
                        loginPhoneEt.setClickable(false);
                        Toast.makeText(context, DataUtil.returnSendCodeHint(context, phone), Toast.LENGTH_SHORT).show();
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
     * 登录
     *
     * @param context
     * @param type    1:密码,2:验证码
     */
    private void login(Context context, int type) {
        try {
            Api.getInstance().login(context, area + phone, pwd, type).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<LoginBean>(context) {
                @Override
                protected void _onNext(LoginBean bean) {
                    CustomProgressDialog.dissmissDialog();
                    if (bean != null && bean.getStatus() == Constant.SUCCESS) {
                        if (StoreUtils.init(context).getLoginUser() != null) {
                            StoreUtils.init(context).logout();
                        }
                        User user = bean.getData();
                        StoreUtils.init(context).storeUser(user);
                        //当用户使用自有账号登录时，可以这样统计：
                        MobclickAgent.onProfileSignIn(area + phone);
                        startMenu();
//                        queryCfdToken(context);
                    }
                }

                @Override
                protected void _onError(String message) {
                    CustomProgressDialog.dissmissDialog();
                    Log.e("登录", message + "");
                    DialogUtils.showErrorDialog(context, getResources().getString(R.string.hint), message);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void queryCfdToken(Context context) {
        try {
            OkGo.get(Constant.CFD_GET_TOKEN)
                    .tag(context)  // 请求方式和请求url
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            startMenu();
                        }

                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            CurrPriceBean bean = new Gson().fromJson(s + "", CurrPriceBean.class);
                            if (bean != null) {
                                StoreUtils.init(context).setRongToken(bean.getMassage());
                            }
                            startMenu();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 跳转至主菜单页面
     */
    private void startMenu() {
        DataUtil.hideJianPan(this, loginPhoneEt);
        DataUtil.hideJianPan(this, loginCodeEt);
        DataUtil.hideJianPan(this, loginPasswordEt);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    long keyBackTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - keyBackTime < 5000) {
                AppManager.getAppManager().finishAllActivity();
            } else {
                Toast.makeText(this, "" + getResources().getString(R.string.key_download), Toast.LENGTH_SHORT).show();
                keyBackTime = System.currentTimeMillis();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
