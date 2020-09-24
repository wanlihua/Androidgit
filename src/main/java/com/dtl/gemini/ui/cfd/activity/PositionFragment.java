package com.dtl.gemini.ui.cfd.activity;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dtl.gemini.MainActivity;
import com.dtl.gemini.R;
import com.dtl.gemini.api.Api;
import com.dtl.gemini.base.BaseAppFragment;
import com.dtl.gemini.bean.BaseBean;
import com.dtl.gemini.bean.DataBean;
import com.dtl.gemini.common.baserx.RxSchedulers;
import com.dtl.gemini.common.baserx.RxSubscriber;
import com.dtl.gemini.common.commonwidget.CustomProgressDialog;
import com.dtl.gemini.enums.CfdTypeEnum;
import com.dtl.gemini.enums.TokenEnum;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.ui.asset.beans.AssetCfdBean;
import com.dtl.gemini.ui.asset.model.AssetsCfd;
import com.dtl.gemini.ui.cfd.adapter.PositionRecordAdapter;
import com.dtl.gemini.ui.cfd.beans.OrderPenddingBean;
import com.dtl.gemini.ui.cfd.model.OrderCreate;
import com.dtl.gemini.ui.home.model.CfdToken;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.DialogUtils;
import com.dtl.gemini.utils.MdataUtils;
import com.dtl.gemini.widget.AutoWheelChoicePopup;
import com.dtl.gemini.widget.MyListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import retrofit2.http.OPTIONS;

/**
 * @author DTL
 * @date 2020/4/11
 * 持仓
 **/
public class PositionFragment extends BaseAppFragment {
    @Bind(R.id.multiple_ll)
    LinearLayout multipleLl;
    @Bind(R.id.multiple_tv)
    TextView multipleTv;
    @Bind(R.id.list)
    MyListView list;
    @Bind(R.id.ll_kong)
    LinearLayout llKong;
    @Bind(R.id.tv_token)
    TextView tvToken;
    @Bind(R.id.ll_token)
    LinearLayout llToken;


    PositionRecordAdapter positionRecordAdapter;
    List<OrderCreate> orderCreateList = new ArrayList<>();

    AutoWheelChoicePopup popup;
    List<String> listMultiple = new ArrayList<>();
    AutoWheelChoicePopup popupToken;
    List<String> listToken = new ArrayList<>();

