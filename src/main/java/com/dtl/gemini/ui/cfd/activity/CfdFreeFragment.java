package com.dtl.gemini.ui.cfd.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.dtl.gemini.R;
import com.dtl.gemini.api.Api;
import com.dtl.gemini.base.BaseAppFragment;
import com.dtl.gemini.common.baserx.RxSchedulers;
import com.dtl.gemini.common.baserx.RxSubscriber;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.db.StoreUtils;
import com.dtl.gemini.enums.CfdTypeEnum;
import com.dtl.gemini.enums.TokenEnum;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.ui.asset.beans.AssetCfdBean;
import com.dtl.gemini.ui.asset.model.AssetsCfd;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.MdataUtils;
import com.dtl.gemini.widget.MyViewPager;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * 自由合约--未用
 *
 * @author DTL
 * @date 2020/4/11
 **/
public class CfdFreeFragment extends BaseAppFragment {
    @Bind(R.id.cfd_account_usable_tv)
    TextView cfdAccountUsableTv;
    @Bind(R.id.cfd_open_position)
    RadioButton cfdOpenPosition;
    @Bind(R.id.cfd_position)
    RadioButton cfdPosition;
    @Bind(R.id.cfd_record)
    RadioButton cfdRecord;
    @Bind(R.id.cfd_transaction_rg)
    RadioGroup cfdTransactionRg;
    @Bind(R.id.cfd_transaction_viewPager)
    MyViewPager cfdTransactionViewPager;
    @Bind(R.id.refersh)
    SmartRefreshLayout refersh;

    private List<Fragment> listFragments = new ArrayList<>(); //pager的Fragment集合
    private MyFragmentPagerAdapter myAdapter;
    String[] pagerTitle;

    public static AssetsCfd assetsCfd;

    int type = 1;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_cfd_free;
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
        StoreUtils.init(getActivity()).setParameter(Constant.MODEL, null);
        DataUtil.setRefershTheme(getActivity(), refersh);
        DataUtil.setRefershNoLoad(refersh);
        String openPosition = getResources().getString(R.string.open_position);
        String position = getResources().getString(R.string.position);
        String record = getResources().getString(R.string.record);
        pagerTitle = new String[]{openPosition, position, record};
        if (listFragments.size() == 0)
            setViewPager();
        queryCfdAsset(getActivity());
    }

    private void setViewPager() {
        OpenPositionFragment fragment0 = new OpenPositionFragment();
        Bundle bundle0 = new Bundle();
        fragment0.setArguments(bundle0);
        listFragments.add(fragment0);

        PositionFragment fragment1 = new PositionFragment();
        Bundle bundle1 = new Bundle();
        fragment1.setArguments(bundle1);
        listFragments.add(fragment1);

        RecordFragment fragment2 = new RecordFragment();
        Bundle bundle2 = new Bundle();
        fragment2.setArguments(bundle2);
        listFragments.add(fragment2);

        myAdapter = new MyFragmentPagerAdapter(getChildFragmentManager());
        myAdapter.setFragments(listFragments);
        cfdTransactionViewPager.setAdapter(myAdapter);
        cfdTransactionViewPager.setOffscreenPageLimit(0);
    }

    private void bindListener() {
        refersh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                queryCfdAsset(getActivity());
                setRefersh();
            }
        });

//        refersh.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
//                MData mData = new MData();
//                if (cfdOpenPosition.isChecked()) {
//                    mData.type = MdataUtils.CFD_OPEN_LOAD;
//                } else if (cfdPosition.isChecked()) {
//                    mData.type = MdataUtils.CFD_HOLD_LOAD;
//                } else if (cfdRecord.isChecked()) {
//                    mData.type = MdataUtils.CFD_RECORD_LOAD;
//                }
//                EventBus.getDefault().post(mData);
//            }
//        });

        cfdTransactionRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setTab();
                setRefersh();
            }
        });

        cfdTransactionViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    cfdOpenPosition.setChecked(true);
                } else if (position == 1) {
                    cfdPosition.setChecked(true);
                } else if (position == 2) {
                    cfdRecord.setChecked(true);
                }
                setTab();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setTab() {
        if (cfdOpenPosition.isChecked()) {
            type = 1;
            cfdTransactionViewPager.setCurrentItem(0);
        } else if (cfdPosition.isChecked()) {
            type = 2;
            cfdTransactionViewPager.setCurrentItem(1);
        } else if (cfdRecord.isChecked()) {
//            cfdTransactionViewPager.setCurrentItem(2);
            startActivity(new Intent(getActivity(), RecordActivity.class));
        }
    }

    private void setRefersh() {
        MData mData = new MData();
        if (cfdOpenPosition.isChecked()) {
            mData.type = MdataUtils.CFD_OPEN_REFERSH;
        } else if (cfdPosition.isChecked()) {
            mData.type = MdataUtils.CFD_HOLD_REFERSH;
        }
        EventBus.getDefault().post(mData);
    }

    private void setCurrentItem() {
        if (type == 1) {
            cfdOpenPosition.setChecked(true);
        } else if (type == 2) {
            type = 2;
            cfdPosition.setChecked(true);
        }
        setTab();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {
        switch (mData.type) {
            case MdataUtils.CFD_TRANSACTION_REFERSH:
                queryCfdAsset(getActivity());
                break;
            case MdataUtils.CFD_OPEN_REFERSH_STOP:
            case MdataUtils.CFD_HOLD_REFERSH_STOP:
            case MdataUtils.CFD_RECORD_REFERSH_STOP:
                DataUtil.stopRefersh(refersh);
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
        setCurrentItem();
    }

    /**
     * 查询合约账户资产
     */
    private void queryCfdAsset(Context context) {
        try {
            Api.getInstance().queryCfdAsset(context, CfdTypeEnum.FREE).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<AssetCfdBean>(context) {
                @Override
                protected void _onNext(AssetCfdBean bean) {
                    if (bean != null) {
                        for (AssetsCfd cfd : bean.getData()) {
                            if (cfd.getCurrency().equals(TokenEnum.USDT.name())) {
                                assetsCfd = cfd;
                            }
                        }
                        if (assetsCfd != null)
                            cfdAccountUsableTv.setText(DataUtil.doubleFour(assetsCfd.getUseableAmount().doubleValue()) + " " + TokenEnum.USDT.name());
                    }
                    MData mData = new MData();
                    mData.type = MdataUtils.CFD_TRANSACTION_REFERSH_STOP;
                    EventBus.getDefault().post(mData);
                    DataUtil.stopRefersh(refersh);
                }

                @Override
                protected void _onError(String message) {
                    Log.e("查询合约账户资产", message + "");
                    MData mData = new MData();
                    mData.type = MdataUtils.CFD_TRANSACTION_REFERSH_STOP;
                    EventBus.getDefault().post(mData);
                    DataUtil.stopRefersh(refersh);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
