package com.dtl.gemini.ui.asset.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.dtl.gemini.MainActivity;
import com.dtl.gemini.R;
import com.dtl.gemini.api.Api;
import com.dtl.gemini.base.BaseAppFragment;
import com.dtl.gemini.common.baserx.RxSchedulers;
import com.dtl.gemini.common.baserx.RxSubscriber;
import com.dtl.gemini.db.StoreUtils;
import com.dtl.gemini.enums.CfdTypeEnum;
import com.dtl.gemini.enums.TokenEnum;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.ui.asset.beans.AssetCfdBean;
import com.dtl.gemini.ui.asset.beans.AssetWalletBean;
import com.dtl.gemini.ui.asset.model.AssetWallet;
import com.dtl.gemini.ui.asset.model.AssetsCfd;
import com.dtl.gemini.ui.other.activity.LoginActivity;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.MdataUtils;
import com.dtl.gemini.utils.StatusBarUtil;
import com.dtl.gemini.widget.MyViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author DTL
 * @date 2020/4/11
 * 资产
 **/
public class AssetFragment extends BaseAppFragment implements View.OnClickListener {

    @Bind(R.id.sum_asset_us)
    TextView sumAssetUs;
    @Bind(R.id.sum_asset_cny)
    TextView sumAssetCny;
    @Bind(R.id.me_see_iv)
    ImageView meSeeIv;
    @Bind(R.id.asset_wallet)
    RadioButton assetWallet;
    @Bind(R.id.asset_cfd)
    RadioButton assetCfd;
    @Bind(R.id.asset_rg)
    RadioGroup assetRg;
    @Bind(R.id.assets_viewPager)
    MyViewPager assetsViewPager;
    @Bind(R.id.asset_refersh)
    SwipeRefreshLayout assetRefersh;

