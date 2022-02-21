package com.zhuorui.commonwidget.dialog

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import base2app.adapter.BaseListAdapter
import base2app.dialog.BaseBottomSheetsDialog
import base2app.ex.color
import base2app.ex.dp2px
import base2app.ex.sansSerifMedium
import base2app.ex.setSafeClickListener
import com.zhuorui.commonwidget.DrawableTextView
import com.zhuorui.commonwidget.LinearSpacingItemDecoration
import com.zhuorui.commonwidget.R
import com.zhuorui.securities.base2app.ex.*

/**
 *    date   : 2021/12/8 14:34
 *    desc   : 操作菜单dialog
 */
open class CommMenuDialog<T>(
    fragment: Fragment,
    style: Int,
    itemTag: Array<T>,
    itemClick: (T) -> Unit
) : BaseBottomSheetsDialog(fragment) {

    companion object {

        const val VERTICAL = 0
        const val HORIZONTAL = 1

        /**
         * 竖向的操作菜单adapter
         */
        private class VerticalMenuAdapter(private val itemClick: (Int) -> Unit) :
            BaseListAdapter<MenuItem>() {
            override fun createViewHolderByParent(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                return VerticalViewHolder(
                    inflateView(
                        parent,
                        R.layout.dialog_comm_menu_vertical_item
                    )
                ).apply {
                    itemView.setSafeClickListener {
                        itemClick.invoke(bindingAdapterPosition)
                    }
                }
            }

            class VerticalViewHolder(v: View) :
                BaseListAdapter.ListItemViewHolder<MenuItem>(v, false, false),
                IListItemViewHolder2 {
                val tvMenu: DrawableTextView? = itemView.findViewById(R.id.tvMenu)
                val lineView: View? = itemView.findViewById(R.id.lineView)
                override fun bind(item: MenuItem?, itemIndex: Int) {
                    tvMenu?.setCompoundDrawablesWithIntrinsicBounds(item?.icon ?: 0, 0, 0, 0)
                    tvMenu?.text = item?.text
                    if (itemIndex == 0) {
                        lineView?.visibility = View.GONE
                    } else {
                        lineView?.visibility = View.VISIBLE
                    }
                }
            }

        }


        /**
         * 横向的操作菜单adapter
         */
        private class HorizontalMenuAdapter(private val itemClick: (Int) -> Unit) :
            BaseListAdapter<MenuItem>() {

            override fun createViewHolderByParent(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                val itemView = DrawableTextView(parent.context).apply {
                    sansSerifMedium()
                    gravity = Gravity.CENTER
                    compoundDrawablePadding = 10f.dp2px().toInt()
                    40f.dp2px().toInt().let { wh -> setDrawableSize(wh, wh) }
                    layoutParams = RecyclerView.LayoutParams(
                        RecyclerView.LayoutParams.MATCH_PARENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT
                    )
                    20f.dp2px().toInt().let {
                        setPadding(0, it, 0, it)
                    }
                    textSize = 12f
                    setTextColor(color(R.color.dialog_daynight_text_color_05))
                }
                return object : RecyclerView.ViewHolder(itemView) {}.apply {
                    itemView.setSafeClickListener {
                        itemClick.invoke(bindingAdapterPosition)
                    }
                }
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                holder.itemView.takeIf { it is DrawableTextView }?.let {
                    val data = getItem(getItemIndex(position))
                    (it as DrawableTextView).let { v ->
                        v.setCompoundDrawablesWithIntrinsicBounds(0, data?.icon ?: 0, 0, 0)
                        v.text = data?.text
                    }
                }
            }

        }
    }

    override val layout: Int
        get() = R.layout.dialog_comm_menu

    private var adapter: BaseListAdapter<MenuItem>? = null

    init {
        requireView().let { rootView ->
            rootView.findViewById<View>(R.id.btnCancel).setSafeClickListener {
                dismiss()
            }
            rootView.findViewById<RecyclerView>(R.id.vRecycler)?.let { rv ->
                if (style == VERTICAL) {
                    rv.addItemDecoration(
                        LinearSpacingItemDecoration(0.5f.dp2px().toInt(), 0, false)
                    )
                    rv.layoutManager = LinearLayoutManager(rv.context)
                    adapter = VerticalMenuAdapter {
                        dismiss()
                        itemClick.invoke(itemTag[it])
                    }
                } else {
                    rv.layoutManager = GridLayoutManager(rv.context, 5)
                    adapter = HorizontalMenuAdapter {
                        dismiss()
                        itemClick.invoke(itemTag[it])
                    }
                }
                rv.adapter = adapter
            }
        }
    }

    fun setOptions(options: List<MenuItem>) {
        adapter?.items = options
    }

    data class MenuItem(val icon: Int, val text: CharSequence)


}