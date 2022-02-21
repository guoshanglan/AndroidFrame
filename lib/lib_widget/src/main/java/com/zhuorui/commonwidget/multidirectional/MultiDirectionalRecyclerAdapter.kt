package com.zhuorui.commonwidget.multidirectional

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.annotation.CallSuper
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zhuorui.commonwidget.R
import com.zhuorui.commonwidget.adapter.HeaderFooterAdapter

/**
 * @date 2020/4/18 11:14
 * @desc 可横向滑动上下滑动的Adapter
 */
abstract class MultiDirectionalRecyclerAdapter<T>(val context: Context) : HeaderFooterAdapter<T>() {

    val multiDirectionalDelegate = MultiDirectionalDelegate.create()

    /**
     * 是否需要底部固定的View，该View不需要滑动
     */
    var hasBottomFixedView: Boolean = false

    /**
     * 是否显示分割线
     */
    var isShowItemDivideLine: Boolean = true

    /**
     * 分割线颜色
     */
    private var itemDivideLineColor: Int? = null

    init {
        isShowStateView = false
    }

    fun setItemDivideLine(isShowItemDivideLine: Boolean, itemDivideLineColor: Int) {
        this.isShowItemDivideLine = isShowItemDivideLine
        this.itemDivideLineColor = itemDivideLineColor
    }


    fun cacheHeaderLinkageView(target: LinkageHorizontalScrollView) {
        multiDirectionalDelegate.cacheHeaderLinkage(target)
    }

    open fun getRootLayout(): Int {
        return if (hasBottomFixedView) {
            R.layout.item_multi_directional_has_bottom_view
        } else {
            R.layout.item_multi_directional_view
        }
    }


    override fun createViewHolderByParent(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = inflateView(parent, getRootLayout())
        val parentView = v?.findViewById<LinearLayout>(R.id.parent_view)
        val linkageHorizontalScrollView: LinkageHorizontalScrollView
        //如果需要底部View
        if (hasBottomFixedView) {
            //添加固定的头部view
            val scrollParentView = v?.findViewById<LinearLayout>(R.id.scroll_parent_view)
            LayoutInflater.from(context).inflate(onCreateFixedTitleLayout(), scrollParentView, true)

            //添加可以滑动的view
            linkageHorizontalScrollView = makeLinkageHorizontalScrollView(context)
            LayoutInflater.from(context).inflate(onCreateScrollContentLayout(), linkageHorizontalScrollView, true)
            scrollParentView?.addView(linkageHorizontalScrollView)

            //添加底部固定的view
            if (onCreateBottomFixedLayout() != 0) {
                LayoutInflater.from(context).inflate(onCreateBottomFixedLayout(), parentView, true)
            }
            return onCreateMultiDirectionalViewHolder(parentView, viewType, linkageHorizontalScrollView)
        } else {
            //添加固定的头部view
            parentView?.addView(LayoutInflater.from(context).inflate(onCreateFixedTitleLayout(), parentView, false))
//            //添加可以滑动的view
            linkageHorizontalScrollView = makeLinkageHorizontalScrollView(context)
            linkageHorizontalScrollView.addView(
                LayoutInflater.from(context).inflate(onCreateScrollContentLayout(), linkageHorizontalScrollView, false)
            )
            parentView?.addView(linkageHorizontalScrollView)
            return onCreateMultiDirectionalViewHolder(v, viewType, linkageHorizontalScrollView)
        }
    }


    override fun isSetDataNotificate(): Boolean {
        return true
    }


