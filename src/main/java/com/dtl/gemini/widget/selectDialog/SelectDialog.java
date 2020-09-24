package com.dtl.gemini.widget.selectDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import com.dtl.gemini.R;
import com.dtl.gemini.widget.HorizontalListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 大图灵
 * 2019/10/17
 * 水平选择Dialog
 **/
public class SelectDialog extends Dialog {
    private HorizontalListView horizontalListView;

    private AdapterView.OnItemClickListener listener;
    List<String> list = new ArrayList<>();
    String select;
    int type = 1;

    /**
     * @param context
     */
    public SelectDialog(Context context) {
        super(context, R.style.dialogFullscreen);
    }

    /**
     * @param context
     * @param theme
     */
    public SelectDialog(Context context, int theme) {
        super(context, theme);
    }

    /**
     * @param context
     */
    public SelectDialog(Context context, List<String> list, String select) {
        super(context, R.style.dialogFullscreen);
        this.list = list;
        this.select = select;
    }

    public SelectDialog(Context context, List<String> list, String select, int type) {
        super(context, R.style.dialogFullscreen);
        this.list = list;
        this.select = select;
        this.type = type;
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_layout);
//        Window window = getWindow();
//        WindowManager.LayoutParams layoutParams = window.getAttributes();
//        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//        layoutParams.dimAmount = 0.5f;
//        window.setGravity(Gravity.TOP);
//        window.setAttributes(layoutParams);
//
//        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        horizontalListView = findViewById(R.id.dialog_select_list);
        SelectAdapter adapter = new SelectAdapter(getContext(), list, select);
        horizontalListView.setAdapter(adapter);
        adapter.setOnclickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listener.onItemClick(null, view, i, l);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dismiss();
        return true;
    }

//    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
//        if (horizontalListView != null) {
//            horizontalListView.setOnItemClickListener(listener);
//        }
//    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.listener = listener;
    }

}
