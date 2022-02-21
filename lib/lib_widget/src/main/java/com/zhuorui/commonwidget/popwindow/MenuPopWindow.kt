package com.zhuorui.commonwidget.popwindow

import android.content.Context
import android.graphics.Paint
import android.os.Build
import android.text.TextPaint
import android.view.*
import android.widget.*
import androidx.annotation.Nullable
import base2app.ex.dp2px
import base2app.ex.drawable
import base2app.ex.sp2px
import base2app.util.StatusBarUtil
import com.zhuorui.commonwidget.MenuPopBackgroundDrawable
import com.zhuorui.commonwidget.R
import com.zhuorui.commonwidget.ZhuoRuiTopBar
import com.zhuorui.commonwidget.popwindow.PopWindowHelper.bindFragmentByMenuPop

/**
 *    date   : 2020-02-21 10:16
 *    desc   : 菜单操作PopWindow
 */
open class MenuPopWindow<T>(context: Context) : ListPopupWindow(context),
    AdapterView.OnItemClickListener {

    private var mListener:OnMenuSelectListener<T>? = null
    private var mTitles: Array<String>? = null
    private var mDatas: Array<T>? = null
    private var mItemSelectedPosition = -1
    private var mAdapter: BaseAdapter? = null
    private var defVerticalOffset = 0
    private var mHeight = 0
    private var mWidth = 0
    private var mArrowHight = 0
    private var mRadius = 0
    private var mSorll = false
    private var mBgColor = 0
    private var mBgShadowColor = 0
    private var mVerticalOffset = 0

    constructor(
        context: Context,
        titles: Array<String>,
        datas: Array<T>
    ) : this(context) {
        initMenu(
            context,
            datas,
            titles,
            titles.size.toFloat(),
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    /**
     * @param maxItem 最大显示item数量
     */
    constructor(
        context: Context,
        maxItem: Float,
        titles: Array<String>,
        datas: Array<T>
    ) : this(context) {
        initMenu(
            context,
            datas,
            titles,
            maxItem,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    constructor(
        context: Context,
        width: Int,
        height: Int,
        titles: Array<String>,
        datas: Array<T>
    ) : this(context) {
        initMenu(context, datas, titles, titles.size.toFloat(), width, height)
    }

    private fun initMenu(
        context: Context,
        datas: Array<T>,
        titles: Array<String>,
        maxItem: Float,
        width: Int,
        height: Int
    ) {
        val density = context.resources.displayMetrics.density
        defVerticalOffset = (density * 5).toInt()
        mArrowHight = (density * 8).toInt()
        mRadius = (density * 6).toInt()
        isModal = true
        mDatas = datas
        mTitles = titles
        mWidth = width
        mAdapter = getAdapter(context, titles)
        if (mAdapter is IMeunPopAdapter) {
            val meunAdapter: IMeunPopAdapter = mAdapter as IMeunPopAdapter
            setPopWidth(mWidth, meunAdapter.getItemMaxWidth())
            setPopHight(height, titles.size, maxItem, meunAdapter.getItemMaxHeight().toFloat())
        } else {
            setPopWidth(mWidth, 0)
            setPopHight(height, titles.size, maxItem, 0f)
        }
        setAdapter(mAdapter)
        setOnItemClickListener(this)
        setOnDismissListener {
            mItemSelectedPosition = -1
        }
        bindFragmentByMenuPop(this, context)
    }

    open fun getAdapter(context: Context, titles: Array<String>): BaseAdapter? {
        return MyAdapter(context, titles)
    }

    fun setBackgroundColor(color: Int, shadowColor: Int) {
        mBgColor = color
        mBgShadowColor = shadowColor
    }

    fun show(anchorView: View, gravity: Int) {
        show(anchorView, anchorView, gravity)
    }

    fun show(anchorView: View, arrowAnchorView: View, gravity: Int) {
        setAnchorView(anchorView)
        setDropDownGravity(gravity)
        val anchorLocation = IntArray(2)
        anchorView.getLocationOnScreen(anchorLocation)
        setPopVerticalOffset(anchorView, anchorLocation, gravity)
        val arrowXOffset = if (anchorView != arrowAnchorView) {
            val arrowLocation = IntArray(2)
            arrowAnchorView.getLocationOnScreen(arrowLocation)
            when {
                gravity and Gravity.LEFT == Gravity.LEFT || gravity and Gravity.START == Gravity.START -> {
                    arrowLocation[0] - anchorLocation[0]
                }
                gravity and Gravity.RIGHT == Gravity.RIGHT || gravity and Gravity.END == Gravity.END -> {
                    anchorLocation[0] + anchorView.width - arrowLocation[0] - arrowAnchorView.width
                }
                else -> {
                    0
                }
            }
        } else {
            -1
        }
        val backgroundDrawable = MenuPopBackgroundDrawable().apply {
            setColor(mBgColor, mBgShadowColor)
            setGravity(gravity)
            setScroll(mSorll)
            setArrowXOffset(arrowXOffset, arrowAnchorView.width)
        }
        setBackgroundDrawable(backgroundDrawable)
        super.show()
        listView?.let {
            it.scrollBarSize = 1f.dp2px().toInt()
            it.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            it.isScrollbarFadingEnabled = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                it.verticalScrollbarThumbDrawable = drawable(R.drawable.pop_list_scroller)
            }
            it.parent?.let { parent ->
                if (parent is View)
                    parent.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            }
            dimBehind(it)
        }
    }

    override fun setVerticalOffset(offset: Int) {
        mVerticalOffset = offset
        super.setVerticalOffset(offset)
    }

    private fun setPopVerticalOffset(
        anchorView: View,
        anchorLocation: IntArray,
        gravity: Int
    ): Boolean {
        var above = false
        val verticalOffset = if (gravity and Gravity.TOP == Gravity.TOP) {
            val topEndPX =
                anchorLocation[1] - defVerticalOffset - mHeight - defVerticalOffset - mVerticalOffset
            val topMinPx =
                ZhuoRuiTopBar.getTopBarHeight(anchorView.context) + StatusBarUtil.getStatusBarHeight(
                    anchorView.context
                )
            if (topEndPX > topMinPx) {
                above = true
                -mHeight - anchorView.height - defVerticalOffset - mVerticalOffset
            } else {
                defVerticalOffset + mVerticalOffset
            }
        } else if (mHeight + defVerticalOffset + defVerticalOffset + anchorLocation[1] + anchorView.height > anchorView.context.resources.displayMetrics.heightPixels) {
            above = true
            -mHeight - anchorView.height - defVerticalOffset - mVerticalOffset
        } else {
            defVerticalOffset + mVerticalOffset
        }
        super.setVerticalOffset(verticalOffset)
        return above
    }

    private fun setPopWidth(width: Int, maxItemWidth: Int) {
        if (width == ViewGroup.LayoutParams.MATCH_PARENT) {
            setWidth(width)
        } else if (width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            if (maxItemWidth > 0) {
                setContentWidth(maxItemWidth)
            } else {
                setWidth(width)
            }
        } else {
            setContentWidth(width)
        }
    }

    private fun setPopHight(height: Int, itemSize: Int, maxItem: Float, maxItemHeight: Float) {
        when (height) {
            ViewGroup.LayoutParams.MATCH_PARENT -> {
                setHeight(height)
            }
            ViewGroup.LayoutParams.WRAP_CONTENT -> {
                mSorll = maxItem < itemSize
                mHeight = if (maxItemHeight > 0) {
                    (maxItem * maxItemHeight + mArrowHight + if (mSorll) (mRadius + mRadius) else 0).toInt()
                } else {
                    height
                }
                setHeight(if (mSorll) mHeight else height)
            }
            else -> {
                mSorll = if (maxItemHeight > 0) height < maxItem * maxItemHeight else false
                mHeight = height + mArrowHight + mRadius + mRadius
                setHeight(mHeight)
            }
        }
    }

    fun setOnMenuSelectListener(@Nullable listener: OnMenuSelectListener<T>?) {
        mListener = listener
    }

    /**
     * 根据data设置选中数据
     */
    fun setItemSelected(d: T) {
        mDatas?.forEachIndexed { index, t ->
            if (t?.equals(d) == true) {
                selected(index)
                return@forEachIndexed
            }
        }

    }

    /**
     * 根据title设置选中数据
     */
    fun setItemSelectedByTitle(d: String) {
        mTitles?.forEachIndexed { index, t ->
            if (t == d) {
                selected(index)
                return@forEachIndexed
            }
        }
    }

    private fun selected(index: Int) {
        mItemSelectedPosition = index
        listView?.postDelayed({
            listView?.let {
                it.smoothScrollToPosition(index)
                it.setSelection(index)
                it.setItemChecked(index, true)
            }
        }, 100)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        dismiss()
        mItemSelectedPosition = position
        mListener?.onItemSelected(mTitles?.get(position), mDatas?.get(position), position)
    }

    fun getItemSelectPostiotn(): Int {
        return mItemSelectedPosition
    }

    interface OnMenuSelectListener<T> {
        fun onItemSelected(title: String?, da: T?, position: Int)
    }

    inner class MyAdapter(context: Context, titles: Array<String>) : BaseAdapter(),
        IMeunPopAdapter {

        private val mInflater = LayoutInflater.from(context)
        private val datas: Array<String> = titles
        private var itemHight = 0

        init {
            itemHight = 40f.dp2px().toInt()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return createViewFromResource(
                mInflater, position, convertView, parent,
                R.layout.item_pop_window_menu
            )
        }

        override fun getItem(position: Int): CharSequence {
            return datas[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return datas.size
        }

        private fun createViewFromResource(
            inflater: LayoutInflater,
            position: Int,
            convertView: View?,
            parent: ViewGroup?,
            resource: Int
        ): View {
            val view: View = convertView ?: inflater.inflate(resource, parent, false)
            val line = view.findViewById<View>(R.id.menu_pop_line)
            line.visibility = if (position == 0) View.GONE else View.VISIBLE
            val text: TextView = view.findViewById(R.id.menu_pop_text)
            text.text = getItem(position)
            text.isSelected = mItemSelectedPosition == position
            text.let { v ->
                when (position) {
                    0 -> {
                        v.background = drawable(R.drawable.selector_dialog_module_pressed_top_r)
                    }
                    datas.lastIndex -> {
                        v.background = drawable(R.drawable.selector_dialog_module_pressed_bottom_r)
                    }
                    else -> {
                        v.background = drawable(R.drawable.selector_dialog_module_pressed)
                    }
                }
            }
            return view
        }

        override fun getItemMaxWidth(): Int {
            val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
            textPaint.textSize = 12f.sp2px()
            val padding = 10f.dp2px() * 2
            var maxW = 0f
            datas.forEach {
                val w = textPaint.measureText(it)
                if (w > maxW) {
                    maxW = w + w * 0.1f
                }
            }
            return (maxW + padding).toInt()
        }

        override fun getItemMaxHeight(): Int {
            return itemHight
        }
    }

    interface IMeunPopAdapter {
        fun getItemMaxWidth(): Int
        fun getItemMaxHeight(): Int
    }

    open fun makeDropDownMeasureSpec(measureSpec: Int): Int {
        val mode: Int = if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
            View.MeasureSpec.UNSPECIFIED
        } else {
            View.MeasureSpec.EXACTLY
        }
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode)
    }


    private fun dimBehind(listView: ListView) {
        if (this.background == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val parent = listView.parent
                if (parent is View) updateViewLayoutDim(parent)
            } else {
                updateViewLayoutDim(listView)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val parentToParent = listView.parent?.parent
                if (parentToParent is View) updateViewLayoutDim(parentToParent)
            } else {
                val parent = listView.parent
                if (parent is View) updateViewLayoutDim(parent)
            }
        }
    }

    private fun updateViewLayoutDim(targetView: View) {
        val windowManager = targetView.context.getSystemService(Context.WINDOW_SERVICE)
        if (windowManager is WindowManager) {
            val layoutParams = targetView.layoutParams
            if (layoutParams is WindowManager.LayoutParams) {
                layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
                layoutParams.dimAmount = 0.5f
                windowManager.updateViewLayout(targetView, layoutParams)
            }
        }
    }
}