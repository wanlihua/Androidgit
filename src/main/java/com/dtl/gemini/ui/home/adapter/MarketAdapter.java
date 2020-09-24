package com.dtl.gemini.ui.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dtl.gemini.R;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.db.StoreUtils;
import com.dtl.gemini.ui.home.activity.KlineActivity;
import com.dtl.gemini.ui.home.model.Market;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.widget.CircleImageView;

import java.util.List;

public class MarketAdapter extends BaseAdapter {
    Context context;
    List<Market> list;
    LayoutInflater inflater;

    public MarketAdapter(Context context, List<Market> list) {
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void refersh(List<Market> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list != null ? list.size() : 0;
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
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_home_list, null);
            holder.ll = convertView.findViewById(R.id.item_home_ll);
            holder.img = convertView.findViewById(R.id.item_home_icon);
            holder.currencyTv = convertView.findViewById(R.id.item_home_currency);
            holder.volTv = convertView.findViewById(R.id.item_home_vol);
            holder.cnyTv = convertView.findViewById(R.id.item_home_cny);
            holder.usTv = convertView.findViewById(R.id.item_home_us);
            holder.gainsTv = convertView.findViewById(R.id.item_home_gains);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Market bean = list.get(position);
        String currency = bean.getCurrency();
        DataUtil.setTokenIcon(holder.img, currency);
        holder.currencyTv.setText(currency + "/USDT");
        holder.volTv.setText("24H Vol " + bean.getVol());
        holder.usTv.setText(bean.getUs());
        holder.cnyTv.setText("￥" + bean.getCny());
        double gain = Double.parseDouble(bean.getGains());
        if (gain > 0) {
            holder.gainsTv.setText("+" + bean.getGains() + "%");
            holder.gainsTv.setBackgroundResource(R.drawable.item_coin_asset_green);
        } else if (gain < 0) {
            holder.gainsTv.setText(bean.getGains() + "%");
            holder.gainsTv.setBackgroundResource(R.drawable.item_coin_asset_red);
        }

        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list != null && list.size() > 0 && position < list.size()) {
                    Market bean = list.get(position);
                    StoreUtils.init(context).setParameter(Constant.TYPE, bean.getMode()+ "");
                    StoreUtils.init(context).setParameter(Constant.CURRENCY, currency);
                    Intent intent = new Intent(context, KlineActivity.class);
                    intent.putExtra(Constant.CURRENCY, bean.getCurrency());
                    context.startActivity(intent);
                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        LinearLayout ll;
        CircleImageView img;
        TextView currencyTv;
        TextView volTv;
        TextView cnyTv;
        TextView usTv;
        TextView gainsTv;
    }
}
