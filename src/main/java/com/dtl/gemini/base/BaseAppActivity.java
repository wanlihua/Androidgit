package com.dtl.gemini.base;

import android.view.View;
import android.widget.TextView;

import com.dtl.gemini.R;
import com.dtl.gemini.common.base.BaseActivity;
import com.dtl.gemini.common.base.BaseModel;
import com.dtl.gemini.common.base.BasePresenter;

public abstract class BaseAppActivity<T extends BasePresenter, E extends BaseModel> extends BaseActivity<T, E> {

    public void setTitle(String title){
        ((TextView)findViewById(R.id.title)).setText(title);
    }

    public void setBackOnClickListener(){
        try {
            findViewById(R.id.back).setOnClickListener((v)->{
                finish();
            });
        }catch (Exception e){}
    }

    public void setRightTile(String title){
        try {
            findViewById(R.id.title_right).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.title_right)).setText(title);
        }catch (Exception e){}
    }

    public void setRightTileClickListener(View.OnClickListener clickListener){
        try {
            findViewById(R.id.title_right).setOnClickListener(clickListener);
        }catch (Exception e){}
    }

    public void showLoading() {
        startProgressDialog();
    }

    public void showLoading(String title) {
        startProgressDialog(title);
    }

    public void stopLoading() {
        stopProgressDialog();
    }

    public void showErrorTip(String msg) {
        showShortToast(msg);
    }

}
