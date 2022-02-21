package com.zhuorui.commonwidget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;


import com.zhuorui.securties.skin.view.ZRSkinAble;

import org.jetbrains.annotations.Nullable;

import base2app.ex.ResourceKt;

/**
 * Date: 2020/1/19
 * Desc:
 */
public class ZRInfoCellBar extends ConstraintLayout implements ZRSkinAble {

    private ImageView iv_logo_cell;
    private ZRDrawableTextView tv_cell_tips;
    private TextView tv_cell_info;
    private View vSpace;
    private int logores;
    private int cellTipsColor;
    private int tipsIcon;

    public ZRInfoCellBar(Context context) {
        this(context, null);
    }

    public ZRInfoCellBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZRInfoCellBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ZRInfoCellBar);
        logores = a.getResourceId(R.styleable.ZRInfoCellBar_logo_icon, 0);
        String info = a.getString(R.styleable.ZRInfoCellBar_info_tips);
        String tips = a.getString(R.styleable.ZRInfoCellBar_cell_tips);
        int infoTextSize=a.getDimensionPixelOffset(R.styleable.ZRInfoCellBar_cell_info_textSize, 0);
        int tipsTextSize=a.getDimensionPixelOffset(R.styleable.ZRInfoCellBar_cell_tips_textSize, 0);
        tipsIcon = a.getResourceId(R.styleable.ZRInfoCellBar_cell_tips_icon,0);
        boolean showCell = a.getBoolean(R.styleable.ZRInfoCellBar_show_cell_info, false);
        cellTipsColor = a.getColor(R.styleable.ZRInfoCellBar_cell_tips_color,  ResourceKt.color(R.color.subtitle_text_color));
        boolean showSpace = a.getBoolean(R.styleable.ZRInfoCellBar_show_space, true);
        a.recycle();
        inflate(context, R.layout.layout_mine_cell_bar, this);
        initView();
        setLogoRes(logores);
        setInfoText(info);
        setInfoTextSize(infoTextSize);
        setRightTips(tips);
        setRightTipsColor(cellTipsColor);
        setRightTipsIcon(tipsIcon);
        setRightTipsTextSize(tipsTextSize);
        vSpace.setVisibility(showSpace ? VISIBLE : GONE);
    }

    public void enterLogoVisibility(int visibility){
        findViewById(R.id.iv_enter_logo).setVisibility(visibility);
    }

    private void initView() {
        iv_logo_cell = findViewById(R.id.iv_logo_cell);
        tv_cell_tips = findViewById(R.id.tv_cell_tips);
        tv_cell_info = findViewById(R.id.tv_cell_info);
        vSpace = findViewById(R.id.v_space);
    }

    public void setInfoText(String text) {
        tv_cell_info.setText(text);
    }

    public void setInfoText(int resId) {
        tv_cell_info.setText(resId);
    }

    /**
     * 设置左边标题字体大小
     * @param textSize
     */
    public void setInfoTextSize(int textSize){
        if (textSize!=0)
            tv_cell_info.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
    }

    public void setRightTips(String text) {
        tv_cell_tips.setText(text);
    }

    /**
     * 设置右边tips文字字体大小
     * @param textSize
     */
    public void setRightTipsTextSize(int textSize){
        if (textSize!=0)
        tv_cell_tips.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
    }

    public void setRightTips(int resId) {
        tv_cell_tips.setText(resId);
    }

    public void setRightTipsColorRes(@ColorRes int color) {
        tv_cell_tips.setTextColor(ContextCompat.getColor(getContext(),color));
    }

    public void setRightTipsIcon(int tipsRes) {
        if (tipsRes != 0) {
            tv_cell_tips.setDrawableLeft(tipsRes);
        }
    }

    public void setRightTipsColor(@ColorInt int color) {
        tv_cell_tips.setTextColor(color);
    }

    public void setLogoRes(int resId) {
        if (resId == 0) {
            iv_logo_cell.setVisibility(GONE);
        } else {
            iv_logo_cell.setVisibility(VISIBLE);
            iv_logo_cell.setImageResource(resId);
        }
    }

    public String getTipsValue() {
        return tv_cell_tips.getText().toString();
    }

    @Override
    public void applyUIMode(@Nullable Resources resources) {
        setLogoRes(logores);
        setRightTipsColor(cellTipsColor);
    }
}
