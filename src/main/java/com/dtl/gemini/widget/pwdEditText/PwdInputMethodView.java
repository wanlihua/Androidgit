package com.dtl.gemini.widget.pwdEditText;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.dtl.gemini.R;

import androidx.annotation.Nullable;

/**
 * 输入键盘
 * Created by Administrator on 2017/4/19.
 */

public class PwdInputMethodView extends LinearLayout implements View.OnClickListener {

    private InputReceiver inputReceiver;

    public PwdInputMethodView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_password_input, this);

        initView();
    }

    private void initView() {
        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);
        findViewById(R.id.btn_4).setOnClickListener(this);
        findViewById(R.id.btn_5).setOnClickListener(this);
        findViewById(R.id.btn_6).setOnClickListener(this);
        findViewById(R.id.btn_7).setOnClickListener(this);
        findViewById(R.id.btn_8).setOnClickListener(this);
        findViewById(R.id.btn_9).setOnClickListener(this);
        findViewById(R.id.btn_0).setOnClickListener(this);
        findViewById(R.id.btn_del).setOnClickListener(this);

        findViewById(R.id.layout_hide).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                setVisibility(GONE);
            }
        });
    }

    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 100;
    private static long lastClickTime;

    @Override
    public void onClick(View v) {
        String num = (String) v.getTag();
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            // 超过点击间隔后再将lastClickTime重置为当前点击时间
            lastClickTime = curClickTime;
            this.inputReceiver.receive(num);
        }
    }


    /**
     * 设置接收器
     *
     * @param receiver
     */
    public void setInputReceiver(InputReceiver receiver) {
        this.inputReceiver = receiver;
    }

    /**
     * 输入接收器
     */
    public interface InputReceiver {

        void receive(String num);
    }
}



