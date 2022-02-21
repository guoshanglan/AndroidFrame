package com.zhuorui.commonwidget.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zhuorui.commonwidget.ZRMultiStateFrame

/**
 *    date   : 2019-12-10 15:50
 *    desc   : 带头部和底部状态View
 */
abstract class HeaderFooterAdapter<T> : ZRStateAdapter<T>() {

    /** 头部*/
    private val TYPE_HEADER = -10000

    /** 底部*/
    private val TYPE_FOOTER = -10001

    /** item*/
    private val TYPE_ITEM = -10003

    /** Header*/
    private var vHeader: View? = null

    /** Footer*/
    private var vFooter: View? = null

    /** 第一条和其他item类型*/
    private var types: Array<Int> = arrayOf(TYPE_ITEM, TYPE_ITEM, TYPE_ITEM)

    /** adapter Position与数据index偏移量*/
    protected var indexOffset = 0

    /** item数量*/
    private var itemCount = 0

    /**
     * 是否让空占位和footView同时可见
     * true 不同时可见
     * false 同时可见
     */
    open fun noEmptyWithFootViewVisible(): Boolean {
        return false
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        initItemViewType()
    }

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                ViewHolder(vHeader!!)
            }
            TYPE_FOOTER -> {
                ViewHolder(vFooter!!)
            }
            else -> {
                super.onCreateViewHolder(parent, viewType)
            }
        }
    }

    /**
     * 获取数据itemType
     * @param position adapter位置
     * @param itemIndex 在数据集合中的位置
     */
    override fun getItemViewType(position: Int, itemIndex: Int): Int {
        return TYPE_ITEM
    }

    /**
     * 获取item数量
     */
    override fun getItemCount(): Int {
        return itemCount
    }

    /**
     * 获取item类型，子类在getDataViewType获取item类型
     */
    final override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1 && vFooter != null) {
            if (noEmptyWithFootViewVisible() && types.indexOf(TYPE_STATE_VIEW) != -1) {
                types[position]
            } else {
                TYPE_FOOTER
            }
        } else if (position < types.size && types[position] != TYPE_ITEM) {
            types[position]
        } else {
            //其他
            getItemViewType(position, getItemIndex(position))
        }
    }

    override fun setItems(items: MutableList<out T>?) {
        this.items.clear()
        if (items != null) {
            this.items.addAll(items)
        }

        initItemViewType()
        if (isSetDataNotificate()) {
            notifyDataSetChanged()
        }
    }

    open fun isSetDataNotificate(): Boolean {
        return false
    }

    override fun addItem(item: T) {
        if (item != null) {
            this.items.add(item)
        }
        initItemViewType()
    }

    override fun addItem(index: Int, item: T) {
        if (item != null) {
            this.items.add(index, item)
        }
        initItemViewType()
    }

    override fun addItems(items: MutableList<out T>?) {
        if (items != null) {
            this.items.addAll(items)
        }
        initItemViewType()
    }

    fun removeItems(position: Int) {
        if (position >= 0 && position < items.size) {
            this.items.removeAt(position)
            initItemViewType()
        }
    }

    override fun clearItems() {
        this.items.clear()
        initItemViewType()
    }

    /**
     * 设置Header
     */
    fun setHeaderView(v: View?) {
        var lp = v?.layoutParams
        if (lp == null) {
            lp = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
        } else {
            lp.width = RecyclerView.LayoutParams.MATCH_PARENT
        }
        v?.layoutParams = lp
        vHeader = v
        initItemViewType()
    }

    fun getHeaderView(): View? {
        return vHeader
    }

    /**
     * 设置Footer
     */
    fun setFooterView(v: View?) {
        var lp = v?.layoutParams
        if (lp == null) {
            lp = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
        } else {
            lp.width = RecyclerView.LayoutParams.MATCH_PARENT
        }
        v?.layoutParams = lp
        vFooter = v
        initItemViewType()
    }

    fun getFooterView(): View? {
        return vFooter
    }

    /**
     * 获取数据大小
     */
    fun getDataCount(): Int {
        return items?.size ?: 0
    }

    /**
     * 根据adapter psition获取item Index
     */
    override fun getItemIndex(position: Int): Int {
        return position - indexOffset
    }

    override fun setFrame(frame: ZRMultiStateFrame,notifyChange:Boolean) {
        val oldShow = showStateItem()
        mStateController.setFrame(checkFrameEmpty(frame))
        if (getDataCount() <= 0 && oldShow != showStateItem()) {
            initItemViewType()
            if (notifyChange) notifyDataSetChanged()
        }
    }

    /**
     * 初始化item类型
     */
    fun initItemViewType() {
        //头部数量
        val hNum = if (vHeader == null) 0 else 1
        //底部数量
        var fNum = if (vFooter == null) 0 else 1
        val dataCount = getDataCount()
        var type = TYPE_ITEM
        var emptyNum = 0
        //判断是否要显示状态视图
        if (dataCount == 0 && showStateItem()) {
            type = TYPE_STATE_VIEW
            emptyNum = 1
        }
        when {
            hNum > 0 -> {
                types[0] = TYPE_HEADER
                types[1] = type
                types[2] = TYPE_ITEM
            }
            else -> {
                types[0] = type
                types[1] = TYPE_ITEM
                types[2] = TYPE_ITEM
            }
        }
        //状态视图不与底部视图共存，重置底部视图数量
        if (noEmptyWithFootViewVisible() && type == TYPE_STATE_VIEW) {
            fNum = 0
        }
        indexOffset = hNum
        itemCount = hNum + dataCount + fNum + emptyNum
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v)

}