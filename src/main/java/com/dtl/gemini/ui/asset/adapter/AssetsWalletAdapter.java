package com.dtl.gemini.ui.asset.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dtl.gemini.R;
import com.dtl.gemini.ui.asset.model.AssetWallet;
import com.dtl.gemini.utils.DataUtil;

import java.util.List;

/**
 * @author DTL
 * @date 2020/4/15
 * 钱包账户适配
 **/
public class AssetsWalletAdapter extends BaseAdapter {

    List<AssetWallet> list;
    Context context;
    ViewHolder viewHolder;
    int type;
    boolean see;

    public AssetsWalletAdapter(Context context) {
        this.context = context;
    }

    public void refersh(List<AssetWallet> list, int type, boolean see) {
        this.list = list;
        this.type = type;
        this.see = see;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_assets_wallet, null);
            viewHolder.logoIv = convertView.findViewById(R.id.logo_iv);
            viewHolder.currencyTv = convertView.findViewById(R.id.currency_tv);
            viewHolder.useableTv = convertView.findViewById(R.id.usable_tv);
            viewHolder.frostTv = convertView.findViewById(R.id.frost_tv);
            viewHolder.addressTv = convertView.findViewById(R.id.address_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        AssetWallet assetWallet = list.get(position);
        String currency = "" + assetWallet.getCurrency();
        DataUtil.setTokenIcon(viewHolder.logoIv, currency);
        viewHolder.currencyTv.setText(currency);
        if (see) {
            viewHolder.useableTv.setText("" + DataUtil.doubleFour(assetWallet.getUsableAmount()));
            viewHolder.frostTv.setText("" + DataUtil.doubleFour(assetWallet.getFrostAmount()));
        } else {
            viewHolder.useableTv.setText("******");
            viewHolder.frostTv.setText("******");
        }
        viewHolder.addressTv.setText(context.getResources().getString(R.string.addres) + "：" + assetWallet.getAddress());
        return convertView;
    }

    class ViewHolder {
        ImageView logoIv;
        TextView currencyTv, useableTv, frostTv, addressTv;
    }
}
