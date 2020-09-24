package com.dtl.gemini.common.baserx;

import android.content.Context;
import android.util.Log;

import com.dtl.gemini.PunkApplication;
import com.dtl.gemini.R;
import com.dtl.gemini.common.baseapp.BaseApplication;
import com.dtl.gemini.common.commonutils.NetWorkUtils;
import com.dtl.gemini.utils.DialogUtils;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import rx.Subscriber;

/**
 * des:订阅封装
 * Created by xsf
 * on 2016.09.10:16
 */

/********************
 * 使用例子
 ********************/
/*_apiService.login(mobile, verifyCode)
        .//省略
        .subscribe(new RxSubscriber<User user>(mContext,false) {
@Override
public void _onNext(User user) {
        // 处理user
        }

@Override
public void _onError(String msg) {
        ToastUtil.showShort(mActivity, msg);
        });*/
public abstract class RxSubscriber<T> extends Subscriber<T> {

    private static final String TAG = "RxSubscriber_ONERROR";

    private Context mContext;
    private String msg;
    private boolean showDialog = true;

    /**
     * 是否显示浮动dialog
     */
    public void showDialog() {
        this.showDialog = true;
    }

    public void hideDialog() {
        this.showDialog = true;
    }

    public RxSubscriber(Context context, String msg, boolean showDialog) {
        this.mContext = context;
        this.msg = msg;
        this.showDialog = showDialog;
    }

    public RxSubscriber(Context context) {
        this(context, "", true);
    }

    public RxSubscriber(Context context, boolean showDialog) {
//        this(context, BaseApplication.getAppContext().getString(R.string.loading), showDialog);
        this(context, "", showDialog);
    }

    @Override
    public void onCompleted() {
        if (showDialog) stopProgressDialog();
        _onAfter();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (showDialog) {
            startProgressDialog(msg);
        }
        _onStart();
    }

    private void startProgressDialog(String msg) {
//        PunkApplication.showDialog(mContext,msg);
    }

    private void stopProgressDialog() {
        PunkApplication.closeDialog();
    }

    @Override
    public void onNext(T t) {
        stopProgressDialog();
        _onNext(t);
    }

    @Override
    public void onError(Throwable e) {
        try {
            _onAfter();

            Log.i(TAG, "onError: " + e.getMessage());
            stopProgressDialog();
            e.printStackTrace();
            //网络
            if (!NetWorkUtils.isNetConnected(BaseApplication.getAppContext())) {
//            _onError(BaseApplication.getAppContext().getString(R.string.no_net));
                _onError("网络不可用");
                DialogUtils.dismissDialog(mContext);
                DialogUtils.showErrorDialog(mContext, mContext.getResources().getString(R.string.hint), mContext.getResources().getString(R.string.no_net));
            }
            //服务器
            else if (e instanceof ServerException) {
                _onError(e.getMessage());
            }
            //手动抛出
            else if (e instanceof IllegalStateException) {
                _onError(e.getMessage());
            }
            //超时
            else if (e instanceof SocketTimeoutException) {
                _onError(e.getMessage());
            }
            //无效链接
            else if (e instanceof ConnectException) {
                _onError(e.getMessage());
            } else {
                _onError(e.getMessage());
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    protected void _onStart() {
    }

    protected abstract void _onNext(T t);

    protected abstract void _onError(String message);

    protected void _onAfter() {
    }

}
