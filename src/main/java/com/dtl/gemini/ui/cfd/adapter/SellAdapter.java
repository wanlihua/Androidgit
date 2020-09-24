package com.dtl.gemini.ui.cfd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dtl.gemini.R;
import com.dtl.gemini.ui.cfd.model.CfdDepth;
import com.dtl.gemini.utils.DataUtil;

import java.util.List;

/**
 * @author DTL
 * @date 2020/5/5
 **/

public class SellAdapter extends BaseAdapter {
    private Context context;
    private List<CfdDepth> list;
    ViewHolder viewHolder;
    double max=1;
    int mutil = 100;

    public SellAdapter(Context context,List<CfdDepth> list) {
        this.context = context;
        this.list = list;
    }

    public SellAdapter(Context context) {
        this.context = context;
    }

    public void refersh(List<CfdDepth> list, double max) {
        this.list = list;
        this.max = max;
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
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cfd_delegate, null);
            viewHolder.progress = convertView.findViewById(R.id.cfd_delegate_pro);
            viewHolder.price = convertView.findViewById(R.id.price_tv);
            viewHolder.number = convertView.findViewById(R.id.number_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.progress.setProgressDrawable(context.getResources().getDrawable(R.drawable.pro_cfd_red));
        viewHolder.progress.setMax((int) (max * mutil));
        CfdDepth bean = list.get(position);
        viewHolder.price.setTextColor(context.getResources().getColor(R.color.asset_red));
        viewHolder.price.setText(DataUtil.doubleFour(bean.getPrice()));
        viewHolder.number.setText(DataUtil.doubleFour(bean.getNumber()));
        viewHolder.progress.setProgress((int) (bean.getNumber() * mutil));
        return convertView;
    }

    class ViewHolder {
        ProgressBar progress;
        TextView price, number;
    }
}
