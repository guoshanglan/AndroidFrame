package com.zhuorui.commonwidget.flow;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Px;
import androidx.annotation.StyleRes;

import java.util.List;


/**
 * 创建:  Poker on 2016/3/10
 * 修改:  liuwei at 2017/8/17
 * 描述:  FlowParamListView:
 */
public class FlowParamListView extends LinearLayout {

    private int tbPadding;
    private int lrMargin;
    private int itemBg;
    private int mSpace;
    private int mDividerHight;
    private int mItemSpace;
    private int mItemAlign;
    private int mTitleTextStyle;
    private int mContentTextStyle;
    private int mGroupItemSpace;
    private int mTitleWidth;
    private int mTitleImgWidht, mTitleImgHight;

    public FlowParamListView(Context context) {
        super(context);
        initWidget(null);
    }

    public FlowParamListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWidget(attrs);
    }

    private void initWidget(AttributeSet attrs) {
        float density = getResources().getDisplayMetrics().density;
        lrMargin = (int) (density * 13);
        tbPadding = (int) (density * 10);
        itemBg = android.R.color.white;
        mSpace = (int) (density * 16);
        mDividerHight = (int) (density * 4);
        mGroupItemSpace = mDividerHight;
        mItemSpace = 0;
        mTitleWidth = -1;
        mTitleImgWidht = -1;
        mTitleImgHight = -1;
        initAttribute();
        setOrientation(VERTICAL);
        this.setVisibility(GONE);
    }

    private void initAttribute() {

    }

    public void setItemAlign(@FlowParamItemView.ItemAlignMode int itemAlign) {
        mItemAlign = itemAlign;
    }

    public void setTitleWidth(@Px int titleWidth) {
        mTitleWidth = titleWidth;
    }

    public void setmTitleImgSize(@Px int width, @Px int hight) {
        mTitleImgWidht = width;
        mTitleImgHight = hight;
    }

    public void setItemSpace(@Px int itemSpace) {
        mItemSpace = itemSpace;
    }

    public void setSpace(@Px int space) {
        mSpace = space;
    }

    public void setGroupItemSpace(@Px int itemSpace){
        mGroupItemSpace = itemSpace;
    }

    public void setDividerHight(@Px int dividerHight) {
        mDividerHight = dividerHight;
    }

    public void setItemTBPadding(@Px int padding) {
        tbPadding = padding;
    }

    public void setItemLRMargin(@Px int margin) {
        lrMargin = margin;
    }

    public void setTextStyle(@StyleRes int titleStyle, @StyleRes int contentStyle) {
        mTitleTextStyle = titleStyle;
        mContentTextStyle = contentStyle;
    }

    public void setData(List<? extends KVInterdace> datas) {
        removeAllViews();
        if (null != datas && !datas.isEmpty()) {
            FlowParamView fpv = getFlowParamView();
            fpv.setDatas(datas);
            addView(fpv);
            this.setVisibility(VISIBLE);
        } else {
            this.setVisibility(GONE);
        }
    }

    public void setDatas(List<List<? extends KVInterdace>> datas) {
        removeAllViews();
        if (null != datas && !datas.isEmpty()) {
            LayoutParams lp = null;
            FlowParamView fpv = null;
            int position = -1;
            for (List<? extends KVInterdace> kvs : datas) {
                if (null != kvs && !kvs.isEmpty()) {
                    ++position;
                    fpv = getFlowParamView();
                    fpv.setDatas(kvs);
                    lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, position == 0 ? 0 : mSpace, 0, 0);
                    addView(fpv, lp);
                }
            }
            setVisibility(position > -1 ? VISIBLE : GONE);
        } else {
            setVisibility(GONE);
        }
    }

    private FlowParamView getFlowParamView() {
        FlowParamView fpv = new FlowParamView(getContext());
        fpv.setPadding(0, tbPadding, 0, tbPadding);
        fpv.setContentMargin(lrMargin, lrMargin);
        fpv.setItemAlign(mItemAlign);
        fpv.setDividerHight(mDividerHight);
        fpv.setBackgroundResource(itemBg);
        fpv.setTitleWidth(mTitleWidth);
        fpv.setmTitleImgSize(mTitleImgWidht, mTitleImgHight);
        fpv.setTitleTextStyle(mTitleTextStyle);
        fpv.setTitleTextStyle(mContentTextStyle);
        fpv.setGroupItemSpace(mGroupItemSpace);
        fpv.setItemSpace(mItemSpace);
        return fpv;
    }

}
