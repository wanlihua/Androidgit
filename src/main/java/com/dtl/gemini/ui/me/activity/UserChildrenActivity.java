package com.dtl.gemini.ui.me.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dtl.gemini.R;
import com.dtl.gemini.api.Api;
import com.dtl.gemini.base.BaseAppActivity;
import com.dtl.gemini.bean.DataBean;
import com.dtl.gemini.common.baserx.RxSchedulers;
import com.dtl.gemini.common.baserx.RxSubscriber;
import com.dtl.gemini.db.StoreUtils;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.model.User;
import com.dtl.gemini.ui.me.adapter.UserChildrenAdapter;
import com.dtl.gemini.ui.me.model.UserChildren;
import com.dtl.gemini.utils.DataUtil;
import com.dtl.gemini.widget.MyListView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 用户团队
 *
 * @author DTL
 * @date 2020/7/27
 **/
public class UserChildrenActivity extends BaseAppActivity {
    @Bind(R.id.refersh)
    SwipeRefreshLayout refersh;
    @Bind(R.id.tv_direct_push_num)
    TextView tvDirectPushNum;
    @Bind(R.id.tv_children_num)
    TextView tvChildrenNum;
    @Bind(R.id.tv_valid_user_num)
    TextView tvValidUserNum;
    @Bind(R.id.tv_team_performance)
    TextView tvTeamPerformance;
    @Bind(R.id.tv_levle_num)
    TextView tvLevleNum;
    @Bind(R.id.ll_kong)
    LinearLayout llKong;
    @Bind(R.id.list_mlv)
    MyListView listMlv;

    int levleNum = 1;

    UserChildren userChildren;
    UserChildrenAdapter adapter;
    List<UserChildren> list = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_children;
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
        setTitle(getResources().getString(R.string.me_team));
        DataUtil.setFocusTv(tvDirectPushNum);
        refershData();
    }

    private void bindListener() {
        setBackOnClickListener();
        refersh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserChildren(UserChildrenActivity.this, userChildren.getPhone());
            }
        });

        listMlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (DataUtil.isFastClick()) {
                    if (list != null && list.size() > 0 &&  list.get(i).getHasChildren()) {
                        levleNum++;
                        getUserChildren(UserChildrenActivity.this, list.get(i).getPhone());
                    } else {
                        Toast.makeText(UserChildrenActivity.this, "该用户无下级", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void refershData() {
        User user = StoreUtils.init(this).getLoginUser();
        adapter = new UserChildrenAdapter(this, list);
        listMlv.setAdapter(adapter);
        if (user != null) {
            getUserChildren(this, user.getPhone());
        }
    }

    private void getUserChildren(Context context, String phone) {
        try {
            Api.getInstance().getUserChildren(context, phone).compose(RxSchedulers.io_main()).subscribe(new RxSubscriber<DataBean<UserChildren>>(context) {
                @Override
                protected void _onNext(DataBean<UserChildren> bean) {
                    if (bean != null && bean.getData() != null) {
                        tvLevleNum.setText("第" + levleNum + "层");
                        userChildren = bean.getData();
                        list = userChildren.getChildrenList();
                        if (levleNum == 1) {
                            tvDirectPushNum.setText(userChildren.getDirectPushNum().toString());
                            tvChildrenNum.setText(userChildren.getTeamUserNumber().toString());
                            tvValidUserNum.setText(userChildren.getTeamValidUserNumber().toString());
                            tvTeamPerformance.setText(userChildren.getSumPerformance());
                        }
                        if (list.size() > 0) {
                            llKong.setVisibility(View.GONE);
                            listMlv.setVisibility(View.VISIBLE);
                        } else {
                            llKong.setVisibility(View.VISIBLE);
                            listMlv.setVisibility(View.GONE);
                        }
                        adapter.refersh(list);
                    }
                    DataUtil.stopRefersh(refersh);
                }

                @Override
                protected void _onError(String message) {
                    Log.e("获取用户下级团队", message + "");
                    DataUtil.stopRefersh(refersh);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        if (levleNum > 1) {
            levleNum--;
            getUserChildren(this, userChildren.getParentPhone());
        } else {
            super.finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {

    }

}