    override fun setItemClickListener(viewHolder: RecyclerView.ViewHolder?) {
        super.setItemClickListener(viewHolder)
        (viewHolder as MultiDirectionalViewHolder<*>).linkageHorizontalScrollView.getChildAt(0).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                //在RecyclerView中的绝对位置
                val absolutePosition = viewHolder.absoluteAdapterPosition
                //在adapter的位置
                val bindingPosition = viewHolder.bindingAdapterPosition
                //数据在数据集合的位置
                val itemIndex = getItemIndex(bindingPosition)
                val item = getItem(itemIndex);
                onClickItem(absolutePosition, bindingPosition, itemIndex, item, viewHolder.itemView)
                if (clickItemCallback != null) clickItemCallback.onClickItem(itemIndex, item, viewHolder.itemView)
            }
        })
    }

    override fun setLongClickListener(viewHolder: RecyclerView.ViewHolder?) {
        super.setLongClickListener(viewHolder)
        (viewHolder as MultiDirectionalViewHolder<*>).linkageHorizontalScrollView.getChildAt(0).setOnLongClickListener { v1: View? ->
            //在RecyclerView中的绝对位置
            val absolutePosition = viewHolder.absoluteAdapterPosition
            //在adapter的位置
            val bindingPosition = viewHolder.bindingAdapterPosition
            //在数据集合的位置
            val itemIndex = getItemIndex(bindingPosition)
            val item = getItem(itemIndex)
            onLongClickItem(absolutePosition, bindingPosition, itemIndex, item, viewHolder.itemView)
            if (longClickItemCallback != null) longClickItemCallback.onLongClickItem(itemIndex, item, viewHolder.itemView)
            true
        }
    }

    abstract fun onCreateMultiDirectionalViewHolder(
        v: View?,
        viewType: Int,
        linkageHorizontalScrollView: LinkageHorizontalScrollView
    ): MultiDirectionalViewHolder<T>

    abstract inner class MultiDirectionalViewHolder<T>(
        itemView: View?,
        val linkageHorizontalScrollView: LinkageHorizontalScrollView,
        needClick: Boolean,
        needLongClick: Boolean
    ) :
        ListItemViewHolder<T>(itemView, needClick, needLongClick), IListItemViewHolder2 {

        @CallSuper
        override fun bind(item: T, index: Int) {
            if (item == null) return
            val viewLine = itemView.findViewById<View>(R.id.view_line)
            if (isShowItemDivideLine) {
                itemView.findViewById<View>(R.id.view_line)
                    .setBackgroundColor(itemDivideLineColor ?: ContextCompat.getColor(context, R.color.main_division_background))
                viewLine.visibility = View.VISIBLE
            } else {
                viewLine.visibility = View.GONE
            }
//            multiDirectionalDelegate.cacheLinkageView(linkageHorizontalScrollView)
            onBindFixedTitleData(item, bindingAdapterPosition)
            onBindScrollContentData(item, bindingAdapterPosition)
            if (hasBottomFixedView) {
                onBindBottomFixedData(item, bindingAdapterPosition)
            }
        }

        @CallSuper
        override fun attached() {
            multiDirectionalDelegate.cacheLinkageView(linkageHorizontalScrollView)
        }

        @CallSuper
        override fun detached() {
            multiDirectionalDelegate.removeCache(linkageHorizontalScrollView)
        }

        abstract fun onBindFixedTitleData(data: T, position: Int)

        abstract fun onBindScrollContentData(data: T, position: Int)

        /**
         * 底部view不一定有，如果设置为有，则外部需要复写该方法。
         */
        open fun onBindBottomFixedData(data: T, position: Int) {

        }
    }


    private fun makeLinkageHorizontalScrollView(context: Context): LinkageHorizontalScrollView {
        val linkageHorizontalScrollView = LinkageHorizontalScrollView(context)
        linkageHorizontalScrollView.overScrollMode = ScrollView.OVER_SCROLL_NEVER
        linkageHorizontalScrollView.isHorizontalScrollBarEnabled = false
        return linkageHorizontalScrollView
    }


    override fun setItems(items: MutableList<out T>?) {
        multiDirectionalDelegate.clearCacheItemView()
        super.setItems(items)
    }

    /**
     * 改layout给定默认实现，如果设置isNeedFixBottomView为true,子类务必覆盖该方法。
     */
    open fun onCreateBottomFixedLayout(): Int {
        return 0
    }

    /**
     * 固定title的布局文件
     */
    abstract fun onCreateFixedTitleLayout(): Int


    /**
     * 可滑动Content的布局文件
     */
    abstract fun onCreateScrollContentLayout(): Int
}