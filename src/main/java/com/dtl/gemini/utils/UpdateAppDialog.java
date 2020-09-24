package com.dtl.gemini.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dtl.gemini.R;
import com.dtl.gemini.common.commonutils.ToastUitl;

/**
 * 大图灵
 * 2019/10/16
 **/
public class UpdateAppDialog {
    Context context;
    static Dialog dialog;
    static TextView title, content1, content2, proTv;
    static Button ok_btn, cancel_btn;
    static LinearLayout llTop, bottomLl1, bottomLl2;
    static ProgressBar pro;

    public UpdateAppDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context, R.style.update_dialogs);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = View.inflate(context, R.layout.dialog_update_app, null);
        llTop = view.findViewById(R.id.ll_top);
        bottomLl1 = view.findViewById(R.id.bottom_ll1);
        bottomLl2 = view.findViewById(R.id.bottom_ll2);
        title = view.findViewById(R.id.title);
        content1 = view.findViewById(R.id.content1);
        content2 = view.findViewById(R.id.content2);
        cancel_btn = view.findViewById(R.id.cancle_btn);
        ok_btn = view.findViewById(R.id.ok_btn);
        proTv = view.findViewById(R.id.pro_tv);
        pro = view.findViewById(R.id.update_pro);
        dialog.setContentView(view);// 将自定义的布局文件设置给dialog
    }

    public static void showDialog(Context context, String titles, String content, int isForce) {
        new com.dtl.gemini.utils.UpdateAppDialog(context);
        bottomLl1.setVisibility(View.VISIBLE);
        bottomLl2.setVisibility(View.GONE);
        title.setText(titles);
        content1.setText(content);
        content1.setMovementMethod(LinkMovementMethod.getInstance());
        content1.setLinkTextColor(context.getResources().getColor(R.color.update_ok));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        if (isForce == 0) {
            cancel();
        } else {
            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ToastUitl.showShort(context.getResources().getString(R.string.update_app_isforce));
                }
            });
        }
    }

    public static void showProDialog(Context context, String titles, String content, int isForce) {
        new com.dtl.gemini.utils.UpdateAppDialog(context);
        bottomLl1.setVisibility(View.GONE);
        bottomLl2.setVisibility(View.VISIBLE);
        title.setText(titles);
        content1.setText(content);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        if (isForce == 0) {
            cancel();
        } else {
            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ToastUitl.showShort(context.getResources().getString(R.string.update_app_isforce));
                }
            });
        }
    }

    public static void setLlTop(View.OnClickListener clickListener) {
        if (llTop != null)
            llTop.setOnClickListener(clickListener);
    }

    public static void setTitle(String str) {
        if (title != null)
            title.setText(str);
    }

    public static void setLlBottom(int type) {
        if (bottomLl1 != null && bottomLl2 != null) {
            if (type == 1) {
                bottomLl1.setVisibility(View.VISIBLE);
                bottomLl2.setVisibility(View.GONE);
            } else {
                bottomLl1.setVisibility(View.GONE);
                bottomLl2.setVisibility(View.VISIBLE);
            }
        }
    }

    public static void setProMax(int maxPro) {
        pro.setMax(maxPro);

    }

    public static void setPro(int pros, String currentSize, String totalSize, String percentage) {
        if (pro != null)
            pro.setProgress(pros);
        if (proTv != null)
            proTv.setText(currentSize + " MB/" + totalSize + " MB(" + percentage + "%)");
    }

    public static void btnOk(View.OnClickListener clickListener) {
        if (ok_btn != null)
            ok_btn.setOnClickListener(clickListener);
    }

    public static void cancel() {
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public static void dismissDialog(Context context) {
        if (context != null && !((Activity) context).isFinishing() && dialog != null) {
            dialog.dismiss();
        } else {
            return;
        }
    }
}
