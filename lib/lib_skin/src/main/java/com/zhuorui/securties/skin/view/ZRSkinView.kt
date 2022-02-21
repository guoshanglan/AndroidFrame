package com.zhuorui.securties.skin.view

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView

/**
 * ZRSkinView
 * @description
 * @date 2020/12/11 13:42
 */
class ZRSkinView(view: View, skinAttrs: List<ZRSkinAttr?>?) :
    ZRBaseSkinView<View?>(view, skinAttrs) {
    /**
     * @param type 对应的类型
     * @return 是否属于边界型的drawable
     */
    private fun isEdgeDrawable(type: ZRSkinAttrType?): Boolean {
        return type === ZRSkinAttrType.DRAWABLE_BOTTOM || type === ZRSkinAttrType.DRAWABLE_LEFT || type === ZRSkinAttrType.DRAWABLE_RIGHT || type === ZRSkinAttrType.DRAWABLE_TOP
    }

    @SuppressLint("ResourceType")
    override fun applyUIMode(resources: Resources?) {
        viewRef.get()?.let { view ->
            mSkinAttrs?.let {
                if (!it.isNullOrEmpty()) {
                    var left: Drawable? = null
                    var top: Drawable? = null
                    var right: Drawable? = null
                    var bottom: Drawable? = null
                    for (attr in it) {
                        attr?.let {
                            val type = attr.mZRSkinAttrType
                            if (!isEdgeDrawable(type)) {
                                attr.apply(view, resources)
                            } else {
                                when {
                                    type === ZRSkinAttrType.DRAWABLE_LEFT -> {
                                        left = view.resources.getDrawable(attr.mResId)
                                    }
                                    type === ZRSkinAttrType.DRAWABLE_RIGHT -> {
                                        right = view.resources.getDrawable(attr.mResId)
                                    }
                                    type === ZRSkinAttrType.DRAWABLE_TOP -> {
                                        top = view.resources.getDrawable(attr.mResId)
                                    }
                                    else -> {
                                        bottom = view.resources.getDrawable(attr.mResId)
                                    }
                                }
                            }
                        }
                    }
                    if (null != left || null != right || null != top || null != bottom) {
                        (view as TextView).setCompoundDrawablesWithIntrinsicBounds(
                                left, top, right,
                                bottom
                        )
                    }
                }
            }

        }
    }
}