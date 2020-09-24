package com.dtl.gemini.ui.asset.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dtl.gemini.R;
import com.dtl.gemini.ui.asset.model.TransactionRecord;
import com.dtl.gemini.utils.DataUtil;

import java.util.List;

/**
 * @author DTL
 * @date 2020/4/28
 * 交易记录
 **/
public class TransactionRecordAdapter extends BaseAdapter {
    Context context;
    List<TransactionRecord> recordList;
    ViewHolder viewHolder;

    public TransactionRecordAdapter(Context context, List<TransactionRecord> recordList) {
        this.context = context;
        this.recordList = recordList;
    }

    public void refersh(List<TransactionRecord> recordList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_transaction_record, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.typeIv = convertView.findViewById(R.id.type_iv);
            viewHolder.typeTv = convertView.findViewById(R.id.type_tv);
            viewHolder.statusTv = convertView.findViewById(R.id.status_tv);
            viewHolder.numberTv = convertView.findViewById(R.id.number_tv);
            viewHolder.dateTv = convertView.findViewById(R.id.date_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TransactionRecord record = recordList.get(position);
        if (record.getType() == 1) {
            //充值
            viewHolder.typeIv.setImageResource(R.mipmap.icon_recharge);
            viewHolder.typeTv.setText(context.getResources().getString(R.string.recharge));
        } else if (record.getType() == 2) {
            //提币
            viewHolder.typeIv.setImageResource(R.mipmap.icon_withdraw);
            viewHolder.typeTv.setText(context.getResources().getString(R.string.withdraw));
        } else if (record.getType() == 3) {
            //兑换
            viewHolder.typeIv.setImageResource(R.mipmap.icon_exchange);
            if (record.getAmount() > 0) {
                viewHolder.typeTv.setText(record.getExtra() + "" + context.getResources().getString(R.string.exchange));
            } else {
                viewHolder.typeTv.setText(context.getResources().getString(R.string.exchange) + "" + record.getExtra());
            }
        } else if (record.getType() == 4) {
            //划转
            viewHolder.typeIv.setImageResource(R.mipmap.icon_transfer);
            viewHolder.typeTv.setText(context.getResources().getString(R.string.transfer));
        } else if (record.getType() == 5) {
            //系统
            viewHolder.typeIv.setImageResource(R.mipmap.icon_sys_group);
            viewHolder.typeTv.setText(context.getResources().getString(R.string.wallet_sys_group));
        }

        if (record.getStatus() == 0) {
            viewHolder.statusTv.setText(context.getResources().getString(R.string.under_review));
        } else if (record.getStatus() == 1) {
            if (record.getType() == 2) {
                viewHolder.statusTv.setText(context.getResources().getString(R.string.deposited));
            } else {
                viewHolder.statusTv.setText(context.getResources().getString(R.string.completed));
            }
        } else if (record.getStatus() == 2) {
            viewHolder.statusTv.setText(context.getResources().getString(R.string.rejected));
        }
        if (record.getAmount() > 0) {
            viewHolder.numberTv.setText("+" + DataUtil.doubleFour(record.getAmount()));
        } else {
            viewHolder.numberTv.setText(DataUtil.doubleFour(record.getAmount()));
        }

        String date = record.getCreateDateTime();
        viewHolder.dateTv.setText(date);
        return convertView;
    }

    class ViewHolder {
        ImageView typeIv;
        TextView typeTv, statusTv, numberTv, dateTv;
    }

}
