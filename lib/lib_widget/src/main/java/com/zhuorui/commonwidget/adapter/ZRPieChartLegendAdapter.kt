package com.zhuorui.commonwidget.adapter

import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import base2app.adapter.BaseListAdapter
import base2app.ex.blod
import base2app.ex.default
import base2app.ex.sansSerifMedium
import base2app.ex.text
import base2app.viewbinding.viewBinding
import com.zhuorui.commonwidget.R
import com.zhuorui.commonwidget.databinding.ItemRecyclerPieChartLegendViewBinding
import com.zhuorui.commonwidget.model.PieChartLegendModel


/**
 * @date 2020/11/17 09:36
 * @desc pie chart legend
 */
class ZRPieChartLegendAdapter(
    private val itemHorizontalGap: Int,
    private val itemVerticalGap: Int,
    private val legendColorSize: Int,
    private val legendColorMode: Int,
    private val legendTitleSize: Float,
    private val legendTitleColor: Int,
    private val legendValueSize: Float,
    private val legendValueColor: Int,
    private val legendValueTextStyle: Int,
    private val legendTitleMarginStart: Int,
    private val legendTitleMarginEnd: Int,
) : BaseListAdapter<PieChartLegendModel>() {

    companion object {
        /**
         * 示例颜色模式:方形
         */
        const val LEGEND_COLOR_MODE_SQUARE = 1

        /**
         * 示例颜色模式:圆形
         */
        const val LEGEND_COLOR_MODE_CIRCLE = 2

        /**
         * 正常字体
         */
        const val TEXT_STYLE_NORMAL = 0

        /**
         * 正常字体
         */
        const val TEXT_STYLE_BOLD = 1

        /**
         * 正常字体
         */
        const val TEXT_STYLE_MEDIUM = 2
    }

    override fun createViewHolderByParent(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return InvestmentLegendViewHolder(inflateView(parent,R.layout.item_recycler_pie_chart_legend_view))
    }

    inner class InvestmentLegendViewHolder(itemView: View?) :
        ListItemViewHolder<PieChartLegendModel>(itemView, false, false) {

        private val binding by viewBinding(ItemRecyclerPieChartLegendViewBinding::bind)

        override fun bind(item: PieChartLegendModel?, itemIndex: Int) {
            item?.let {
                itemView.setPadding(
                    itemHorizontalGap,
                    itemVerticalGap / 2,
                    itemHorizontalGap,
                    itemVerticalGap / 2
                )
                with(binding) {
                    tvLegendColor.layoutParams =
                        LinearLayout.LayoutParams(legendColorSize, legendColorSize)
                    it.legendColor?.let { color ->
                        if (legendColorMode == LEGEND_COLOR_MODE_CIRCLE) {
                            tvLegendColor.background = GradientDrawable().apply {
                                shape = GradientDrawable.OVAL
                                setSize(legendColorSize, legendColorSize)
                                setColor(color)
                            }
                        } else {
                            tvLegendColor.setBackgroundColor(color)
                        }
                    }

                    tvLegendTitle.layoutParams =
                        LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                            .apply {
                                marginStart = legendTitleMarginStart
                                marginEnd = legendTitleMarginEnd
                            }
                    tvLegendTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, legendTitleSize)
                    tvLegendTitle.setTextColor(legendTitleColor)
                    tvLegendTitle.text = it.legendTitle ?: text(R.string.empty_tip)
                    when (legendValueTextStyle) {
                        TEXT_STYLE_BOLD -> tvLegendValue.blod()
                        TEXT_STYLE_MEDIUM -> tvLegendValue.sansSerifMedium()
                        else -> tvLegendValue.default()
                    }
                    tvLegendValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, legendValueSize)
                    tvLegendValue.setTextColor(legendValueColor)
                    tvLegendValue.text = it.legendValue ?: text(R.string.empty_tip)
                }
            }
        }
    }
}