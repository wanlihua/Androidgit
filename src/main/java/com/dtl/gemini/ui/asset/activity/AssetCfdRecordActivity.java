package com.dtl.gemini.ui.asset.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dtl.gemini.R;
import com.dtl.gemini.api.Api;
import com.dtl.gemini.base.BaseAppActivity;
import com.dtl.gemini.bean.DataBean;
import com.dtl.gemini.common.baserx.RxSchedulers;
import com.dtl.gemini.common.baserx.RxSubscriber;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.db.StoreUtils;
import com.dtl.gemini.enums.CfdTypeEnum;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.model.User;
import com.dtl.gemini.ui.asset.adapter.CfdAssetsRecordAdapter;
import com.dtl.gemini.ui.asset.model.AssetCfdRecord;
import com.dtl.gemini.ui.other.activity.LoginActivity;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.widget.AutoWheelChoicePopup;
import com.dtl.gemini.widget.MyListView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 合约资产记录
 *
 * @author DTL
 * @date 2020/7/29
 **/
public class AssetCfdRecordActivity extends BaseAppActivity {
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.list_mlv)
    MyListView listMlv;
    @Bind(R.id.ll_kong)
    LinearLayout llKong;
    @Bind(R.id.refersh)
    SmartRefreshLayout refersh;
    @Bind(R.id.tv_type)
    TextView tvType;
    @Bind(R.id.ll_type)
    LinearLayout llType;

    List<AssetCfdRecord> list;
    CfdAssetsRecordAdapter adapter;

    User user;
    Context context;
    CfdTypeEnum cfdTypeEnum = CfdTypeEnum.DOUBLE;

    AutoWheelChoicePopup popup;
    List<String> listType = new ArrayList<>();

    String all, type1, type3, type4, type5, type6, type7, type8, type9, type10, type11, type13, type15, type16, type17, type18, type19;

    @Override

    public int getLayoutId() {
        return R.layout.activity_asset_cfd_record;
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
        context = this;
        DataUtil.setFocusTv(title);
        Bundle bundle = getIntent().getBundleExtra(Constant.BUNDLE);
        if (bundle != null) {
            cfdTypeEnum = DataUtil.getCfdTypeEnum(bundle.getString(Constant.TYPE));
        }
        if (cfdTypeEnum == CfdTypeEnum.DOUBLE) {
            setTitle(getResources().getString(R.string.asset_double_cfd));
        } else {
            setTitle(getResources().getString(R.string.asset_free_cfd));
        }
        DataUtil.setRefershTheme(this, refersh);
        DataUtil.setRefershNoLoad(refersh);

        list = new ArrayList<>();
        adapter = new CfdAssetsRecordAdapter(context);
        listMlv.setAdapter(adapter);

        all = getResources().getString(R.string.all);
        type11 = getResources().getString(R.string.asset_cfd_record_type11);
        type13 = getResources().getString(R.string.asset_cfd_record_type13);
        type15 = getResources().getString(R.string.asset_cfd_record_type15);
        type16 = getResources().getString(R.string.asset_cfd_record_type16);
        type17 = getResources().getString(R.string.asset_cfd_record_type17);
        type18 = getResources().getString(R.string.asset_cfd_record_type18);
        type1 = getResources().getString(R.string.asset_cfd_record_type1);
        type3 = getResources().getString(R.string.asset_cfd_record_type3);
        type4 = getResources().getString(R.string.asset_cfd_record_type4);
        type5 = getResources().getString(R.string.asset_cfd_record_type5);
        type6 = getResources().getString(R.string.asset_cfd_record_type6);
        type7 = getResources().getString(R.string.asset_cfd_record_type7);
        type8 = getResources().getString(R.string.asset_cfd_record_type8);
        type9 = getResources().getString(R.string.asset_cfd_record_type9);
        type10 = getResources().getString(R.string.asset_cfd_record_type10);
        listType.add(all);
        listType.add(type1);
        listType.add(type3);
//        listType.add(type4);
        listType.add(type5);
        listType.add(type6);
        listType.add(type7);
        listType.add(type8);
        listType.add(type9);
        listType.add(type10);
        listType.add(type11);
        listType.add(type13);
        listType.add(type15);
        listType.add(type16);
        listType.add(type17);
        listType.add(type18);
        initPopup();
        refershData();
    }


    private void initPopup() {
        if (listType == null || listType.size() == 0)
            return;
        popup = new AutoWheelChoicePopup(this, listType);
        popup.setBtnOnclick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvType.setText(popup.getSelecedData());
                refershData();
                popup.dismiss();
            }
        });
    }

    private String returnType() {
        return tvType.getText().toString();
    }

    private void bindListener() {
        setBackOnClickListener();

        refersh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refershData();
            }
        });

        refersh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refershData();
            }
        });

        llType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataUtil.isFastClick()) {
                    if (popup != null && listType != null && listType.size() > 0)
                        popup.showAtLocation(findViewById(R.id.asset_cfd_record_view), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
            }
        });

    }

    private void refershData() {
        user = StoreUtils.init(this).getLoginUser();
        if (user != null) {
            queryCfdAssetRecord();
        } else {
            DataUtil.stopRefersh(refersh);
            LoginActivity.startAction(this);
        }
    }

    /**
     * 查看合约账户记录
     */
    private void queryCfdAssetRecord() {
        try {
            Api.getInstance().queryCfdAssetRecord(context, cfdTypeEnum).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<DataBean<List<AssetCfdRecord>>>(context) {
                @Override
                protected void _onNext(DataBean<List<AssetCfdRecord>> bean) {
                    if (bean.getData() != null && bean.getData().size() > 0) {
                        if (list != null) {
                            list.clear();
                        }
                        for (AssetCfdRecord record : bean.getData()) {
                            if (returnType().equals(all)) {
                                list.add(record);
                            } else if (returnType().equals(type1)) {
                                if (record.getType() == 1 || record.getType() == 2) {
                                    list.add(record);
                                }
                            } else if (returnType().equals(type3)) {
                                if (record.getType() == 3) {
                                    list.add(record);
                                }
                            } else if (returnType().equals(type4)) {
                                if (record.getType() == 4) {
                                    list.add(record);
                                }
                            } else if (returnType().equals(type5)) {
                                if (record.getType() == 5) {
                                    list.add(record);
                                }
                            } else if (returnType().equals(type6)) {
                                if (record.getType() == 6) {
                                    list.add(record);
                                }
                            } else if (returnType().equals(type7)) {
                                if (record.getType() == 7) {
                                    list.add(record);
                                }
                            } else if (returnType().equals(type8)) {
                                if (record.getType() == 8) {
                                    list.add(record);
                                }
                            } else if (returnType().equals(type9)) {
                                if (record.getType() == 9) {
                                    list.add(record);
                                }
                            } else if (returnType().equals(type10)) {
                                if (record.getType() == 10) {
                                    list.add(record);
                                }
                            } else if (returnType().equals(type11)) {
                                if (record.getType() == 11) {
                                    list.add(record);
                                }
                            } else if (returnType().equals(type13)) {
                                if (record.getType() == 13 || record.getType() == 14) {
                                    list.add(record);
                                }
                            } else if (returnType().equals(type15)) {
                                if (record.getType() == 15 ) {
                                    list.add(record);
                                }
                            } else if (returnType().equals(type16)) {
                                if (record.getType() == 16|| record.getType() == 19) {
                                    list.add(record);
                                }
                            } else if (returnType().equals(type17)) {
                                if (record.getType() == 17) {
                                    list.add(record);
                                }
                            } else if (returnType().equals(type18)) {
                                if (record.getType() == 18) {
                                    list.add(record);
                                }
                            }
                        }
                        if (list.size() > 0) {
                            llKong.setVisibility(View.GONE);
                            listMlv.setVisibility(View.VISIBLE);
                            adapter.refersh(list);
                        } else {
                            llKong.setVisibility(View.VISIBLE);
                            listMlv.setVisibility(View.GONE);
                        }
                    } else {
                        llKong.setVisibility(View.VISIBLE);
                        listMlv.setVisibility(View.GONE);
                    }
                    DataUtil.stopRefersh(refersh);
                }

                @Override
                protected void _onError(String message) {
                    Log.e("查看合约账户记录", message + "");
                    DataUtil.stopRefersh(refersh);
                    llKong.setVisibility(View.VISIBLE);
                    listMlv.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {

    }

}
