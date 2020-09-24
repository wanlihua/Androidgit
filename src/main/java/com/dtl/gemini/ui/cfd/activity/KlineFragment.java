package com.dtl.gemini.ui.cfd.activity;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dtl.gemini.MainActivity;
import com.dtl.gemini.R;
import com.dtl.gemini.base.BaseAppFragment;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.kline.KlineApi;
import com.dtl.gemini.kline.KlineApiImpl;
import com.dtl.gemini.kline.beans.HuoBiBean;
import com.dtl.gemini.kline.model.HuoBi;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.MdataUtils;
import com.dtl.gemini.widget.klineview.kline.KData;
import com.dtl.gemini.widget.klineview.kline.KLineView;
import com.dtl.gemini.widget.selectDialog.SelectDialog;
import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
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
 * 行情
 **/
public class KlineFragment extends BaseAppFragment implements View.OnClickListener {
    @Bind(R.id.curr_price_tv)
    TextView currPriceTv;
    @Bind(R.id.curr_price_iv)
    ImageView currPriceIv;
    @Bind(R.id.day_increase_tv)
    TextView dayIncreaseTv;
    @Bind(R.id.day_hight_tv)
    TextView dayHightTv;
    @Bind(R.id.day_low_tv)
    TextView dayLowTv;
    @Bind(R.id.day_vol_tv)
    TextView dayVolTv;
    @Bind(R.id.kline_date_tv)
    TextView klineDateTv;
    @Bind(R.id.kline_ma_tv)
    TextView klineMaTv;
    @Bind(R.id.kline_macd_tv)
    TextView klineMacdTv;
    @Bind(R.id.kline_view)
    KLineView klineView;
    @Bind(R.id.kine_progress_bar)
    ProgressBar kineProgressBar;
    @Bind(R.id.refersh)
    SmartRefreshLayout refersh;
    @Bind(R.id.type_ll)
    LinearLayout typeLl;

    List<KData> list1Kline, list2Kline;

    List<String> dateList, maList, macdList;
    String select, currency = "BTC", time = "1min";

    boolean flag = true;