    String all;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_cfd_position;
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
        DataUtil.setFocusTv(multipleTv);
        all = getResources().getString(R.string.all);
        positionRecordAdapter = new PositionRecordAdapter(getActivity());
        list.setAdapter(positionRecordAdapter);
    }

    private void bindListener() {
        multipleLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataUtil.isFastClick()) {
                    if (popup != null && listMultiple != null && listMultiple.size() > 0)
                        popup.showAtLocation(getActivity().findViewById(R.id.position_ll), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
            }
        });

        llToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataUtil.isFastClick()) {
                    if (popupToken != null && listToken != null && listToken.size() > 0)
                        popupToken.showAtLocation(getActivity().findViewById(R.id.position_ll), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
            }
        });

        positionRecordAdapter.setCloseOnclick(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (DataUtil.isFastClick()) {
                    OrderCreate order = orderCreateList.get(i);
                    DialogUtils.ClosePosition(getActivity(), order.getStartType(), order.getTrend(), DataUtil.numberFour(order.getUseableAmount()), order.getCurrency());
                    DialogUtils.btn(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (DataUtil.isFastClick()) {
                                DataUtil.hideJianPan(getActivity(), DialogUtils.et1);
                                DataUtil.hideJianPan(getActivity(), DialogUtils.et2);
                                String closePrices = DialogUtils.getEt1String();
                                String closeNumbers = DialogUtils.getEt2String();
                                if (closePrices == null || closePrices.equals("") || !DataUtil.isNumeric(closePrices) || Double.parseDouble(closePrices) == 0.00) {
                                    String hint = getResources().getString(R.string.close_prices);
                                    Toast.makeText(getActivity(), DataUtil.returnNoNullHint(getActivity(), hint), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (closeNumbers == null || closeNumbers.equals("") || !DataUtil.isNumeric(closeNumbers) || Double.parseDouble(closeNumbers) == 0.00) {
                                    String hint = getResources().getString(R.string.close_numbers);
                                    Toast.makeText(getActivity(), DataUtil.returnNoNullHint(getActivity(), hint), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                DialogUtils.dismissDialog();
                                String openPrice = DataUtil.doubleFour(order.getStartPrice());
                                String closePrice = DataUtil.doubleFour(Double.parseDouble(closePrices));
                                String closeNumber = DataUtil.doubleFour(Double.parseDouble(closeNumbers));
                                DialogUtils.ClosePositionConfirm(getActivity(), false, order.getId(), openPrice, OpenPositionFragment.currPrice, closePrice, closeNumber);
                                DialogUtils.btn(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (DataUtil.isFastClick()) {
                                            DialogUtils.dismissDialog();
                                            cfdOrderPlanClose(getActivity(), 2, order, Double.parseDouble(closeNumber), Double.parseDouble(closePrice));
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
        positionRecordAdapter.setSpeedCloseOnclick(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (DataUtil.isFastClick()) {
                    OrderCreate order = orderCreateList.get(i);
                    DialogUtils.SpeedClosePosition(getActivity(), order.getStartType(), order.getTrend(), DataUtil.numberFour(order.getUseableAmount()), order.getCurrency());
                    DialogUtils.btn(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (DataUtil.isFastClick()) {
                                DataUtil.hideJianPan(getActivity(), DialogUtils.et1);
                                String closeNumbers = DialogUtils.getEt1String();
                                if (closeNumbers == null || closeNumbers.equals("") || !DataUtil.isNumeric(closeNumbers) || Double.parseDouble(closeNumbers) == 0.00) {
                                    String hint = getResources().getString(R.string.close_numbers);
                                    Toast.makeText(getActivity(), DataUtil.returnNoNullHint(getActivity(), hint), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                DialogUtils.dismissDialog();
                                String openPrice = DataUtil.doubleFour(order.getStartPrice());
                                String closeNumber = DataUtil.doubleFour(Double.parseDouble(closeNumbers));
                                cfdOrderPlanClose(getActivity(), 1, order, Double.parseDouble(closeNumber), Double.parseDouble(openPrice));
                            }
                        }
                    });
                }
            }
        });

        positionRecordAdapter.setAddBalanceOnclick(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (DataUtil.isFastClick()) {
                    OrderCreate order = orderCreateList.get(i);
                    DialogUtils.showAddBalance(getActivity(), order.getStartType(), order.getTrend(), OpenPositionFragment.assetsCfd.getUseableAmount().doubleValue());
                    DialogUtils.btn(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (DataUtil.isFastClick()) {
                                DataUtil.hideJianPan(getActivity(), DialogUtils.et1);
                                String amounts = DialogUtils.getEt1String();
                                if (amounts == null || amounts.equals("") || !DataUtil.isNumeric(amounts) || Double.parseDouble(amounts) == 0.00) {
                                    String hint = getResources().getString(R.string.number);
                                    Toast.makeText(getActivity(), DataUtil.returnNoNullHint(getActivity(), hint), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                double amount = Double.parseDouble(amounts);
                                if (amount > OpenPositionFragment.assetsCfd.getUseableAmount().doubleValue()) {
                                    Toast.makeText(getActivity(), getResources().getString(R.string.usable_number_no), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                DialogUtils.dismissDialog();
                                cfdOrderAddBalance(getActivity(), order.getId(), amount);
                            }
                        }
                    });
                }
            }
        });
    }

    private void initPopupMultiple() {
        if (listMultiple == null || listMultiple.size() == 0)
            return;
        popup = new AutoWheelChoicePopup(getActivity(), listMultiple);
        popup.setBtnOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multipleTv.setText(popup.getSelecedData());
                queryCfdOrderHold(getActivity());
                popup.dismiss();
            }
        });
    }

    private void initPopupToken() {
        if (listToken == null || listToken.size() == 0)
            return;
        popupToken = new AutoWheelChoicePopup(getActivity(), listToken);
        popupToken.setBtnOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvToken.setText(popupToken.getSelecedData());
                queryCfdOrderHold(getActivity());
                popupToken.dismiss();
            }
        });
    }

    /**
     * 获取倍数
     *
     * @return
     */
    private int returnMultiple() {
        String multiples = multipleTv.getText().toString().replaceAll("X", "").trim();
        int multiple = 0;
        if (multiples != null && !multiples.equals("") && DataUtil.isNumeric(multiples)) {
            multiple = Integer.parseInt(multiples);
        }
        return multiple;
    }

    /**
     * 获取币种
     *
     * @return
     */
    private String returnToken() {
        return tvToken.getText().toString().replaceAll(" ", "").trim();
    }

    /**
     * 查询持仓记录
     *
     * @param context
     */
    private void queryCfdOrderHold(Context context) {
        try {
            Api.getInstance().queryCfdOrderHold(context, CfdFragment.cfdTypeEnum).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<OrderPenddingBean>(context) {
                @Override
                protected void _onNext(OrderPenddingBean bean) {
                    if (bean.getData() != null && bean.getData().size() > 0) {
                        if (orderCreateList != null) {
                            orderCreateList.clear();
                        }
                        for (OrderCreate order : bean.getData()) {
                            if (returnMultiple() == 0 && returnToken().equals(all)) {
                                orderCreateList.add(order);
                            } else if (returnMultiple() > 0 && returnToken().equals(all)) {
                                if (returnMultiple() == order.getMultiple()) {
                                    orderCreateList.add(order);
                                }
                            } else if (returnMultiple() == 0 && !returnToken().equals(all)) {
                                if (returnToken().equals(order.getCurrency())) {
                                    orderCreateList.add(order);
                                }
                            }
                        }
                        if (orderCreateList.size() > 0) {
                            llKong.setVisibility(View.GONE);
                            list.setVisibility(View.VISIBLE);
                            positionRecordAdapter.refersh(orderCreateList);
                        } else {
                            llKong.setVisibility(View.VISIBLE);
                            list.setVisibility(View.GONE);
                        }
                    } else {
                        llKong.setVisibility(View.VISIBLE);
                        list.setVisibility(View.GONE);
                    }
                    MData mData = new MData();
                    mData.type = MdataUtils.CFD_HOLD_REFERSH_STOP;
                    EventBus.getDefault().post(mData);
                    CustomProgressDialog.dissmissDialog();
                }

                @Override
                protected void _onError(String message) {
                    Log.e("查询持仓记录", message + "");
                    llKong.setVisibility(View.VISIBLE);
                    list.setVisibility(View.GONE);
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
     * 1:闪电 2：计划
     *
     * @param context
     */
    private void cfdOrderPlanClose(Context context, int type, OrderCreate order, double amount, double price) {
        try {
            CustomProgressDialog.showDialog(context);
            Api.getInstance().cfdOrderPlanClose(context, type, order.getId(), amount, price, CfdFragment.cfdTypeEnum).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<BaseBean>(context) {
                @Override
                protected void _onNext(BaseBean bean) {
                    if (bean != null) {
                        queryCfdOrderHold(context);
                        if (type == 1) {
                            queryCfdAsset(getActivity());
                            DialogUtils.ClosePositionConfirm(getActivity(), true, order.getId(), DataUtil.doubleFour(order.getStartPrice()), OpenPositionFragment.currPrice, DataUtil.doubleFour(Double.parseDouble(bean.getMassage())), DataUtil.doubleFour(amount));
                            DialogUtils.btn(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (DataUtil.isFastClick()) {
                                        DialogUtils.dismissDialog();
                                    }
                                }
                            });
                        }
                        Toast.makeText(context, getResources().getString(R.string.close_order_ok), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                protected void _onError(String message) {
                    Toast.makeText(context, getResources().getString(R.string.close_order_error), Toast.LENGTH_SHORT).show();
                    CustomProgressDialog.dissmissDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 追加保证金
     *
     * @param context
     * @param orderId
     * @param amount
     */
    private void cfdOrderAddBalance(Context context, String orderId, double amount) {
        try {
            CustomProgressDialog.showDialog(context);
            Api.getInstance().cfdOrderAddBalance(context, orderId, amount, CfdFragment.cfdTypeEnum).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<DataBean>(context) {
                @Override
                protected void _onNext(DataBean bean) {
                    if (bean != null) {
                        queryCfdOrderHold(context);
                        queryCfdAsset(context);
                        Toast.makeText(context, getResources().getString(R.string.add_balance_ok), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                protected void _onError(String message) {
                    Toast.makeText(context, getResources().getString(R.string.add_balance_error), Toast.LENGTH_SHORT).show();
                    CustomProgressDialog.dissmissDialog();
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
     */
    private void queryCfdAsset(Context context) {
        try {
            Api.getInstance().queryCfdAsset(context, CfdFragment.cfdTypeEnum).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<AssetCfdBean>(context) {
                @Override
                protected void _onNext(AssetCfdBean bean) {
                    if (bean != null && bean.getData().size() > 0) {
                        for (AssetsCfd cfd : bean.getData()) {
                            if (cfd.getCurrency().equals(TokenEnum.USDT.name())) {
                                OpenPositionFragment.assetsCfd = cfd;
                            }
                        }
                    }
                }

                @Override
                protected void _onError(String message) {
                    Log.e("查询合约账户资产", message + "");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {
        switch (mData.type) {
            case MdataUtils.CFD_HOLD_REFERSH:
                queryCfdOrderHold(getActivity());
                break;
            case MdataUtils.CFD_MULTIPLE:
                if (listMultiple != null)
                    listMultiple.clear();
                listMultiple.add(all);
                if (OpenPositionFragment.listMultiple != null && OpenPositionFragment.listMultiple.size() > 0) {
                    for (String s : OpenPositionFragment.listMultiple) {
                        listMultiple.add(s);
                    }
                }
                initPopupMultiple();
                break;
            case MdataUtils.CFD_TOKEN:
                if (listToken != null)
                    listToken.clear();
                listToken.add(all);
                tvToken.setText(all);
                if (CfdFragment.listToken != null && CfdFragment.listToken.size() > 0) {
                    for (String s : CfdFragment.listToken) {
                        listToken.add(s);
                    }
                }
                initPopupToken();
                if (orderCreateList != null) {
                    orderCreateList.clear();
                }
                positionRecordAdapter.refersh(orderCreateList);
                queryCfdOrderHold(getActivity());
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        queryCfdAsset(getActivity());
    }
}
