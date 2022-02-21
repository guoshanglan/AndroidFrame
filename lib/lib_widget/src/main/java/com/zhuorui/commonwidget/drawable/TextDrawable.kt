package com.zhuorui.commonwidget.drawable

import android.annotation.SuppressLint
import android.graphics.*
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RectShape
import android.graphics.drawable.shapes.RoundRectShape
import android.view.Gravity
import androidx.annotation.ColorInt
import java.util.*
import kotlin.math.abs
import kotlin.math.ceil


/**
 * date : 2020/9/9
 * dest : TextDrawable
 */
open class TextDrawable private constructor(var builder: Builder, mDrawableParams: DrawableParams) :
    BaseDrawable(
            mDrawableParams
    ) {

    private val textPaint: Paint
    private val borderPaint: Paint
    internal var text: String?

    @ColorInt
    private var color: Int
    private var shape: RectShape?
    private var fontSize: Int
    private var radius: Float
    private var borderThickness: Int
    private var borderColor: Int

    private var textHeight: Int

    private var textWidth: Int


    init {
        // shape properties
        shape = builder.shape
        radius = builder.radius

        // text and color
        text =
            if (builder.toUpperCase) builder.text.orEmpty().toUpperCase(Locale.ROOT)
            else builder.text
        color = builder.color

        // text paint settings
        fontSize = builder.fontSize
        textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.flags = Paint.ANTI_ALIAS_FLAG
        textPaint.isFilterBitmap = true
        textPaint.color = builder.textColor
        textPaint.isAntiAlias = true
        textPaint.style = Paint.Style.FILL
        when {
            builder.isBold -> {
                textPaint.typeface = Typeface.DEFAULT_BOLD
            }
            builder.isMedium -> {
                textPaint.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
            }
            else -> {
                textPaint.typeface = builder.font
            }
        }
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.strokeWidth = builder.borderThickness.toFloat()
        textPaint.textSize = fontSize.toFloat()

        // border paint settings
        borderThickness = builder.borderThickness
        borderColor = builder.borderColor
        borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        borderPaint.strokeWidth = borderThickness.toFloat()
        borderPaint.isAntiAlias = true

        val fontMetrics: Paint.FontMetrics = textPaint.fontMetrics
        textHeight = ((abs(fontMetrics.top) - fontMetrics.bottom) * 2).toInt()
        textWidth = textPaint.measureText(text).toInt()
    }
    // shape properties

    fun enforceBuilder() {
        shape = builder.shape
        radius = builder.radius

        // text and color
        text =
            if (builder.toUpperCase) builder.text.orEmpty().toUpperCase(Locale.ROOT)
            else builder.text
        color = builder.color

        // text paint settings
        fontSize = builder.fontSize
        textPaint.color = builder.textColor
        textPaint.isAntiAlias = true
        textPaint.style = Paint.Style.FILL

        when {
            builder.isBold -> {
                textPaint.typeface = Typeface.DEFAULT_BOLD
            }
            builder.isMedium -> {
                textPaint.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
            }
            else -> {
                textPaint.typeface = builder.font
            }
        }

        textPaint.textAlign = Paint.Align.CENTER
        textPaint.strokeWidth = builder.borderThickness.toFloat()
        textPaint.textSize = fontSize.toFloat()

        // border paint settings
        borderThickness = builder.borderThickness
        borderColor = builder.borderColor

        borderPaint.strokeWidth = borderThickness.toFloat()
        borderPaint.isAntiAlias = true

        val fontMetrics: Paint.FontMetrics = textPaint.fontMetrics
        textHeight = ((abs(fontMetrics.top) - fontMetrics.bottom) * 2).toInt()
        textWidth = textPaint.measureText(text).toInt()

//        autoSize()
    }

    private fun autoSize() {
        if (bounds.width() != 0 && textWidth > bounds.width()) {
            while (textWidth > bounds.width()) {
                textPaint.textSize -= 2
                textWidth = textPaint.measureText(text).toInt()
            }
            val fontMetrics: Paint.FontMetrics = textPaint.fontMetrics
            textHeight = ((abs(fontMetrics.top) - fontMetrics.bottom) * 2).toInt()
            textWidth = textPaint.measureText(text).toInt()
        }
    }

    private fun getDarkerShade(@ColorInt color: Int): Int {
        return Color.rgb(
                (SHADE_FACTOR * Color.red(color)).toInt(),
                (SHADE_FACTOR * Color.green(color)).toInt(),
                (SHADE_FACTOR * Color.blue(color)).toInt()
        )
    }

    override fun measureContent() {//加上padding的目的是因为当触发这个方法时 , 宽高都是不确定的 ,所以需要 padding 来确定最大值
        bounds.set(
                0, 0, textWidth + mDrawableParams.paddingLeft + mDrawableParams.paddingRight,
                textHeight + mDrawableParams.paddingTop + mDrawableParams.paddingButtom
        )
    }


    @SuppressLint("CanvasSize")
    override fun draw(canvas: Canvas) {
        val count = canvas.save()
        if (canvas.height > bounds.height() && mDrawableParams.gravity != -1) {
            when (mDrawableParams.gravity) {
                Gravity.BOTTOM -> {
                    canvas.translate(0f, (canvas.height - bounds.height()).toFloat())
                }
                Gravity.CENTER -> {
                    canvas.translate(0f, (canvas.height - bounds.height()) / 2f)
                }
              /*  Gravity.TOP -> {top 就是从0点开始画
                    canvas.translate(0f, (canvas.height - bounds.height()).toFloat())
                }*/
            }
        }

        val r = bounds

        if (color != -1)
            drawBackground(canvas)

        // draw border
        if (borderThickness > 0) {
            drawBorder(canvas)
        }
        val width = r.width()
        val height = r.height()

        autoSize()
        val textBounds = Rect()
        textPaint.getTextBounds(text, 0, text?.length ?: 0, textBounds)

        if (canvas.height == bounds.height() && mDrawableParams.gravity != -1) {
            var x = 0f
            var y = 0f
            if (mDrawableParams.gravity == Gravity.CENTER_VERTICAL) {
                y = (height - textHeight) / 2f
            } else if (mDrawableParams.gravity == Gravity.CENTER) {
                y = (height - textHeight) / 2f
                x = (width - textWidth) / 2f
            }
            when (mDrawableParams.gravity and Gravity.VERTICAL_GRAVITY_MASK) {
                Gravity.BOTTOM -> {
                    y = (height - textHeight).toFloat()
                }
                Gravity.CENTER_VERTICAL -> {
                    y = (height - textHeight) / 2f
                }
            }
            when (mDrawableParams.gravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
                Gravity.END, Gravity.RIGHT -> {
                    x = (width - textWidth).toFloat()
                }
            }
            canvas.translate(x, y)
            canvas.drawText(
                    text.orEmpty(),
                    r.left + (textWidth / 2).toFloat(),
                    r.top + textHeight / 2 - textBounds.exactCenterY(),
                    textPaint
            )
        }else{
            canvas.drawText(
                    text.orEmpty(),
                    r.left + (width / 2).toFloat(),
                    r.top + height / 2 - textBounds.exactCenterY(),
                    textPaint
            )
        }
        canvas.restoreToCount(count)
    }

    private fun drawBackground(canvas: Canvas) {
        borderPaint.color = color
        borderPaint.style = Paint.Style.FILL
        val rect = RectF(bounds)
        val inset = ceil((borderThickness / 2).toDouble())
        rect.inset(inset.toInt().toFloat(), inset.toInt().toFloat())
        when (shape) {
            is OvalShape -> canvas.drawOval(rect, borderPaint)
            is RoundRectShape -> canvas.drawRoundRect(rect, radius, radius, borderPaint)
            else -> canvas.drawRect(rect, borderPaint)
        }
    }

    private fun drawBorder(canvas: Canvas) {
        borderPaint.color =
            if (borderColor == -1) getDarkerShade(color) else borderColor
        borderPaint.style = Paint.Style.STROKE

        val rect = RectF(bounds)
        val inset = ceil((borderThickness / 2).toDouble())
        rect.inset(inset.toInt().toFloat(), inset.toInt().toFloat())
        when (shape) {
            is OvalShape -> canvas.drawOval(rect, borderPaint)
            is RoundRectShape -> canvas.drawRoundRect(rect, radius, radius, borderPaint)
            else -> canvas.drawRect(rect, borderPaint)
        }
    }

    override fun setAlpha(alpha: Int) {
        textPaint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        textPaint.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    fun adjust(what: TextDrawable.() -> Unit = {}): TextDrawable {
        this.what()
        return this
    }

    class Builder {

        var text: String? = null
            private set

        @ColorInt
        var color: Int = 0
            private set
        var borderThickness: Int = 0
            private set
        var borderColor: Int = 0
            private set
        var width: Int = 0
            private set
        var height: Int = 0
            private set
        var font: Typeface? = null
            private set
        var shape: RectShape? = null
            private set
        var textColor: Int = 0
            private set
        var fontSize: Int = 0
            private set
        var isBold: Boolean = false
            private set
        var isMedium: Boolean = false
            private set
        var toUpperCase: Boolean = false
            private set
        var radius: Float = 0.toFloat()
            private set

        init {
            text = ""
            color = Color.GRAY
            textColor = Color.WHITE
            borderThickness = 0
            borderColor = -1
            color = -1
            width = -1
            height = -1
            shape = RectShape()
            font = DEFAULT_FONT
            fontSize = -1
            isBold = false
            isMedium = false
            toUpperCase = false
        }

        fun width(width: Int): Builder {
            this.width = width
            return this
        }

        fun height(height: Int): Builder {
            this.height = height
            return this
        }

        fun textColor(@ColorInt color: Int): Builder {
            this.textColor = color
            return this
        }

        fun text(text: String): Builder {
            this.text = text
            return this
        }

        fun withBorder(thickness: Int): Builder {
            return withBorder(thickness, this.borderColor)
        }

        fun withBorder(thickness: Int, @ColorInt color: Int): Builder {
            this.borderThickness = thickness
            this.borderColor = color
            return this
        }

        fun useFont(font: Typeface): Builder {
            this.font = font
            return this
        }

        fun fontSize(size: Int): Builder {
            this.fontSize = size
            return this
        }

        fun bold(): Builder {
            this.isBold = true
            return this
        }

        fun medium(): Builder {
            this.isMedium = true
            return this
        }

        fun toUpperCase(): Builder {
            this.toUpperCase = true
            return this
        }

        internal fun beginConfig(): Builder {
            return this
        }

        internal fun endConfig(): Builder {
            return this
        }

        fun rect(): Builder {
            this.shape = RectShape()
            return this
        }

        fun round(): Builder {
            this.shape = OvalShape()
            return this
        }

        fun backgroundColor(@ColorInt color: Int): Builder {
            this.color = color
            return this
        }

        fun roundRect(radius: Int): Builder {
            this.radius = radius.toFloat()
            val radii = floatArrayOf(
                    radius.toFloat(), radius.toFloat(), radius.toFloat(), radius.toFloat(),
                    radius.toFloat(), radius.toFloat(), radius.toFloat(), radius.toFloat()
            )
            this.shape = RoundRectShape(radii, EMPTY_RECT, null)
            return this
        }

        fun build(mDrawableParams: DrawableParams): TextDrawable {
            return TextDrawable(this, mDrawableParams)
        }

        companion object {
            private val EMPTY_RECT = RectF()

            @JvmStatic
            val DEFAULT_FONT: Typeface by lazy {
                Typeface.create("sans-serif-light", Typeface.NORMAL)
            }
        }
    }

    companion object {
        private const val SHADE_FACTOR = 0.9F

        @JvmStatic
        fun builder(what: Builder.() -> Unit = {}): Builder {
            val builder = Builder()
            builder.beginConfig()
            builder.what()
            builder.endConfig()
            return builder
        }

        @JvmStatic
        fun build(
                what: Builder.() -> Unit = {},
                mParams: DrawableParams.() -> Unit = {}
        ): TextDrawable =
            builder(what).build(DrawableParams.builder(mParams))

    }
}