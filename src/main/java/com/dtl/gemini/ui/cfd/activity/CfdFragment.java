package com.dtl.gemini.ui.cfd.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.dtl.gemini.model.MData;
import com.dtl.gemini.ui.cfd.beans.TokenBean;
import com.dtl.gemini.ui.home.activity.KlineActivity;
import com.dtl.gemini.ui.home.model.CfdToken;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.MdataUtils;
import com.dtl.gemini.widget.AutoWheelChoicePopup;
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
import lombok.Setter;

/**
 * @author DTL
 * @date 2020/4/11
 **/
public class CfdFragment extends BaseAppFragment {
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
    @Bind(R.id.tv_token)
    TextView tvToken;
    @Bind(R.id.rl_token)
    RelativeLayout rlToken;
    @Bind(R.id.tv_price_us)
    TextView tvPriceUs;
    @Bind(R.id.tv_price_cny)
    TextView tvPriceCny;
    @Bind(R.id.iv_kline)
    ImageView ivKline;
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

    int type = 1;
    public static CfdTypeEnum cfdTypeEnum = CfdTypeEnum.DOUBLE;
    List<CfdToken> list = new ArrayList<>();
    public static CfdToken cfdToken;
    public static String currency = "BTC";
    AutoWheelChoicePopup popup;
    public static List<String> listToken = new ArrayList<>();


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_cfd;
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
        if (StoreUtils.init(getActivity()).getParameter(Constant.CURRENCY) != null) {
            currency = StoreUtils.init(getActivity()).getParameter(Constant.CURRENCY);
        }
        if (StoreUtils.init(getActivity()).getParameter(Constant.TYPE) != null) {
            int type = Integer.parseInt(StoreUtils.init(getActivity()).getParameter(Constant.TYPE));
            if (type == CfdTypeEnum.DOUBLE.grade) {
                cfdTypeEnum = CfdTypeEnum.DOUBLE;
            } else {
                cfdTypeEnum = CfdTypeEnum.FREE;
            }
            StoreUtils.init(getActivity()).setParameter(Constant.TYPE, null);
        }
        setTitleTab();
        StoreUtils.init(getActivity()).setParameter(Constant.MODEL, null);
        DataUtil.setRefershTheme(getActivity(), refersh);
        DataUtil.setRefershNoLoad(refersh);
        String openPosition = getResources().getString(R.string.open_position);
        String position = getResources().getString(R.string.position);
        String record = getResources().getString(R.string.record);
        pagerTitle = new String[]{openPosition, position, record};
        if (listFragments.size() == 0)
            setViewPager();
    }

    private void setViewPager() {
        OpenPositionFragment fragment0 = new OpenPositionFragment(); //开仓
        Bundle bundle0 = new Bundle();
        fragment0.setArguments(bundle0);
        listFragments.add(fragment0);

        PositionFragment fragment1 = new PositionFragment();// * 持仓
        Bundle bundle1 = new Bundle();
        fragment1.setArguments(bundle1);
        listFragments.add(fragment1);

        RecordFragment fragment2 = new RecordFragment();// * 记录
        Bundle bundle2 = new Bundle();
        fragment2.setArguments(bundle2);
        listFragments.add(fragment2);

        myAdapter = new MyFragmentPagerAdapter(getChildFragmentManager());
        myAdapter.setFragments(listFragments);
        cfdTransactionViewPager.setAdapter(myAdapter);
        cfdTransactionViewPager.setOffscreenPageLimit(0);
    }

    private void initPopupToken(String token) {
        if (listToken == null || listToken.size() == 0)
            return;
        popup = new AutoWheelChoicePopup(getActivity(), listToken,token);
        popup.setBtnOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currency = popup.getSelecedData();
                tvToken.setText(currency + "/USDT");
                for (CfdToken token : list) {
                    if (currency.equals(token.getCurrency())) {
                        cfdToken = token;
                    }
                }
                setTokenEvent();
                popup.dismiss();
            }
        });
    }

    private void setTokenEvent() {
        MData mData = new MData();
        mData.type = MdataUtils.CFD_TOKEN;
        EventBus.getDefault().post(mData);
    }

    private void bindListener() {
        refersh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//                queryCfdTokens(getActivity());
                setRefersh();
            }
        });

        ivKline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataUtil.isFastClick()) {
                    StoreUtils.init(getActivity()).setParameter(Constant.TYPE, cfdTypeEnum.grade + "");
                    StoreUtils.init(getActivity()).setParameter(Constant.CURRENCY, currency);
                    Intent intent = new Intent(getActivity(), KlineActivity.class);
                    intent.putExtra(Constant.CURRENCY, currency);
                    startActivity(intent);
                }
            }
        });

        klineLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataUtil.isFastClick()) {
                    StoreUtils.init(getActivity()).setParameter(Constant.CURRENCY, null);
                    cfdTypeEnum = CfdTypeEnum.DOUBLE;
                    setTitleTab();
                }
            }
        });

        transactionLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataUtil.isFastClick()) {
                    StoreUtils.init(getActivity()).setParameter(Constant.CURRENCY, null);
                    cfdTypeEnum = CfdTypeEnum.FREE;
                    setTitleTab();
                }
            }
        });

        rlToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataUtil.isFastClick()) {
                    if (popup != null && listToken != null && listToken.size() > 0)
                        popup.showAtLocation(getActivity().findViewById(R.id.cfd_view), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                }
            }
        });

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

    private void setTitleTab() {
        if (cfdTypeEnum == CfdTypeEnum.DOUBLE) {//双仓
            klineLl.setBackgroundResource(R.drawable.shape_bottom_line_bg);
            transactionLl.setBackgroundResource(R.color.transparent);
            klineIv.setImageResource(R.mipmap.icon_cfd_kline_on);
            klineTv.setTextColor(getResources().getColor(R.color.v2_btn));
            transactionIv.setImageResource(R.mipmap.icon_cfd_transaction_off);
            transactionTv.setTextColor(getResources().getColor(R.color.text));
        } else {//自由
            transactionLl.setBackgroundResource(R.drawable.shape_bottom_line_bg);
            klineLl.setBackgroundResource(R.color.transparent);
            transactionIv.setImageResource(R.mipmap.icon_cfd_transaction_on);
            transactionTv.setTextColor(getResources().getColor(R.color.v2_btn));
            klineIv.setImageResource(R.mipmap.icon_cfd_kline_off);
            klineTv.setTextColor(getResources().getColor(R.color.text));
        }
        queryCfdTokens(getActivity());
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
                queryCfdTokens(getActivity());
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
     * 获取合约币种
     *
     * @param context
     */
    private void queryCfdTokens(Context context) {
        try {
            Api.getInstance().queryCfdTokens(context, cfdTypeEnum).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<TokenBean>(context) {
                @Override
                protected void _onNext(TokenBean bean) {
                    if (bean != null && bean.getData() != null && bean.getData().size() > 0) {
                        listToken.clear();
                        list = bean.getData();
                        if (StoreUtils.init(getActivity()).getParameter(Constant.CURRENCY) == null) {
                            cfdToken = bean.getData().get(0);
                            currency = cfdToken.getCurrency();
                        }
                        StoreUtils.init(getActivity()).setParameter(Constant.CURRENCY, null);
                        tvToken.setText(currency + "/USDT");
                        for (CfdToken token : bean.getData()) {
                            listToken.add(token.getCurrency());
                        }
                        setTokenEvent();
                        initPopupToken(currency);
                    }
                    DataUtil.stopRefersh(refersh);
                }

                @Override
                protected void _onError(String message) {
                    DataUtil.stopRefersh(refersh);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
