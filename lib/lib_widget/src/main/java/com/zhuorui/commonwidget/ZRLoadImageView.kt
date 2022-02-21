package com.zhuorui.commonwidget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import base2app.ex.drawable
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.zhuorui.commonwidget.util.OpenGLRenderUtil
import com.zhuorui.securities.base2app.glide.ZRGlide

/**
 *    date   : 2020/9/24 10:07
 *    desc   : 网络图片加载View
 */
class ZRLoadImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var mPath: String? = null
    private var picW = 0
    private var picH = 0

    private var errorDrawable:Drawable? = null  //错误展位图

    /**
     * /OpenGLRenderer绘制bitmap的最大限制,宽或高超出限制不能无法正常显示
     */
    private var openglRenderMax: Int = 4096

    private val img: ZRImageView = ZRImageView(context).apply {
        id = View.generateViewId()
        isSaveEnabled = false
        layoutParams = LayoutParams(0, 0)
        scaleType = ImageView.ScaleType.CENTER_CROP
    }

    init {
        if (!isInEditMode)
            openglRenderMax = OpenGLRenderUtil.getOpenglRenderLimitValue()
        var radius = 0
        var imgBackgroundColor = 0
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ZRLoadImageView, 0, 0)
        for (i in 0 until ta.indexCount) {
            when (val attr = ta.getIndex(i)) {
                R.styleable.ZRLoadImageView_zr_background -> {
                    imgBackgroundColor = ta.getColor(attr, imgBackgroundColor)
                }
                R.styleable.ZRLoadImageView_zr_radius -> {
                    radius = ta.getDimensionPixelOffset(attr, radius)
                }
            }
        }
        if (imgBackgroundColor != 0) {
            setImgBackgroundColor(imgBackgroundColor)
        }
        if (radius > 0) {
            setRadius(radius)
        }
        addView(img)
        ConstraintSet().let {
            it.clone(this)
            it.connect(img.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
            it.connect(img.id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
            it.connect(img.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
            it.connect(img.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            it.applyTo(this)
        }
    }

    fun setScaleType(scaleType: ImageView.ScaleType) {
        img.scaleType = scaleType

    }

    fun setRadius(@Px radius: Int) {
        img.setCornerRadius(radius)
    }

    fun setImgBackgroundColor(color: Int) {
        img.setBackgroundColor(color)
    }

    fun getImageView(): ZRImageView {
        return img;
    }

    //设置占位图
    fun setPlaceHoler(@DrawableRes placeholder: Int) {
        errorDrawable = if (placeholder == 0){
            null
        }else{
            drawable(placeholder)
        }
    }

    //设置占位图
    fun setPlaceHoler(placeholder: Drawable?) {
        errorDrawable = placeholder
    }

    fun setPath(path: String?, w: Int, h: Int) {
        picW = w
        picH = h
        mPath = path
        when {
            TextUtils.isEmpty(path) -> {
                mPath = null
                img.setImageDrawable(errorDrawable)
            }
            else -> {
                mPath = path
                img.setImageResource(0)
                loadData(path!!)
            }
        }
    }

    private fun loadData(path: String) {
        val target = object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                img.setImageBitmap(resource)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                img.setImageResource(0)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                errorDrawable?.let {
                    img.setImageDrawable(it)
                }
            }

        }
        /*计算加载图片和Glide剪裁的尺寸*/
        //显示尺寸和图片尺寸比较，最小值
        var w = width.coerceAtLeast(layoutParams.width).coerceAtMost(picW)
        var h = height.coerceAtLeast(layoutParams.height).coerceAtMost(picH)
        //限制最大高度，宽度一般最大为屏幕宽度，openglRenderMax远大于屏幕宽度，所以宽度不处理
        if (h > openglRenderMax) {
            w = (w * 1f / h * openglRenderMax).toInt()
            h = openglRenderMax
        }

        ZRGlide.with(this).asBitmap().load(dealWithPath(path, w, h))
            .apply(RequestOptions().override(w, h).centerCrop()).into(target)


    }

    private fun dealWithPath(path: String, w: Int, h: Int): String {
        return if (path.contains("aliyuncs.com")) {
            //缩放图限制：缩放图宽与高的乘积不能超过4096 px*4096 px，且单边长度不能超过4096 px
            //缩放规则详见https://help.aliyun.com/document_detail/44688.html?spm=a2c4g.11186623.6.731.af0757d9yYIfKf
            var rw: Int = w.coerceAtMost(4096)
            var rh: Int = h.coerceAtMost(4096)
            if (picW == rw && picH == rh) {
                //缩放大小与原图尺寸一致辞时，不使用缩放API
                path
            } else {
                //缩放策略：短边重合，长边等比例缩放
                "$path?x-oss-process=image/resize,m_mfit,h_${rh},w_${rw}"
            }
        } else {
            path
        }
    }

}