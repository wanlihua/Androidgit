package com.dtl.gemini.ui.cfd.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.dtl.gemini.R;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.ui.cfd.model.OrderHistory;
import com.dtl.gemini.utils.DataUtil;

import java.util.List;

/**
 * @author DTL
 * @date 2020/5/5
 * 已平仓记录
 **/
public class ClosedRecordAdapter extends BaseAdapter {
    Context context;
    List<OrderHistory> recordList;
    ViewHolder viewHolder;

    private AdapterView.OnItemClickListener listener;

    public ClosedRecordAdapter(Context context) {
        this.context = context;
    }

    public void refersh(List<OrderHistory> recordList) {
        this.recordList = recordList;
        notifyDataSetChanged();
    }

    public void setCancelListener(AdapterView.OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        if (recordList == null) {
            return 0;
        }
        return recordList.size();
    }

    @Override
    public Object getItem(int position) {
        return recordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cfd_closed_record, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.type = convertView.findViewById(R.id.type_tv);
            viewHolder.multiple = convertView.findViewById(R.id.multiple_tv);
            viewHolder.id = convertView.findViewById(R.id.id_tv);
            viewHolder.status = (TextView) convertView.findViewById(R.id.status_tv);
            viewHolder.positionNumber = (TextView) convertView.findViewById(R.id.position_number_tv);
            viewHolder.openPrice = convertView.findViewById(R.id.open_price_tv);
            viewHolder.transactionNumber = convertView.findViewById(R.id.transaction_number_tv);
            viewHolder.closeNumber = convertView.findViewById(R.id.close_number_tv);
            viewHolder.closePrice = convertView.findViewById(R.id.close_price_tv);
            viewHolder.cfdProfit = convertView.findViewById(R.id.cfd_profit_tv);
            viewHolder.date = convertView.findViewById(R.id.date_tv);
            viewHolder.cancelOrder = convertView.findViewById(R.id.cancel_order_btn);
            viewHolder.positionNumberTv = convertView.findViewById(R.id.position_number);
            viewHolder.closeNumberTv = convertView.findViewById(R.id.close_number);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        OrderHistory record = recordList.get(position);
        if (record.getTrend() == 1) {
            viewHolder.type.setTextColor(context.getResources().getColor(R.color.zhang));
            if (record.getStartType() == 1) {
                viewHolder.type.setText(context.getResources().getString(R.string.speed_open_rise));
            } else {
                viewHolder.type.setText(context.getResources().getString(R.string.plan_open_rise));
            }
        } else {
            viewHolder.type.setTextColor(context.getResources().getColor(R.color.die));
            if (record.getStartType() == 1) {
                viewHolder.type.setText(context.getResources().getString(R.string.speed_open_fall));
            } else {
                viewHolder.type.setText(context.getResources().getString(R.string.plan_open_fall));
            }
        }
        viewHolder.positionNumberTv.setText(context.getString(R.string.position_number, record.getCurrency()));
        viewHolder.closeNumberTv.setText(context.getString(R.string.close_number, record.getCurrency()));
        viewHolder.multiple.setText(record.getMultiple() + "X");
        viewHolder.id.setText(DataUtil.returnPhoneHint(record.getId()));
        viewHolder.cancelOrder.setEnabled(false);
        viewHolder.cancelOrder.setClickable(false);
        viewHolder.closePrice.setText(DataUtil.doubleFour(record.getFinishPrice()));
        //1,"委托中，没有平仓" 2,"已撤单" 3,"已平仓",4,"已爆仓"
        if (record.getStatus() == 1) {
            viewHolder.status.setText(context.getResources().getString(R.string.cfd_order_close));
        } else if (record.getStatus() == 2) {
            viewHolder.status.setText(context.getResources().getString(R.string.cfd_order_cancel));
        } else if (record.getStatus() == 3) {
            viewHolder.status.setText(context.getResources().getString(R.string.closed));
        } else if (record.getStatus() == 4) {
            viewHolder.status.setText(context.getResources().getString(R.string.cfd_order_overflow));
            viewHolder.closePrice.setText(DataUtil.doubleFour(record.getFinishMarketPrice()));
        } else if (record.getStatus() == 5) {//平仓-止盈
            viewHolder.status.setText(context.getResources().getString(R.string.closed));
        } else if (record.getStatus() == 6) {//平仓-止损
            viewHolder.status.setText(context.getResources().getString(R.string.closed));
        }
        viewHolder.positionNumber.setText(DataUtil.doubleFour(record.getOrderAmount()));
        viewHolder.openPrice.setText(DataUtil.doubleFour(record.getStartPrice()));
        viewHolder.transactionNumber.setText(DataUtil.doubleFour(record.getOrderBalance()));
        viewHolder.closeNumber.setText(DataUtil.doubleFour(record.getFinishAmount()));
        viewHolder.cfdProfit.setText(DataUtil.doubleFour(record.getIncome()));
        String dates = DataUtil.returnToTime(record.getCreateTime(), Constant.DATE_FORMAT_SSSZ, Constant.DATE_FORMAT_ALL);
        viewHolder.date.setText(dates);
        viewHolder.cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordList != null && recordList.size() > 0)
                    listener.onItemClick(null, v, position, position);
            }
        });

        return convertView;
    }

    class ViewHolder {
        Button cancelOrder;
        TextView type, multiple, id, status, positionNumber, positionNumberTv,
                openPrice, transactionNumber, closeNumber, closeNumberTv, closePrice, cfdProfit, date;
    }

}
