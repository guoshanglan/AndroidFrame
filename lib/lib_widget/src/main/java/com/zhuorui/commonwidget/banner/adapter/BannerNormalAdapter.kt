package com.zhuorui.commonwidget.banner.adapter

import android.graphics.drawable.Drawable
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder
import com.zhuorui.commonwidget.R
import com.zhuorui.commonwidget.banner.CornerImageView
import com.zhuorui.commonwidget.banner.model.BannerModel
import com.zhuorui.securities.base2app.glide.ZRGlide


/**
@author guoshanglan
@description:通用banner适配器
@date : 2021/8/12 16:02
@param placeholder 加载占位图
 */
class BannerNormalAdapter(val placeholder: Drawable?) : BaseBannerAdapter<BannerModel>() {

    private val drawableCrossFadeFactory: DrawableCrossFadeFactory =
        DrawableCrossFadeFactory.Builder(300).setCrossFadeEnabled(true).build()

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_page_indicator
    }

    override fun bindData(
        holder: BaseViewHolder<BannerModel>?,
        data: BannerModel?,
        position: Int,
        pageSize: Int,
    ) {
        if (data == null) return
        val view: CornerImageView? = holder?.findViewById(R.id.banner_image)
        data.imageUrl?.let { url ->
            view?.let {
                ZRGlide.with(it).load(url).placeholder(placeholder)
                    .transition(DrawableTransitionOptions.with(drawableCrossFadeFactory)).into(it)
            }
        } ?: kotlin.run { view?.setImageDrawable(null) }
    }
}