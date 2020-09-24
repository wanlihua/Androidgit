package com.dtl.gemini.ui.asset.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dtl.gemini.R;
import com.dtl.gemini.ui.asset.model.Withdrawal;
import com.dtl.gemini.utils.DataUtil;

import java.util.List;

/**
 * @author DTL
 * @date 2020/4/29
 * 提币记录
 **/
public class WithdrawalRecordAdapter extends BaseAdapter {
    Context context;
    List<Withdrawal> recordList;
    ViewHolder viewHolder;

    public WithdrawalRecordAdapter(Context context) {
        this.context = context;
    }

    public void refersh(List<Withdrawal> recordList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_withdrawal_record, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.currencyIv = convertView.findViewById(R.id.currency_iv);
            viewHolder.typeTv = convertView.findViewById(R.id.type_tv);
            viewHolder.statusTv = convertView.findViewById(R.id.status_tv);
            viewHolder.numberTv = (TextView) convertView.findViewById(R.id.number_tv);
            viewHolder.dateTv = (TextView) convertView.findViewById(R.id.date_tv);
            viewHolder.extralTv = (TextView) convertView.findViewById(R.id.extral_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Withdrawal record = recordList.get(position);
        String currency = record.getCurrency();
        DataUtil.setTokenIcon(viewHolder.currencyIv, currency);
        viewHolder.typeTv.setText(currency + context.getResources().getString(R.string.withdraw));
        if (record.getStatus() == 0) {
            viewHolder.statusTv.setText(context.getResources().getString(R.string.under_review));
        } else if (record.getStatus() == 1) {
            viewHolder.statusTv.setText(context.getResources().getString(R.string.deposited));
        } else if (record.getStatus() == 2) {
            viewHolder.statusTv.setText(context.getResources().getString(R.string.rejected));
        }
        viewHolder.numberTv.setText(DataUtil.numberFour(record.getAmount()));
        String date = record.getCreateDateTime();
        viewHolder.dateTv.setText(date);
        viewHolder.extralTv.setVisibility(View.GONE);
        if (record.getRefuseReason() != null) {
            viewHolder.extralTv.setVisibility(View.VISIBLE);
            viewHolder.extralTv.setText(record.getRefuseReason().toString());
        }
        return convertView;
    }

    class ViewHolder {
        ImageView currencyIv;
        TextView typeTv, statusTv, numberTv, dateTv, extralTv;
    }

}
