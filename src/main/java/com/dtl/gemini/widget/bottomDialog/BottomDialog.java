package com.dtl.gemini.widget.bottomDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dtl.gemini.R;

import java.util.ArrayList;
import java.util.List;


/**
 * [底部弹出dialog]
 *
 * @author devin.hu
 * @version 1.0
 * @date 2014-1-15
 **/
public class BottomDialog extends Dialog implements AdapterView.OnItemClickListener, View.OnClickListener {

    private TextView cancelBtn;
    private TextView comfirBtn;
    private ListView lv;
    private View.OnClickListener confirmListener;
    private View.OnClickListener cancelListener;
    private AdapterView.OnItemClickListener lvonItemClickListener;
    List<String> list = new ArrayList<>();

    /**
     * @param context
     */
    public BottomDialog(Context context) {
        super(context, R.style.dialogFullscreen);
    }

    /**
     * @param context
     * @param theme
     */
    public BottomDialog(Context context, int theme) {
        super(context, theme);
    }

    /**
     * @param context
     */
    public BottomDialog(Context context, List<String> list) {
        super(context, R.style.dialogFullscreen);
        this.list = list;
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_select_layout);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.5f;
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(layoutParams);

        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        cancelBtn = (TextView) findViewById(R.id.bottom_select_cancel);
        comfirBtn = (TextView) findViewById(R.id.bottom_select_confirm);
        lv = (ListView) findViewById(R.id.bottom_select_list);
        BottomAdapter adapter = new BottomAdapter(getContext(), list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dismiss();
        return true;
    }

    public void setConfirmListener(View.OnClickListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public void setCancelListener(View.OnClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public void setlvListener(AdapterView.OnItemClickListener lvonItemClickListener) {
        this.lvonItemClickListener = lvonItemClickListener;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (lvonItemClickListener != null) {
            lvonItemClickListener.onItemClick(parent, view, position, id);
        }
        return;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bottom_select_confirm) {
            if (confirmListener != null) {
                confirmListener.onClick(v);
            }
            return;
        }

        if (id == R.id.bottom_select_cancel) {
            if (cancelListener != null) {
                cancelListener.onClick(v);
            }
            dismiss();
            return;
        }
    }
}
