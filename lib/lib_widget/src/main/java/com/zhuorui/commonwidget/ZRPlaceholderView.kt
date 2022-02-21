package com.zhuorui.commonwidget

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.*
import androidx.appcompat.view.ContextThemeWrapper
import base2app.ex.color
import base2app.ex.dp2px
import base2app.ex.setSafeClickListener
import base2app.ex.sp2px
import com.zhuorui.securties.skin.view.ZRSkinAble
import java.lang.annotation.ElementType.*
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy.CLASS
import java.lang.annotation.Target

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/4/19 13:55
 *    desc   : 占位view
 */
class ZRPlaceholderView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), ZRSkinAble {

    companion object {

        /**
         * 正常模式
         */
        const val MODE_NORMAL = 0

        /**
         * 极简模式
         */
        const val MODE_MINIMALISM = 1

        /**
         * Button模式
         */
        const val MODE_BUTTON = 2


        @IntDef(MODE_NORMAL, MODE_MINIMALISM, MODE_BUTTON)
        @Retention(CLASS)
        @Target(METHOD, PARAMETER, FIELD, LOCAL_VARIABLE)
        annotation class Mode

    }

    private var pan: Paint? = null
    private var mModel = MODE_NORMAL
    @DrawableRes
    private var mIconResId = 0
    @Px
    private var mIconWidth = 0
    @Px
    private var mIconHight = 0
    @Px
    private var mTitleTextSize = 0
    @ColorRes
    private var mTitleTextColorRes = 0
    @Px
    private var mTipsTextSize = 0
    @ColorRes
    private var mTipsTextColorRes = 0
    @Px
    private var mBtnTextSize = 0
    @Px
    private var mBtnMiniWidth = 0
    @Px
    private var mBtnMiniHight = 0
    @ColorRes
    private var mBtnTextColorRes = 0
    private var mBtnText: CharSequence = ""
    private var mTipsText: CharSequence? = null
    private var mTitleText: CharSequence? = null
    private var mBtnClickListener: OnClickListener? = null
    @StyleRes
    private var mStateBtnStyle = R.style.PlaceholderStateButton

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        attrs?.let {
            val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ZRPlaceholderView)
            initStyleable(a)
            a.recycle()
            pan = initPaint()
        }

    }

    constructor(context: Context, @StyleRes styleId: Int) : this(context) {
        if (styleId != 0) {
            val a: TypedArray = context.obtainStyledAttributes(styleId, R.styleable.ZRPlaceholderView)
            initStyleable(a)
            a.recycle()
            pan = initPaint()
        }
    }

    private fun initPaint(): Paint? {
        pan?.detached()
        return when (mModel) {
            MODE_MINIMALISM -> MinimalismPan(this)
            MODE_BUTTON -> ButtonPan(this)
            else -> NormalPan(this)
        }.also { it.initView() }
    }

    private fun initStyleable(a: TypedArray) {
        mModel = a.getInteger(R.styleable.ZRPlaceholderView_mode, MODE_NORMAL)

        mIconResId = a.getResourceId(R.styleable.ZRPlaceholderView_iconRes,0)
        mIconWidth = a.getDimensionPixelOffset(R.styleable.ZRPlaceholderView_iconWidth, 0)
        mIconHight = a.getDimensionPixelOffset(R.styleable.ZRPlaceholderView_iconHeight, 0)

        mTitleText = a.getString(R.styleable.ZRPlaceholderView_titleText)
        mTitleTextSize = a.getDimensionPixelOffset(R.styleable.ZRPlaceholderView_titleTextSize, 0)
        mTitleTextColorRes = a.getResourceId(R.styleable.ZRPlaceholderView_titleTextColor, 0)

        mTipsText = a.getString(R.styleable.ZRPlaceholderView_tipsText)
        mTipsTextSize = a.getDimensionPixelOffset(R.styleable.ZRPlaceholderView_tipsTextSize, 12f.dp2px().toInt())
        mTipsTextColorRes = a.getResourceId(R.styleable.ZRPlaceholderView_tipsTextColor, 0)

        mBtnText = a.getString(R.styleable.ZRPlaceholderView_btnText) ?: ""
        mBtnTextSize = a.getDimensionPixelOffset(R.styleable.ZRPlaceholderView_btnTextSize, 0)
        mBtnMiniWidth = a.getDimensionPixelOffset(R.styleable.ZRPlaceholderView_btnMiniWidth, 0)
        mBtnMiniHight = a.getDimensionPixelOffset(R.styleable.ZRPlaceholderView_btnMiniHeight, 0)
        mBtnTextColorRes = a.getResourceId(R.styleable.ZRPlaceholderView_btnTextColor, 0)

    }


    override fun applyUIMode(resources: Resources?) {
        pan?.applyUIMode()
    }

    fun setMode(@Mode model: Int) {
        if (mModel == model && pan != null) return
        mModel = model
        pan = initPaint()
    }

    fun setIcon(@DrawableRes icon: Int) {
        mIconResId = icon
        pan?.setIcon(icon)
    }

    fun setIconSize(@Px width: Int, @Px height: Int) {
        mIconWidth = width
        mIconHight = height
        pan?.setIconSize(width, height)
    }

    fun setTitleText(text: CharSequence?) {
        mTitleText = text
        pan?.setTitleText(text)
    }

    fun setTitleTextSize(@Px textSize: Int) {
        mTitleTextSize = textSize
        pan?.setTipsTextSize(textSize)
    }

    fun setTitleTextColor(@ColorRes colorId: Int) {
        mTitleTextColorRes = colorId
        pan?.setTitleTextColor(colorId)
    }

    fun setTipsText(text: CharSequence?) {
        mTipsText = text
        pan?.setTipsText(text)
    }

    fun setTipsTextSize(@Px textSize: Int) {
        mTipsTextSize = textSize
        pan?.setTipsTextSize(textSize)
    }

    fun setTipsTextColor(@ColorRes colorId: Int) {
        mTipsTextColorRes = colorId
        pan?.setTipsTextColor(colorId)
    }

    fun setButtonText(text: CharSequence?) {
        mBtnText = text ?: ""
        pan?.setButtonText(text)
    }

    fun setStateBtnStyle(@StyleRes styleId: Int){
        if (styleId != 0){
            mStateBtnStyle = styleId
            pan?.setStateBtnStyle(styleId)
        }
    }

    fun setButtonTextSize(@Px textSize: Int) {
        mBtnTextSize = textSize
        pan?.setButtonTextSize(textSize)
    }

    fun setButtonTextColor(@ColorRes colorId: Int) {
        mBtnTextColorRes = colorId
        pan?.setButtonTextColor(colorId)
    }

    fun setButtonSize(@Px miniWidth: Int, @Px miniHeight: Int) {
        mBtnMiniWidth = miniWidth
        mBtnMiniHight = miniHeight
        pan?.setButtonSize(miniWidth, miniHeight)
    }

    fun setButtonCLickListener(click: OnClickListener?) {
        mBtnClickListener = click
        pan?.setButtonCLickListener(click)
    }

    private inner class MinimalismPan(val rootView: LinearLayout) : Paint {

        private var vTips: TextView? = null
        private var vTitle: TextView? = null

        override fun initView() {
            setTitleText(mTitleText)
            setTipsText(mTipsText)
        }

        override fun detached() {
            val array = arrayOf(vTitle, vTips)
            array.forEach { v ->
                v?.parent?.let {
                    (it as ViewGroup).removeView(v)
                }
            }
            vTips = null
            vTitle = null
        }

        override fun setIcon(icon: Int) {
        }

        override fun setIconSize(width: Int, height: Int) {
        }

        override fun setTitleText(text: CharSequence?) {
            if (TextUtils.isEmpty(text)) {
                vTitle?.let { v ->
                    if (rootView.indexOfChild(v) == 0) {
                        rootView.getChildAt(1)?.let {
                            it.layoutParams as LayoutParams
                        }?.let {
                            it.topMargin = 0
                        }
                    }
                    rootView.removeView(v)
                }
            } else {
                val v = vTitle ?: TextView(rootView.context).also {
                    it.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                    it.gravity = Gravity.CENTER
                    if (mTitleTextColorRes != 0) {
                        it.setTextColor(color(mTitleTextColorRes))
                    }
                    if (mTitleTextSize > 0) {
                        it.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleTextSize.toFloat())
                    }
                }
                v.text = text
                if (v.parent == null) {
                    rootView.getChildAt(0)?.let {
                        it.layoutParams as LayoutParams
                    }?.let {
                        it.topMargin = 10f.dp2px().toInt()
                    }
                    rootView.addView(v, 0)
                }
                vTitle = v
            }
        }

        override fun setTitleTextSize(textSize: Int) {
            vTitle?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        }

        override fun setTitleTextColor(colorId: Int) {
            if (colorId != 0) {
                vTitle?.setTextColor(color(colorId))
            }
        }

        override fun setTipsText(text: CharSequence?) {
            if (TextUtils.isEmpty(text)) {
                vTips?.let { v ->
                    if (rootView.indexOfChild(v) == 0) {
                        rootView.getChildAt(1)?.let {
                            it.layoutParams as LayoutParams
                        }?.let {
                            it.topMargin = 0
                        }
                    }
                    rootView.removeView(v)
                }
            } else {
                val v = vTips ?: TextView(rootView.context).also {
                    it.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                    it.gravity = Gravity.CENTER
                    if (mTitleTextColorRes != 0) {
                        it.setTextColor(color(mTipsTextColorRes))
                    }
                    if (mTipsTextSize > 0) {
                        it.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTipsTextSize.toFloat())
                    }
                }
                if (mBtnClickListener != null && !TextUtils.isEmpty(mBtnText)) {
                    val ssb = SpannableStringBuilder(text)
                    SpannableString("，$mBtnText").apply {
                        val color = if (mBtnTextColorRes != 0) {
                            color(mBtnTextColorRes)
                        } else {
                            color(R.color.selector_text_btn_textcolor)
                        }
                        setSpan(
                            ForegroundColorSpan(color),
                            1,
                            length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        setSpan(
                            MyClickableSpan(mBtnClickListener!!),
                            2,
                            length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }.let {
                        ssb.append(it)
                    }
                    v.movementMethod = LinkMovementMethod.getInstance()
                    v.text = ssb
                } else {
                    v.movementMethod = null
                    v.text = text
                }
                if (v.parent == null) {
                    var index = 0
                    if (vTitle != null) index++
                    if (index == 0) {
                        rootView.getChildAt(0)?.let {
                            it.layoutParams as LayoutParams
                        }?.let {
                            it.topMargin = 10f.dp2px().toInt()
                        }
                    } else {
                        v.layoutParams?.let {
                            it as LayoutParams
                        }?.let {
                            it.topMargin = 10f.dp2px().toInt()
                        }
                    }
                    rootView.addView(v, index)
                }
                vTips = v
            }
        }

        override fun setTipsTextSize(textSize: Int) {
            if (textSize > 0) {
                vTips?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
            }
        }

        override fun setTipsTextColor(colorId: Int) {
            if (colorId != 0) {
                vTips?.setTextColor(color(colorId))
            }
        }

        override fun setButtonText(text: CharSequence?) {
            setTipsText(mTipsText)
        }

        override fun setButtonTextSize(textSize: Int) {
        }

        override fun setButtonTextColor(colorId: Int) {
            setTipsText(mTipsText)
        }

        override fun setButtonSize(miniWidth: Int, miniHeight: Int) {

        }

        override fun setButtonCLickListener(click: OnClickListener?) {
            setTipsText(mTipsText)
        }

        override fun applyUIMode() {
            if (mTitleTextColorRes != 0) {
                vTitle?.setTextColor(color(mTitleTextColorRes))
            }
            if (mTipsTextColorRes != 0) {
                vTitle?.setTextColor(color(mTipsTextColorRes))
            }
            setTipsText(mTipsText)
        }

        override fun setStateBtnStyle(styleId: Int) {

        }

    }

    private open inner class NormalPan(val rootView: LinearLayout) : Paint {

        private var vTips: TextView? = null
        private var vIcon: IconView? = null
        private var vTitle: TextView? = null
        protected var vBtn: TextView? = null

        override fun initView() {
            setTitleText(mTitleText)
            setIcon(mIconResId)
            setTipsText(mTipsText)
            mBtnClickListener?.let {
                setButtonCLickListener(it)
            }
        }

        override fun detached() {
            val array = arrayOf(vTitle, vTips, vIcon, vBtn)
            array.forEach { v ->
                v?.parent?.let {
                    (it as ViewGroup).removeView(v)
                }
            }
            vBtn?.setOnClickListener(null)
            vTips = null
            vTitle = null
            vIcon = null
            vBtn = null

        }

        override fun setIcon(@DrawableRes icon: Int) {
            if (icon == 0) {
                vIcon?.let { icon ->
                    rootView.getChildAt(1)?.let {
                        it.layoutParams as LayoutParams
                    }?.let {
                        it.topMargin = 0
                    }
                    rootView.removeView(icon)
                }
            } else {
                val vic = vIcon ?: IconView(rootView.context).also {
                    it.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                    it.setDrawableSize(mIconWidth, mIconHight)
                }
                vic.setImageResource(icon)
                if (vic.parent == null) {
                    rootView.getChildAt(0)?.let {
                        it.layoutParams as LayoutParams
                    }?.let {
                        it.topMargin = 10f.dp2px().toInt()
                    }
                    rootView.addView(vic, 0)
                }
                vIcon = vic
            }
        }

        override fun setIconSize(@Px width: Int, @Px height: Int) {
            vIcon?.setDrawableSize(width, height)
        }

        override fun setTitleText(text: CharSequence?) {
            if (TextUtils.isEmpty(text)) {
                vTitle?.let { v ->
                    if (rootView.indexOfChild(v) == 0) {
                        rootView.getChildAt(1)?.let {
                            it.layoutParams as LayoutParams
                        }?.let {
                            it.topMargin = 0
                        }
                    }
                    rootView.removeView(v)
                }
            } else {
                val v = vTitle ?: TextView(rootView.context).also {
                    it.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                    it.gravity = Gravity.CENTER
                    if (mTitleTextColorRes != 0) {
                        it.setTextColor(color(mTitleTextColorRes))
                    }
                    if (mTitleTextSize > 0) {
                        it.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleTextSize.toFloat())
                    }
                }
                v.text = text
                if (v.parent == null) {
                    val index = if (vIcon == null) 0 else 1
                    if (index == 0) {
                        rootView.getChildAt(0)?.let {
                            it.layoutParams as LayoutParams
                        }?.let {
                            it.topMargin = 10f.dp2px().toInt()
                        }
                    } else {
                        v.layoutParams?.let {
                            it as LayoutParams
                        }?.let {
                            it.topMargin = 10f.dp2px().toInt()
                        }
                    }
                    rootView.addView(v, index)
                }
                vTitle = v
            }
        }

        override fun setTitleTextSize(@Px textSize: Int) {
            vTitle?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())

        }

        override fun setTitleTextColor(@ColorRes colorId: Int) {
            if (colorId != 0) {
                vTitle?.setTextColor(color(colorId))
            }

        }

        override fun setTipsText(text: CharSequence?) {
            if (TextUtils.isEmpty(text)) {
                vTips?.let { v ->
                    if (rootView.indexOfChild(v) == 0) {
                        rootView.getChildAt(1)?.let {
                            it.layoutParams as LayoutParams
                        }?.let {
                            it.topMargin = 0
                        }
                    }
                    rootView.removeView(v)
                }
            } else {
                val v = vTips ?: TextView(rootView.context).also {
                    it.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                    it.gravity = Gravity.CENTER
                    if (mTitleTextColorRes != 0) {
                        it.setTextColor(color(mTipsTextColorRes))
                    }
                    if (mTipsTextSize > 0) {
                        it.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTipsTextSize.toFloat())
                    }
                }
                v.text = text
                if (v.parent == null) {
                    var index = 0
                    if (vIcon != null) index++
                    if (vTitle != null) index++
                    if (index == 0) {
                        rootView.getChildAt(0)?.let {
                            it.layoutParams as LayoutParams
                        }?.let {
                            it.topMargin = 10f.dp2px().toInt()
                        }
                    } else {
                        v.layoutParams?.let {
                            it as LayoutParams
                        }?.let {
                            it.topMargin = 10f.dp2px().toInt()
                        }
                    }
                    rootView.addView(v, index)
                }
                vTips = v
            }
        }

        override fun setTipsTextSize(@Px textSize: Int) {
            if (textSize > 0) {
                vTips?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
            }
        }

        override fun setTipsTextColor(@ColorRes colorId: Int) {
            if (colorId != 0) {
                vTips?.setTextColor(color(colorId))
            }
        }

        override fun setButtonText(text: CharSequence?) {
            vBtn?.text = mBtnText
        }

        override fun setButtonTextSize(@Px textSize: Int) {
            vBtn?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        }

        override fun setButtonTextColor(@ColorRes colorId: Int) {
            if (colorId != 0) {
                vBtn?.setTextColor(color(colorId))
            }
        }

        override fun setButtonSize(@Px miniWidth: Int, @Px miniHeight: Int) {
        }

        override fun setButtonCLickListener(click: OnClickListener?) {
            vBtn = if (click == null) {
                val btn = vBtn ?: return
                btn.setOnClickListener(null)
                rootView.removeView(btn)
                null
            } else {
                val btn = vBtn ?: getButtonView()
                if (btn.parent == null) {
                    rootView.addView(btn)
                }
                btn.setSafeClickListener {
                    mBtnClickListener?.onClick(btn)
                }
                btn
            }
        }

        override fun applyUIMode() {
            vIcon?.setImageResource(mIconResId)
            if (mTitleTextColorRes != 0) {
                vTitle?.setTextColor(color(mTitleTextColorRes))
            }
            if (mTipsTextColorRes != 0) {
                vTips?.setTextColor(color(mTipsTextColorRes))
            }
            if (mBtnTextColorRes != 0) {
                vBtn?.setTextColor(color(mBtnTextColorRes))
            }
        }

        override fun setStateBtnStyle(styleId: Int) {
            if (vBtn != null) {
                rootView.removeView(vBtn)
                vBtn = getButtonView().also {
                    rootView.addView(it)
                    setSafeClickListener {
                        mBtnClickListener?.onClick(it)
                    }
                }
            }
        }

        protected open fun getButtonView(): TextView {
            val button = TextView(rootView.context)
            val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            if (mBtnTextColorRes != 0) {
                button.setTextColor(color(mBtnTextColorRes))
            }
            button.setTextSize(TypedValue.COMPLEX_UNIT_PX, if (mBtnTextSize > 0) mBtnTextSize.toFloat() else 12f.sp2px())
            lp.setMargins(0, 10f.dp2px().toInt(), 0, 10f.dp2px().toInt())
            button.layoutParams = lp
            button.gravity = Gravity.CENTER
            button.text = mBtnText
            return button
        }

    }

    private inner class ButtonPan(rootView: LinearLayout) : NormalPan(rootView) {

        override fun setButtonSize(miniWidth: Int, miniHeight: Int) {
            vBtn?.apply {
                layoutParams?.height = if (miniHeight > 0) miniHeight else LayoutParams.WRAP_CONTENT
                minWidth = miniWidth
            }
        }

        override fun getButtonView(): TextView {
            val button = StateButton(ContextThemeWrapper(context, mStateBtnStyle))
            var width:Int = LayoutParams.WRAP_CONTENT
            var height:Int = LayoutParams.WRAP_CONTENT
            var minWidth = 0
            intArrayOf(
                android.R.attr.layout_width,
                android.R.attr.layout_height,
                android.R.attr.minWidth
            ).forEach {
                val a: TypedArray = button.context.obtainStyledAttributes(mStateBtnStyle, intArrayOf(it))
                when(it){
                    android.R.attr.minWidth ->{
                        if (a.getType(0) == TypedValue.TYPE_DIMENSION) {
                            minWidth = a.getDimensionPixelOffset(0, minWidth)
                        }
                    }
                    android.R.attr.layout_width -> {
                        if (a.getType(0) == TypedValue.TYPE_DIMENSION) {
                            width = a.getDimensionPixelOffset(0, width)
                        }
                    }
                    android.R.attr.layout_height -> {
                        if (a.getType(0) == TypedValue.TYPE_DIMENSION) {
                            height = a.getDimensionPixelOffset(0, height)
                        }
                    }
                }
                a.recycle()
            }
            if (mBtnMiniHight > 0) {
                height = mBtnMiniHight
            }
            if (mBtnMiniWidth > 0) {
                minWidth = mBtnMiniWidth
            }
            if (mBtnTextSize > 0) {
                button.setTextSize(TypedValue.COMPLEX_UNIT_PX, mBtnTextSize.toFloat())
            }
            button.minWidth = minWidth
            val lp = LayoutParams(width, height)
            lp.setMargins(0, 10f.dp2px().toInt(), 0, 10f.dp2px().toInt())
            button.layoutParams = lp
            button.text = mBtnText
            return button
        }
    }

    interface Paint {

        fun initView()

        fun detached()

        fun setIcon(@DrawableRes icon: Int)

        fun setIconSize(@Px width: Int, @Px height: Int)

        fun setTitleText(text: CharSequence?)

        fun setTitleTextSize(@Px textSize: Int)

        fun setTitleTextColor(@ColorRes colorId: Int)

        fun setTipsText(text: CharSequence?)

        fun setTipsTextSize(@Px textSize: Int)

        fun setTipsTextColor(@ColorRes colorId: Int)

        fun setButtonText(text: CharSequence?)

        fun setButtonTextSize(@Px textSize: Int)

        fun setButtonTextColor(@ColorRes colorId: Int)

        fun setButtonSize(@Px miniWidth: Int, @Px miniHeight: Int)

        fun setButtonCLickListener(click: OnClickListener?)

        fun applyUIMode()

        fun setStateBtnStyle(styleId: Int)

    }

    private class IconView @JvmOverloads constructor(
            context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
    ) : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr) {

        private var mDrawableWidth = 0
        private var mDrawableHeight = 0
        private var mBoundsRect = Rect()

        init {
            adjustViewBounds = true
        }

        fun setDrawableSize(width: Int, height: Int) {
            mDrawableWidth = width
            mDrawableHeight = height
            drawable?.let {
                setImageDrawable(it)
            }
        }

        override fun setImageDrawable(drawable: Drawable?) {
            drawable?.let {
                resetBounds(it)
                it.bounds = mBoundsRect
            }
            super.setImageDrawable(drawable)
        }

        private fun resetBounds(d: Drawable) {
            mBoundsRect = if (mDrawableWidth > 0 && mDrawableHeight > 0) {
                Rect(0, 0, mDrawableWidth, mDrawableHeight)
            } else if (mDrawableWidth > 0) {
                val h = mDrawableWidth * 1f / d.minimumWidth * d.minimumHeight
                Rect(0, 0, mDrawableWidth, h.toInt())
            } else if (mDrawableHeight > 0) {
                val w = mDrawableHeight * 1f / d.minimumHeight * d.minimumWidth
                Rect(0, 0, w.toInt(), mDrawableHeight)
            } else {
                Rect(0, 0, d.minimumWidth, d.minimumHeight)
            }
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            setMeasuredDimension(mBoundsRect.width(), mBoundsRect.height())
        }

    }

    private class MyClickableSpan(val listener: OnClickListener) : ClickableSpan() {

        override fun updateDrawState(ds: TextPaint) {
        }

        override fun onClick(widget: View) {
            listener.onClick(widget)
        }

    }


}