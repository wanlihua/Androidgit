package com.dtl.gemini.common.base;

import android.content.Context;

public abstract class BasePresenter<T extends com.dtl.gemini.common.base.BaseView, E extends com.dtl.gemini.common.base.BaseModel> {
    public Context mContext;
    public E mModel;
    public T mView;
    public com.dtl.gemini.common.baserx.RxManager mRxManage = new com.dtl.gemini.common.baserx.RxManager();

    public void setVM(T v, E m) {
        this.mView = v;
        this.mModel = m;
        mModel.setTag(mContext);
        this.onStart();
    }

    public void onStart() {
    }

    public void onDestroy() {
        mRxManage.clear();
    }
}
