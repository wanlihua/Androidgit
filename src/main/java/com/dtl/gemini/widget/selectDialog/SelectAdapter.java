package com.dtl.gemini.widget.selectDialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dtl.gemini.R;

import java.util.List;

/**
 * 大图灵
 * 2019/3/13
 **/
public class SelectAdapter extends BaseAdapter {

    List<String> list;
    Context context;
    ViewHolder viewHolder;
    String select;
    private AdapterView.OnItemClickListener listener;


    public SelectAdapter(Context context, List<String> list, String select) {
        this.context = context;
        this.list = list;
        this.select = select;
    }

    public void refersh(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setOnclickListener(AdapterView.OnItemClickListener listener) {
        this.listener = listener;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.dialog_select_item, null);
            viewHolder.ll = convertView.findViewById(R.id.dialog_select_item_ll);
            viewHolder.tv = convertView.findViewById(R.id.dialog_select_item_tv);
            viewHolder.iv = convertView.findViewById(R.id.dialog_select_item_iv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv.setText(list.get(position));
        if (list.get(position).equals(select)) {
            viewHolder.tv.setTextColor(context.getResources().getColor(R.color.v2_btn));
            viewHolder.iv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tv.setTextColor(context.getResources().getColor(R.color.text1));
            viewHolder.iv.setVisibility(View.GONE);
        }
        viewHolder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list != null && list.size() > 0)
                    listener.onItemClick(null, v, position, position);
            }
        });

        return convertView;
    }

    class ViewHolder {
        LinearLayout ll;
        TextView tv;
        ImageView iv;
    }
}
