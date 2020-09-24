package com.dtl.gemini.widget;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dtl.gemini.R;


/**
 * 大图灵
 * 2019/2/18
 * 左上角弹窗
 **/
public class MorePopWindow extends PopupWindow {
    Context context;
    public RelativeLayout rl1;
    public RelativeLayout rl2;
    public RelativeLayout rl3;
    public RelativeLayout rl4;
    public RelativeLayout rl5;
    public TextView textView1;
    public TextView textView2;
    public TextView textView3;
    public TextView textView4;
    public TextView textView5;

    @SuppressLint("InflateParams")
    public MorePopWindow(Context context, Object tv1, Object tv2, Object tv3, Object tv4, Object tv5) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.popupwindow_add, null);

        // 设置SelectPicPopupWindow的View
        this.setContentView(content);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);
        rl1 = (RelativeLayout) content.findViewById(R.id.rl1);
        rl2 = (RelativeLayout) content.findViewById(R.id.rl2);
        rl3 = (RelativeLayout) content.findViewById(R.id.rl3);
        rl4 = (RelativeLayout) content.findViewById(R.id.rl4);
        rl5 = (RelativeLayout) content.findViewById(R.id.rl5);
        textView1 = (TextView) content.findViewById(R.id.tv1);
        textView2 = (TextView) content.findViewById(R.id.tv2);
        textView3 = (TextView) content.findViewById(R.id.tv3);
        textView4 = (TextView) content.findViewById(R.id.tv4);
        textView5 = (TextView) content.findViewById(R.id.tv5);
        if (tv1 != null)
            rl1.setVisibility(View.VISIBLE);
        else
            rl1.setVisibility(View.GONE);
        if (tv2 != null)
            rl2.setVisibility(View.VISIBLE);
        else
            rl2.setVisibility(View.GONE);
        if (tv3 != null)
            rl3.setVisibility(View.VISIBLE);
        else
            rl3.setVisibility(View.GONE);
        if (tv4 != null)
            rl4.setVisibility(View.VISIBLE);
        else
            rl4.setVisibility(View.GONE);
        if (tv5 != null)
            rl5.setVisibility(View.VISIBLE);
        else
            rl5.setVisibility(View.GONE);
        textView1.setText(tv1 + "");
        textView2.setText(tv2 + "");
        textView3.setText(tv3 + "");
        textView4.setText(tv4 + "");
        textView5.setText(tv5 + "");
    }

    public void tv1Btn(View.OnClickListener clickListener) {
        textView1.setOnClickListener(clickListener);
    }

    public void tv2Btn(View.OnClickListener clickListener) {
        textView2.setOnClickListener(clickListener);
    }

    public void tv3Btn(View.OnClickListener clickListener) {
        textView3.setOnClickListener(clickListener);
    }

    public void tv4Btn(View.OnClickListener clickListener) {
        textView4.setOnClickListener(clickListener);
    }

    public void tv5Btn(View.OnClickListener clickListener) {
        textView5.setOnClickListener(clickListener);
    }


    public void dismissDialog() {
        if (this.isShowing())
            MorePopWindow.this.dismiss();
        else
            return;
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, 0, 0);
        } else {
            this.dismiss();
        }
    }
}
