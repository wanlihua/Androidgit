package com.dtl.gemini.ui.me.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dtl.gemini.MainActivity;
import com.dtl.gemini.R;
import com.dtl.gemini.base.BaseAppActivity;
import com.dtl.gemini.db.LanguageUtils;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.ui.me.adapter.LanguageAdapter;
import com.dtl.gemini.ui.me.model.Language;
import com.dtl.gemini.utils.DataUtil;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import butterknife.Bind;

/**
 * @author DTL
 * @date 2020/4/28
 * 语言设置
 **/
public class LanguageActivity extends BaseAppActivity {

    @Bind(R.id.list)
    ListView list;

    private ArrayList<Language> languageArrayList;
    LanguageAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_language;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    public void initView() {
        init();
        bindListener();
    }

    private void init() {
        setTitle(getResources().getString(R.string.switch_language));
        languageArrayList = new ArrayList<>();
        setLanguages();
        //获取系统支持语言
        adapter = new LanguageAdapter(this, languageArrayList);
        list.setAdapter(adapter);
    }

    private void bindListener() {
        setBackOnClickListener();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (DataUtil.isFastClick())
                    //设置语言
                    settingLanguage(languageArrayList.get(i).getShortName());
            }
        });
    }

    private void settingLanguage(String shortName) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Configuration configuration = LanguageUtils.returnConfigration(LanguageActivity.this, shortName);
        getResources().updateConfiguration(configuration, metrics);
        LanguageUtils.init(this).delParameter();
        LanguageUtils.init(this).setParameter(shortName);
        Intent intent = new Intent(LanguageActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void setLanguages() {
        Language language1 = new Language("简体中文", "Simplified Chinese", "zh");
        Language language2 = new Language("English", "English", "en");
        Language language3 = new Language("Italiano", "Italian", "it");
        Language language4 = new Language("Español", "Spanish", "es");
        Language language5 = new Language("Deutsch", "German", "de");
        Language language6 = new Language("Nederlands", "Dutch", "nl");
        Language language7 = new Language("العربية", "Arabic", "ar");
        Language language8 = new Language("Português(Brasil)", "Portuguese(Brazil)", "pt_br");
        Language language9 = new Language("한국어", "Korean", "ko");
        Language language10 = new Language("Français", "French", "fr");
        Language language11 = new Language("Русский", "Russian", "ru");
        Language language12 = new Language("日本語", "Japanese", "ja");
        languageArrayList.add(language1);
//        languageArrayList.add(language9);
        languageArrayList.add(language2);
//        languageArrayList.add(language12);
//        languageArrayList.add(language3);
//        languageArrayList.add(language4);
//        languageArrayList.add(language5);
//        languageArrayList.add(language6);
//        languageArrayList.add(language7);
//        languageArrayList.add(language8);
//        languageArrayList.add(language10);
//        languageArrayList.add(language11);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {

    }

}
