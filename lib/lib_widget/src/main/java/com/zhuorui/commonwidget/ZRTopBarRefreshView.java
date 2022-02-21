package com.zhuorui.commonwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

/**
 * @date 2020/6/2 11:28
 * @desc
 */
public class ZRTopBarRefreshView extends LinearLayout {
    private ImageView iv_refresh;
    private ProgressBar loading;

    public ZRTopBarRefreshView(Context context) {
        this(context, null);
    }

    public ZRTopBarRefreshView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZRTopBarRefreshView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_top_bar_refresh, this);
        iv_refresh = findViewById(R.id.iv_refresh);
        loading = findViewById(R.id.loading);
    }

    public void stopLoading() {
        iv_refresh.setVisibility(VISIBLE);
        loading.setVisibility(GONE);
    }

    public void startLoading() {
        iv_refresh.setVisibility(GONE);
        loading.setVisibility(VISIBLE);
    }
}
