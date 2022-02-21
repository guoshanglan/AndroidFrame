package com.zhuorui.commonwidget.popwindow

import android.content.Context
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import com.zhuorui.commonwidget.popwindow.PopWindowHelper.bindFragmentByPop

/**
 * @date 2021/5/19 15:29
 * @desc 黑暗背景PopupWindow
 */
open class DimPopupWindow : PopupWindow {

    constructor(contentView: View) : super(contentView, 0, 0)

    constructor(width: Int, height: Int) : super(null, width, height)

    constructor(context: Context, contentView: View, width: Int, height: Int) : super(
        contentView,
        width,
        height
    ) {
        bindFragmentByPop(this, context)
    }

    override fun showAsDropDown(anchor: View?) {
        super.showAsDropDown(anchor)
        dimBehind()
    }

    override fun showAsDropDown(anchor: View?, xoff: Int, yoff: Int) {
        super.showAsDropDown(anchor, xoff, yoff)
        dimBehind()
    }

    override fun showAsDropDown(anchor: View?, xoff: Int, yoff: Int, gravity: Int) {
        super.showAsDropDown(anchor, xoff, yoff, gravity)
        dimBehind()
    }

    override fun showAtLocation(parent: View?, gravity: Int, x: Int, y: Int) {
        super.showAtLocation(parent, gravity, x, y)
        dimBehind()
    }

    private fun dimBehind() {
        contentView?.let {
            if (this.background == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val parent = it.parent
                    if (parent is View) updateViewLayoutDim(parent)
                } else {
                    updateViewLayoutDim(it)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val parentToParent = it.parent?.parent
                    if (parentToParent is View) updateViewLayoutDim(parentToParent)
                } else {
                    val parent = it.parent
                    if (parent is View) updateViewLayoutDim(parent)
                }
            }
        }
    }

    private fun updateViewLayoutDim(targetView: View) {
        val windowManager = targetView.context.getSystemService(Context.WINDOW_SERVICE)
        if (windowManager is WindowManager) {
            val layoutParams = targetView.layoutParams
            if (layoutParams is WindowManager.LayoutParams) {
                layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
                layoutParams.dimAmount = dimAmount()
                windowManager.updateViewLayout(targetView, layoutParams)
            }
        }
    }

    open fun dimAmount(): Float = 0.5f
}