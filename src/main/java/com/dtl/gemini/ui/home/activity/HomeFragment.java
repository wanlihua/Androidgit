package com.dtl.gemini.ui.home.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dtl.gemini.MainActivity;
import com.dtl.gemini.R;
import com.dtl.gemini.api.Api;
import com.dtl.gemini.base.BaseAppFragment;
import com.dtl.gemini.bean.DataBean;
import com.dtl.gemini.common.baserx.RxSchedulers;
import com.dtl.gemini.common.baserx.RxSubscriber;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.db.MarketStoreUtils;
import com.dtl.gemini.enums.CfdTypeEnum;
import com.dtl.gemini.kline.KlineApi;
import com.dtl.gemini.kline.KlineApiImpl;
import com.dtl.gemini.kline.beans.HuoBiBean;
import com.dtl.gemini.kline.beans.MarketBean;
import com.dtl.gemini.kline.model.HuoBi;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.ui.cfd.beans.TokenBean;
import com.dtl.gemini.ui.home.adapter.MarketAdapter;
import com.dtl.gemini.ui.home.beans.LunBoBean;
import com.dtl.gemini.ui.home.beans.NoticesBean;
import com.dtl.gemini.ui.home.model.CfdToken;
import com.dtl.gemini.ui.home.model.LunBo;
import com.dtl.gemini.ui.home.model.Market;
import com.dtl.gemini.ui.home.model.Notice;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.StatusBarUtil;
import com.dtl.gemini.widget.AutoVerticalTextview.VerticalTextview;
import com.dtl.gemini.widget.GlideImageLoader;
import com.dtl.gemini.widget.MyListView;
import com.dtl.gemini.widget.klineview.kline.KData;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.google.gson.Gson;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author DTL
 * @date 2020/4/11
 **/
public class HomeFragment extends BaseAppFragment implements View.OnClickListener {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.home_banner)
    Banner homeBanner;
    @Bind(R.id.home_refersh)
    SwipeRefreshLayout homeRefersh;
    @Bind(R.id.home_notice)
    VerticalTextview homeNotice;
    @Bind(R.id.rb_double_cfd)
    RadioButton rbDoubleCfd;
    @Bind(R.id.rb_free_cfd)
    RadioButton rbFreeCfd;
    @Bind(R.id.rg_cfd)
    RadioGroup rgCfd;
    @Bind(R.id.list_mlv)
    MyListView listMlv;

    List<String> images;
    LunBoBean listLunBoBeans;
    int noticeInt = 1;
    Thread thread;
    boolean flag = true;
    List<Notice> noticeBeanList;

    MarketAdapter adapter;
    List<Market> lists = new ArrayList<>();

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (homeNotice != null)
                        homeNotice.startAutoScroll();
                    noticeInt++;
                    break;
                case 10000002:
                    queryCfdTokens(getActivity());
                    break;
            }
        }
    };

    CfdTypeEnum cfdTypeEnum = CfdTypeEnum.DOUBLE;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_home;
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
//        getCfd();
        DataUtil.setFocusTv(title);
//        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ritalic.ttf");
//        title.setTypeface(typeface);
        adapter = new MarketAdapter(getContext(), lists);
        listMlv.setAdapter(adapter);
        images = new ArrayList<>();
        refershData();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //清理内存中的缓存
