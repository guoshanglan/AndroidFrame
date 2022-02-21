package com.zhuorui.commonwidget.flow;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.annotation.StyleRes;

import com.zhuorui.commonwidget.R;

import java.util.List;


/**
 * 创建:  Poker on 2016/3/3
 * 修改:  liuwei at 2017/8/17
 * 描述:  FlowParamView:
 */
public class FlowParamView extends LinearLayout {
    private int mTitleWidth;
    private int mTitleImgWidht, mTitleImgHight;
    private int mTitleTextStyle;
    private int mContentTextStyle;
    private int mItemSpace;
    private int mItemAlign;
    private int mGroupItemSpace;
    private int mDividerColor;
    private int mDividerHight;
    private int mDividerLeftMargin;
    private int mDividerTopMargin;
    private int mDividerRightMargin;
    private int mDividerBottomMargin;
    private int mContentLeftMargin;
    private int mContentRightMargin;

    public FlowParamView(Context context) {
        this(context,null);
    }

    public FlowParamView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowParamView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        initAttribute(attrs);
    }

    private void initAttribute(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FlowParamView);
        mTitleWidth = a.getDimensionPixelOffset(R.styleable.FlowParamView_fpv_title_width, -1);
        mTitleImgWidht = a.getDimensionPixelOffset(R.styleable.FlowParamView_fpv_title_img_width, -1);
        mTitleImgHight = a.getDimensionPixelOffset(R.styleable.FlowParamView_fpv_title_img_hight, -1);
        mTitleTextStyle = a.getResourceId(R.styleable.FlowParamView_fpv_title_text_style, mTitleTextStyle);
        mContentTextStyle = a.getResourceId(R.styleable.FlowParamView_fpv_content_text_style, mContentTextStyle);
        mItemSpace = a.getDimensionPixelOffset(R.styleable.FlowParamView_fpv_item_space, mItemSpace);
        mItemAlign = a.getInteger(R.styleable.FlowParamView_fpv_item_align, KVInterdace.ITEM_ALIGN_LEFT);
        mGroupItemSpace = a.getDimensionPixelOffset(R.styleable.FlowParamView_fpv_group_item_space, mGroupItemSpace);
        mDividerColor = a.getColor(R.styleable.FlowParamView_fpv_divider_color, mDividerColor);
        mDividerHight = a.getDimensionPixelOffset(R.styleable.FlowParamView_fpv_divider_hight, mDividerHight);
        mDividerLeftMargin = a.getDimensionPixelOffset(R.styleable.FlowParamView_fpv_divider_margin_left, mDividerLeftMargin);
        mDividerTopMargin = a.getDimensionPixelOffset(R.styleable.FlowParamView_fpv_divider_margin_top, mDividerTopMargin);
        mDividerRightMargin = a.getDimensionPixelOffset(R.styleable.FlowParamView_fpv_divider_margin_right, mDividerRightMargin);
        mDividerBottomMargin = a.getDimensionPixelOffset(R.styleable.FlowParamView_fpv_divider_margin_bottom, mDividerBottomMargin);
        mContentLeftMargin = a.getDimensionPixelOffset(R.styleable.FlowParamView_fpv_content_margin_left, mContentLeftMargin);
        mContentRightMargin = a.getDimensionPixelOffset(R.styleable.FlowParamView_fpv_content_margin_right, mContentRightMargin);
        a.recycle();
        if (mItemAlign == KVInterdace.ITEM_ALIGN_CENTER_HORIZONTAL)
            setOrientation(HORIZONTAL);
    }

    public void setTitleWidth(@Px int titleWidth) {
        mTitleWidth = titleWidth;
    }

    public void setmTitleImgSize(@Px int width, @Px int hight) {
        mTitleImgWidht = width;
        mTitleImgHight = hight;
    }

    public void setTitleTextStyle(@StyleRes int style) {
        mTitleTextStyle = style;
    }

    public void setContentTextStyle(@StyleRes int style) {
        mContentTextStyle = style;
    }

    public void setItemSpace(@Px int itemSpace) {
        mItemSpace = itemSpace;
    }

    public void setItemAlign(@FlowParamItemView.ItemAlignMode int align) {
        mItemAlign = align;
    }

    public void setGroupItemSpace(@Px int itemSpace) {
        mGroupItemSpace = itemSpace;
    }

    public void setDividerColor(@ColorInt int color) {
        mDividerColor = color;
    }

    public void setDividerHight(@Px int hight) {
        mDividerHight = hight;
    }

    public void setDividerMargin(@Px int leftMargin, @Px int topMargin, @Px int rightMargin, @Px int bottomMargin) {
        mDividerLeftMargin = leftMargin;
        mDividerTopMargin = topMargin;
        mDividerRightMargin = rightMargin;
        mDividerBottomMargin = bottomMargin;
    }

    public void setContentMargin(@Px int leftMargin, @Px int rightMargin) {
        mContentLeftMargin = leftMargin;
        mContentRightMargin = rightMargin;
    }

    public void setDatas(List<? extends KVInterdace> datas) {
        int itemCount = initItemCount();
        if (null != datas) {
            int datalen = datas.size();
            FlowParamItemView item;
            boolean isVERTICAL = getOrientation() == VERTICAL;
            for (int i = 0; i < datalen; i++) {
                if (i < itemCount) {
                    item = (FlowParamItemView) getChildAt(getItemContentPos(i));
                    item.setVisibility(VISIBLE);
                    View divider = getChildAt(getItemDividerPos(i));
                    if (divider != null && i < datalen - 1)
                        divider.setVisibility(VISIBLE);
                } else {
                    LayoutParams clp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    clp.setMargins(mContentLeftMargin, clp.topMargin, mContentRightMargin, clp.bottomMargin);
                    item = new FlowParamItemView(getContext());
//                    clp.weight = 1;
                    item.setLayoutParams(clp);
                    addView(item);
                    LayoutParams dlp;
                    if (isVERTICAL) {
                        dlp = new LayoutParams(LayoutParams.MATCH_PARENT, mDividerHight);
                    } else {
                        dlp = new LayoutParams(mDividerHight, mDividerHight);
                    }
                    dlp.setMargins(mDividerLeftMargin, mDividerTopMargin, mDividerRightMargin, mDividerBottomMargin);
                    View divider = new View(getContext());
                    divider.setLayoutParams(dlp);
                    divider.setBackgroundColor(mDividerColor);
                    addView(divider);
                    if (datalen - 1 <= i) {
                        divider.setVisibility(GONE);
                    }
                }
                item.setStyle(mTitleTextStyle, mContentTextStyle, mItemSpace, mItemAlign);
                item.setTitleWidth(mTitleWidth);
                item.setTitleImgSize(mTitleImgWidht, mTitleImgHight);
                item.setGroupItemSpace(mGroupItemSpace);
                item.bindData(datas.get(i));
            }
        }
    }

    /**
     * 初化状态，并返回已经添加的Item条数
     *
     * @return item size
     */
    private int initItemCount() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).setVisibility(GONE);
        }
        int i = childCount / 2;
        int y = (childCount % 2 == 0 ? 0 : 1);
        return i + y;
    }

    /**
     * 获取content View 下标
     *
     * @param index 位置
     * @return content View position
     */
    private int getItemContentPos(int index) {
        return index * 2;
    }

    /**
     * 获取Divider View 下标
     *
     * @param index 位置
     * @return divider View position
     */
    private int getItemDividerPos(int index) {
        return getItemContentPos(index) + 1;
    }

}
