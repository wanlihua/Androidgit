package com.dtl.gemini.ui.cfd.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.dtl.gemini.R;
import com.dtl.gemini.ui.cfd.model.OrderCreate;
import com.dtl.gemini.utils.DataUtil;

import java.util.List;

/**
 * @author DTL
 * @date 2020/5/5
 * 持仓
 **/
public class PositionRecordAdapter extends BaseAdapter {
    Context context;
    List<OrderCreate> list;
    ViewHolder viewHolder;

    AdapterView.OnItemClickListener closeItemClickListener;
    AdapterView.OnItemClickListener speedCloseItemClickListener;
    AdapterView.OnItemClickListener addBalanceItemClickListener;

    public PositionRecordAdapter(Context context) {
        this.context = context;
    }

    public void refersh(List<OrderCreate> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    /**
     * 平仓
     *
     * @param closeItemClickListener
     */
    public void setCloseOnclick(AdapterView.OnItemClickListener closeItemClickListener) {
        this.closeItemClickListener = closeItemClickListener;
    }

    /**
     * 闪电平仓
     *
     * @param speedCloseItemClickListener
     */
    public void setSpeedCloseOnclick(AdapterView.OnItemClickListener speedCloseItemClickListener) {
        this.speedCloseItemClickListener = speedCloseItemClickListener;
    }

    /**
     * 追加保证金
     *
     * @param addBalanceItemClickListener
     */
    public void setAddBalanceOnclick(AdapterView.OnItemClickListener addBalanceItemClickListener) {
        this.addBalanceItemClickListener = addBalanceItemClickListener;
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

    @SuppressLint("StringFormatInvalid")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cfd_position_record, null);
            viewHolder = new ViewHolder();
            viewHolder.type = convertView.findViewById(R.id.type_tv);
            viewHolder.id = convertView.findViewById(R.id.id_tv);
            viewHolder.multiple = convertView.findViewById(R.id.multiple_tv);
            viewHolder.profitRate = (TextView) convertView.findViewById(R.id.profit_rate_tv);
            viewHolder.positionNumber = (TextView) convertView.findViewById(R.id.position_number_tv);
            viewHolder.openPrice = convertView.findViewById(R.id.open_price_tv);
            viewHolder.cfdProfit = convertView.findViewById(R.id.cfd_profit_tv);
            viewHolder.currNumber = convertView.findViewById(R.id.curr_number_tv);
            viewHolder.transactionNumber = convertView.findViewById(R.id.transaction_number_tv);
            viewHolder.closePosition = convertView.findViewById(R.id.close_position_btn);
            viewHolder.speedClosePosition = convertView.findViewById(R.id.speed_close_position_btn);
            viewHolder.positionNumberTv = convertView.findViewById(R.id.position_number);
            viewHolder.currNumberTv = convertView.findViewById(R.id.curr_number);
            viewHolder.estimatedLiquidationPrice = convertView.findViewById(R.id.estimated_liquidation_price_tv);
            viewHolder.addBalance = convertView.findViewById(R.id.add_balance_btn);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        OrderCreate record = list.get(position);
//        开多 l 爆仓价格 = （买入价格 * 持仓量 - 保证金）/持仓量
//        开空 l 爆仓价格 = （买入价格 * 持仓量 + 保证金）/持仓量
        double estimatedLiquidationPrice = 0;
        if (record.getTrend() == 1) {
            estimatedLiquidationPrice = (record.getStartPrice() * record.getUseableAmount() - record.getUseableBalance()) / record.getUseableAmount();
            viewHolder.type.setTextColor(context.getResources().getColor(R.color.zhang));
            if (record.getStartType() == 1) {
                viewHolder.type.setText(context.getResources().getString(R.string.speed_open_rise));
            } else {
                viewHolder.type.setText(context.getResources().getString(R.string.plan_open_rise));
            }
        } else {
            estimatedLiquidationPrice = (record.getStartPrice() * record.getUseableAmount() + record.getUseableBalance()) / record.getUseableAmount();
            viewHolder.type.setTextColor(context.getResources().getColor(R.color.die));
            if (record.getStartType() == 1) {
                viewHolder.type.setText(context.getResources().getString(R.string.speed_open_fall));
            } else {
                viewHolder.type.setText(context.getResources().getString(R.string.plan_open_fall));
            }
        }
        viewHolder.estimatedLiquidationPrice.setText(DataUtil.doubleFour(estimatedLiquidationPrice));
        viewHolder.id.setText(DataUtil.returnPhoneHint(record.getId()));
        viewHolder.positionNumberTv.setText(context.getString(R.string.position_number, record.getCurrency()));
        viewHolder.currNumberTv.setText(context.getString(R.string.curr_number, record.getCurrency()));
        viewHolder.multiple.setText(record.getMultiple() + " X");
        double profit = record.getPredictIncome() / record.getBalance();
        if (profit < 0) {
            viewHolder.profitRate.setBackgroundResource(R.drawable.radius_red_24);
            viewHolder.profitRate.setText(DataUtil.numberTwo(profit * 100) + "%");
        } else {
            viewHolder.profitRate.setBackgroundResource(R.drawable.radius_green_24);
            viewHolder.profitRate.setText("+" + DataUtil.numberTwo(profit * 100) + "%");
        }
        viewHolder.positionNumber.setText(DataUtil.doubleFour(record.getAmount()));
        viewHolder.openPrice.setText(DataUtil.doubleFour(record.getStartPrice()));
        viewHolder.cfdProfit.setText(DataUtil.doubleFour(record.getPredictIncome()));
        viewHolder.currNumber.setText(DataUtil.doubleFour(record.getUseableAmount()));
        viewHolder.transactionNumber.setText(DataUtil.doubleFour(record.getUseableBalance()));
        viewHolder.closePosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list != null && list.size() > 0)
                    closeItemClickListener.onItemClick(null, v, position, position);
            }
        });
        viewHolder.speedClosePosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list != null && list.size() > 0)
                    speedCloseItemClickListener.onItemClick(null, v, position, position);
            }
        });
        viewHolder.addBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list != null && list.size() > 0)
                    addBalanceItemClickListener.onItemClick(null, v, position, position);
            }
        });
        return convertView;
    }


    class ViewHolder {
        Button closePosition, speedClosePosition, addBalance;
        TextView type, id, multiple, profitRate, positionNumber, openPrice, cfdProfit,
                estimatedLiquidationPrice, currNumber, transactionNumber, positionNumberTv, currNumberTv;
    }
}