//                Glide.get(getActivity()).clearMemory();
//                //清理硬盘中的缓存
//                Glide.get(getActivity()).clearDiskCache();
//            }
//        }).start();
    }

    private void bindListener() {
        homeRefersh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refershData();
            }
        });
        homeBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                if (DataUtil.isFastClick()) {
                    if (listLunBoBeans != null && listLunBoBeans.getData().size() > 0 && listLunBoBeans.getData().get(position).getUrl() != null && !listLunBoBeans.getData().get(position).getUrl().equals("")) {
                        WebActivity.startAction(getActivity(), listLunBoBeans.getData().get(position).getTitle(), listLunBoBeans.getData().get(position).getUrl().toString());
                    }
                } else {
                    return;
                }
            }
        });

        homeNotice.setOnItemClickListener(new VerticalTextview.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (DataUtil.isFastClick()) {
                    if (noticeBeanList != null && noticeBeanList.size() > 0) {
                        if (position < noticeBeanList.size()) {
                            Intent intent = new Intent(getActivity(), NoticeDetailsActivity.class);
                            intent.putExtra("bean", noticeBeanList.get(position));
                            startActivity(intent);
                        }
                    }
                } else {
                    return;
                }
            }
        });

        rgCfd.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                setTab();
            }
        });

    }

    private void setTab() {
        if (rbDoubleCfd.isChecked()) {
            cfdTypeEnum = CfdTypeEnum.DOUBLE;
        } else if (rbFreeCfd.isChecked()) {
            cfdTypeEnum = CfdTypeEnum.FREE;
        }
        queryCfdTokens(getActivity());
    }

    private void refershData() {
        queryListLunbo();
        queryCfdTokens(getActivity());
    }

    /**
     * 获取轮播
     */
    private void getCfd() {
        try {
            Api.getInstance().getCfd(getActivity()).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<DataBean>(getActivity()) {
                @Override
                protected void _onNext(DataBean bean) {

                }

                @Override
                protected void _onError(String message) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取轮播
     */
    private void queryListLunbo() {
        try {
            Api.getInstance().queryListLunbo(getActivity()).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<LunBoBean>(getActivity()) {
                @Override
                protected void _onNext(LunBoBean bean) {
                    if (bean != null && bean.getData().size() > 0) {
                        if (images != null)
                            images.clear();
                        listLunBoBeans = bean;
                        for (LunBo lunbo : listLunBoBeans.getData()) {
                            images.add(lunbo.getImgUrl());
                        }
                        initBannerEvent(images);
                    } else {
                        setBanner();
                    }
                    queryNoticeAll();
                }

                @Override
                protected void _onError(String message) {
                    Log.e("获取轮播", message + "");
                    setBanner();
                    queryNoticeAll();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBanner() {
        List<Integer> images = new ArrayList<>();
        if (DataUtil.returnLanguuage(getActivity()).equals("ko")) {
            images.add(R.mipmap.icon_home_banner);
        } else if (DataUtil.returnLanguuage(getActivity()).equals("ja")) {
            images.add(R.mipmap.icon_home_banner);
        } else {
            images.add(R.mipmap.icon_home_banner);
        }
        initBannerEvent(images);
    }

    private void initBannerEvent(List<?> imageUrls) {
        if (homeBanner != null && imageUrls != null && imageUrls.size() > 0) {
            //设置图片加载器
            homeBanner.setImageLoader(new GlideImageLoader());
            //设置图片集合
            homeBanner.setImages(imageUrls);
            //设置banner动画效果
            homeBanner.setBannerAnimation(Transformer.CubeOut);
            //设置标题集合（当banner样式有显示title时）
//        homeBanner.setBannerTitles(titles);
            //设置自动轮播，默认为true
            homeBanner.isAutoPlay(true);
            //设置轮播时间
            homeBanner.setDelayTime(3000);
            //设置轮播样式
//            public static final int NOT_INDICATOR = 0;
//            public static final int CIRCLE_INDICATOR = 1;
//            public static final int NUM_INDICATOR = 2;
//            public static final int NUM_INDICATOR_TITLE = 3;
//            public static final int CIRCLE_INDICATOR_TITLE = 4;
//            public static final int CIRCLE_INDICATOR_TITLE_INSIDE = 5;
            homeBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
            //设置指示器位置（当banner模式中有指示器时）
            homeBanner.setIndicatorGravity(BannerConfig.CENTER);
            //banner设置方法全部调用完毕时最后调用
            homeBanner.start();
        }
    }


    /**
     * 获取合约币种
     *
     * @param context
     */
    private void queryCfdTokens(Context context) {
        try {
            setCurrencyPrice();
            Api.getInstance().queryCfdTokens(context, cfdTypeEnum).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<TokenBean>(context) {
                @Override
                protected void _onNext(TokenBean bean) {
                    if (bean != null && bean.getData() != null && bean.getData().size() > 0) {
                        for (CfdToken token : bean.getData()) {
                            queryKline(context, token.getCurrency(), token.getMode());
                        }
                    }
                }

                @Override
                protected void _onError(String message) {
                    DataUtil.stopRefersh(homeRefersh);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取通知消息
     */
    private void queryNoticeAll() {
        ArrayList<String> titleList = new ArrayList<String>();
        noticeBeanList = new ArrayList<>();
        try {
            Api.getInstance().queryNoticeAll(getActivity()).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<NoticesBean>(getActivity()) {
                @Override
                protected void _onNext(NoticesBean bean) {
                    if (bean.getData() != null && bean.getData().size() > 0) {
                        noticeBeanList = bean.getData();
                        for (Notice notice : noticeBeanList) {
                            titleList.add(notice.getTitle() + "");
                        }
                        setTitleList(titleList);
                    } else {
                        titleList.add("");
                        setTitleList(titleList);
                    }
                }

                @Override
                protected void _onError(String message) {
                    Log.e("获取通知消息", message + "");
                    titleList.add(getResources().getString(R.string.app_name));
                    setTitleList(titleList);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTitleList(ArrayList list) {
        if (homeNotice != null)
            homeNotice.setTextList(list);
        if (noticeInt == 1) {
            homeNotice.setText(14, 1, getActivity().getResources().getColor(R.color.text));//设置属性
            homeNotice.setTextStillTime(3000);//设置停留时长间隔
            homeNotice.setAnimTime(300);//设置进入和退出的时间间隔
            handler.sendEmptyMessage(1);
        }
    }

    /**
     * 获取加密货币-涨幅、最高，最低等
     *
     * @param context
     * @param currency
     * @param mode
     */
    private void queryKline(Context context, String currency, int mode) {
        try {
            KlineApi.getHuobiDetails(context, currency, new KlineApiImpl() {
                @Override
                public void onSuccess(Object str) {
                    if (homeRefersh != null) {
                        HuoBiBean huoBiBean = new Gson().fromJson(str + "", HuoBiBean.class);
                        if (huoBiBean != null && huoBiBean.getTick() != null) {
                            HuoBi huoBi = huoBiBean.getTick();
                            String priceUs = DataUtil.doubleFour(huoBi.getClose());
                            double gain = huoBi.getClose() - huoBi.getOpen();//涨跌额
                            String gains = DataUtil.numberTwo(gain / huoBi.getOpen() * 100);//涨跌幅
                            String vol = DataUtil.transfers(context, huoBi.getVol());
                            String priceCny = DataUtil.doubleTwo(huoBi.getClose() * MainActivity.usd);
                            Market market = new Market(currency, vol, priceUs, priceCny, gain, gains);
                            market.setTokenId(DataUtil.setTokenId(currency, 0));
                            market.setMode(mode);
                            if (MarketStoreUtils.init(context).getMarket(currency) != null) {
                                MarketStoreUtils.init(context).deleteMarket(currency);
                            }
                            if (!MarketStoreUtils.init(context).addMarket(market)) {//如果本地存在该币种行情，删除添加新的行情
                                MarketStoreUtils.init(context).deleteMarket(currency);
                                MarketStoreUtils.init(context).addMarket(market);
                            }
                            setCurrencyPrice();
                        }
                        DataUtil.stopRefersh(homeRefersh);
                    }
                }

                @Override
                public void onError(Object str) {
                    if (homeRefersh != null) {
                        setCurrencyPrice();
                        DataUtil.stopRefersh(homeRefersh);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCurrencyPrice() {
        List<Market> list = MarketStoreUtils.init(getActivity()).getMarkets();
        if (list != null && list.size() > 0) {
            lists.clear();
            for (Market model : list) {
                if (model.getMode() == cfdTypeEnum.grade) {
                    lists.add(model);
                }
            }
            ListSort(lists);
        }
        adapter.refersh(lists);
    }

    @Override
    public void onResume() {
        super.onResume();
        StatusBarUtil.setFragmentStatusBar(getActivity(), R.color.white);
        if (noticeInt > 1)
            handler.sendEmptyMessage(1);
        flag = true;
        thread = new Thread(new MyThread());
        thread.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (homeNotice != null)
            homeNotice.stopAutoScroll();
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            thread = null;
        }
        flag = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            thread = null;
        }
        flag = false;
    }

    @OnClick({})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        }
    }

    public class MyThread implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (flag) {
                try {
                    Thread.sleep(30000);// 线程暂停，单位毫秒
                    Message message = new Message();
                    message.what = 10000002;
                    handler.sendMessage(message);// 发送消息
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {

    }

    public static void ListSort(List<Market> list) {
        Collections.sort(list, new Comparator<Market>() {
            @Override
            public int compare(Market o1, Market o2) {
                try {
                    if (o2.getTokenId() < o1.getTokenId()) {
                        return 1;
                    } else if (o2.getTokenId() > o1.getTokenId()) {
                        return -1;
                    } else {
                        return 0;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

}
