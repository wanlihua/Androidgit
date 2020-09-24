package com.dtl.gemini.ui.asset.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dtl.gemini.R;
import com.dtl.gemini.ui.asset.model.Recharge;
import com.dtl.gemini.utils.DataUtil;

import java.util.List;

/**
 * @author DTL
 * @date 2020/4/29
 * 充币记录
 **/
public class RechargeRecordAdapter extends BaseAdapter {
    Context context;
    List<Recharge> recordList;
    ViewHolder viewHolder;

    public RechargeRecordAdapter(Context context) {
        this.context = context;
    }

    public void refersh(List<Recharge> recordList) {
        this.recordList = recordList;
        notifyDataSetChanged();
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_recharge_record, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.currencyIv = convertView.findViewById(R.id.currency_iv);
            viewHolder.typeTv = convertView.findViewById(R.id.type_tv);
            viewHolder.statusTv = convertView.findViewById(R.id.status_tv);
            viewHolder.numberTv = (TextView) convertView.findViewById(R.id.number_tv);
            viewHolder.dateTv = (TextView) convertView.findViewById(R.id.date_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Recharge record = recordList.get(position);
        String currency = record.getCurrency();
        DataUtil.setTokenIcon(viewHolder.currencyIv, currency);
        viewHolder.typeTv.setText(currency + context.getResources().getString(R.string.recharge));
        viewHolder.statusTv.setText(context.getResources().getString(R.string.completed));
        viewHolder.numberTv.setText(DataUtil.doubleFour(record.getAmount()));
        String date = record.getCreateDateTime();
        viewHolder.dateTv.setText(date);
        return convertView;
    }

    class ViewHolder {
        ImageView currencyIv;
        TextView typeTv, statusTv, numberTv, dateTv;
    }

}
