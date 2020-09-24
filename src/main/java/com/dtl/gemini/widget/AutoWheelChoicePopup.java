package com.dtl.gemini.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;
import com.dtl.gemini.R;

import java.util.List;

/**
 * 自定义滚动弹窗菜单
 *
 * @author psx
 * @time 2019/3/1 19:47
 */
public class AutoWheelChoicePopup extends PopupWindow implements View.OnClickListener {

    private Context context;
    private PopupClickListener listener;
    private TextView cancleBt, okBt;
    private WheelView wheelView;
    private List<String> list;
    private String selected = "";
    private int position = 0;

    public interface PopupClickListener {
        void onClick(View v);
    }

    public AutoWheelChoicePopup(Context context, List<String> list, PopupClickListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
        this.list = list;
        selected = list.get(0);
        initView();
    }

    public AutoWheelChoicePopup(Context context, List<String> list) {
        super(context);
        this.context = context;
        this.list = list;
        selected = list.get(0);
        initView();
    }

    public AutoWheelChoicePopup(Context context, List<String> list, String selected) {
        super(context);
        this.context = context;
        this.list = list;
        this.selected = selected;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.popup_auto_whell_view, null);
        cancleBt = view.findViewById(R.id.popup_auto_cancle_bt);
        okBt = view.findViewById(R.id.popup_auto_ok_bt);
        wheelView = view.findViewById(R.id.popup_auto_wheel);
        wheelView.setAdapter(new ArrayWheelAdapter(list));
        wheelView.setCyclic(false);
        wheelView.setTextSize(18);
        wheelView.setDividerColor(R.color.text3);
        wheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                selected = list.get(index);
                position = index;
            }
        });
        if (selected != null && !selected.equals("")) {
            for (int i = 0; i < list.size(); i++) {
                if (selected.equals(list.get(i))) {
                    position = i;
                }
            }
        }
        wheelView.setCurrentItem(position);
        cancleBt.setOnClickListener(this);
        cancleBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setContentView(view);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x50000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int height = view.findViewById(R.id.popup_auto_wheel_view).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    public String getSelecedData() {
        return selected;
    }

    public int getSelecedIndex() {
        return position;
    }

    public void setBtnOnclick(View.OnClickListener onclick) {
        okBt.setOnClickListener(onclick);
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v);
    }
}
