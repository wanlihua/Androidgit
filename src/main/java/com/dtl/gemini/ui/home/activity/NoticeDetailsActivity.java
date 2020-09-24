package com.dtl.gemini.ui.home.activity;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.dtl.gemini.R;
import com.dtl.gemini.base.BaseAppActivity;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.ui.home.model.Notice;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;

/**
 * 消息详情
 *
 * @author DTL
 * @date 2020/4/11
 **/
public class NoticeDetailsActivity extends BaseAppActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.notice_details_date)
    TextView noticeDetailsDate;
    @Bind(R.id.notice_details_title)
    TextView noticeDetailsTitle;
    @Bind(R.id.notice_details_content)
    TextView noticeDetailsContent;

    Notice bean;

    @Override
    public int getLayoutId() {
        return R.layout.activity_notice_details;
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
        setTitle(getResources().getString(R.string.notice_details));
        setBackOnClickListener();
        bean = (Notice) getIntent().getSerializableExtra("bean");
        noticeDetailsDate.setText(bean.getCreateDateTime());
        noticeDetailsTitle.setText(bean.getTitle());
        noticeDetailsContent.setText(Html.fromHtml(bean.getContent()));
        noticeDetailsContent.setMovementMethod(LinkMovementMethod.getInstance());
        noticeDetailsContent.setLinkTextColor(getResources().getColor(R.color.v1_blue));
    }

    private void bindListener() {
        setBackOnClickListener();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {

    }
}
