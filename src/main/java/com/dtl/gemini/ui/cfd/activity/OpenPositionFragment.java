package com.dtl.gemini.ui.cfd.activity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dtl.gemini.MainActivity;
import com.dtl.gemini.R;
import com.dtl.gemini.api.Api;
import com.dtl.gemini.base.BaseAppFragment;
import com.dtl.gemini.bean.BaseBean;
import com.dtl.gemini.common.baserx.RxSchedulers;
import com.dtl.gemini.common.baserx.RxSubscriber;
import com.dtl.gemini.common.commonwidget.CustomProgressDialog;
import com.dtl.gemini.enums.CfdTypeEnum;
import com.dtl.gemini.enums.TokenEnum;
import com.dtl.gemini.kline.KlineApi;
import com.dtl.gemini.kline.KlineApiImpl;
import com.dtl.gemini.kline.beans.ContractBean;
import com.dtl.gemini.kline.beans.DepthV2Bean;
import com.dtl.gemini.kline.model.Contract;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.ui.asset.beans.AssetCfdBean;
import com.dtl.gemini.ui.asset.model.AssetsCfd;
import com.dtl.gemini.ui.cfd.adapter.BuyAdapter;
import com.dtl.gemini.ui.cfd.adapter.DelegateRecordAdapter;
import com.dtl.gemini.ui.cfd.adapter.SellAdapter;
import com.dtl.gemini.ui.cfd.beans.CurrPriceBean;
import com.dtl.gemini.ui.cfd.beans.MultipleBean;
import com.dtl.gemini.ui.cfd.beans.OrderPenddingBean;
import com.dtl.gemini.ui.cfd.model.CfdDepth;
import com.dtl.gemini.ui.cfd.model.OrderCreate;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.DialogUtils;
import com.dtl.gemini.utils.MdataUtils;
import com.dtl.gemini.utils.StatusBarUtil;
import com.dtl.gemini.widget.AutoWheelChoicePopup;
import com.dtl.gemini.widget.MyListView;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 开仓
 *
 * @author DTL
 * @date 2020/4/11
 **/
public class OpenPositionFragment extends BaseAppFragment implements View.OnClickListener {

    @Bind(R.id.speed_position_tv)
    TextView speedPositionTv;
    @Bind(R.id.plan_position_tv)
    TextView planPositionTv;
    @Bind(R.id.multiple_text)
    TextView multipleText;
    @Bind(R.id.plan_price_text)
    EditText planPriceText;
    @Bind(R.id.plan_price_ll)
    LinearLayout planPriceLl;
    @Bind(R.id.number_text)
    EditText numberText;
    /**
     * 可开
     */
    @Bind(R.id.can_open_number_tv) //S数量可开仓数量
    TextView canOpenNumberTv;
    @Bind(R.id.cfd_seekbar)
    SeekBar cfdSeekbar;
    @Bind(R.id.transaction_number_tv)
    TextView transactionNumberTv;
    @Bind(R.id.stop_loss_tv)
    EditText stopLossTv;
    @Bind(R.id.stop_profit_tv)
    EditText stopProfitTv;
    @Bind(R.id.plan_open_rise_btn)
    Button planOpenRiseBtn;
    @Bind(R.id.plan_open_fall_btn)
    Button planOpenFallBtn;
    @Bind(R.id.index_price_tv)
    TextView indexPriceTv;
    @Bind(R.id.plan_open_fall_list)
    MyListView planOpenFallList;
    @Bind(R.id.curr_price_tv)
    TextView currPriceTv;
    @Bind(R.id.curr_price_cny_tv)
    TextView currPriceCnyTv;
    @Bind(R.id.plan_open_rise_list)
    MyListView planOpenRiseList;
    @Bind(R.id.delegate_record_list)
    MyListView delegateRecordList;
    @Bind(R.id.cfd_fee_tv)//手续费
    TextView cfdFeeTv;

    int type = 1;//1:极速开仓,2:计划开仓
    int numCount = 1, seekbarCount = 1, multipleCount = 1;
    int seekbarAgain = 10000, minAmount = 100;
    AutoWheelChoicePopup popup;
    public static List<String> listMultiple = new ArrayList<>();

