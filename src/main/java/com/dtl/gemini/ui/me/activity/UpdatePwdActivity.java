package com.dtl.gemini.ui.me.activity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dtl.gemini.R;
import com.dtl.gemini.api.Api;
import com.dtl.gemini.base.BaseAppActivity;
import com.dtl.gemini.bean.BaseBean;
import com.dtl.gemini.common.baserx.RxSchedulers;
import com.dtl.gemini.common.baserx.RxSubscriber;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.RegularUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author DTL
 * @date 2020/4/28
 * 修改密码
 **/
public class UpdatePwdActivity extends BaseAppActivity implements View.OnClickListener {

    @Bind(R.id.pwd1_et)
    EditText pwd1Et;
    @Bind(R.id.see_pwd1_iv)
    ImageView seePwd1Iv;
    @Bind(R.id.pwd2_et)
    EditText pwd2Et;
    @Bind(R.id.see_pwd2_iv)
    ImageView seePwd2Iv;
    @Bind(R.id.code_et)
    EditText codeEt;
    @Bind(R.id.getcode_tv)
    TextView getcodeTv;

    boolean isRunning = false, pwd1See = false, pwd2See = false;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                if (msg.what == 0) {
                    getcodeTv.setText(getResources().getText(R.string.send));
                    setGetCodeEnd(true);
                } else {
                    getcodeTv.setText(msg.what + "s");
                }
            } catch (Exception e) {
            }
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_update_pwd;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        setTitle(getResources().getString(R.string.update_login_pwd));
        setBackOnClickListener();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {

    }

    private void setGetCodeEnd(Boolean status) {
        if (status) {
            getcodeTv.setEnabled(true);
            getcodeTv.setClickable(true);
        } else {
            getcodeTv.setEnabled(false);
            getcodeTv.setClickable(false);
        }
    }

    private void hideJianPan() {
        DataUtil.hideJianPan(this, pwd1Et);
        DataUtil.hideJianPan(this, pwd2Et);
        DataUtil.hideJianPan(this, codeEt);
    }

    @OnClick({R.id.getcode_tv, R.id.see_pwd1_iv, R.id.see_pwd2_iv, R.id.confirm_btn})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.see_pwd1_iv:
                if (DataUtil.isFastClick()) {
                    setPwdSee(pwd1See, pwd1Et, seePwd1Iv);
                }
                break;
            case R.id.see_pwd2_iv:
                if (DataUtil.isFastClick()) {
                    setPwdSee(pwd2See, pwd2Et, seePwd2Iv);
                }
                break;
            case R.id.getcode_tv:
                if (DataUtil.isFastClick()) {
                    hideJianPan();
                    getCode(this);
                }
                break;
            case R.id.confirm_btn:
                if (DataUtil.isFastClick()) {
                    hideJianPan();
                    String pwd1 = pwd1Et.getText().toString().trim();
                    String pwd2 = pwd2Et.getText().toString().trim();
                    String code = codeEt.getText().toString().trim();
                    if (!RegularUtil.valiPwd(pwd1)) {
                        Toast.makeText(this, getResources().getString(R.string.pwd_formar_error), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!pwd1.equals(pwd2)) {
                        Toast.makeText(this, getResources().getString(R.string.pwd_vali_error), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (code.length() < 6) {
                        Toast.makeText(this, getResources().getString(R.string.code_formar_error), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    updatePwd(this, pwd1, code);
                }
                break;
        }
    }

    /**
     * 获取验证码
     *
     * @param context
     */
    private void getCode(Context context) {
        try {
            Api.getInstance().sendUpdateLoginPassword(context).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<BaseBean>(context) {
                @Override
                protected void _onNext(BaseBean bean) {
                    if (bean != null && bean.getStatus() == Constant.SUCCESS) {
                        Toast.makeText(context, getResources().getString(R.string.send_ok), Toast.LENGTH_SHORT).show();
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
     * 修改密码
     *
     * @param context
     * @param pwd
     */
    private void updatePwd(Context context, String pwd, String code) {
        try {
            Api.getInstance().updateLoginPassword(context, pwd, code).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<BaseBean>(context) {
                @Override
                protected void _onNext(BaseBean bean) {
                    if (bean != null && bean.getStatus() == Constant.SUCCESS) {
                        Toast.makeText(context, getResources().getString(R.string.update_ok), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                protected void _onError(String message) {
                    Toast.makeText(context, message + "", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 密码显示
     *
     * @param status    状态
     * @param editText  文本框
     * @param imageView 显示按钮
     */
    private void setPwdSee(boolean status, EditText editText, ImageView imageView) {
        if (status) {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
            imageView.setImageResource(R.mipmap.icon_see_on);
        } else {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            imageView.setImageResource(R.mipmap.icon_see_off);
        }
        status = !status;
    }

}
