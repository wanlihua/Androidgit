package com.dtl.gemini.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.dtl.gemini.MainActivity;
import com.dtl.gemini.R;


/**
 * 大图灵
 * 2019/10/16
 **/
public class DialogUtils {
    Context context;
    public static Dialog dialog;
    public static TextView title, content, time, to, closeTv, tv6;
    public static Button btn, no;
    public static ImageView close, iv1;
    public static RadioGroup rg;
    public static RadioButton rb1, rb2;
    public static EditText et1, et2;

    public DialogUtils(Context context, String wallet) {
        this.context = context;
        dialog = new Dialog(context, R.style.loading_dialogs);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = null;
        if (wallet.equals("error")) {
            view = View.inflate(context, R.layout.dialog_error_hint, null);
            title = view.findViewById(R.id.title);
            content = view.findViewById(R.id.content);
            btn = view.findViewById(R.id.wallets_identity_ok);//确定
        } else if (wallet.equals("exit")) {
            view = View.inflate(context, R.layout.dialog_exit_hint, null);
            title = view.findViewById(R.id.title);
            btn = view.findViewById(R.id.exit_ok);
            no = view.findViewById(R.id.exit_cancel);
        } else if (wallet.equals("message")) {
            view = View.inflate(context, R.layout.dialog_message_hint, null);
            title = view.findViewById(R.id.title);
            btn = view.findViewById(R.id.message_ok);
            content = view.findViewById(R.id.message_content);
            close = view.findViewById(R.id.message_close);
        } else if (wallet.equals("input")) {
            view = View.inflate(context, R.layout.dialog_input, null);
            title = view.findViewById(R.id.dialog_title);
            et1 = view.findViewById(R.id.dialog_edit);
            btn = view.findViewById(R.id.dialog_confirm);
            no = view.findViewById(R.id.dialog_cancel);
        } else if (wallet.equals("ragbtn")) {
            view = View.inflate(context, R.layout.dialog_radbtn, null);
            title = view.findViewById(R.id.dialog_title);
            rg = view.findViewById(R.id.dialog_rg);
            rb1 = view.findViewById(R.id.dialog_rb1);
            rb2 = view.findViewById(R.id.dialog_rb2);
        } else if (wallet.equals("msg_set")) {
            view = View.inflate(context, R.layout.dialog_msg_set, null);
            title = view.findViewById(R.id.dialog_copy);
            content = view.findViewById(R.id.dialog_delete);
            time = view.findViewById(R.id.dialog_recall);
        } else if (wallet.equals("confirm")) {
            view = View.inflate(context, R.layout.dialog_confirm, null);
            close = view.findViewById(R.id.img_tip_logo);
            title = view.findViewById(R.id.dialog_title);
            content = view.findViewById(R.id.dialog_content);
            btn = view.findViewById(R.id.dialog_confirm);
            no = view.findViewById(R.id.dialog_cancel);
        }
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
    }

