package com.dtl.gemini.ui.cfd.activity;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.dtl.gemini.R;
import com.dtl.gemini.base.BaseAppFragment;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.db.StoreUtils;
import com.dtl.gemini.enums.CfdTypeEnum;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.MdataUtils;
import com.dtl.gemini.utils.StatusBarUtil;
import com.dtl.gemini.widget.NoScrollViewPager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 合约--未用
 * @author DTL
 * @date 2020/4/11
 **/
public class CfdFragments extends BaseAppFragment {
    @Bind(R.id.kline_iv)
    ImageView klineIv;
    @Bind(R.id.kline_tv)
    TextView klineTv;
    @Bind(R.id.kline_ll)
    LinearLayout klineLl;
    @Bind(R.id.transaction_iv)
    ImageView transactionIv;
    @Bind(R.id.transaction_tv)
    TextView transactionTv;
    @Bind(R.id.transaction_ll)
    LinearLayout transactionLl;
    @Bind(R.id.assets_viewPager)
    NoScrollViewPager assetsViewPager;

    private List<Fragment> listFragments = new ArrayList<>(); //pager的Fragment集合
    private MyFragmentPagerAdapter myAdapter;
    String[] pagerTitle;
    int position = 1;

    public static CfdTypeEnum cfdTypeEnum = CfdTypeEnum.DOUBLE;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_cfds;
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
        String cfdDouble = getResources().getString(R.string.double_cfd);//双仓合约
        String cfdFree = getResources().getString(R.string.free_cfd);//自由合约
        pagerTitle = new String[]{cfdDouble, cfdFree};
        if (listFragments.size() == 0)
            setViewPager();
    }

    private void bindListener() {

        klineLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataUtil.isFastClick()) {
                    setTab(0);
                }
            }
        });

        transactionLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataUtil.isFastClick()) {
                    setTab(1);
                }
            }
        });

        assetsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setViewPager() {
        CfdDoubleFragment fragment0 = new CfdDoubleFragment();
        Bundle bundle0 = new Bundle();
        fragment0.setArguments(bundle0);
        listFragments.add(fragment0);

        CfdFreeFragment fragment1 = new CfdFreeFragment();
        Bundle bundle1 = new Bundle();
        fragment1.setArguments(bundle1);
        listFragments.add(fragment1);

        myAdapter = new MyFragmentPagerAdapter(getChildFragmentManager());
        myAdapter.setFragments(listFragments);
        assetsViewPager.setAdapter(myAdapter);
        String activity = StoreUtils.init(getActivity()).getParameter(Constant.MODEL);
        if (activity != null && activity.equals("cfd")) {
            setTab(1);
        }
        assetsViewPager.setOffscreenPageLimit(0);
    }

    private void setTab(int type) {
        position = type;
        if (type == 0) {//双仓
            klineLl.setBackgroundResource(R.drawable.shape_bottom_line_bg);
            klineIv.setImageResource(R.mipmap.icon_cfd_kline_on);
            klineTv.setTextColor(getResources().getColor(R.color.v2_btn));
            transactionIv.setImageResource(R.mipmap.icon_cfd_transaction_off);
            transactionTv.setTextColor(getResources().getColor(R.color.text));
            assetsViewPager.setCurrentItem(0);
            cfdTypeEnum = CfdTypeEnum.DOUBLE;
        } else if (type == 1) {//自由
            assetsViewPager.setCurrentItem(1);
            transactionIv.setImageResource(R.mipmap.icon_cfd_transaction_on);
            transactionTv.setTextColor(getResources().getColor(R.color.v2_btn));
            klineLl.setBackgroundResource(R.drawable.shape_bottom_line_bg0);
            klineIv.setImageResource(R.mipmap.icon_cfd_kline_off);
            klineTv.setTextColor(getResources().getColor(R.color.text));
            cfdTypeEnum = CfdTypeEnum.FREE;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {
        switch (mData.type) {
            case MdataUtils.CFD_KLINE_TO_TRANSACTION:
                setTab(1);
                break;
        }
    }

    class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
        List<Fragment> fragments;

        public MyFragmentPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public void setFragments(List<Fragment> fragments) {
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pagerTitle[position];
        }

        @Override
        public int getCount() {
            return fragments.size();
        }


        @Override
        public int getItemPosition(Object object) {
            //这是ViewPager适配器的特点,有两个值 POSITION_NONE，POSITION_UNCHANGED，默认就是POSITION_UNCHANGED,
            // 表示数据没变化不用更新.notifyDataChange的时候重新调用getViewForPage
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        StatusBarUtil.setFragmentStatusBar(getActivity(), R.color.white);
    }
}
