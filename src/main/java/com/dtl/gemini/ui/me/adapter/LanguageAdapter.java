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
import com.dtl.gemini.ui.me.model.Language;

import java.util.ArrayList;

/**
 * @author DTL
 * @date 2020/4/28
 **/

public class LanguageAdapter extends BaseAdapter {
    Context context;
    ArrayList<Language> list;
    ViewHolder viewHolder;

    public LanguageAdapter(Context context, ArrayList<Language> list) {
        this.context = context;
        this.list = list;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.language_item, null);
            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.english = convertView.findViewById(R.id.name_english);
            viewHolder.check = convertView.findViewById(R.id.check);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Language localeInfo = list.get(i);
        viewHolder.name.setText(localeInfo.getName());
        viewHolder.english.setText(localeInfo.getNameEnglish());
        String shortName = LanguageUtils.init(context).getParameter();
        if (localeInfo.getShortName().equals(shortName)) {
            viewHolder.check.setVisibility(View.VISIBLE);
        } else {
            viewHolder.check.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    class ViewHolder {
        TextView name;
        TextView english;
        ImageView check;
    }
}
