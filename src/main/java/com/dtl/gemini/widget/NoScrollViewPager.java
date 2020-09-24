package com.dtl.gemini.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * 大图灵
 * 2019/4/30
 **/
public class NoScrollViewPager extends ViewPager {
    private boolean noScroll = false;

    //是否可以左右滑动？true 可以，像Android原生ViewPager一样。
    // false 禁止ViewPager左右滑动。
    private boolean scrollable = false;

    public NoScrollViewPager(@NonNull Context context) {
        super(context);
    }

    public NoScrollViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return scrollable;
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent arg0) {
//        if (noScroll)
//            return false;
//        else
//            return super.onInterceptTouchEvent(arg0);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return scrollable;
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent arg0) {
//        /* return false;//super.onTouchEvent(arg0); */
//        if (noScroll)
//            return false;
//        else
//            return super.onTouchEvent(arg0);
//    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }

}
