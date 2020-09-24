package com.dtl.gemini.base;

import com.dtl.gemini.common.base.BaseModel;
import com.dtl.gemini.common.base.BasePresenter;
import com.dtl.gemini.common.base.BaseFragment;
/**
 * Created by conan on 2017/2/20.
 */

public abstract class BaseAppFragment<T extends BasePresenter, E extends BaseModel> extends BaseFragment<T, E> {

    public void showErrorTip(String msg) {
        showShortToast(msg);
    }
}
