package com.dtl.gemini.widget.pwdEditText;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.dtl.gemini.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

/**
 * Created by Laiyimin on 2017/4/20.
 */

public class PayFragment extends DialogFragment implements View.OnClickListener {

    public static final String EXTRA_CONTENT = "extra_content";    //提示框内容

    private PayPwdView psw_input;
    private PayPwdView.InputCallBack inputCallBack;
    static Dialog dialog;
    private static ImageView close;
    private View.OnClickListener closeListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 使用不带Theme的构造器, 获得的dialog边框距离屏幕仍有几毫米的缝隙。
        dialog = new Dialog(getActivity(), R.style.BottomDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Content前设定
        dialog.setContentView(R.layout.fragment_pay);
        dialog.setCanceledOnTouchOutside(false); // 外部点击取消

        // 设置宽度为屏宽, 靠近屏幕底部。
        final Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.AnimBottom);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
        lp.gravity = Gravity.TOP;
        window.setAttributes(lp);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        initView(dialog);
        return dialog;
    }

    private void initView(Dialog dialog) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            TextView tv_content = (TextView) dialog.findViewById(R.id.tv_content);
            tv_content.setText(bundle.getString(EXTRA_CONTENT));
        }

        psw_input = (PayPwdView) dialog.findViewById(R.id.payPwdView);
        close = (ImageView) dialog.findViewById(R.id.iv_close);
        PwdInputMethodView inputMethodView = (PwdInputMethodView) dialog.findViewById(R.id.inputMethodView);
        psw_input.setInputMethodView(inputMethodView);
        psw_input.setInputCallBack(inputCallBack);
        close.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                if (closeListener != null) {
                    closeListener.onClick(v);
                }
                break;

        }
    }

    public void closeDialog(View.OnClickListener clickListener) {
        if (close != null)
            close.setOnClickListener(clickListener);
    }

    public void setCloseListener(View.OnClickListener closeListener) {
        this.closeListener = closeListener;
    }

    public static void dismissDialog() {
        if (dialog != null) {
            dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
            dialog.dismiss();
        } else {
            return;
        }
    }

    /**
     * 设置输入回调
     *
     * @param inputCallBack
     */
    public void setPaySuccessCallBack(PayPwdView.InputCallBack inputCallBack) {
        this.inputCallBack = inputCallBack;
    }

}
