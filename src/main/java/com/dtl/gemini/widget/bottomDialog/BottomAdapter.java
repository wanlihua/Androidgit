package com.dtl.gemini.widget.bottomDialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dtl.gemini.R;

import java.util.List;

/**
 * 大图灵
 * 2019/3/13
 **/
public class BottomAdapter extends BaseAdapter {

    List<String> list;
    Context context;
    ViewHolder viewHolder;

    public BottomAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    public void refersh(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.bottom_select_item, null);
            viewHolder.ll = convertView.findViewById(R.id.bottom_select_item_ll);
            viewHolder.tv = convertView.findViewById(R.id.bottom_select_item_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv.setText(list.get(position));
        return convertView;
    }

    class ViewHolder {
        LinearLayout ll;
        TextView tv;
    }
}
