package com.zhuorui.commonwidget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import base2app.ex.color
import base2app.ex.drawable
import base2app.ex.logd
import base2app.ex.sp2px
import com.zhuorui.commonwidget.databinding.LayoutTitleExpandableViewBinding
import com.zhuorui.commonwidget.expandable.ExpandableLayout

/**
 * @author xuzuoliang
 * @email micrason@163.com
 * @date 2020/11/19 15:06
 * @desc 带Title的折叠View，提供公共标题和展开View封装，内部装载内容自定义
 */
class ZRTitleExpandableView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = LayoutTitleExpandableViewBinding.inflate(LayoutInflater.from(context),this)


    init {
        orientation = VERTICAL
//        View.inflate(context, R.layout.layout_title_expandable_view, this)
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.ZRTitleExpandableView)

        val backgroundDrawable = typeArray.getResourceId(R.styleable.ZRTitleExpandableView_expandableBackground, 0)

        if (backgroundDrawable != 0) {
            background = drawable(backgroundDrawable)
        }

        //设置可折叠layout
        val expandableLayoutResource = typeArray.getResourceId(
            R.styleable
                .ZRTitleExpandableView_expandableLayoutResource, 0
        )
        if (expandableLayoutResource != 0) {
            View.inflate(context, expandableLayoutResource, binding.expandableLayout)
        }


        //设置title文本
        val expandableTitle = typeArray.getString(
            R.styleable
                .ZRTitleExpandableView_expandableTitle
        )
        binding.tvExpandTitle.text = expandableTitle

        //设置titleColor
        val expandableTitleColor = typeArray.getColor(
            R.styleable
                .ZRTitleExpandableView_expandableTitleColor,
           color(R.color.main_content_text_color)
        )
        binding.tvExpandTitle.setTextColor(expandableTitleColor)

        //设置titleSize
        val expandableTitleSize = typeArray.getDimension(
            R.styleable
                .ZRTitleExpandableView_expandableTitleSize,
            14.sp2px()
        )
        binding.tvExpandTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, expandableTitleSize)

        binding.expandableLayout.setOnExpansionUpdateListener { expansionFraction, state ->
            logd("expansionFraction:$expansionFraction")
            when (state) {
                ExpandableLayout.State.COLLAPSING -> {
                    binding.imgArrow.rotation = -expansionFraction * 180
                }
                ExpandableLayout.State.EXPANDING -> {
                    binding.imgArrow.rotation = -expansionFraction * 180
                }
                ExpandableLayout.State.EXPANDED -> {
                    binding.imgArrow.rotation = 180f
                }
                ExpandableLayout.State.COLLAPSED -> {
                    binding.imgArrow.rotation = 0f
                }
            }
        }
        //设置默认是否展开
        val isExpanded = typeArray.getBoolean(R.styleable.ZRTitleExpandableView_expanded, false)
        binding.expandableLayout.setExpanded(isExpanded, false)


        //顶部分割线样式
        val topDivide = typeArray.getBoolean(R.styleable.ZRTitleExpandableView_expandableTopDivide, false)
        if (topDivide) {
            val topDivideColor = typeArray.getColor(
                R.styleable.ZRTitleExpandableView_expandableTopDivideColor,
               color(R.color.main_division_background)
            )
            binding.viewTitleDivide.let {
                it.visibility = VISIBLE
                it.setBackgroundColor(topDivideColor)
            }
        }


        typeArray.recycle()
        binding.layoutExpandableTitle.setOnClickListener {
            binding.expandableLayout.toggle()
        }

    }

    /**
     * 添加可折叠View
     */
    fun addExpandableView(expandableView: View) {
        binding.expandableLayout.addView(expandableView)
    }

    /**
     * 设置展开收起状态
     */
    fun setExpanded(isExpanded: Boolean, animation: Boolean = true) {
        binding.expandableLayout.setExpanded(isExpanded, animation)
    }

    /**
     * 设置title
     */
    fun setExpandableTitle(expandableTitle: String) {
        binding.tvExpandTitle.text = expandableTitle
    }

    /**
     * 设置titleColor
     */
    fun setExpandableTitleColor(expandableTitleColor: Int) {
        binding.tvExpandTitle.setTextColor(expandableTitleColor)
    }

    /**
     * 设置titleSize
     */
    fun setExpandableTitleSize(expandableTitleSize: Float) {
        binding.tvExpandTitle.textSize = expandableTitleSize
    }
}