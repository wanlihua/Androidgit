package com.dtl.gemini.widget;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import com.dtl.gemini.db.StoreUtils;
import com.dtl.gemini.model.User;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;

/**
 * 大图灵: created by Administrator on 2019/1/9 18:45
 */
public class JavaScriptMethods extends Object {
    private Context context;
    private WebView mWebView;
    private Handler mHandler = new Handler();
    private User user;
    private String headUrl, token, username = null, recommenderid = null;

    public JavaScriptMethods(Context context, WebView webView) {
        this.context = context;
        this.mWebView = webView;
    }

    @JavascriptInterface //android4.2之后，如果不加上该注解，js无法调用android方法（安全）
    public String getUserInfo() {
        Log.e("JS交互", "Js调用了方法");
        user = StoreUtils.init(context).getLoginUser();
        token = StoreUtils.init(context).getToken();
        if (user != null) {
            try {
                username = new String(user.getUsername().getBytes("UTF-8"), "UTF-8");
//                if (user.getRecommenderid().trim().equals("无"))
//                    recommenderid = null;
//                else
//                    recommenderid = new String(user.getRecommenderid().getBytes("UTF-8"), "UTF-8");
                headUrl = new String(user.getHeadUrl().toString().getBytes("UTF-8"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        JSONObject json = null;
        try {
            //当页面加载完成后，调用js方法
            json = new JSONObject();
//            json.put("userid", user.getId() + "");
            json.put("username", username);
            json.put("headurl", headUrl);
//            json.put("phone", user.getPhone() + "");
            json.put("recommenderid", recommenderid);
            json.put("token", token + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

}
