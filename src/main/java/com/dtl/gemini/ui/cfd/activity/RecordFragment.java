package com.dtl.gemini.ui.cfd.activity;

import com.dtl.gemini.R;
import com.dtl.gemini.base.BaseAppFragment;
import com.dtl.gemini.model.MData;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author DTL
 * @date 2020/4/11
 * 记录
 **/
public class RecordFragment extends BaseAppFragment {
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_cfd_record;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    protected void initView() {
        init();
        bindListener();
    }

    private void init() {

    }

    private void bindListener() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {

    }
}
