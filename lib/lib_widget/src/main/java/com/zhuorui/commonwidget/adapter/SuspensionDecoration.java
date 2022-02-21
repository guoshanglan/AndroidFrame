package com.zhuorui.commonwidget.adapter;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhuorui.commonwidget.R;
import com.zhuorui.commonwidget.model.BaseIndexPinyinBean;


import java.util.List;

import base2app.ex.PixelExKt;
import base2app.ex.ResourceKt;

/**
 * @date 2020/7/23 18:00
 * @desc 分类悬停
 */
public class SuspensionDecoration extends RecyclerView.ItemDecoration {
    private List<? extends BaseIndexPinyinBean> datas;
    private int mItemHeaderHeight;    //头部的高
    private int mTextPaddingLeft;
    //画笔，绘制头部和分割线
    private Paint mItemHeaderPaint;
    private Paint mTextPaint;
    private Paint mLinePaint;
    private Rect mTextRect;

    public SuspensionDecoration(List<BaseIndexPinyinBean> datas) {
        this.datas = datas;
        mItemHeaderHeight = (int) PixelExKt.dp2px(30);
        mTextPaddingLeft = (int) PixelExKt.dp2px(13);

        mTextRect = new Rect();
        mItemHeaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mItemHeaderPaint.setColor(ResourceKt.color(R.color.suspension_decoration_background));

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize((int) PixelExKt.dp2px(14));
        mTextPaint.setColor(ResourceKt.color(R.color.main_content_text_color));

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(ResourceKt.color(R.color.main_division_background));
    }

    /**
     * 绘制Item的分割线和组头
     *
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int count = parent.getChildCount();//获取可见范围内Item的总数
        for (int i = 0; i < count; i++) {
            View view = parent.getChildAt(i);
            int position = parent.getChildLayoutPosition(view);
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();
            String indexTag = datas.get(position).getBaseIndexTag();
            if (isItemHeader(position)) {
                c.drawRect(left, view.getTop() - mItemHeaderHeight, right, view.getTop(), mItemHeaderPaint);
                mTextPaint.getTextBounds(indexTag, 0, 1, mTextRect);
                c.drawText(indexTag, left + mTextPaddingLeft, (view.getTop() - mItemHeaderHeight) + mItemHeaderHeight / 2 + mTextRect.height() / 2, mTextPaint);
            } else {
                c.drawRect((int) PixelExKt.dp2px(13), view.getTop() - 1, right, view.getTop(), mLinePaint);
            }
        }
    }

    /**
     * 绘制Item的顶部布局（吸顶效果）
     *
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int position = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();
        View view = parent.findViewHolderForAdapterPosition(position).itemView;
        int top = parent.getPaddingTop();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        String indexTag = datas.get(position).getBaseIndexTag();
        if (isItemHeader(position + 1)) {
            int bottom = Math.min(mItemHeaderHeight, view.getBottom());
            c.drawRect(left, top + view.getTop() - mItemHeaderHeight, right, top + bottom, mItemHeaderPaint);
            mTextPaint.getTextBounds(indexTag, 0, 1, mTextRect);
            c.drawText(indexTag, left + mTextPaddingLeft, top + mItemHeaderHeight / 2 + mTextRect.height() / 2 - (mItemHeaderHeight - bottom), mTextPaint);
        } else {
            c.drawRect(left, top, right, top + mItemHeaderHeight, mItemHeaderPaint);
            mTextPaint.getTextBounds(indexTag, 0, 1, mTextRect);
            c.drawText(indexTag, left + mTextPaddingLeft, top + mItemHeaderHeight / 2 + mTextRect.height() / 2, mTextPaint);
        }
        c.save();
    }

    /**
     * 设置Item的间距
     *
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildLayoutPosition(view);
        if (isItemHeader(position)) {
            outRect.top = mItemHeaderHeight;
        } else {
            outRect.top = 1;
        }
    }

    /**
     * 判断position对应的Item是否是组的第一项
     *
     * @param position
     * @return
     */
    public boolean isItemHeader(int position) {
        if (position == 0) {
            return true;
        } else {
            String lastIndexTag = datas.get(position - 1).getBaseIndexTag();
            String currentIndexTag = datas.get(position).getBaseIndexTag();
            //判断上一个数据的组别和下一个数据的组别是否一致，如果不一致则是不同组，也就是为第一项（头部）
            return !lastIndexTag.equals(currentIndexTag);
        }
    }
}