    public DialogUtils(Context context, int order) {
        this.context = context;
        dialog = new Dialog(context, R.style.loading_dialogs);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = null;
        if (order == 1) {//提币
            view = View.inflate(context, R.layout.dialog_withdrawal_confirm, null);
            title = view.findViewById(R.id.dialog_title);
            closeTv = view.findViewById(R.id.dialog_close_tv);
            iv1 = view.findViewById(R.id.see_pwd_iv);
            et1 = view.findViewById(R.id.code_et);
            et2 = view.findViewById(R.id.asset_pwd);
            time = view.findViewById(R.id.getcode_tv);
            to = view.findViewById(R.id.to_activity);
            btn = view.findViewById(R.id.dialog_confirm);
        } else if (order == 2) {//平仓
            view = View.inflate(context, R.layout.dialog_cfd_close_position, null);
            title = view.findViewById(R.id.dialog_title);
            content = view.findViewById(R.id.dialog_content);
            time = view.findViewById(R.id.dialog_time);
            closeTv = view.findViewById(R.id.dialog_close_tv);
            et1 = view.findViewById(R.id.dialog_et1);
            et2 = view.findViewById(R.id.dialog_et2);
            btn = view.findViewById(R.id.dialog_confirm);
        } else if (order == 3) {//平仓确认
            view = View.inflate(context, R.layout.dialog_close_positino_confirm, null);
            title = view.findViewById(R.id.dialog_title);
            closeTv = view.findViewById(R.id.dialog_close_tv);
            content = view.findViewById(R.id.open_price_tv);
            time = view.findViewById(R.id.curr_price_tv);
            to = view.findViewById(R.id.close_price_tv);
            tv6 = view.findViewById(R.id.close_number_tv);
            btn = view.findViewById(R.id.dialog_confirm);
        } else if (order == 4) {//闪电平仓
            view = View.inflate(context, R.layout.dialog_speed_close_position, null);
            title = view.findViewById(R.id.dialog_title);
            content = view.findViewById(R.id.dialog_content);
            closeTv = view.findViewById(R.id.dialog_close_tv);
            et1 = view.findViewById(R.id.dialog_et1);
            btn = view.findViewById(R.id.dialog_confirm);
        } else if (order == 5) {//闪电平仓确认
            view = View.inflate(context, R.layout.dialog_speed_close_confirm, null);
            title = view.findViewById(R.id.dialog_title);
            closeTv = view.findViewById(R.id.dialog_close_tv);
            content = view.findViewById(R.id.open_price_tv);
            to = view.findViewById(R.id.close_price_tv);
            tv6 = view.findViewById(R.id.close_number_tv);
            btn = view.findViewById(R.id.dialog_confirm);
        } else if (order == 6) {//追加保证金
            view = View.inflate(context, R.layout.dialog_cfd_add_balance, null);
            title = view.findViewById(R.id.dialog_title);
            closeTv = view.findViewById(R.id.dialog_close_tv);
            et1 = view.findViewById(R.id.dialog_et1);
            content = view.findViewById(R.id.dialog_content);
            btn = view.findViewById(R.id.dialog_confirm);
        }
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
    }


    /**
     * 错误提示
     */
    public static void showErrorDialog(Context context, String titles, String contents) {
        new DialogUtils(context, "error");
        title.setText(titles);
        content.setText(contents);
        dialog.show();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
    }

