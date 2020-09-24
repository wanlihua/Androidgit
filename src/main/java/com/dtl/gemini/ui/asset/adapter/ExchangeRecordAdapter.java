package com.dtl.gemini.ui.asset.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dtl.gemini.R;
import com.dtl.gemini.ui.asset.model.Exchange;
import com.dtl.gemini.utils.DataUtil;

import java.util.List;

/**
 * @author DTL
 * @date 2020/4/29
 * 兑换记录
 **/
public class ExchangeRecordAdapter extends BaseAdapter {
    List<Exchange> list;
    Context context;
    ViewHolder viewHolder;

    public ExchangeRecordAdapter(Context context) {
        this.context = context;
    }

    public void refersh(List<Exchange> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_exchange_record, null);
            viewHolder.t1Img = convertView.findViewById(R.id.t1_img);
            viewHolder.t2Img = convertView.findViewById(R.id.t2_img);
            viewHolder.t1Tv = convertView.findViewById(R.id.t1);
            viewHolder.t2Tv = convertView.findViewById(R.id.t2);
            viewHolder.rateTv = convertView.findViewById(R.id.rate);
            viewHolder.t1CountTv = convertView.findViewById(R.id.t1_count);
            viewHolder.t2CountTv = convertView.findViewById(R.id.t2_count);
            viewHolder.feeTv = convertView.findViewById(R.id.fee_count);
            viewHolder.endtimeTv = convertView.findViewById(R.id.endtime);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Exchange beans = list.get(position);
        String t1Currency = beans.getExchangeCurrency();
        String t2Currency = beans.getReceiveCurrency();
        DataUtil.setTokenIcon(viewHolder.t1Img, t1Currency);
        DataUtil.setTokenIcon(viewHolder.t2Img, t2Currency);
        viewHolder.t1Tv.setText(t1Currency + "  " + context.getResources().getString(R.string.exchange) + "  ");
        viewHolder.t2Tv.setText(t2Currency);
        double rate = 0.00;
        if (t1Currency != null && t1Currency.equals("USDT")) {
            rate = Double.parseDouble(DataUtil.doubleEight(1 / beans.getRate()));
        } else {
            rate = beans.getRate();
        }
        viewHolder.rateTv.setText("1 " + t1Currency + "≈" + DataUtil.numberSix(rate) + " " + t2Currency);
        viewHolder.t1CountTv.setText(DataUtil.doubleFour(beans.getExchangeAmount()) + " " + t1Currency);
        viewHolder.t2CountTv.setText(DataUtil.doubleFour(beans.getReceiveAmount()) + " " + t2Currency);
        viewHolder.feeTv.setText(DataUtil.doubleFour(beans.getFeeAmount()) + " " + t1Currency);
        viewHolder.endtimeTv.setText(beans.getCreateDateTime());
        return convertView;
    }

    class ViewHolder {
        ImageView t1Img, t2Img;
        TextView t1Tv, t2Tv, rateTv, t1CountTv, t2CountTv, feeTv, endtimeTv;
    }
}
