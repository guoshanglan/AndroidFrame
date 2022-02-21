package com.zhuorui.commonwidget.multidirectional;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * @date 2020/4/18 14:35
 * @desc
 */
public class LinkageHorizontalScrollView extends HorizontalScrollView {

    private List<OnScrollListener> listeners = new ArrayList<>();

    private boolean mScrollable = true;

    public LinkageHorizontalScrollView(Context context) {
        this(context,null);
    }

    public LinkageHorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LinkageHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOverScrollMode(OVER_SCROLL_NEVER);
        setHorizontalScrollBarEnabled(false);
    }

    public void setScrollable(boolean scrollable) {
        mScrollable = scrollable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!mScrollable) {
            return false;
        }
        return super.onTouchEvent(ev);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
//        scrollTo(mLastScrollX, 0);
    }


    /**
     * @param l    Current horizontal scroll origin.
     * @param t    Current vertical scroll origin.
     * @param oldl Previous horizontal scroll origin.
     * @param oldt Previous vertical scroll origin.
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        for(OnScrollListener listener : listeners){
            listener.onScroll(l);
        }
    }

    public interface OnScrollListener{
        void onScroll(int scrollX);
    }

    public void addOnScrollListener(OnScrollListener listener) {
        this.listeners.add(listener);
    }

}