    /**
     * 退出登录
     */
    public static void showExitDialog(Context context, String titles) {
        new DialogUtils(context, "exit");
        title.setText(titles);
        if (context != null && dialog != null)
            dialog.show();
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 确认提示-默认
     */
    public static void showMessageDialog(Context context) {
        new DialogUtils(context, "message");
        dialog.show();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 确认提示-标题，内容
     */
    public static void showMessageDialog(Context context, String titles, String contents) {
        new DialogUtils(context, "message");
        title.setText(titles);
        content.setText(contents);
        dialog.show();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 确认提示
     */
    public static void showTitleMessageDialog(Context context, String message, String contents, String btns) {
        new DialogUtils(context, "message");
        title.setText(message);
        content.setText(contents);
        btn.setText(btns);
        dialog.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 确认-取消
     */
    public static void showConfirmDialog(Context context, Object ImgId, String titles, String contents) {
        new DialogUtils(context, "confirm");
        if (ImgId != null)
            close.setImageResource(Integer.parseInt(ImgId + ""));
        title.setText(titles);
        content.setText(contents);
        dialog.show();
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
    }

    /**
     * 确认/取消
     * 输入文字
     *
     * @param context
     * @param titles
     */
    public static void showInputDialog(Context context, String titles, String hints) {
        new DialogUtils(context, "input");
        title.setText(titles);
        et1.setText(hints);
        et1.setSelection(et1.getText().length());
        dialog.show();
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataUtil.hideJianPan(context, et1);
                dismissDialog();
            }
        });
    }

    /**
     * 双项选择
     *
     * @param context
     * @param titles
     */
    public static void showRgbtnDialog(Context context, String titles, String rb1s, String rb2s, String select) {
        new DialogUtils(context, "ragbtn");
        title.setText(titles);
        rb1.setText(rb1s);
        rb2.setText(rb2s);
        if (select.equals(rb1s))
            rb1.setChecked(true);
        if (select.equals(rb2s))
            rb2.setChecked(true);
        dialog.show();
    }

    /**
     * 多项文本选择
     *
     * @param context
     * @param tv1
     * @param tv2
     * @param tv3
     */
    public static void showTextDialog(Context context, Object tv1, Object tv2, Object tv3) {
        new DialogUtils(context, "msg_set");
        if (tv1 != null) {
            title.setVisibility(View.VISIBLE);
            title.setText(tv1 + "");
        } else {
            title.setVisibility(View.GONE);
        }
        if (tv2 != null) {
            content.setVisibility(View.VISIBLE);
            content.setText(tv2 + "");
        } else {
            content.setVisibility(View.GONE);
        }
        if (tv3 != null) {
            time.setVisibility(View.VISIBLE);
            time.setText(tv3 + "");
        } else {
            time.setVisibility(View.GONE);
        }
        dialog.show();
    }

    /**
     * 提币
     *
     * @param context
     * @param titles
     * @param contents
     * @param tos
     * @param btns
     */
    public static void confirmWithdrawal(Context context, Object titles, Object contents, Object tos, Object btns) {
        new DialogUtils(context, 1);
        if (titles != null) {
            title.setText(titles.toString());
        }
        if (contents != null) {
            et2.setHint(contents.toString());
        }
        if (tos != null) {
            to.setText(tos.toString());
        }
        if (btns != null) {
            btn.setText(btns.toString());
        }
        closeTv();
        dialog.show();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.share_animation);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);//设置横向全屏
    }

    /**
     * 平仓
     *
     * @param context
     * @param type    类型
     * @param trend
     */
    @SuppressLint("StringFormatInvalid")
    public static void ClosePosition(Context context, int type, int trend, Object currNumber, String currency) {
        new DialogUtils(context, 2);
        if (trend == 1) {
            title.setTextColor(context.getResources().getColor(R.color.zhang));
            if (type == 1) {
                title.setText(context.getResources().getString(R.string.speed_open_rise));
            } else {
                title.setText(context.getResources().getString(R.string.plan_open_rise));
            }
        } else {
            title.setTextColor(context.getResources().getColor(R.color.die));
            if (type == 1) {
                title.setText(context.getResources().getString(R.string.speed_open_fall));
            } else {
                title.setText(context.getResources().getString(R.string.plan_open_fall));
            }
        }
        if (et2 != null) {
            et2.setHint(context.getString(R.string.close_number, currency));
        }
        if (currNumber != null) {
            content.setText(context.getString(R.string.curr_number, currency) + ":" + currNumber.toString());
        }
        et2.setText(currNumber.toString());
        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                DataUtil.checkEditInputNumber(et1);
                String price = et1.getText().toString().trim();
                if (price.length() <= 0 || !DataUtil.isNumeric(price)) {
                    return;
                }
                time.setText("=" + DataUtil.numberTwo(Double.parseDouble(price) * MainActivity.usd) + " CNY");
            }
        });

        et2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                DataUtil.checkEditInputNumber(et2);
            }
        });
        closeTv();
        dialog.show();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.share_animation);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);//设置横向全屏
    }

    /**
     * 平仓确认
     *
     * @param context
     * @param cancelHide
     * @param openPrice   开仓价格
     * @param currPrice   当前价格
     * @param closePrice  平仓价格
     * @param closeNumber 平仓数量
     */
    public static void ClosePositionConfirm(Context context, boolean cancelHide, Object id, Object openPrice, Object currPrice, Object closePrice, Object closeNumber) {
        if (cancelHide) {
            new DialogUtils(context, 5);
        } else {
            new DialogUtils(context, 3);
            if (currPrice != null) {
                time.setText(currPrice.toString());
            }
        }
        if (id != null) {
            title.setText(context.getResources().getString(R.string.transaction_id) + ":" + DataUtil.returnPhoneHint(id.toString()));
        }
        if (openPrice != null) {
            content.setText(openPrice.toString());
        }
        if (closePrice != null) {
            to.setText(closePrice.toString());
        }
        if (closeNumber != null) {
            tv6.setText(closeNumber.toString());
        }
        closeTv();
        dialog.show();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.share_animation);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);//设置横向全屏
    }

    /**
     * 闪电平仓
     *
     * @param context
     * @param type       类型
     * @param currNumber 可平仓数量
     */
    public static void SpeedClosePosition(Context context, int type, int trend, Object currNumber, String currency) {
        new DialogUtils(context, 4);
        if (trend == 1) {
            title.setTextColor(context.getResources().getColor(R.color.zhang));
            if (type == 1) {
                title.setText(context.getResources().getString(R.string.speed_open_rise));
            } else {
                title.setText(context.getResources().getString(R.string.plan_open_rise));
            }
        } else {
            title.setTextColor(context.getResources().getColor(R.color.die));
            if (type == 1) {
                title.setText(context.getResources().getString(R.string.speed_open_fall));
            } else {
                title.setText(context.getResources().getString(R.string.plan_open_fall));
            }
        }
        if (currNumber != null) {
            content.setText(context.getString(R.string.curr_number, currency) + ":" + currNumber.toString());
        }
        et1.setText(currNumber.toString());
        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                DataUtil.checkEditInputNumber(et1);
            }
        });
        closeTv();
        dialog.show();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.share_animation);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);//设置横向全屏
    }

    /**
     * 追加保证金
     *
     * @param context
     * @param type    类型
     * @param trend
     * @param usable
     */
    @SuppressLint("StringFormatInvalid")
    public static void showAddBalance(Context context, int type, int trend, double usable) {
        new DialogUtils(context, 6);
        if (trend == 1) {
            title.setTextColor(context.getResources().getColor(R.color.zhang));
            if (type == 1) {
                title.setText(context.getResources().getString(R.string.speed_open_rise));
            } else {
                title.setText(context.getResources().getString(R.string.plan_open_rise));
            }
        } else {
            title.setTextColor(context.getResources().getColor(R.color.die));
            if (type == 1) {
                title.setText(context.getResources().getString(R.string.speed_open_fall));
            } else {
                title.setText(context.getResources().getString(R.string.plan_open_fall));
            }
        }
        content.setText(context.getResources().getString(R.string.usable) + "：" + usable);
        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                DataUtil.checkEditInputNumber(et1);
                String amount = et1.getText().toString().trim();
                if (amount.length() <= 0 || !DataUtil.isNumeric(amount)) {
                    return;
                }
            }
        });
        closeTv();
        dialog.show();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.share_animation);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);//设置横向全屏
    }

    public static void setTimeOnclick(View.OnClickListener clickListener) {
        if (time != null)
            time.setOnClickListener(clickListener);
    }

    public static void setToOnclick(View.OnClickListener clickListener) {
        if (to != null)
            to.setOnClickListener(clickListener);
    }

    public static void btn(View.OnClickListener clickListener) {
        if (btn != null)
            btn.setOnClickListener(clickListener);
    }

    public static void no(View.OnClickListener clickListener) {
        if (no != null)
            no.setOnClickListener(clickListener);
    }

    public static void closeTv() {
        closeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public static void close() {
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public static void dismissDialog(Context context) {
        boolean status = ((Activity) context).isFinishing();
        if (context != null && status == false && dialog != null) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return;
        }
    }

    public static void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        } else {
            return;
        }
    }

    public static void setTv1(View.OnClickListener clickListener) {
        if (title != null)
            title.setOnClickListener(clickListener);
    }

    public static void setTv2(View.OnClickListener clickListener) {
        if (content != null)
            content.setOnClickListener(clickListener);
    }

    public static void setTv3(View.OnClickListener clickListener) {
        if (time != null)
            time.setOnClickListener(clickListener);
    }

    public static String getEt1String() {
        String str = "";
        if (et1 != null)
            str = et1.getText().toString().trim();
        return str;
    }

    public static String getEt2String() {
        String str = "";
        if (et2 != null)
            str = et2.getText().toString().trim();
        return str;
    }

    public static void setCheckedChangeListener(RadioGroup.OnCheckedChangeListener clickListener) {
        if (rg != null)
            rg.setOnCheckedChangeListener(clickListener);
    }


}