    DelegateRecordAdapter delegateRecordAdapter;
    List<OrderCreate> orderCreateList;
    BuyAdapter buyAdapter;
    SellAdapter sellAdapter;
    public static AssetsCfd assetsCfd;

    public static String currPrice = "0.00";

    List<CfdDepth> buyList = new ArrayList<>();
    List<CfdDepth> sellList = new ArrayList<>();

    private boolean confirmStatus = true;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 10000001:
                    refershTimer();
                    break;
            }
        }
    };


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_cfd_open;
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
        setEnble();
        cfdSeekbar.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_pro_bg));
        cfdSeekbar.setMax(seekbarAgain);
        delegateRecordAdapter = new DelegateRecordAdapter(getActivity());
        delegateRecordList.setAdapter(delegateRecordAdapter);
        buyAdapter = new BuyAdapter(getActivity(), buyList);
        sellAdapter = new SellAdapter(getActivity(), sellList);
        planOpenFallList.setAdapter(sellAdapter);
        planOpenRiseList.setAdapter(buyAdapter);
        queryCfdMultiple(getActivity());
    }

    private void setEnble() {
        planOpenRiseBtn.setBackgroundResource(R.drawable.item_coin_asset_green);
        planOpenFallBtn.setBackgroundResource(R.drawable.item_coin_asset_red);
        planOpenRiseBtn.setEnabled(true);
        planOpenRiseBtn.setClickable(true);
        planOpenFallBtn.setEnabled(true);
        planOpenFallBtn.setClickable(true);
        if (CfdFragment.cfdTypeEnum == CfdTypeEnum.DOUBLE && CfdFragment.cfdToken != null) {
            if (CfdFragment.cfdToken.getTrend() == 1) {//买涨 --禁掉开空
                planOpenFallBtn.setBackgroundResource(R.drawable.item_coin_asset_gray);
                planOpenFallBtn.setEnabled(false);
                planOpenFallBtn.setClickable(false);
            } else if (CfdFragment.cfdToken.getTrend() == 2) {//买跌 --禁掉开多
                planOpenRiseBtn.setBackgroundResource(R.drawable.item_coin_asset_gray);
                planOpenRiseBtn.setEnabled(false);
                planOpenRiseBtn.setClickable(false);
            } else if (CfdFragment.cfdToken.getTrend() == 4) {//多、空都不能
                planOpenFallBtn.setBackgroundResource(R.drawable.item_coin_asset_gray);
                planOpenFallBtn.setEnabled(false);
                planOpenFallBtn.setClickable(false);
                planOpenRiseBtn.setBackgroundResource(R.drawable.item_coin_asset_gray);
                planOpenRiseBtn.setEnabled(false);
                planOpenRiseBtn.setClickable(false);
            } else if (CfdFragment.cfdToken.getTrend() == 3) {//多、空都能

            }
        }
    }

    private void initPopupMultiple() {
        if (listMultiple == null || listMultiple.size() == 0)
            return;
        popup = new AutoWheelChoicePopup(getActivity(), listMultiple);
        popup.setBtnOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multipleText.setText(popup.getSelecedData());
                setCanOpenNumber();
                setTransactionNumber();
                popup.dismiss();
            }
        });
    }

    private void bindListener() {

        planPriceText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                DataUtil.checkEditInputNumber(planPriceText);
                setCanOpenNumber();
                setTransactionNumber();
            }
        });

        numberText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                DataUtil.checkEditInputNumber(numberText);
                if (numCount == 1) {
                    seekbarCount++;
                    int pr1 = (int) (returnOpenNumber() / returnCanOpenNumber() * seekbarAgain);
//                    if (returnCanOpenNumber() < minAmount)
//                        pr1 = (int) (returnOpenNumber() * seekbarAgain);
//                    else
//                        pr1 = (int) returnOpenNumber();
                    cfdSeekbar.setProgress(pr1);
                }
                seekbarCount = 1;
                setTransactionNumber();
            }
        });

        stopLossTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                DataUtil.checkEditInputNumber(stopLossTv);
            }
        });

        stopProfitTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                DataUtil.checkEditInputNumber(stopProfitTv);
            }
        });

        cfdSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekbarCount == 1) {
                    numCount++;
                    if (progress == 0) {
                        numberText.setText(DataUtil.doubleFour(0.0));
                    } else if (progress == cfdSeekbar.getMax()) {
                        numberText.setText(DataUtil.doubleFour(returnCanOpenNumber()));
                    } else {
                        double agni = (double) progress / cfdSeekbar.getMax();
                        String number = DataUtil.doubleFour(agni * returnCanOpenNumber());
                        numberText.setText(number);
                    }
                }
                numCount = 1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                numCount = 1;
            }
        });

        delegateRecordAdapter.setCancelListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (DataUtil.isFastClick()) {
                    if (orderCreateList != null && orderCreateList.size() > 0) {
                        DialogUtils.showConfirmDialog(getActivity(), null, getResources().getString(R.string.hint), getResources().getString(R.string.cancel_order_hint));
                        DialogUtils.btn(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (DataUtil.isFastClick()) {
                                    DialogUtils.dismissDialog();
                                    cfdOrderCancel(getActivity(), orderCreateList.get(i).getId());
                                }
                            }
                        });
                    }
                }
            }
        });

    }

    private void refershTimer() {
        queryCfdCurrPrice(getActivity(), CfdFragment.currency);
    }

    private void setTab(int type) {
        this.type = type;
        String plan_open_rise = getResources().getString(R.string.plan_open_rise);
        String plan_open_fall = getResources().getString(R.string.plan_open_fall);
        String speed_open_rise = getResources().getString(R.string.speed_open_rise);
        String speed_open_fall = getResources().getString(R.string.speed_open_fall);
        if (type == 1) {
            //极速开仓
            planPriceText.setText("");
            planPriceLl.setVisibility(View.GONE);
            speedPositionTv.setBackgroundResource(R.drawable.login_btn_bg);
            speedPositionTv.setTextColor(getResources().getColor(R.color.white));
            planPositionTv.setBackgroundResource(R.color.transparent);
            planPositionTv.setTextColor(getResources().getColor(R.color.text1));
            planOpenRiseBtn.setText(speed_open_rise);
            planOpenFallBtn.setText(speed_open_fall);
        } else {
            //计划开仓
            planPriceText.setText(returnCurrPrice() + "");
            planPriceLl.setVisibility(View.VISIBLE);
            planPositionTv.setBackgroundResource(R.drawable.login_btn_bg);
            planPositionTv.setTextColor(getResources().getColor(R.color.white));
            speedPositionTv.setBackgroundResource(R.color.transparent);
            speedPositionTv.setTextColor(getResources().getColor(R.color.text1));
            planOpenRiseBtn.setText(plan_open_rise);
            planOpenFallBtn.setText(plan_open_fall);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {
        switch (mData.type) {
            case MdataUtils.CFD_OPEN_REFERSH:
                queryCfdMultiple(getActivity());
                //刷新
                break;
            case MdataUtils.CFD_OPEN_LOAD:
                //加载
                break;
            case MdataUtils.CFD_TOKEN:
                numberText.setHint(CfdFragment.currency);
                queryCfdAsset(getActivity());
                setEnble();
                break;
        }
    }


    /**
     * 设置交易量(开仓使用USDT)
     */
    private double setTransactionNumber() {
//        //开仓使用USDT=开仓数量*开仓价格/开仓倍数    +  开仓数量*开仓价格*手续费比例
//        double transactionNumber = returnOpenNumber() * returnOpenPrice() / returnMultiple() + returnOpenNumber() * returnOpenPrice() * returnFeeScale();
        double transactionNumber = returnOpenNumber() * returnOpenPrice() / returnMultiple();
        //开仓使用USDT = 开仓百分比*可用
//        double transactionNumber = (double) cfdSeekbar.getProgress() / seekbarAgain * returnUsableNumber();
        transactionNumberTv.setText(DataUtil.doubleFour(transactionNumber) + " " + TokenEnum.USDT.name());
        return transactionNumber;
    }

    /**
     * 设置可开数量
     */
    private void setCanOpenNumber() {
//        canOpenNumberTv.setText("0.00456 " + CfdFragment.currency);

        canOpenNumberTv.setText("0.09884 " + CfdFragment.currency);
        //可开仓数量=合约账户余额 /（开仓价格/杠杆倍数+开仓价格*手续费比例）
        //可开仓数量=（合约可用余额-可用余额*手续费比例*倍数）*倍数/开仓价格
        String canOpenNumber = DataUtil.doubleFour((returnUsableNumber() - returnUsableNumber() * returnFeeScale() * returnMultiple()) * returnMultiple() / returnOpenPrice());
        if (!DataUtil.isNumeric(canOpenNumber)) {
            return;
        }
//        canOpenNumberTv.setText(canOpenNumber + " " + CfdFragment.currency);      //这一行设置的数目要是去除注释就是-0000 跟着接口走


//        if (Double.parseDouble(canOpenNumber) > 1.00 && Integer.parseInt(canOpenNumber) >= minAmount) {
//            cfdSeekbar.setMax(Integer.parseInt(canOpenNumber));
//        } else {
//            cfdSeekbar.setMax(Integer.parseInt(canOpenNumber) * seekbarAgain);
//        }
        if (returnCanOpenNumber() > 0.0) {
            seekbarCount++;
        }
        int progress = (int) (returnOpenNumber() / Double.parseDouble(canOpenNumber) * seekbarAgain);
        cfdSeekbar.setProgress(progress);
        seekbarCount = 1;
    }

    /**
     * 获取手续费比例
     *
     * @return
     */
    private double returnFeeScale() {
        String feeScales = cfdFeeTv.getText().toString().replaceAll(getResources().getString(R.string.fee) + ":", "").trim();
        double feeScale = 0.00;
        if (feeScales != null && !feeScales.equals("") && DataUtil.isNumeric(feeScales)) {
            feeScale = Double.parseDouble(feeScales);
        }
        return feeScale;
    }

    /**
     * 获取倍数
     *
     * @return
     */
    private int returnMultiple() {
        String multiples = multipleText.getText().toString().replaceAll("X", "").trim();
        int multiple = 10;
        if (multiples != null && !multiples.equals("") && DataUtil.isNumeric(multiples)) {
            multiple = Integer.parseInt(multiples);
        }
        return multiple;
    }

    /**
     * 获取开仓价格
     *
     * @return
     */
    private double returnOpenPrice() {
        String prices = "0.00";
        if (type == 1) {
            prices = currPriceTv.getText().toString().replaceAll("USDT", "").trim();
        } else {
            prices = planPriceText.getText().toString().trim();
        }
        double price = 0.00;
        if (prices != null && !prices.equals("") && DataUtil.isNumeric(prices)) {
            price = Double.parseDouble(prices);
        }
        return price;
    }

    /**
     * 获取实时价格
     *
     * @return
     */
    private double returnCurrPrice() {
        String prices = currPriceTv.getText().toString().replaceAll("USDT", "").trim();
        double price = 0.00;
        if (prices != null && !prices.equals("") && DataUtil.isNumeric(prices)) {
            price = Double.parseDouble(prices);
        }
        return price;
    }

    /**
     * 获取开仓数量
     *    开仓数量在这边
     * @return
     */
    private double returnOpenNumber() {
        String openNumbers = numberText.getText().toString().trim();
        double openNumber = 0.00;
        if (openNumbers != null && !openNumbers.equals("") && DataUtil.isNumeric(openNumbers)) {
            openNumber = Double.parseDouble(openNumbers);
        }
        return openNumber;
    }

    /**
     * 获取可开数量
     *  canOpenNumbers
     * @return
     */
    //11111
    private double returnCanOpenNumber() {
        String canOpenNumbers = canOpenNumberTv.getText().toString().replaceAll(CfdFragment.currency, "").trim();
        double canOpenNumber = 0.00;
        if (canOpenNumbers != null && !canOpenNumbers.equals("") && DataUtil.isNumeric(canOpenNumbers)) {
            canOpenNumber = Double.parseDouble(canOpenNumbers);
        }
        return canOpenNumber;
    }




    /**
     * 获取可用余额
     *
     * @return
     */
    private double returnUsableNumber() {
        double usable = 0.00;
        if (assetsCfd != null) {
            usable = assetsCfd.getUseableAmount().doubleValue();
        }
        return usable;
    }

    /**
     * 获取止损价格
     *
     * @return
     */
    private double returnStopLossPrice() {
        String stopLosss = stopLossTv.getText().toString().trim();
        double stopLoss = 0.00;
        if (stopLosss != null && !stopLosss.equals("") && DataUtil.isNumeric(stopLosss)) {
            stopLoss = Double.parseDouble(stopLosss);
        }
        return stopLoss;
    }

    /**
     * 获取止盈价格
     *
     * @return
     */
    private double returnStopProfitPrice() {
        String stopProfits = stopProfitTv.getText().toString().trim();
        double stopProfit = 0.00;
        if (stopProfits != null && !stopProfits.equals("") && DataUtil.isNumeric(stopProfits)) {
            stopProfit = Double.parseDouble(stopProfits);
        }
        return stopProfit;
    }

    /**
     * 获取火币合约指数信息
     *
     * @param context
     */
    private void getHuobiContract(Context context) {
        try {
            KlineApi.getHuobiContract(context, CfdFragment.currency, new KlineApiImpl() {
                @Override
                public void onSuccess(Object str) {
                    if (indexPriceTv != null) {
                        ContractBean contractBean = new Gson().fromJson(str + "", ContractBean.class);
                        if (contractBean != null && contractBean.getData() != null && contractBean.getData().size() > 0) {
                            Contract contract = contractBean.getData().get(0);
                            indexPriceTv.setText(DataUtil.doubleFour(contract.getIndex_price()) + " USDT");
                        }
                    }
                }

                @Override
                public void onError(Object str) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取火币合约深度信息
     *
     * @param context
     */
    private void getHuobiContractDepth(Context context) {
        try {
            KlineApi.getHuobiContractDepthV2(context, CfdFragment.currency, "step0", new KlineApiImpl() {
                @Override
                public void onSuccess(Object str) {
                    if (buyAdapter != null) {
                        DepthV2Bean depthBean = new Gson().fromJson(str + "", DepthV2Bean.class);
                        if (depthBean != null && depthBean.getTick() != null) {
                            DepthV2Bean.TickBean depth = depthBean.getTick();
                            if (depth.getAsks().size() > 0) {//卖盘 开空
                                if (sellList != null)
                                    sellList.clear();
                                List<Double> array = new ArrayList<>();
                                for (int i = 0; i < depth.getAsks().size(); i++) {
                                    if (i > 4) {
                                        break;
                                    }
                                    List<Double> list = depth.getAsks().get(i);
                                    double price = list.get(0);
//                                    double amount = list.get(1) * 100 / price;
                                    double amount = list.get(1);
                                    CfdDepth depth1 = new CfdDepth(price, amount);
                                    array.add(amount);
                                    sellList.add(depth1);
                                }
                                sellAdapter.refersh(sellList, DataUtil.returnMaxNumber(array));
                            }
                            if (depth.getBids().size() > 0) {//买盘 开多
                                if (buyList != null)
                                    buyList.clear();
                                List<Double> array = new ArrayList<>();
                                for (int i = 0; i < depth.getBids().size(); i++) {
                                    if (i > 4) {
                                        break;
                                    }
                                    List<Double> list = depth.getBids().get(i);
                                    double price = list.get(0);
//                                    double amount = list.get(1) * 100 / price;
                                    double amount = list.get(1);
                                    CfdDepth depth1 = new CfdDepth(price, amount);
                                    array.add(amount);
                                    buyList.add(depth1);
                                }
                                buyAdapter.refersh(buyList, DataUtil.returnMaxNumber(array));
                            }
                        }
                    }
                }

                @Override
                public void onError(Object str) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 查询可开倍数
     */
    private void queryCfdMultiple(Context context) {
        try {
            Api.getInstance().queryCfdMultiple(context, CfdFragment.cfdTypeEnum).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<MultipleBean>(context) {
                @Override
                protected void _onNext(MultipleBean bean) {
                    if (bean != null && bean.getData().size() > 0) {
                        if (listMultiple != null)
                            listMultiple.clear();
                        for (String s : bean.getData()) {
                            listMultiple.add(s + " X");
                        }
                        MData mData = new MData();
                        mData.type = MdataUtils.CFD_MULTIPLE;
                        EventBus.getDefault().post(mData);
                        if (multipleCount == 1)
                            multipleText.setText(listMultiple.get(0));
                    }
                    initPopupMultiple();
                    queryCfdFee(context);
                    queryCfdAsset(context);
                    multipleCount++;
                }

                @Override
                protected void _onError(String message) {
                    Log.e("查询可开倍数", message + "");
                    queryCfdFee(context);
                    queryCfdAsset(context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查手续费
     *
     * @param context
     */
    private void queryCfdFee(Context context) {
        try {
            Api.getInstance().queryCfdFee(context, CfdFragment.cfdTypeEnum).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<CurrPriceBean>(context) {
                @Override
                protected void _onNext(CurrPriceBean bean) {
                    if (bean != null) {
                        if (cfdFeeTv != null) {
                            String fee = bean.getMassage();
                            if (fee != null && !fee.equals("")) {
                                fee = new BigDecimal(fee).stripTrailingZeros().toPlainString();
                            }
                            cfdFeeTv.setText(getResources().getString(R.string.fee) + ":" + fee);
                        }
                    }
                    queryCfdCurrPrice(context, CfdFragment.currency);
                    queryCfdOrderPendding(context);
                }

                @Override
                protected void _onError(String message) {
                    Log.e("查手续费", message + "");
                    queryCfdCurrPrice(context, CfdFragment.currency);
                    queryCfdOrderPendding(context);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取实时价格
     *
     * @param context
     */
    private void queryCfdCurrPrice(Context context, String currency) {
        try {
            getHuobiContract(getActivity());
            getHuobiContractDepth(getActivity());
            Api.getInstance().queryCfdCurrPrice(context, currency, CfdFragment.cfdTypeEnum).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<CurrPriceBean>(context) {
                @Override
                protected void _onNext(CurrPriceBean bean) {
                    if (bean != null) {
                        String priceUs = DataUtil.doubleFour(Double.parseDouble(bean.getMassage()));
                        String priceCny = DataUtil.returnCurrencyDouble(currency, (Double.parseDouble(bean.getMassage()) * MainActivity.usd));
                        if (currPriceCnyTv != null) {
                            currPrice = priceUs;
                            currPriceTv.setText(priceUs + " USDT");
                            currPriceCnyTv.setText(priceCny + " CNY");
//                            double gain = bean.getData().get(0).getClose().doubleValue() - bean.getData().get(0).getOpen().doubleValue();//涨跌额
//                            String gains = DataUtil.numberTwo(gain / bean.getData().get(0).getOpen().doubleValue() * 100);//涨跌幅
//                            if (gain > 0) {
//                                currPriceCnyTv.setTextColor(getResources().getColor(R.color.zhang));
//                            } else if (gain < 0) {
//                                currPriceCnyTv.setTextColor(getResources().getColor(R.color.die));
//                            } else {
//                                currPriceCnyTv.setTextColor(getResources().getColor(R.color.text2));
//                            }
                        }
                    }
                    setCanOpenNumber();
                }

                @Override
                protected void _onError(String message) {
                    Log.e("获取实时价格", message + "");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开仓
     *
     * @param context
     * @param trend   1、开多   2、开空
     */
    private void cfdOrderCreate(Context context, int trend) {
        try {
            CustomProgressDialog.showDialog(context);
            Api.getInstance().cfdOrderCreate(context, type, CfdFragment.currency, trend, returnMultiple(), returnOpenNumber(), returnOpenPrice(), returnStopProfitPrice(), returnStopLossPrice(), CfdFragment.cfdTypeEnum).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<BaseBean>(context) {
                @Override
                protected void _onNext(BaseBean bean) {
                    if (bean != null) {
                        queryCfdAsset(context);
                        queryCfdOrderPendding(context);
                        numberText.setText("");
                        stopLossTv.setText("");
                        stopProfitTv.setText("");
                        Toast.makeText(context, getResources().getString(R.string.open_position_ok), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                protected void _onError(String message) {
                    Toast.makeText(context, getResources().getString(R.string.open_position_error) + ":" + message, Toast.LENGTH_SHORT).show();
                    CustomProgressDialog.dissmissDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询委托单记录
     *
     * @param context
     */
    private void queryCfdOrderPendding(Context context) {
        try {
            Api.getInstance().queryCfdOrderPendding(context, CfdFragment.cfdTypeEnum).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<OrderPenddingBean>(context) {
                @Override
                protected void _onNext(OrderPenddingBean bean) {
                    if (bean != null && bean.getData().size() > 0) {
                        orderCreateList = bean.getData();
                        delegateRecordAdapter.refersh(orderCreateList);
                    }
                    MData mData = new MData();
                    mData.type = MdataUtils.CFD_OPEN_REFERSH_STOP;
                    EventBus.getDefault().post(mData);
                    CustomProgressDialog.dissmissDialog();
                }

                @Override
                protected void _onError(String message) {
                    Log.e("查询委托单记录", message + "");
                    orderCreateList = new ArrayList<>();
                    delegateRecordAdapter.refersh(orderCreateList);
                    MData mData = new MData();
                    mData.type = MdataUtils.CFD_OPEN_REFERSH_STOP;
                    EventBus.getDefault().post(mData);
                    CustomProgressDialog.dissmissDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 撤销委托单
     *
     * @param context
     */
    private void cfdOrderCancel(Context context, String orderId) {
        try {
            CustomProgressDialog.showDialog(context);
            Api.getInstance().cfdOrderCancel(context, orderId, CfdFragment.cfdTypeEnum).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<BaseBean>(context) {
                @Override
                protected void _onNext(BaseBean bean) {
                    if (bean != null) {
                        queryCfdOrderPendding(context);
                        Toast.makeText(context, getResources().getString(R.string.cancel_order_ok), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                protected void _onError(String message) {
                    CustomProgressDialog.dissmissDialog();
                    Toast.makeText(context, getResources().getString(R.string.cancel_order_error) + ":" + message, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.speed_position_tv, R.id.plan_position_tv, R.id.multiple_rl, R.id.plan_open_rise_btn, R.id.plan_open_fall_btn})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.speed_position_tv:
                setTab(1);
                break;
            case R.id.plan_position_tv:
                setTab(2);
                break;
            case R.id.multiple_rl:
                if (DataUtil.isFastClick()) {
                    hideJianPan();
                    if (popup != null && listMultiple != null && listMultiple.size() > 0)
                        popup.showAtLocation(getActivity().findViewById(R.id.open_position_view), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
                break;
            case R.id.plan_open_rise_btn://开多  买涨  plan_open_rise_btn  11111   极速开多
                if (DataUtil.isFastClick()) {
                    hideJianPan();
                    if (confirmCheck(1)) {
                        cfdOrderCreate(getActivity(), 1);
                    }
                }
                break;
            case R.id.plan_open_fall_btn://开空 买跌
                if (DataUtil.isFastClick()) {
                    hideJianPan();
                    if (confirmCheck(2)) {
                        cfdOrderCreate(getActivity(), 2);
                    }
                }
                break;
        }
    }

    /**
     * 确认判断
     *
     * @param trend 1、开多   2、开空
     * @return
     */
    private boolean confirmCheck(int trend) {
        confirmStatus = true;
        if (type == 2 && returnOpenPrice() == 0.00) {
            String hint = getResources().getString(R.string.open_prcies);
            Toast.makeText(getActivity(), DataUtil.returnNoNullHint(getActivity(), hint), Toast.LENGTH_SHORT).show();
            confirmStatus = false;
        }
        if (returnOpenPrice() > 0.00 && (returnOpenPrice() > returnCurrPrice() * 1.5 || returnOpenPrice() < returnCurrPrice() * 0.5)) {
            confirmStatus = false;
            DialogUtils.showConfirmDialog(getActivity(), null, getResources().getString(R.string.hint), getResources().getString(R.string.price_hint_or_low_hint));
            DialogUtils.btn(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (DataUtil.isFastClick()) {
                        DialogUtils.dismissDialog();
                        cfdOrderCreate(getActivity(), trend);
                    }
                }
            });
            DialogUtils.no(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (DataUtil.isFastClick()) {
                        DialogUtils.dismissDialog();
//                        planPriceText.setText("");
                    }
                }
            });
        }
        if (returnOpenNumber() == 0.00) {
            String hint = getResources().getString(R.string.number);
            Toast.makeText(getActivity(), DataUtil.returnNoNullHint(getActivity(), hint), Toast.LENGTH_SHORT).show();
            confirmStatus = false;
        }
        if (returnStopLossPrice() > 0.00 || returnStopProfitPrice() > 0.00) {
            if (trend == 1) {
//                开多, 止损价格只能输入低于开仓价格；止盈价格只能输入高于开仓价格；
                if (returnStopLossPrice() > 0.00 && returnStopLossPrice() > returnOpenPrice()) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.stop_loss_small_open_hint), Toast.LENGTH_SHORT).show();
                    confirmStatus = false;
                }
                if (returnStopProfitPrice() > 0.00 && returnStopProfitPrice() < returnOpenPrice()) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.stop_profit_big_open_hint), Toast.LENGTH_SHORT).show();
                    confirmStatus = false;
                }
            } else {
//                开空,止损价格只能输入高于开仓价格；止盈价格只能输入低于开仓价格；
                if (returnStopLossPrice() > 0.00 && returnStopLossPrice() < returnOpenPrice()) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.stop_loss_big_open_hint), Toast.LENGTH_SHORT).show();
                    confirmStatus = false;
                }
                if (returnStopProfitPrice() > 0.00 && returnStopProfitPrice() > returnOpenPrice()) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.stop_profit_small_open_hint), Toast.LENGTH_SHORT).show();
                    confirmStatus = false;
                }
            }
        }
        //2222
        if (returnOpenNumber() > returnCanOpenNumber()) {
            String hint = getResources().getString(R.string.can_open_number).replaceAll("：", "").trim();
            Toast.makeText(getActivity(), DataUtil.returnNumberNoHint(getActivity(), hint), Toast.LENGTH_SHORT).show();
            confirmStatus = false;
        }
        return confirmStatus;
    }

    /**
     * 查询合约账户资产
     *
     * @param context
     */
    private void queryCfdAsset(Context context) {
        try {
            Api.getInstance().queryCfdAsset(context, CfdFragment.cfdTypeEnum).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<AssetCfdBean>(context) {
                @Override
                protected void _onNext(AssetCfdBean bean) {
                    if (bean != null && bean.getData().size() > 0) {
                        for (AssetsCfd cfd : bean.getData()) {
                            if (cfd.getCurrency().equals(TokenEnum.USDT.name())) {
                                assetsCfd = cfd;
                            }
                        }
                    }
                    setCanOpenNumber();
                    setTransactionNumber();
                }

                @Override
                protected void _onError(String message) {
                    setCanOpenNumber();
                    setTransactionNumber();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 隐藏输入框
     */
    private void hideJianPan() {
        DataUtil.hideJianPan(getActivity(), numberText);
        DataUtil.hideJianPan(getActivity(), planPriceText);
        DataUtil.hideJianPan(getActivity(), stopLossTv);
        DataUtil.hideJianPan(getActivity(), stopProfitTv);
    }

    boolean flag = true;
    Thread thread;

    @Override
    public void onResume() {
        super.onResume();
        StatusBarUtil.setFragmentStatusBar(getActivity(), R.color.white);
        flag = true;
        thread = new Thread(new MyThread());
        thread.start();
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
        hideJianPan();
    }

    public class MyThread implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (flag) {
                try {
                    Thread.sleep(2000);// 线程暂停，单位毫秒
                    Message message = new Message();
                    message.what = 10000001;
                    handler.sendMessage(message);// 发送消息
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

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
}
