package com.zhuorui.commonwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Px;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * date   : 2019-11-27 18:00
 * desc   : 选中Highlight内容展示View
 */
public class HighlightContentView extends LinearLayout {

    private @ColorInt int defaultColor = 0;
    private int mTitleMaxWidth = 0;
    private int mTitlePaddingLeft = 0;

    public HighlightContentView(Context context) {
        this(context, null);
    }

    public HighlightContentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HighlightContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
    }

    public void setTitleMaxWidth(@Px int maxWidth){
        mTitleMaxWidth = maxWidth;
    }

    public void setTitlePaddingLeft(@Px int padding){
        mTitlePaddingLeft = padding;
    }

    public void setDefaultColor(@ColorInt int defaultColor) {
        this.defaultColor = defaultColor;
    }

    public void setData(LinkedHashMap<CharSequence, CharSequence> data, int[] techColors) {
        init();
        int position = 0;
        for (Map.Entry<CharSequence, CharSequence> entry : data.entrySet()) {
            View lineView = getLineView(position);
            TextView vTitle = getTitleViewByLineView(lineView);
            TextView vConttentView = getConttentViewByLineView(lineView);
            if (defaultColor != 0) {
                vTitle.setTextColor(defaultColor);
                vConttentView.setTextColor(defaultColor);
            }
            if (position == 0) vTitle.setMaxWidth(Integer.MAX_VALUE);
            vTitle.setText(entry.getKey());
            vConttentView.setText(entry.getValue());
            if (position - 1 >= 0) {
                View view = getColorViewByLineView(lineView);
                view.setVisibility(VISIBLE);
                view.setBackgroundColor(techColors[position - 1]);
            }
            position++;
        }
    }

    public void setData(LinkedHashMap<CharSequence,CharSequence> data) {
        init();
        int position = 0;
        for (Map.Entry<CharSequence, CharSequence> entry : data.entrySet()) {
            View lineView = getLineView(position);
            TextView vTitle = getTitleViewByLineView(lineView);
            TextView vConttentView = getConttentViewByLineView(lineView);
            if (defaultColor != 0) {
                vTitle.setTextColor(defaultColor);
                vConttentView.setTextColor(defaultColor);
            }
            vTitle.setText(entry.getKey());
            vConttentView.setText(entry.getValue());
            position++;
        }
    }

    private void init() {
        if (getChildCount() != 0) {
            for (int i = 0; i < getChildCount(); i++) {
                getChildAt(i).setVisibility(GONE);
            }
        }
    }

    private TextView getConttentViewByLineView(View lineView) {
        return lineView.findViewById(R.id.h_tv_content);
    }

    private TextView getTitleViewByLineView(View lineView) {
        return lineView.findViewById(R.id.h_tv_title);
    }

    private View getColorViewByLineView(View lineView) {
        return lineView.findViewById(R.id.line);
    }

    private View getLineView(int position) {
        View v;
        if (position < getChildCount()) {
            v = getChildAt(position);
            v.setVisibility(VISIBLE);
        } else {
            v = LayoutInflater.from(getContext()).inflate(R.layout.item_highlight_content, null);
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (mTitleMaxWidth > 0){
                ((TextView)v.findViewById(R.id.h_tv_title)).setMaxWidth(mTitleMaxWidth);
            }
            if (mTitlePaddingLeft > 0){
                TextView title = v.findViewById(R.id.h_tv_title);
                title.setPadding(mTitlePaddingLeft,title.getPaddingTop(),title.getPaddingRight(),title.getPaddingBottom());
            }
            lp.topMargin = position == 0 ? 0 : (int) (getResources().getDisplayMetrics().density * 4);
            addView(v,lp);
        }
        return v;
    }
}
