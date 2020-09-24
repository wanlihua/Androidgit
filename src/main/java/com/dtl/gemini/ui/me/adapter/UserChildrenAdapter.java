package com.dtl.gemini.ui.me.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dtl.gemini.R;
import com.dtl.gemini.db.LanguageUtils;
import com.dtl.gemini.ui.asset.model.TransactionRecord;
import com.dtl.gemini.ui.me.model.Language;
import com.dtl.gemini.ui.me.model.UserChildren;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DTL
 * @date 2020/4/28
 **/

public class UserChildrenAdapter extends BaseAdapter {
    Context context;
    List<UserChildren> list;
    ViewHolder viewHolder;

    public UserChildrenAdapter(Context context, List<UserChildren> list) {
        this.context = context;
        this.list = list;
    }

    public void refersh(List<UserChildren> list) {
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user_children, null);
            viewHolder.name = convertView.findViewById(R.id.tv_username);
            viewHolder.grade = convertView.findViewById(R.id.tv_grade);
            viewHolder.directPushNum = convertView.findViewById(R.id.tv_direct_push_num);
            viewHolder.validUserNum = convertView.findViewById(R.id.tv_valid_user_num);
            viewHolder.teamPerformance = convertView.findViewById(R.id.tv_team_performance);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        UserChildren bean = list.get(i);
        viewHolder.name.setText(bean.getUserName());
        viewHolder.grade.setText(bean.getGradeText());
        viewHolder.directPushNum.setText(bean.getDirectPushNum().toString());
        viewHolder.validUserNum.setText(bean.getTeamValidUserNumber().toString());
        viewHolder.teamPerformance.setText(bean.getSumPerformance());
        return convertView;
    }

    class ViewHolder {
        TextView name, grade, directPushNum, validUserNum, teamPerformance;
    }
}
