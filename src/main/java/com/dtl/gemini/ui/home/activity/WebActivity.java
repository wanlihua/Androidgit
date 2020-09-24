package com.dtl.gemini.ui.home.activity;

import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.dtl.gemini.R;
import com.dtl.gemini.base.BaseAppActivity;
import com.dtl.gemini.common.commonwidget.CustomProgressDialog;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.utils.DataUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;

/**
 * @author DTL
 * @date 2020/4/11
 * 网页详情
 **/
public class WebActivity extends BaseAppActivity {
    @Bind(R.id.webview)
    WebView webView;
    @Bind(R.id.g_back)
    ImageView gBack;

    int width, height;

    @Override
    public int getLayoutId() {
        return R.layout.activity_web;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        init();
        bindListener();
    }

    public static void startAction(Context context, String title, String url) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    private void init() {
        DataUtil.setLanguage(this);
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        width = outMetrics.widthPixels;
        height = outMetrics.heightPixels;
        String url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");
//        if (title != null && !title.equals("")) {
//            setTitle(title);
//        }
        if (url != null && !url.equals("")) {
            webView.loadUrl(url);
        }

        WebSettings settings = webView.getSettings();
        //支持缩放，默认为true。
        settings.setSupportZoom(true);
        //调整图片至适合webview的大小
        settings.setUseWideViewPort(true);
        // 缩放至屏幕的大小
        settings.setLoadWithOverviewMode(true);
        //设置默认编码
        settings.setDefaultTextEncodingName("utf-8");
        //设置自动加载图片
        settings.setLoadsImagesAutomatically(true);
        //获取触摸焦点
        webView.requestFocusFromTouch();
        //允许访问文件
        settings.setAllowFileAccess(true);
        //开启javascript
        settings.setJavaScriptEnabled(true);
        //支持通过JS打开新窗口
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        //提高渲染的优先级
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // 设置出现缩放工具
        settings.setBuiltInZoomControls(false);
        //不显示webview缩放按钮
        settings.setDisplayZoomControls(true);
        //自适应屏幕
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        /**
         * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
         * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
         * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
         * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
         */
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存，只从网络获取数据.
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(new MyWebViewClient());
    }

    private void bindListener() {
        gBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webView.canGoBack()) {
                    webView.goBack();
                    return;
                } else {
                    finish();
                }
            }
        });
    }


    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {//页面加载完成
            super.onPageFinished(view, url);
            if (WebActivity.this != null && !WebActivity.this.isFinishing() && view != null)
                CustomProgressDialog.dissmissDialog();
            imgReset();//重置webview中img标签的图片大小
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
            if (WebActivity.this != null && !WebActivity.this.isFinishing() && view != null)
                CustomProgressDialog.showDialog(WebActivity.this);
        }

    }

    private void imgReset() {
        try {
            if (webView != null) {
                webView.loadUrl("javascript:(function(){" +
                        "var objs = document.getElementsByTagName('img'); " +
                        "for(var i=0;i<objs.length;i++)  " +
                        "{"
                        + "var img = objs[i];   " +
                        " img.style.maxWidth = '" + width + "px'; img.style.height = 'auto';  " +
                        "}" +
                        "})()");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("e", "InformationDetailActivity");
        }
    }

    //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
    private WebChromeClient webChromeClient = new WebChromeClient() {
        //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
            localBuilder.setMessage(message).setPositiveButton("确定", null);
            localBuilder.setCancelable(false);
            localBuilder.create().show();

            //注意:
            //必须要这一句代码:result.confirm()表示:
            //处理结果为确定状态同时唤醒WebCore线程
            //否则不能继续点击按钮
            result.confirm();
            return true;
        }

        //获取网页标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }

        //加载进度回调
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (webView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {//点击返回按钮的时候判断有没有上一页
            webView.goBack(); // goBack()表示返回webView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * JS调用android的方法
     *
     * @param str
     * @return
     */
    @JavascriptInterface //仍然必不可少
    public void getClient(String str) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }

        // Otherwise defer to system default behavior.
        super.onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {

    }
}
