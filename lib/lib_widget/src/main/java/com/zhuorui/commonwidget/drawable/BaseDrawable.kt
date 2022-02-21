package com.zhuorui.commonwidget.drawable

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup.MarginLayoutParams

/**
 * date : 2020/9/9
 * dest : BaseDrawable
 */
abstract class BaseDrawable(var mDrawableParams: DrawableParams) : Drawable() {

    var id: Int = -1
    var mView: DrawableLayout? = null

    /**
     * 当 DrawableParams 无法确定宽高时 , 由内部自行 measure 出内容的宽高出来
     */
    abstract fun measureContent()

    /**
     * 更新当前的 drawable
     */
    fun invalidate() {
        mView?.invalidate(this)
    }

    open class DrawableParams : MarginLayoutParams {//目前只考虑内边距

        var weight = 0f

        //如果宽高不确定(WRAP_CONTENT, WRAP_CONTENT) , 那么gravity代表的是整体画布的偏移处理 , 反之则是内容的偏移处理, 横向内容只负责上下的 gravity
        var gravity = -1

        var paddingLeft = 0

        var paddingRight = 0

        var paddingTop = 0

        var paddingButtom = 0

        var visibility = View.VISIBLE

        var clickable = false

        constructor() : super(WRAP_CONTENT, WRAP_CONTENT)

        constructor(width: Int, height: Int) : super(width, height) {
            weight = 0f
        }

        constructor(width: Int, height: Int, weight: Float) : super(width, height) {
            this.weight = weight
        }

        constructor(source: DrawableParams) : super(source) {
            weight = source.weight
            gravity = source.gravity
        }

        fun padding(padding: Int): DrawableParams {
            paddingLeft = padding

            paddingRight = padding

            paddingTop = padding

            paddingButtom = padding
            return this
        }

        fun padding(left: Int, top: Int, right: Int, bottom: Int): DrawableParams {
            paddingLeft = left

            paddingRight = right

            paddingTop = top

            paddingButtom = bottom
            return this
        }

        fun visibility(visibility: Int): DrawableParams {
            this.visibility = visibility
            return this
        }

        fun clickable(clickable: Boolean): DrawableParams {
            this.clickable = clickable
            return this
        }

        fun weight(weight: Float): DrawableParams {
            this.weight = weight
            return this
        }

        fun gravity(gravity: Int): DrawableParams {
            this.gravity = gravity
            return this
        }

        //按简单的逻辑来讲 , 除了固定的宽高以及 weight 不允许使用 WRAP_CONTENT 或者 MATCH_PARENT 来进行适配比
        companion object {
            const val WRAP_CONTENT = -2

            const val FILL_PARENT = -1

            const val MATCH_PARENT = -1

            @JvmStatic
            fun builder(what: DrawableParams.() -> Unit = {}): DrawableParams {
                val builder = DrawableParams()
                builder.what()
                return builder
            }
        }
    }
}