    private List<Fragment> listFragments = new ArrayList<>(); //pager的Fragment集合
    private MyFragmentPagerAdapter myAdapter;
    String[] pagerTitle;
    public static boolean see = true;
    public static List<AssetWallet> assetWalletList = new ArrayList<>();
    public static AssetsCfd assetsDoubleCfd, assetsFreeCfd;
    String max = "0.00", walletMax = "0.00", cfdMax = "0.00";
    BigDecimal cfdDouble = BigDecimal.ZERO, cfdFree = BigDecimal.ZERO;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_asset;
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
        String assetWallet = getResources().getString(R.string.asset_wallet);
        String assetCfd = getResources().getString(R.string.asset_cfd);
        pagerTitle = new String[]{assetWallet, assetCfd};
        if (listFragments.size() == 0)
            setViewPager();
        refershData();
    }

    private void setViewPager() {
        AssetWalletFragment fragment0 = new AssetWalletFragment();
        Bundle bundle0 = new Bundle();
        fragment0.setArguments(bundle0);
        listFragments.add(fragment0);

        AssetCfdFragment fragment1 = new AssetCfdFragment();
        Bundle bundle1 = new Bundle();
        fragment1.setArguments(bundle1);
        listFragments.add(fragment1);

        myAdapter = new MyFragmentPagerAdapter(getChildFragmentManager());
        myAdapter.setFragments(listFragments);
        assetsViewPager.setAdapter(myAdapter);
        assetsViewPager.setOffscreenPageLimit(0);
    }

    private void bindListener() {
        assetRefersh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (StoreUtils.init(getActivity()).getLoginUser() != null) {
                    refershData();
                } else {
                    DataUtil.stopRefersh(assetRefersh);
                    LoginActivity.startAction(getActivity());
                }
            }
        });

        assetRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setTab();
            }
        });

        assetsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    assetWallet.setChecked(true);
                } else if (position == 1) {
                    assetCfd.setChecked(true);
                }
                setTab();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void setTab() {
        if (assetWallet.isChecked()) {
            assetsViewPager.setCurrentItem(0);
        } else if (assetCfd.isChecked()) {
            assetsViewPager.setCurrentItem(1);
        }
        if (StoreUtils.init(getActivity()).getLoginUser() != null)
            setAssetMax();
        else
            noLoginSee();
    }

    private void refershData() {
//        if (StoreUtils.init(getActivity()).getLoginUser() != null) {
        queryWalletAssets(getActivity());
//        } else {
//            noLoginSee();
//            MData mData = new MData();
//            mData.type = MdataUtils.ASSET_REFERSH;
//            EventBus.getDefault().post(mData);
//        }
    }

    private void noLoginSee() {
        if (see) {
            meSeeIv.setImageResource(R.mipmap.icon_see_on);
            sumAssetUs.setText("---");
            sumAssetCny.setText("≈--- CNY");
        } else {
            meSeeIv.setImageResource(R.mipmap.icon_see_off);
            sumAssetUs.setText("******");
            sumAssetCny.setText("≈****** CNY");
        }
    }

    /**
     * 查询钱包账户资产
     */
    private void queryWalletAssets(Context context) {
        try {
            Api.getInstance().queryWalletAssets(context).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<AssetWalletBean>(context) {
                @Override
                protected void _onNext(AssetWalletBean bean) {
                    if (bean != null) {
                        assetWalletList = bean.getData();
                        walletMax = "0.00";
                        double usableAmount = 0.00;//可用
                        double frostAmount = 0.00;//冻结
                        for (AssetWallet wallet : assetWalletList) {
                            String price = "1.00";
                            if (MainActivity.mapPrice.get(wallet.getCurrency()) != null) {//价格
                                price = MainActivity.mapPrice.get(wallet.getCurrency());
                            }
                            usableAmount += wallet.getUsableAmount() * Double.parseDouble(price);
                            frostAmount += wallet.getFrostAmount() * Double.parseDouble(price);
                        }
                        walletMax = DataUtil.numberTwo((usableAmount + frostAmount));
                    }
                    setAssets();
                    queryCfdAsset(context, CfdTypeEnum.DOUBLE);
                    queryCfdAsset(context, CfdTypeEnum.FREE);
                }

                @Override
                protected void _onError(String message) {
                    Log.e("查询账户资产", message + "");
                    setAssets();
                    queryCfdAsset(context, CfdTypeEnum.DOUBLE);
                    queryCfdAsset(context, CfdTypeEnum.FREE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询合约账户资产
     *
     * @param context
     * @param cfdTypeEnum 账户类型
     */
    private void queryCfdAsset(Context context, CfdTypeEnum cfdTypeEnum) {
        try {
            Api.getInstance().queryCfdAsset(context, cfdTypeEnum).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<AssetCfdBean>(context) {
                @Override
                protected void _onNext(AssetCfdBean bean) {
                    if (bean != null && bean.getData().size() > 0) {
                        for (AssetsCfd cfd : bean.getData()) {
                            if (cfd.getCurrency().equals(TokenEnum.USDT.name())) {
                                if (cfdTypeEnum == CfdTypeEnum.DOUBLE) {
                                    assetsDoubleCfd = cfd;
                                } else {
                                    assetsFreeCfd = cfd;
                                }
                            }
                        }
                        cfdMax = "0.00";
                        String price = "1.00";
                        if (MainActivity.mapPrice.get(TokenEnum.USDT.name()) != null) {//价格
                            price = MainActivity.mapPrice.get(TokenEnum.USDT.name());
                        }
                        cfdDouble = assetsDoubleCfd.getUseableAmount().add(assetsDoubleCfd.getFrostAmount()).add(assetsDoubleCfd.getFrostProfit()).setScale(4, RoundingMode.DOWN);
                        cfdFree = assetsFreeCfd.getUseableAmount().add(assetsFreeCfd.getFrostAmount()).add(assetsFreeCfd.getFrostProfit()).setScale(4, RoundingMode.DOWN);
                        cfdMax = DataUtil.numberTwo((cfdDouble.add(cfdFree).doubleValue()) * Double.parseDouble(price));
                    }
                    setAssets();
                }

                @Override
                protected void _onError(String message) {
                    DataUtil.stopRefersh(assetRefersh);
                    Log.e("查询合约账户资产", message + "");
                    setAssets();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAssets(){
        setAssetMax();
        DataUtil.stopRefersh(assetRefersh);
        MData mData = new MData();
        mData.type = MdataUtils.ASSET_REFERSH;
        EventBus.getDefault().post(mData);
    }

    private void setAssetMax() {
        double amount = Double.parseDouble(walletMax) + Double.parseDouble(cfdMax);
        max = DataUtil.doubleFour(amount);
//        if (assetWallet.isChecked()) {
//            max = walletMax;
//        } else if (assetCfd.isChecked()) {
//            max = cfdMax;
//        }
        setSee();
    }

    private void setSee() {
        if (see) {
            meSeeIv.setImageResource(R.mipmap.icon_see_on);
//            sumAssetUs.setText(DataUtil.returnLanguuagePrice(getActivity(), max));
            sumAssetUs.setText(DataUtil.doubleTwo(Double.parseDouble(max) / MainActivity.usd));
            sumAssetCny.setText("≈" + max + " CNY");
        } else {
            meSeeIv.setImageResource(R.mipmap.icon_see_off);
            sumAssetUs.setText("******");
            sumAssetCny.setText("≈****** CNY");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {
        switch (mData.type) {
            case MdataUtils.ASSET_WITHDRAW:
            case MdataUtils.ASSET_EXCHANGE:
            case MdataUtils.ASSET_TRANSFER:
                refershData();
                break;
            case MdataUtils.ASSET_CFD_REFERSH:
                queryCfdAsset(getActivity(), AssetCfdFragment.cfdTypeEnum);
                break;
        }
    }

    @OnClick({R.id.me_see_iv})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.me_see_iv:
                see = !see;
                setSee();
                MData mData = new MData();
                mData.type = MdataUtils.ASSET_SEE;
                EventBus.getDefault().post(mData);
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
        StatusBarUtil.setStatusBarColor2(getActivity());
    }
}
