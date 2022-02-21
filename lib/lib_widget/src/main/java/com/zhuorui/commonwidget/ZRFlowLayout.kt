package com.zhuorui.commonwidget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhuorui.securities.base2app.util.SimpleWeakObjectPool
import kotlinx.android.extensions.LayoutContainer


/**
 * @date 2020/11/11 17:21
 * @desc 自动换行标签Layout
 */
class ZRFlowLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    ViewGroup(context, attrs, defStyle), ViewGroup.OnHierarchyChangeListener {

    // 行间距
    private val mRowSpacing: Float

    // 列间距
    private val mColumnSpacing: Float

    private var objectPool: SimpleWeakObjectPool<View>? = null


    init {
        setOnHierarchyChangeListener(this)
        objectPool = SimpleWeakObjectPool(5)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ZRFlowLayout)
        mRowSpacing = typedArray.getDimensionPixelSize(R.styleable.ZRFlowLayout_verticalSpace, 0).toFloat()
        mColumnSpacing = typedArray.getDimensionPixelSize(R.styleable.ZRFlowLayout_horizontalSpace, 0).toFloat()
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        var expectHeight = 0 // 期望高度，累加 child 的 height
        var lineWidth = 0 // 单行宽度，动态计算当前行的宽度。
        var lineHeight = 0 // 单行高度，取该行中高度最大的view
        var widthSpacing: Float
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            // 测量子view 的宽高
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight
            widthSpacing = if (i == 0) 0f else mColumnSpacing

            // 这里进行的是预判断。追加该child 后，行宽
            // 若未超过提供的最大宽度，则行宽需要追加child 的宽度，并且计算该行的最大高度。
            // 若超过提供的最大宽度，则需要追加该行的行高，并且更新下一行的行宽为当前child 的测量宽度。
            if (lineWidth + widthSpacing + childWidth + paddingLeft + paddingRight <= widthSize) { // 未超过一行
                // 追加行宽。
                lineWidth += (widthSpacing + childWidth).toInt()
                // 不断对比，获取该行的最大高度
                lineHeight = lineHeight.coerceAtLeast(childHeight)
            } else { // 超过一行
                // 更新最新一行的宽度为此child 的测量宽度
                lineWidth = childWidth
                // 期望高度追加当前行的行高。
                expectHeight += lineHeight + mRowSpacing.toInt()
            }
        }

        // 这里添加的是最后一行的高度。因为上面是在换行时才追加的行高，在不需要换行时并没有追加行高，丢失了不满足换行条件下的行高。
        // 举例说明：比如一行最多显示5个，但是当前只有1个，或者当前有6个的情况下，少了一行的行高。
        expectHeight += lineHeight

        // 追加ViewGroup 的内边距
        expectHeight += paddingTop + paddingBottom
        setMeasuredDimension(widthSize, resolveSize(expectHeight, heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = r - l
        var childLeftOffset = paddingLeft // child view 的left偏移距离，用于记录左边位置。
        var childTopOffset = paddingTop // child view 的top偏移距离，用于记录顶部位置。
        var lineHeight = 0 // 行高
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            //跳过View.GONE的子View
            if (child.visibility == GONE) {
                continue
            }
            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight
            if (childLeftOffset + childWidth + paddingRight <= width) { // 该child 加入后未超过一行宽度。
                // 行高取这一行中最高的child height
                lineHeight = lineHeight.coerceAtLeast(childHeight)
            } else { // 超过一行，换行显示。换行后的左侧偏移为初始值，顶部偏移为当前偏移量+当前行高
                childLeftOffset = paddingLeft
                childTopOffset += lineHeight + mRowSpacing.toInt()
                lineHeight = childHeight
            }
            child.layout(childLeftOffset, childTopOffset, childLeftOffset + childWidth, childTopOffset + childHeight)

            // 更新左侧偏移距离(+间距)，即下一个child 的left
            childLeftOffset += childWidth + mColumnSpacing.toInt()
        }
    }

    fun setAdapter(adapter: IFlowAdapter<*>) {
        if (adapter.getCount() <= 0) {
            removeAllViews()
            return
        }
        val oldCount = childCount
        val newCount = adapter.getCount()
        //如果缓存的View有多余的，则清除多余的
        if (newCount < oldCount) {
            removeViews(newCount, oldCount - newCount)
        }
        //重新遍历设置数据
        for (i in 0 until newCount) {
            val hasChild = i < oldCount
            var convertView = if (hasChild) getChildAt(i) else null
            if (convertView == null) {
                //convertView为空，先检查缓冲池是否有值
                convertView = objectPool?.get()
                val childView = adapter.getView(i, convertView, this)
                addView(childView, i, childView?.layoutParams)
            } else {
                adapter.getView(i, convertView, this)
            }
        }
        requestLayout()
    }

    abstract class SimpleFlowAdapter<T>(private val mDataList: List<T>) : IFlowAdapter<T> {

        override fun getCount(): Int {
            return mDataList.size
        }

        override fun getItem(position: Int): T {
            return mDataList[position]
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var localView = convertView
            val flowLayoutViewHolder: FlowLayoutViewHolder
            if (localView == null) {
                localView = LayoutInflater.from(parent.context).inflate(getLayoutResource(), parent, false)
                flowLayoutViewHolder = onCreateViewHolder(localView)
                localView.tag = flowLayoutViewHolder
            } else {
                flowLayoutViewHolder = localView.tag as FlowLayoutViewHolder
            }
            bindData(flowLayoutViewHolder, position, getItem(position))
            return localView!!
        }

        abstract fun bindData(flowLayoutViewHolder: FlowLayoutViewHolder, position: Int, data: T)

        abstract fun onCreateViewHolder(itemView: View): FlowLayoutViewHolder

        abstract fun getLayoutResource(): Int

        abstract class FlowLayoutViewHolder(val itemView: View) : LayoutContainer {
            override val containerView: View? get() = itemView
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }


    interface IFlowAdapter<T> {
        fun getCount(): Int
        fun getItem(position: Int): T
        fun getView(position: Int, convertView: View?, parent: ViewGroup): View?
    }


    override fun onChildViewAdded(parent: View?, child: View?) {
    }

    override fun onChildViewRemoved(parent: View?, child: View?) {
        child?.let {
            //已经被移除的View，加入缓存池
            objectPool?.put(it)
        }
    }
}

