package com.dtl.gemini.ui.asset.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.dtl.gemini.R;
import com.dtl.gemini.base.BaseAppFragment;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.db.StoreUtils;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.model.User;
import com.dtl.gemini.ui.asset.adapter.AssetsWalletAdapter;
import com.dtl.gemini.ui.asset.model.AssetWallet;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.utils.MdataUtils;
import com.dtl.gemini.widget.MyListView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * @author DTL
 * @date 2020/4/15
 * 钱包账户
 **/
public class AssetWalletFragment extends BaseAppFragment {

    @Bind(R.id.assets_list)
    MyListView assetsList;

    User user;
    AssetsWalletAdapter assetsAdapter;
    List<AssetWallet> list;


    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_asset_wallet;
    }

    @Override
    public void initPresenter() {
    }

    @Override
    public void initView() {
        init();
        bindListenner();
    }


    private void init() {
        list = new ArrayList<>();
        assetsAdapter = new AssetsWalletAdapter(getActivity());
        assetsList.setAdapter(assetsAdapter);
        refershWallet();
    }

    private void bindListenner() {
        assetsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (DataUtil.isFastClick()) {
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.CURRENCY,list.get(position).getCurrency());
                    DataUtil.startActivity(getActivity(), AssetDetailsActivity.class, bundle);
                }
            }
        });
    }

    private void refershWallet() {
        user = StoreUtils.init(getActivity()).getLoginUser();
        if (user != null)
            queryAssets(getActivity());
        else
            noLogin();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {
        switch (mData.type) {
            case MdataUtils.ASSET_REFERSH:
                refershWallet();
                break;
            case MdataUtils.ASSET_SEE:
                if (list != null && list.size() > 0)
                    assetsAdapter.refersh(list, 1, AssetFragment.see);
                break;
        }
    }

    /**
     * 查询账户资产
     */
    private void queryAssets(Context context) {
        if (AssetFragment.assetWalletList != null && AssetFragment.assetWalletList.size() > 0) {
            if (list != null)
                list.clear();
            getActivity().runOnUiThread(() -> {
                for (AssetWallet bb : AssetFragment.assetWalletList) {
                    list.add(bb);
                }
                assetsAdapter.refersh(list, 1, AssetFragment.see);
            });
        } else {
            noLogin();
        }
    }

    private void noLogin() {
        if (list != null)
            list.clear();
        assetsAdapter.refersh(list, 1, AssetFragment.see);
    }

}
