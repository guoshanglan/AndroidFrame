package com.zhuorui.commonwidget;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 用于Grid模式RecyclerView控制间距
 */
public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

   private int spanCount; //列数
   private int spacingTb; //上下间隔
   private int spacingLr; //左右间隔
   private boolean includeEdge; //是否包含边缘

    public GridSpacingItemDecoration(int spanCount, int spacingTb, int spacingLr, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacingTb = spacingTb;
        this.spacingLr = spacingLr;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
       
       //这里是关键，需要根据你有几列来判断
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column

        if (includeEdge) {
            outRect.left = spacingLr - column * spacingLr / spanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacingLr / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

            if (position < spanCount) { // top edge
                outRect.top = spacingTb;
            }
            outRect.bottom = spacingTb; // item bottom
        } else {
            outRect.left = column * spacingLr / spanCount; // column * ((1f / spanCount) * spacing)
            outRect.right = spacingLr - (column + 1) * spacingLr / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.top = spacingTb; // item top
            }
        }
    }
}