package com.zhuorui.commonwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * date   : 2019/9/11 10:26
 * desc   : 右侧两个点击图标的TitleBar,右侧第二个btn在右侧第一个右边
 */
public class MoreIconTitleBar extends ZhuoRuiTopBar {

    public MoreIconTitleBar(Context context) {
        this(context, null);
    }

    public MoreIconTitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MoreIconTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setRight2ClickListener(OnClickListener l) {
        List<View> vs = getRightViews();
        View v = vs.size() > 0 ? getRightViews().get(0) : null;
        if (v != null) v.setOnClickListener(l);
    }

    public void setRightClickListener(OnClickListener l) {
        List<View> vs = getRightViews();
        View v = vs.size() > 1 ? getRightViews().get(1) : null;
        if (v != null) v.setOnClickListener(l);
    }

    public void stopLoading() {
        List<View> vs = getRightViews();
        if (vs.size() <= 0) return;
        for (View v : vs) {
            if (v instanceof ZRTopBarRefreshView) {
                ((ZRTopBarRefreshView) v).stopLoading();
                v.setEnabled(true);
                break;
            }
        }
    }

    public void startLoading() {
        List<View> vs = getRightViews();
        if (vs.size() <= 0) return;
        for (View v : vs) {
            if (v instanceof ZRTopBarRefreshView) {
                ((ZRTopBarRefreshView) v).startLoading();
                v.setEnabled(false);
                break;
            }
        }
    }

}