    Thread thread;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 10000002:
                    queryPrice(getActivity());
                    break;
            }
        }
    };
    int refershStatus = 1, type = 1;

    String date1, date2, date3, date4, date5, date6, date7;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_kline;
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
        DataUtil.setRefershTheme(getActivity(), refersh);
        list1Kline = new ArrayList<>();
        list2Kline = new ArrayList<>();
        dateList = new ArrayList<>();
        date1 = getResources().getString(R.string.kline_1min);
        date2 = getResources().getString(R.string.kline_5min);
        date3 = getResources().getString(R.string.kline_30min);
        date4 = getResources().getString(R.string.kline_60min);
        date5 = getResources().getString(R.string.kline_1day);
        date6 = getResources().getString(R.string.kline_1week);
        date7 = getResources().getString(R.string.kline_1month);
        dateList.add(date1);
        dateList.add(date2);
        dateList.add(date3);
        dateList.add(date4);
        dateList.add(date5);
        dateList.add(date6);
        dateList.add(date7);
        maList = new ArrayList<>();
        maList.add("MA");
        maList.add("EMA");
        maList.add("BOLL");
        macdList = new ArrayList<>();
        macdList.add("MACD");
        macdList.add("KDJ");
        macdList.add("RSI");
        queryPrice(getActivity());
    }

    private void bindListener() {
        refersh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refershStatus = 1;
                kineProgressBar.setVisibility(View.VISIBLE);
                queryPrice(getActivity());
            }
        });
    }

    private void setTimeTab() {
        kineProgressBar.setVisibility(View.VISIBLE);
        String date = klineDateTv.getText().toString().trim();
        if (date.equals(date1)) {
            time = "1min";
        } else if (date.equals(date2)) {
            time = "5min";
        } else if (date.equals(date3)) {
            time = "30min";
        } else if (date.equals(date4)) {
            time = "60min";
        } else if (date.equals(date5)) {
            time = "1day";
        } else if (date.equals(date6)) {
            time = "1week";
        } else if (date.equals(date7)) {
            time = "1mon";
        }
        queryKline(getActivity());
    }

    private void setTypeTab() {
        String ma = klineMaTv.getText().toString().trim();
        String macd = klineMacdTv.getText().toString().trim();
        if (ma.equals("MA")) {
            //主图展示MA
            klineView.setMainImgType(KLineView.MAIN_IMG_MA);
        } else if (ma.equals("EMA")) {
            //主图展示EMA
            klineView.setMainImgType(KLineView.MAIN_IMG_EMA);
        } else if (ma.equals("BOLL")) {
            //主图展示BOLL
            klineView.setMainImgType(KLineView.MAIN_IMG_BOLL);
        }
        if (macd.equals("MACD")) {
            //副图展示MACD
            klineView.setDeputyImgType(KLineView.DEPUTY_IMG_MACD);
        } else if (macd.equals("KDJ")) {
            //副图展示KDJ
            klineView.setDeputyImgType(KLineView.DEPUTY_IMG_KDJ);
        } else if (macd.equals("RSI")) {
            //副图展示RSI
            klineView.setDeputyImgType(KLineView.DEPUTY_IMG_RSI);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {

    }

    /**
     * 获取加密货币-涨幅、最高，最低等
     *
     * @param context
     */
    /**
     * 获取加密货币-涨幅、最高，最低等
     *
     * @param context
     */
    private void queryPrice(Context context) {
        try {
            KlineApi.getHuobiDetails(context, currency, new KlineApiImpl() {
                @Override
                public void onSuccess(Object str) {
                    if (currPriceTv != null) {
                        HuoBiBean huoBiBean = new Gson().fromJson(str + "", HuoBiBean.class);
                        if (huoBiBean != null && huoBiBean.getTick() != null) {
                            HuoBi huoBi = huoBiBean.getTick();
                            String priceUs = DataUtil.doubleTwo(huoBi.getClose());
                            double gain = huoBi.getClose() - huoBi.getOpen();//涨跌额
                            String gains = DataUtil.numberTwo(gain / huoBi.getOpen() * 100);//涨跌幅
                            String hight = DataUtil.doubleTwo(huoBi.getHigh());
                            String low = DataUtil.doubleTwo(huoBi.getLow());
                            String vol = DataUtil.transfers(context, huoBi.getVol());
                            String priceCny = DataUtil.doubleTwo(huoBi.getClose() * MainActivity.usd);
                            currPriceTv.setText(priceUs);
                            dayHightTv.setText(hight);
                            dayLowTv.setText(low);
                            dayVolTv.setText(vol);
                            if (gain > 0) {
                                dayIncreaseTv.setText("+" + gains + "%");
                                currPriceTv.setTextColor(getResources().getColor(R.color.zhang));
                                currPriceIv.setImageResource(R.mipmap.icon_kline_up);
                                dayIncreaseTv.setBackgroundResource(R.drawable.item_coin_asset_green);
                            } else {
                                dayIncreaseTv.setText(gains + "%");
                                currPriceTv.setTextColor(getResources().getColor(R.color.die));
                                currPriceIv.setImageResource(R.mipmap.icon_kline_down);
                                dayIncreaseTv.setBackgroundResource(R.drawable.item_coin_asset_red);
                            }
                        }
                    }
                }

                @Override
                public void onError(Object str) {

                }
            });
            if (refershStatus == 1)
                queryKline(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void queryPrice(Context context) {
//        try {
//            KlineApi.queryBiAnKlineOne(context, currency, "1day", new KlineApiImpl() {
//                @Override
//                public void onSuccess(Object str) {
//                    if (currPriceTv != null) {
//                        JSONArray array = JSON.parseArray(str.toString());
//                        if (array.size() > 0) {
//                            JSONArray kline = (JSONArray) array.get(0);
//                            long time = (long) kline.get(0);
//                            double open = Double.parseDouble((String) kline.get(1));
//                            double close = Double.parseDouble((String) kline.get(4));
//                            double height = Double.parseDouble((String) kline.get(2));
//                            double low = Double.parseDouble((String) kline.get(3));
//                            double vol = Double.parseDouble((String) kline.get(5));
//                            String priceUs = DataUtil.returnCurrencyDouble(currency, close);
//                            double gain = close - open;//涨跌额
//                            String gains = DataUtil.numberTwo(gain / open * 100);//涨跌幅
//                            String hights = DataUtil.returnCurrencyDouble(currency, height);
//                            String lows = DataUtil.returnCurrencyDouble(currency, low);
//                            String vols = DataUtil.transfers(context, vol);
//                            currPriceTv.setText(priceUs);
//                            dayHightTv.setText(hights);
//                            dayLowTv.setText(lows);
//                            dayVolTv.setText(vols);
//                            if (gain > 0) {
//                                dayIncreaseTv.setText("+" + gains + "%");
//                                currPriceTv.setTextColor(getResources().getColor(R.color.zhang));
//                                currPriceIv.setImageResource(R.mipmap.icon_kline_up);
//                                dayIncreaseTv.setBackgroundResource(R.drawable.item_coin_asset_green);
//                            } else {
//                                dayIncreaseTv.setText(gains + "%");
//                                currPriceTv.setTextColor(getResources().getColor(R.color.die));
//                                currPriceIv.setImageResource(R.mipmap.icon_kline_down);
//                                dayIncreaseTv.setBackgroundResource(R.drawable.item_coin_asset_red);
//                            }
//                        }
//                    }
//                }
//
//                @Override
//                public void onError(Object str) {
//                }
//            });
//            if (refershStatus == 1)
//                queryKline(context);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 获取K线数据--虚拟货币
     *
     * @param context
     */
    private void queryKline(Context context) {
        try {
            KlineApi.getHuobiKline(context, currency, time, Constant.KLINE_SIZE, new KlineApiImpl() {
                @Override
                public void onSuccess(Object str) {
                    if (kineProgressBar != null) {
                        HuoBiBean huoBiBean = new Gson().fromJson(str + "", HuoBiBean.class);
                        if (huoBiBean.getData() != null && huoBiBean.getData().size() > 0) {
                            if (list1Kline != null)
                                list1Kline.clear();
                            if (list2Kline != null)
                                list2Kline.clear();
                            for (int i = huoBiBean.getData().size() - 1; i > huoBiBean.getData().size() / 2 - 1; i--) {
                                HuoBi huoBi = huoBiBean.getData().get(i);
                                long time = huoBi.getId() * 1000;
                                double open = huoBi.getOpen();
                                double close = huoBi.getClose();
                                double height = huoBi.getHigh();
                                double low = huoBi.getLow();
                                double vol = huoBi.getAmount();
                                list2Kline.add(new KData(time, open, close, height, low, vol));
                            }
                            for (int i = huoBiBean.getData().size() / 2 - 1; i > -1; i--) {
                                HuoBi huoBi = huoBiBean.getData().get(i);
                                long time = huoBi.getId() * 1000;
                                double open = huoBi.getOpen();
                                double close = huoBi.getClose();
                                double height = huoBi.getHigh();
                                double low = huoBi.getLow();
                                double vol = huoBi.getAmount();
                                list1Kline.add(new KData(time, open, close, height, low, vol));
                            }
                            if (klineView != null) {
                                //初始化控件加载数据
                                klineView.initKDataList(list1Kline);
                                klineView.addPreDataList(list2Kline, false);
                                //设置十字线移动模式，默认为0：固定指向收盘价
                                klineView.setCrossHairMoveMode(KLineView.CROSS_HAIR_MOVE_CLOSE);
                                //是否显示副图
                                klineView.setDeputyPicShow(true);
                            }
                        }
                        kineProgressBar.setVisibility(View.GONE);
                        refershStatus++;
                        DataUtil.stopRefersh(refersh);
                    }
                }

                @Override
                public void onError(Object str) {
                    if (kineProgressBar != null) {
                        kineProgressBar.setVisibility(View.GONE);
                        DataUtil.stopRefersh(refersh);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void queryKline(Context context) {
//        try {
//            KlineApi.queryBiAnKline(context, currency, time, new KlineApiImpl() {
//                @Override
//                public void onSuccess(Object str) {
//                    if (kineProgressBar != null) {
//                        JSONArray array = JSON.parseArray(str.toString());
//                        if (array.size() > 6) {
//                            if (list1Kline != null)
//                                list1Kline.clear();
//                            if (list2Kline != null)
//                                list2Kline.clear();
//                            for (int i = array.size() - 1; i > array.size() / 2 - 1; i--) {
//                                JSONArray array1 = (JSONArray) array.get(i);
//                                long time = (long) array1.get(0);
//                                double open = Double.parseDouble((String) array1.get(1));
//                                double close = Double.parseDouble((String) array1.get(4));
//                                double height = Double.parseDouble((String) array1.get(2));
//                                double low = Double.parseDouble((String) array1.get(3));
//                                double vol = Double.parseDouble((String) array1.get(5));
//                                list2Kline.add(new KData(time, open, close, height, low, vol));
//                            }
//                            for (int i = array.size() / 2 - 1; i > -1; i--) {
//                                JSONArray array1 = (JSONArray) array.get(i);
//                                long time = (long) array1.get(0);
//                                double open = Double.parseDouble((String) array1.get(1));
//                                double close = Double.parseDouble((String) array1.get(4));
//                                double height = Double.parseDouble((String) array1.get(2));
//                                double low = Double.parseDouble((String) array1.get(3));
//                                double vol = Double.parseDouble((String) array1.get(5));
//                                list1Kline.add(new KData(time, open, close, height, low, vol));
//                            }
//                            if (klineView != null) {
//                                ListTimerUp(list1Kline);
//                                ListTimerUp(list2Kline);
//                                //初始化控件加载数据
//                                klineView.initKDataList(list2Kline);
////                            klvMain.addSingleData(listMultiple.get(0));
//                                klineView.addPreDataList(list1Kline, false);
//                                //设置十字线移动模式，默认为0：固定指向收盘价
//                                klineView.setCrossHairMoveMode(KLineView.CROSS_HAIR_MOVE_CLOSE);
//                                //是否显示副图
//                                klineView.setDeputyPicShow(true);
//                            }
//                        }
//                        kineProgressBar.setVisibility(View.GONE);
//                        refershStatus++;
//                        DataUtil.stopRefersh(refersh);
//                    }
//                }
//
//                @Override
//                public void onError(Object str) {
//                    if (kineProgressBar != null) {
//                        kineProgressBar.setVisibility(View.GONE);
//                        DataUtil.stopRefersh(refersh);
//                    }
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    private void showDialog(List<String> list) {
        SelectDialog dialog = new SelectDialog(getActivity(), list, select, 2);
        if (list == null || list.size() == 0)
            return;
        dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                dialog.dismiss();
                if (type == 1) {
                    klineDateTv.setText(list.get(position));
                    setTimeTab();
                } else if (type == 2) {
                    klineMaTv.setText(list.get(position));
                    setTypeTab();
                } else if (type == 3) {
                    klineMacdTv.setText(list.get(position));
                    setTypeTab();
                }
            }
        });

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        //获取通知栏高度  重要的在这，获取到通知栏高度
        int notificationBar = Resources.getSystem().getDimensionPixelSize(
                Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
        //获取控件 的绝对坐标,( y 轴坐标是控件上部到屏幕最顶部（不包括控件本身）)
        //location [0] 为x绝对坐标;location [1] 为y绝对坐标
        int[] location = new int[2];
        typeLl.getLocationInWindow(location); //获取在当前窗体内的绝对坐标
        typeLl.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
        wlp.x = 0;//对 dialog 设置 x 轴坐标
        wlp.y = location[1] + typeLl.getHeight(); //对dialog设置y轴坐标
        wlp.gravity = Gravity.TOP | Gravity.CENTER;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wlp);
        dialog.show();
    }

    @OnClick({R.id.kline_date_tv, R.id.kline_ma_tv, R.id.kline_macd_tv, R.id.kline_max_iv, R.id.speed_open_rise_btn, R.id.speed_open_fall_btn})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.kline_date_tv:
                if (DataUtil.isFastClick()) {
                    type = 1;
                    if (dateList != null && dateList.size() > 0)
                        showDialog(dateList);
                }
                break;
            case R.id.kline_ma_tv:
                if (DataUtil.isFastClick()) {
                    type = 2;
                    if (maList != null && maList.size() > 0)
                        showDialog(maList);
                }
                break;
            case R.id.kline_macd_tv:
                if (DataUtil.isFastClick()) {
                    type = 3;
                    if (macdList != null && macdList.size() > 0)
                        showDialog(macdList);
                }
                break;
            case R.id.kline_max_iv:
                if (DataUtil.isFastClick()) {

                }
                break;
            case R.id.speed_open_rise_btn:
            case R.id.speed_open_fall_btn:
                if (DataUtil.isFastClick()) {
                    MData mData = new MData();
                    mData.type = MdataUtils.CFD_KLINE_TO_TRANSACTION;
                    EventBus.getDefault().post(mData);
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        DataUtil.setRefershNoLoad(refersh);
        flag = true;
        thread = new Thread(new MyThread());
        thread.start();
    }

    public class MyThread implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (flag) {
                try {
                    Thread.sleep(2000);// 线程暂停，单位毫秒
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

    @Override
    public void onPause() {
        super.onPause();
        if (thread != null) {
            flag = false;
            thread.interrupt();
        }
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            thread = null;
        }
        flag = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (thread != null) {
            flag = false;
            thread.interrupt();
        }
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            thread = null;
        }
        flag = false;
    }

    private void ListTimerUp(List<KData> list) {
        Collections.sort(list, new Comparator<KData>() {
            @Override
            public int compare(KData o1, KData o2) {
                try {
                    if (o2.getTime() < o1.getTime()) {
                        return 1;
                    } else if (o2.getTime() > o1.getTime()) {
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
