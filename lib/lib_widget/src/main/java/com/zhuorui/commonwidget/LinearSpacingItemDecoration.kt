package com.zhuorui.commonwidget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * 用于Linear模式RecyclerView控制间距
 */
class LinearSpacingItemDecoration(
    private val spacing: Int, //间隔
    private val edgeoffset: Int, //顶部边缘偏移量
    private val includeEdge: Boolean // 是否包含边缘
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {

        //这里是关键，需要根据你有几列来判断
        val position = parent.getChildAdapterPosition(view) // item position
        val childCount = parent.layoutManager?.childCount

        if (includeEdge) {
            outRect.top = spacing// top edge
            if (position == parent.layoutManager!!.itemCount) {
                outRect.bottom = spacing // item bottom
            }
        } else {
            if (position > 0) { // top edge
                outRect.top = spacing
                if (position == childCount) {
                    outRect.bottom = edgeoffset
                }
            } else if (position == 0) {
                outRect.top = edgeoffset
            }
        }
    }
}
