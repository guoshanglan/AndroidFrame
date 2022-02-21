package com.zrlib.permission

import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.zhuorui.commonwidget.dialog.MessageDialog
import com.zhuorui.securities.base2app.ex.blod
import com.zhuorui.securities.base2app.ex.color
import com.zhuorui.securities.base2app.ex.dp2px
import com.zhuorui.securities.base2app.ex.text
import com.zrlib.permission.bean.Permission

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2020/10/13 15:38
 *    desc   : 获取权限对话框
 */
class PremissionDialog(fragment: Fragment) : MessageDialog(fragment) {

    private var premissionLayout: ConstraintLayout = ConstraintLayout(fragment.context)

    init {
        setMessageTitle(text(R.string.premission_str_application_permission))
        setMessageDialogStyle(MessageDialogStyle().apply {
            setLeftText(text(R.string.prremission_str_refuse))
            setRightText(text(R.string.prremission_str_delegating))
        })
        getContextView()?.let {
            replaceContentView(it)
        }
    }

    private fun getContextView(): View? {
        val ctx = context?.get() ?: return null
        val content = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val lr = 20f.dp2px().toInt()
            setPadding(lr, 8f.dp2px().toInt(), lr, 16f.dp2px().toInt())
        }
        TextView(ctx)
            .apply {
                text = text(R.string.premission_apply_pre_tips)
                setTextColor(color(R.color.dialog_content_text))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            .let {
                content.addView(it)
            }
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        lp.topMargin = 10f.dp2px().toInt()
        content.addView(premissionLayout, lp)
        return content
    }

    fun setPremissions(premission: Array<Permission>) {
        premissionLayout.removeAllViews()
        var lfetPadding = 0
        var gravity = Gravity.CENTER
        if (premission.size > 1) {
            lfetPadding = 14f.dp2px().toInt()
            gravity = Gravity.START or Gravity.CENTER_VERTICAL
        }
        premission.forEach {
            TextView(premissionLayout.context).apply {
                id = View.generateViewId()
                text = "･ ${it.permissionNameDesc}"
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                setTextColor(color(R.color.dialog_title_text))
                layoutParams =
                    ConstraintLayout.LayoutParams(0, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                setPadding(lfetPadding, 0, 0, 0)
                this.gravity = gravity
            }.let {
                it.blod()
                premissionLayout.addView(it)
            }
        }
        val childCount = premissionLayout.childCount
        if (childCount == 1) {
            val id = premissionLayout.getChildAt(0).id
            ConstraintSet().let {
                it.clone(premissionLayout)
                it.connect(id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                it.connect(id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
                it.connect(id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
                it.applyTo(premissionLayout)
            }
        } else if (childCount > 1) {
            val spaceCount = childCount.coerceAtMost(2)
            ConstraintSet().let {
                it.clone(premissionLayout)
                for (i in 0 until childCount) {
                    val id = premissionLayout.getChildAt(i).id
                    if (i < spaceCount) {
                        it.connect(
                            id,
                            ConstraintSet.TOP,
                            ConstraintSet.PARENT_ID,
                            ConstraintSet.TOP
                        )
                        when (i) {
                            0 -> {
                                it.setHorizontalChainStyle(id, ConstraintSet.CHAIN_PACKED)
                                it.connect(
                                    id,
                                    ConstraintSet.LEFT,
                                    ConstraintSet.PARENT_ID,
                                    ConstraintSet.LEFT
                                )
                                it.connect(
                                    id,
                                    ConstraintSet.RIGHT,
                                    premissionLayout.getChildAt(i + 1).id,
                                    ConstraintSet.LEFT
                                )
                            }
                            spaceCount - 1 -> {
                                it.connect(
                                    id,
                                    ConstraintSet.LEFT,
                                    premissionLayout.getChildAt(i - 1).id,
                                    ConstraintSet.RIGHT
                                )
                                it.connect(
                                    id,
                                    ConstraintSet.RIGHT,
                                    ConstraintSet.PARENT_ID,
                                    ConstraintSet.RIGHT
                                )
                            }
                            else -> {
                                it.connect(
                                    id,
                                    ConstraintSet.LEFT,
                                    premissionLayout.getChildAt(i - 1).id,
                                    ConstraintSet.RIGHT
                                )
                                it.connect(
                                    id,
                                    ConstraintSet.RIGHT,
                                    premissionLayout.getChildAt(i + 1).id,
                                    ConstraintSet.LEFT
                                )
                            }
                        }
                    } else {
                        val preId = premissionLayout.getChildAt(i - spaceCount).id
                        it.connect(
                            id,
                            ConstraintSet.TOP,
                            preId,
                            ConstraintSet.BOTTOM,
                            8f.dp2px().toInt()
                        )
                        it.connect(id, ConstraintSet.LEFT, preId, ConstraintSet.LEFT)
                        it.connect(id, ConstraintSet.RIGHT, preId, ConstraintSet.RIGHT)
                    }
                }
                it.applyTo(premissionLayout)
            }
        }
    }
}