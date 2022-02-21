package com.zhuorui.commonwidget.drawable

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * date : 2020/9/9
 * dest : DrawableLayout
 */
class DrawableLayout : View, ViewTreeObserver.OnGlobalLayoutListener, ViewTreeObserver.OnPreDrawListener {

    private val mDrawableMap = LinkedHashMap<Int, BaseDrawable>()

    private var isMeasureFinished: Boolean = false

    private var mGestureDetector: GestureDetector? = null

    var mOnDrawableClickListener: OnDrawableClickListener? = null

    constructor(context: Context?) : this(context, null) {}

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0) {}

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        viewTreeObserver.addOnPreDrawListener(this)
//        viewTreeObserver.addOnGlobalLayoutListener(this)
        mGestureDetector = GestureDetector(getContext(), DrawableGestureListener())
    }

    private var mLastMeasureWidth = width

    /**
     * 可以考虑将 measure 流程丢到子线程
     */
    private fun measureDrawables() {
        if (mDrawableMap.isEmpty() || width == 0 || height == 0) return
        var weightCount = 0f
        val fixedList = ArrayList<BaseDrawable>()//不定长的drawable
        var residueWidth = width
        for (drawable in mDrawableMap.values) {//筛选出使用weight的drawable
            if (drawable.mDrawableParams.visibility == GONE) continue
            weightCount += drawable.mDrawableParams.weight
            residueWidth -= drawable.mDrawableParams.paddingLeft + drawable.mDrawableParams.paddingRight
            if (drawable.mDrawableParams.weight != 0f) {
                fixedList.add(drawable)
            } else {//计算出剩余宽度
                if (drawable.mDrawableParams.width > 0) {
                    residueWidth -= drawable.mDrawableParams.width
                    resetDrawableBounds(drawable)
                } else {//需要根据具体内容来 measure 出宽度
                    drawable.measureContent()
                    residueWidth -= drawable.bounds.width()
                }
            }
        }

        for (drawable in fixedList) {
            drawable.mDrawableParams.width =
                (drawable.mDrawableParams.weight / weightCount * residueWidth).toInt()
            resetDrawableBounds(drawable)
        }

        var lastRight = 0
        for (drawable in mDrawableMap.values) {
            if (drawable.mDrawableParams.visibility == View.GONE) continue

            adjustDrawablePadding(lastRight, drawable)
            lastRight = drawable.bounds.right + drawable.mDrawableParams.paddingRight
        }
        isMeasureFinished = true
        mLastMeasureWidth = width
    }

    /**
     * 调整 padding
     */
    private fun adjustDrawablePadding(lastRight: Int, drawable: BaseDrawable) {
        val bWidth = drawable.bounds.width()
        val height = drawable.bounds.height()
        //宽高都是不会变的 , 这里只做了平移变相的操作
        drawable.bounds.left = max(0, lastRight + drawable.mDrawableParams.paddingLeft)
        drawable.bounds.right = min(width, drawable.bounds.left + bWidth)
        if (height == this.height || drawable.mDrawableParams.height == BaseDrawable.DrawableParams.MATCH_PARENT) { //如果是满高场景 , 将会缩小 drawable 的高度
            drawable.bounds.top =
                drawable.mDrawableParams.paddingTop
            drawable.bounds.bottom = this.height - drawable.mDrawableParams.paddingButtom
        } else {//单向的满足宽高比
            drawable.bounds.top =
                drawable.mDrawableParams.paddingTop - drawable.mDrawableParams.paddingButtom
            drawable.bounds.bottom = drawable.bounds.top + height
        }
    }

    /**
     * 重置 drawable bound 的宽度
     */
    private fun resetDrawableBounds(drawable: BaseDrawable) {
        if (drawable.bounds.width() != drawable.mDrawableParams.width) {
            var setHeight = drawable.mDrawableParams.height
            if (setHeight <= 0) setHeight = height
            drawable.bounds.set(0, 0, drawable.mDrawableParams.width, setHeight)
//            if (drawable is TextDrawable){
//                logd("${drawable.text} resetDrawableBounds ${drawable.bounds.gson() }")
//            }
        }
    }

    override fun requestLayout() {
        super.requestLayout()
        invalidate()//这里确保一下如果外部的宽度变化重新measure一次
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            if (mLastMeasureWidth != width)
                measureDrawables()//这里确保一下如果外部的宽度变化重新measure一次
            for (drawable in mDrawableMap.values) {
                if (drawable.mDrawableParams.visibility == VISIBLE) {
                    drawable.draw(it)
                }
            }
        }
    }

    /**
     * 根据对应的 id 找到对应的 Drawable
     */
    fun findDrawableById(id: Int): BaseDrawable? {
        return mDrawableMap[id]
    }

    /**
     * 按顺序追加 Drawable
     */
    fun appendDrawable(id: Int, drawable: BaseDrawable): DrawableLayout {
        mDrawableMap[id] = drawable
        drawable.mView = this
        drawable.id = id
        return this
    }

    /**
     * 用于简单批量的刷新text内容之后 , 统一刷新处理
     */
    fun enforceAllTextBuilderAndRefresh() {
        for (drawable in mDrawableMap.values){
            if (drawable is TextDrawable){
                drawable.enforceBuilder()
            }
        }
        invalidate()
    }

    /**
     * 用于全量刷新 , postInvalidate 最终也会调用此方法
     */
    override fun invalidate() {
        //刷新之前提前 measure
        measureDrawables()
        super.invalidate()
    }

    /**
     * 不调用 measure 方法进行刷新 UI
     */
    fun invalidateWithNotMeasure() {
        super.invalidate()
    }

    fun setTextValues(id: Int, what: TextDrawable.() -> Unit = {}): DrawableLayout {
        (findDrawableById(id) as TextDrawable).adjust(what).enforceBuilder()
        return this
    }

    fun setImageValue(id: Int, what: ImageDrawable.() -> Unit = {}): DrawableLayout {
        (findDrawableById(id) as ImageDrawable).adjust(what)
        return this
    }


    /**
     * 用于刷新对应 id 的 drawable
     */
    fun invalidate(id: Int) {
        if (isMeasureFinished) {//仅有在第一次 measure 完成之后才会需要刷新脏数据或者重新判断宽高
            findDrawableById(id)?.let {// TODO: 2020/9/22 这里需要再校验一下脏数据块刷新问题 , 按官方的讲需要关闭硬件加速
                postInvalidate(it.bounds.left, it.bounds.top, it.bounds.right, it.bounds.bottom)
            }
        }
    }

    /**
     * 用于刷新对应的子 drawable
     */
    fun invalidate(drawable: BaseDrawable) {
        if (isMeasureFinished) {//仅有在第一次 measure 完成之后才会需要刷新脏数据或者重新判断宽高
            if (drawable.mDrawableParams.width != 0 || drawable.mDrawableParams.weight != 0f) {
                postInvalidate(
                    drawable.bounds.left,
                    drawable.bounds.top,
                    drawable.bounds.right,
                    drawable.bounds.bottom
                )
            } else {//如果宽高不确定则需要重新 measure
                postInvalidate()
            }
        }
    }

    /**
     * 用 onPreDraw 会出现偶尔性的宽度拿不到的情况 ,  故而同时使用
     */
    override fun onGlobalLayout() {
        if (width == 0) return
        measureDrawables()
        postInvalidate()
        viewTreeObserver.removeOnGlobalLayoutListener(this)
        viewTreeObserver.removeOnPreDrawListener(this)
    }

    /**
     * 用 onGlobalLayout 会出现偶尔性的宽度 recycleview 拿不到的情况 , 故而同时使用
     */
    override fun onPreDraw(): Boolean {
        if (width == 0) return true
        measureDrawables()
        postInvalidate()
        viewTreeObserver.removeOnGlobalLayoutListener(this)
        viewTreeObserver.removeOnPreDrawListener(this)
        return true
    }

    /**
     * 防止泄露问题
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewTreeObserver.removeOnGlobalLayoutListener(this)
        viewTreeObserver.removeOnPreDrawListener(this)
    }

    fun setOnDrawableClickListener(l: OnDrawableClickListener?) {
        mOnDrawableClickListener = l
    }

    interface OnDrawableClickListener{
        fun onClick(id: Int)
    }

    inner class DrawableGestureListener : SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent?): Boolean {
            if (mDrawableMap.isEmpty() || width == 0 || height == 0 || mOnDrawableClickListener == null || e == null) return false
            for (drawable in mDrawableMap.values) {//筛选出使用weight的drawable
                if (!drawable.mDrawableParams.clickable) continue
                if (drawable.bounds.contains(e.x.toInt(), drawable.bounds.centerY()) && drawable.mDrawableParams.clickable) {//目前最好只根据x范围进行判断点击范围
                    return true //如果不返回 true  onSingleTapConfirmed 无法生效
                }
            }
            return false
        }

        /**
         * 单击事件
         */
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (mDrawableMap.isEmpty() || width == 0 || height == 0 || mOnDrawableClickListener == null) return false

            for (drawable in mDrawableMap.values) {//筛选出使用weight的drawable
                if (!drawable.mDrawableParams.clickable) continue
                if (drawable.bounds.contains(
                        e.x.toInt(),
                        drawable.bounds.centerY()
                    )
                ) {//目前最好只根据x范围进行判断点击范围
                    Log.d("onSingleTapConfirmed", "drawable.bounds.contains onSingleTapConfirmed: $drawable ")
                    mOnDrawableClickListener!!.onClick(drawable.id)
                    return true
                }
            }
            return false
        }

        /**
         * 双击事件, 只会出发一次,横竖屏切换时会用得到
         */
        override fun onDoubleTap(e: MotionEvent): Boolean {
            if (mDrawableMap.isEmpty() || width == 0 || height == 0) return false
            return true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return mGestureDetector!!.onTouchEvent(event)||super.onTouchEvent(event)
    }

}