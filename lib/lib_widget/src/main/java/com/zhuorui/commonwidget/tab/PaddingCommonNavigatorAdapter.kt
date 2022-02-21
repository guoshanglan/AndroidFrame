package com.zhuorui.commonwidget.tab

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.view.Gravity
import androidx.annotation.ColorRes
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import base2app.ex.*
import com.zhuorui.commonwidget.R
import com.zhuorui.securities.base2app.ex.*
import com.zhuorui.securties.skin.view.ZRSkinAble
import net.lucode.hackware.magicindicator.FragmentContainerHelper
import net.lucode.hackware.magicindicator.buildins.ArgbEvaluatorHolder
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.model.PositionData


/**
 *    date   : 2019-11-18 17:39
 *    desc   : 两端对齐 CommonNavigatorAdapter
 */
open class PaddingCommonNavigatorAdapter(titles: Array<String>) : CommonNavigatorAdapter() {


    /*如已显示在界面，再设置相关属性，需要调用view的更新方法*/

    private val mTitles: Array<String> = titles
    private val mWeights = FloatArray(titles.size)
    private var mViewPager: ViewPager? = null
    private var mViewPager2: ViewPager2? = null
    private var mListener: OnCommonNavigatorSelectListener? = null
    private var mTextSizeSp: Float = 14f
    private var mTotalWidthPx: Float = 0f
    private var mPaddingPx: Int = 0
    private var mSelectedColorId = R.color.main_content_text_color
    private var mNormalColorId = R.color.main_label_unselected_text_color
    private var mIndicatorColorId = R.color.brand_main_color
    private var indicatorHeight = 2f.dp2px()
    private var indicatorWidth = 20f.dp2px()
    private var indicatorMode: Int = LinePagerIndicator.MODE_WRAP_CONTENT
    private var indicatorXOffset = 0f
    private var mRoundRadius = 1f.dp2px()
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        indicatorHeight = 2f.dp2px()
        paint.textSize = 14f.dp2px()
        calculation()
    }

    fun setNormalColor(@ColorRes color: Int) {
        mNormalColorId = color
    }

    fun setSelectedColor(@ColorRes color: Int) {
        mSelectedColorId = color
    }

    fun setTotalWidthPx(widthPx: Float) {
        mTotalWidthPx = widthPx
        calculation()
    }

    fun setTextSizeSp(textSizeDp: Float) {
        mTextSizeSp = textSizeDp
        paint.textSize = mTextSizeSp.dp2px()
        calculation()
    }

    fun setIndicatorMode(mode: Int) {
        indicatorMode = mode
    }

    fun setIndicatorColor(@ColorRes color: Int) {
        mIndicatorColorId = color
    }

    fun setIndicatorHeight(height: Float) {
        indicatorHeight = height
    }

    fun setIndicatorXOffset(xOffset: Float) {
        indicatorXOffset = xOffset
    }

    fun setIndicatorWidth(width: Float) {
        indicatorWidth = width
    }

    fun setRoundRadius(radius: Float) {
        mRoundRadius = radius
    }


    private fun calculation() {
        val size = mTitles.size
        var totalTextWidth = 0f
        for ((index, element) in mTitles.withIndex()) {
            val textWidth = paint.measureText(element)
            mWeights[index] = textWidth
            totalTextWidth += textWidth
        }
        mPaddingPx = ((mTotalWidthPx - totalTextWidth) / (size - 1) * 0.5f).toInt()
        for ((index, textWidth) in mWeights.withIndex()) {
            val padding = when (index) {
                0, size - 1 -> {
                    mPaddingPx
                }
                else -> {
                    mPaddingPx + mPaddingPx
                }
            }
            mWeights[index] = mWeights[index] + padding
        }
    }

    override fun getCount(): Int {
        return mTitles.size
    }


    override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
        val titleView = PaddinTitleView(context,mNormalColorId,mSelectedColorId)
        titleView.textSize = mTextSizeSp
        titleView.text = mTitles[index]
        titleView.setSafeClickListener {
            mViewPager2?.currentItem = index
            mViewPager?.currentItem = index
            mListener?.onSelected(index)
        }
        if (mTotalWidthPx > 0f) {
            //比计算pading缩小一点，留一点显示空间，因为对文字的计算不是绝对精确
            val padding: Int = (mPaddingPx * 0.9).toInt()
            when (index) {
                0 -> {
                    titleView.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
                    titleView.setPadding(0, 0, padding, 0)
                }
                count - 1 -> {
                    titleView.gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
                    titleView.setPadding(padding, 0, 0, 0)
                }
                else -> {
                    titleView.gravity = Gravity.CENTER
                    titleView.setPadding(padding, 0, padding, 0)
                }
            }
        }
        return titleView
    }

    override fun getTitleWeight(context: Context?, index: Int): Float {
        if (mTotalWidthPx == 0f) {
            return super.getTitleWeight(context, index)
        }
        return mWeights[index]
    }

    override fun getIndicator(context: Context?): IPagerIndicator {
        val indicator = PaddingIndicator(context)
        indicator.mode = indicatorMode
        indicator.lineHeight = indicatorHeight
        indicator.lineWidth = indicatorWidth
        indicator.xOffset = indicatorXOffset
        indicator.roundRadius = mRoundRadius
        return indicator
    }

    fun bindViewPager(viewPager: ViewPager) {
        mViewPager = viewPager
    }

    fun bindViewPager(viewPager: ViewPager2) {
        mViewPager2 = viewPager
    }

    fun bindListener(l: OnCommonNavigatorSelectListener) {
        mListener = l
    }

    inner class PaddingIndicator : LinePagerIndicator, ZRSkinAble {

        constructor(context: Context?):super(context){
            applyUIMode(context?.resources)
        }

        override fun applyUIMode(resources: Resources?) {
            setColors(color(if (mIndicatorColorId == 0) mSelectedColorId else mIndicatorColorId))
            invalidate()
        }

        override fun onAttachedToWindow() {
            super.onAttachedToWindow()
            skin { applyUIMode(it) }

        }

        override fun onDetachedFromWindow() {
            super.onDetachedFromWindow()
            unregistSkin()

        }

        private val exactlyLineRect = RectF()
        private var mPositionDataList: List<PositionData>? = null

        override fun onPositionDataProvide(dataList: MutableList<PositionData>?) {
            super.onPositionDataProvide(dataList)
            mPositionDataList = dataList
        }

        override fun onDraw(canvas: Canvas?) {
            if (mode == MODE_EXACTLY) {
                canvas?.drawRoundRect(exactlyLineRect, roundRadius, roundRadius, paint)
            } else {
                super.onDraw(canvas)
            }
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            if (mode == MODE_EXACTLY) {
                mPositionDataList
                if (mPositionDataList.isNullOrEmpty()) {
                    return
                }
                val mColors = colors
                // 计算颜色
                if (mColors != null && mColors.size > 0) {
                    val currentColor = mColors[Math.abs(position) % mColors.size]
                    val nextColor = mColors[Math.abs(position + 1) % mColors.size]
                    val color = ArgbEvaluatorHolder.eval(positionOffset, currentColor, nextColor)
                    paint.color = color
                }

                // 计算锚点位置
                val current =
                    FragmentContainerHelper.getImitativePositionData(mPositionDataList, position)
                val next = FragmentContainerHelper.getImitativePositionData(
                    mPositionDataList,
                    position + 1
                )
                val currentOffset =
                    (current.mContentRight - current.mContentLeft - lineWidth) * 0.5f
                val leftX = current.mContentLeft + currentOffset
                val rightX = current.mContentRight - currentOffset
                val nextOffset = (next.mContentRight - next.mContentLeft - lineWidth) * 0.5f
                val nextLeftX = next.mContentLeft + nextOffset
                val nextRightX = next.mContentRight - nextOffset
                exactlyLineRect.left =
                    leftX + (nextLeftX - leftX) * startInterpolator.getInterpolation(positionOffset)
                exactlyLineRect.right =
                    rightX + (nextRightX - rightX) * endInterpolator.getInterpolation(positionOffset)
                exactlyLineRect.top = height.toFloat() - lineHeight - yOffset
                exactlyLineRect.bottom = height - yOffset
                invalidate()
            } else {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
        }


    }

    /**
     * 当左右设置padding不相等时，计算Indicator的位置信息
     */
    inner class PaddinTitleView(context: Context?, normalColorId: Int? = null, selectedColorId: Int? = null) : CommonNavigatorTitleView(context, normalColorId, selectedColorId) {

        override fun getContentLeft(): Int {
            val bound = Rect()
            paint.getTextBounds(text.toString(), 0, text.length, bound)
            val contentWidth = bound.width()
            return paddingLeft + left + (width - paddingLeft - paddingRight) / 2 - contentWidth / 2
        }

        override fun getContentRight(): Int {
            val bound = Rect()
            paint.getTextBounds(text.toString(), 0, text.length, bound)
            val contentWidth = bound.width()
            return contentLeft + contentWidth
        }


    }

    interface OnCommonNavigatorSelectListener {
        fun onSelected(index: Int)
    }

}