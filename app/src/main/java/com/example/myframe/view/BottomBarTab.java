package com.example.myframe.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;


import com.example.myframe.R;

import base2app.ex.PixelExKt;
import base2app.ex.ResourceKt;


/**
 * Created by YoKeyword on 16/6/3.
 */
public class BottomBarTab extends FrameLayout {
    public ImageView mIcon;
    public TextView mTvTitle;
    public Context mContext;
    public int mTabPosition = -1;
    private TextView mTvUnreadCount;
    private int mTitleResId;
    private final int selectedColor = Color.parseColor("#FF5A67D9");
    private final int unselectedColor = Color.parseColor("#FF818191");

    public BottomBarTab(Context context, @DrawableRes int icon, @StringRes int titleResId) {
        this(context, null, icon, titleResId);
    }

    public BottomBarTab(Context context, AttributeSet attrs, int icon, @StringRes int titleResId) {
        this(context, attrs, 0, icon, titleResId);
    }

    public BottomBarTab(Context context, AttributeSet attrs, int defStyleAttr, int icon, @StringRes int titleResId) {
        super(context, attrs, defStyleAttr);
        init(context, icon, titleResId);
    }

    private void init(Context context, int icon, @StringRes int titleResId) {
        mTitleResId = titleResId;
        mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(new int[]{R.attr.selectableItemBackgroundBorderless});
        Drawable drawable = typedArray.getDrawable(0);
        setBackground(drawable);
        typedArray.recycle();

        LinearLayout lLContainer = new LinearLayout(context);
        lLContainer.setOrientation(LinearLayout.VERTICAL);
        lLContainer.setGravity(Gravity.CENTER);
        LayoutParams paramsContainer = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsContainer.gravity = Gravity.CENTER;
        lLContainer.setLayoutParams(paramsContainer);

        mIcon = new ImageView(context);
        mIcon.setImageResource(icon);
        mTvTitle = new TextView(context);
        mTvTitle.setText(ResourceKt.text(mTitleResId));
        mTvTitle.setTextSize(11);
        mTvTitle.setTextColor(unselectedColor);
        layoutParams();
        lLContainer.addView(mIcon);
        lLContainer.addView(mTvTitle);
        addView(lLContainer);
    }

    public void refreshTitle() {
        mTvTitle.setText(ResourceKt.text(mTitleResId));
    }

    protected void layoutParams() {
        int size = (int) PixelExKt.dp2px(24f);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
        mIcon.setLayoutParams(params);
        LinearLayout.LayoutParams paramsTv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsTv.topMargin = (int) PixelExKt.dp2px(2f);
        mTvTitle.setLayoutParams(paramsTv);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        mIcon.setSelected(selected);
        if (selected) {
//            mIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.tab_checked_select));
            mTvTitle.setTextColor(selectedColor);
        } else {
//            mIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.tab_deault_tv_select));
            mTvTitle.setTextColor(unselectedColor);
        }
    }

    public void setTabPosition(int position) {
        mTabPosition = position;
        if (position == 0) {
            setSelected(true);
        }
    }

    public int getTabPosition() {
        return mTabPosition;
    }

    /**
     * 设置未读数量
     */
    public void setUnreadCount(int num) {
        if (num <= 0) {
            mTvUnreadCount.setText(String.valueOf(0));
            mTvUnreadCount.setVisibility(GONE);
        } else {
            mTvUnreadCount.setVisibility(VISIBLE);
            if (num > 99) {
                mTvUnreadCount.setText("99+");
            } else {
                mTvUnreadCount.setText(String.valueOf(num));
            }
        }
    }

    /**
     * 获取当前未读数量
     */
    public int getUnreadCount() {
        int count = 0;
        if (TextUtils.isEmpty(mTvUnreadCount.getText())) {
            return count;
        }
        if (mTvUnreadCount.getText().toString().equals("99+")) {
            return 99;
        }
        try {
            count = Integer.parseInt(mTvUnreadCount.getText().toString());
        } catch (Exception ignored) {
        }
        return count;
    }

    public void setTitleResId(@StringRes int titleResId) {
        mTitleResId = titleResId;
        refreshTitle();
    }
}
