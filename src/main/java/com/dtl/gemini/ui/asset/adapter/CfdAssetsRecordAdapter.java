package com.dtl.gemini.ui.asset.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dtl.gemini.R;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.ui.asset.model.AssetCfdRecord;
import com.dtl.gemini.ui.asset.model.Withdrawal;
import com.dtl.gemini.utils.DataUtil;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author DTL
 * @date 2020/4/29
 * 合约账户资产记录
 **/
public class CfdAssetsRecordAdapter extends BaseAdapter {
    Context context;
    List<AssetCfdRecord> recordList;
    ViewHolder viewHolder;

    public CfdAssetsRecordAdapter(Context context) {
        this.context = context;
    }

    public void refersh(List<AssetCfdRecord> recordList) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cfd_asset_record, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.typeTv = convertView.findViewById(R.id.type_tv);
            viewHolder.statusTv = convertView.findViewById(R.id.status_tv);
            viewHolder.numberTv = (TextView) convertView.findViewById(R.id.number_tv);
            viewHolder.dateTv = (TextView) convertView.findViewById(R.id.date_tv);
            viewHolder.extralTv = (TextView) convertView.findViewById(R.id.extral_tv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        AssetCfdRecord record = recordList.get(position);
        viewHolder.numberTv.setText(DataUtil.numberFour(record.getAfterUseable() - record.getBeforeUseable()));
        String date = record.getCreateTime();
        viewHolder.dateTv.setText(DataUtil.returnToTime(date, Constant.DATE_FORMAT_SSSZ, Constant.DATE_FORMAT_ALL));
        viewHolder.extralTv.setVisibility(View.GONE);
        String reason = "";
        if (record.getReason() != null) {
//            viewHolder.extralTv.setVisibility(View.VISIBLE);
//            viewHolder.extralTv.setText(record.getReason().toString());
            reason = record.getReason().toString();
        }
        switch (record.getType()) {
            case 11:
                reason = context.getResources().getString(R.string.asset_cfd_record_type11);
                break;
            case 13:
            case 14:
                reason = context.getResources().getString(R.string.asset_cfd_record_type13);
                break;
            case 15:
                reason = context.getResources().getString(R.string.asset_cfd_record_type15);
                break;
            case 17:
                reason = context.getResources().getString(R.string.asset_cfd_record_type17);
                viewHolder.numberTv.setText(record.getReason());
                if (DataUtil.isNumeric(record.getReason())) {
                    viewHolder.numberTv.setText(DataUtil.doubleFour(new BigDecimal(record.getReason()).doubleValue()));
                }
                break;
            case 18:
                reason = context.getResources().getString(R.string.asset_cfd_record_type18);
                viewHolder.numberTv.setText(record.getReason());
                if (DataUtil.isNumeric(record.getReason())) {
                    viewHolder.numberTv.setText(DataUtil.doubleFour(new BigDecimal(record.getReason()).doubleValue()));
                }                break;
            case 16:
            case 19:
                reason = context.getResources().getString(R.string.asset_cfd_record_type16);
                viewHolder.numberTv.setText(record.getReason());
                if (DataUtil.isNumeric(record.getReason())) {
                    viewHolder.numberTv.setText(DataUtil.doubleFour(new BigDecimal(record.getReason()).doubleValue()));
                }
                break;
        }
        viewHolder.typeTv.setText(reason);
        return convertView;
    }

    class ViewHolder {
        TextView typeTv, statusTv, numberTv, dateTv, extralTv;
    }

}
