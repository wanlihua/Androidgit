package com.dtl.gemini.ui.me.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dtl.gemini.R;
import com.dtl.gemini.base.BaseAppActivity;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.utils.DataUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;

/**
 * 客服
 *
 * @author DTL
 * @date 2020/5/28
 **/
public class KefuActivity extends BaseAppActivity {

    @Bind(R.id.kefu_ll)
    LinearLayout kefuLl;
    @Bind(R.id.kefu_email)
    TextView kefuEmail;

    @Override
    public int getLayoutId() {
        return R.layout.activity_kefu;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        kefuEmail.setText(Constant.kefu_email);
        setTitle(getResources().getString(R.string.me_kefu));
        setBackOnClickListener();
        kefuLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataUtil.isFastClick()) {
                    // 将文本内容放到系统剪贴板里。
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(kefuEmail.getText().toString().trim());
                    Toast.makeText(KefuActivity.this, getResources().getString(R.string.copy_ok), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {

    }

}
