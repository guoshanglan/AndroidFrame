package com.zrlib.matisse.ui.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2020/9/11 09:03
 *    desc   :
 */
class CutterView(context: Context?, attr: AttributeSet?) : SubsamplingScaleImageView(context, attr),
    SubsamplingScaleImageView.OnStateChangedListener {

    /**
     * 长或宽与裁剪区域重合的Scale
     */
    private var frameScale = 1f
    private var inDrag = false
    private var mCircle = false
    private var inCheck = false
    private var mFrame: Rect = Rect()
    private var path: String? = null
    private val eraser = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        eraser.color = Color.parseColor("#000000")
        orientation = ORIENTATION_USE_EXIF
        setMinimumScaleType(SCALE_TYPE_CUSTOM)
        setPanLimit(PAN_LIMIT_OUTSIDE)
        setOnStateChangedListener(this)
    }

    /**
     * 设置剪裁显示区域是否圆形
     */
    fun setCircle(b: Boolean) {
        mCircle = b
        invalidate()
    }

    /**
     * 设置剪裁显示区域
     */
    fun setCutterFrame(frame: Rect) {
        mFrame = frame
        invalidate()
    }

    /**
     * 剪裁图片保存到指定位置
     */
    fun cutter(path: String): Boolean {
        val result = createClippedBitmap() ?: return false
        val fileStream = FileOutputStream(File(path))
        result.compress(Bitmap.CompressFormat.JPEG, 100, fileStream)
        fileStream.flush()
        fileStream.close()
        if (!result.isRecycled) {
            result.recycle()
        }
        return true
    }

    /**
     * 设置图片地址（必须为本地文件地址）
     */
    fun setPath(path: String) {
        this.path = path
        setImage(ImageSource.uri(path))
    }

    override fun onDraw(canvas: Canvas?) {
        if (mFrame.width() > 0 && mFrame.height() > 0) {
            if (mCircle) {
                canvas?.drawCircle(
                    mFrame.exactCenterX(),
                    mFrame.exactCenterY(),
                    mFrame.width() / 2.toFloat(),
                    eraser
                )
            } else {
                canvas?.drawRect(mFrame, eraser)
            }
        }
        super.onDraw(canvas)
    }

    override fun onImageLoaded() {
        super.onImageLoaded()
        val imgW = oWidth().toFloat()
        val imgH = oHeight().toFloat()
        when {
            imgW >= imgH -> {
                //宽图
                frameScale = mFrame.height() / imgH
                minScale = width * 0.33f / imgH
                maxScale = max(width * 3 / imgH, minScale)
            }
            else -> {
                //长图
                frameScale = mFrame.width() / imgW
                minScale = width * 0.33f / imgW
                maxScale = max(width * 3 / imgW, minScale)
            }
        }
        setScaleAndCenter(frameScale, PointF(imgW / 2, imgH / 2))
    }

    override fun onScaleChanged(newScale: Float, origin: Int) {
        if (!inDrag && !inCheck) {
            resolvePositionCheck()
        }
    }

    override fun onCenterChanged(newCenter: PointF?, origin: Int) {
        if (!inDrag && !inCheck) {
            resolvePositionCheck()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                resolvePositionCheck()
                inDrag = false
            }
            MotionEvent.ACTION_DOWN -> {
                inDrag = true
                inCheck = false
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * 获取显示宽
     */
    private fun oWidth(): Int {
        return if (appliedOrientation == 0 || appliedOrientation == 180) {
            sWidth
        } else {
            sHeight
        }
    }

    /**
     * 获取显示高
     */
    private fun oHeight(): Int {
        return if (appliedOrientation == 0 || appliedOrientation == 180) {
            sHeight
        } else {
            sWidth
        }
    }


    /**
     * 获取剪裁框对图片位置限制
     */
    private fun getLimitRect(): RectF {
        val limitRect = RectF()
        val left = mFrame.width() / 2 / scale
        val top = mFrame.height() / 2 / scale
        val right = left + (oWidth() * scale - mFrame.width()) / scale
        val bottom = top + (oHeight() * scale - mFrame.height()) / scale
        limitRect.set(left, top, right, bottom)
        return limitRect
    }

    /**
     * 检查显示图片位置
     */
    private fun resolvePositionCheck() {
        if (scale < frameScale) {
            inCheck = true
            animateScaleAndCenter(
                frameScale,
                PointF((oWidth() / 2).toFloat(), oHeight() / 2.toFloat())
            )
                ?.withDuration(100)
                ?.withEasing(EASE_OUT_QUAD)
                ?.withInterruptible(false)
                ?.start()
            return
        }
        val center = center ?: return
        val limitRect = getLimitRect()
        val x = when {
            center.x < limitRect.left -> {
                if (oWidth() * scale < mFrame.width()) {
                    if (center.x < limitRect.right) {
                        limitRect.right
                    } else {
                        center.x
                    }
                } else {
                    limitRect.left
                }
            }
            center.x > limitRect.right -> {
                if (oWidth() * scale < mFrame.width()) {
                    limitRect.left
                } else {
                    limitRect.right
                }
            }
            else -> {
                center.x
            }
        }
        val y = when {
            center.y < limitRect.top -> {
                if (oHeight() * scale < mFrame.height()) {
                    if (center.y < limitRect.bottom) {
                        limitRect.bottom
                    } else {
                        center.y
                    }
                } else {
                    limitRect.top
                }
            }
            center.y > limitRect.bottom -> {
                if (oHeight() * scale < mFrame.height()) {
                    limitRect.top
                } else {
                    limitRect.bottom
                }
            }
            else -> {
                center.y
            }
        }
        if (x != center.x || y != center.y) {
            inCheck = true
            animateCenter(PointF(x, y))
                ?.withDuration(100)
                ?.withEasing(EASE_OUT_QUAD)
                ?.withInterruptible(false)
                ?.start()
        }
    }

    /**
     * 获取显示剪裁位置
     */
    private fun getCutterPositionRect(): Rect {
        return Rect().also {
            center?.let { center ->
                val left = (center.x - mFrame.width() / 2 / scale).toInt()
                val top = (center.y - mFrame.height() / 2 / scale).toInt()
                val right = (center.x + mFrame.width() / 2 / scale).toInt()
                val bottom = (center.y + mFrame.height() / 2 / scale).toInt()
                it.left = left.coerceAtLeast(0)
                it.top = top.coerceAtLeast(0)
                it.right = right.coerceAtMost(oWidth())
                it.bottom = bottom.coerceAtMost(oHeight())
            }

        }
    }

    /**
     * 获取原图上的真正的裁剪框位置
     */
    private fun getRealRect(): Rect {
        val srcRect = getCutterPositionRect()
        return when (appliedOrientation) {
            90 -> Rect(
                srcRect.top, (sHeight - srcRect.right),
                srcRect.bottom, (sHeight - srcRect.left)
            )
            180 -> Rect(
                (sWidth - srcRect.right), (sHeight - srcRect.bottom),
                (sWidth - srcRect.left), (sHeight - srcRect.top)
            )
            270 -> Rect(
                (sWidth - srcRect.bottom), srcRect.left,
                (sWidth - srcRect.top), srcRect.right
            )
            else -> Rect(srcRect.left, srcRect.top, srcRect.right, srcRect.bottom)
        }
    }

    /**
     * 获取剪裁Bitmap
     */
    private fun createClippedBitmap(): Bitmap? {
        val maxWidth = mFrame.width() * 2
        // 获取在显示的图片中裁剪的位置
        val clipRect = getRealRect()
        val ops = BitmapFactory.Options()
        val outputMatrix = Matrix()
        outputMatrix.setRotate(appliedOrientation.toFloat())
        // 如果裁剪之后的图片宽高仍然太大,则进行缩小
        if (clipRect.width() > maxWidth) {
            ops.inSampleSize = findBestSample(clipRect.width(), maxWidth)
        }
        // 裁剪
        return path?.let {
            val decoder: BitmapRegionDecoder = BitmapRegionDecoder.newInstance(it, false)
            val source = decoder.decodeRegion(clipRect, ops)
            if (!decoder.isRecycled) {
                decoder.recycle()
            }
            return Bitmap.createBitmap(source, 0, 0, source.width, source.height, outputMatrix, false)
        }
    }

    /**
     * 计算最好的采样大小。
     *
     * @param origin 当前宽度
     * @param target 限定宽度
     * @return sampleSize
     */
    private fun findBestSample(origin: Int, target: Int): Int {
        var sample = 2
        var out = origin / sample
        while (out > target) {
            sample += 1
            out = origin / sample
        }
        return sample
    }
}