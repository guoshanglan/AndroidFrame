package com.zhuorui.securties.debug.fps.view

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zhuorui.securties.debug.R
import com.zhuorui.securties.debug.fps.FpsMonitor
import com.zhuorui.securties.debug.fps.ms2Date
import java.util.*

/**
 * JankInfoAdapter
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  9:59
 */
class JankInfoAdapter : RecyclerView.Adapter<JankInfoAdapter.MyViewHolder>() {

    var datas = mutableListOf<JankInfoData>()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("NotifyDataSetChanged")
        fun bind(data: JankInfoData) {

            itemView.findViewById<TextView>(R.id.occurTime_Tv).text = itemView.context.getString(R.string.occurrence_time,
                data.jankInfo.occurredTime?.let { ms2Date(it) })

            itemView.findViewById<TextView>(R.id.costTime_Tv).text = itemView.context.getString(R.string.cost_time, data.jankInfo.frameCost.toString())

            itemView.findViewById<TextView>(R.id.breviary_Tv).text =
                data.jankInfo.stackCountEntries?.get(0)?.first ?: "null"

            itemView.findViewById<View>(R.id.jank_delete).setOnClickListener {
                datas.remove(data)
                FpsMonitor.mBox.remove(data.jankInfo)
                this@JankInfoAdapter.notifyDataSetChanged()
            }

            if (data.jankInfo.resolved) {
                itemView.findViewById<ImageView>(R.id.solve_status)
                    .setImageResource(R.drawable.selected)
            } else {
                itemView.findViewById<ImageView>(R.id.solve_status)
                    .setImageResource(R.drawable.fps_alarm)
            }

            itemView.setOnClickListener {
                itemView.findViewById<View>(R.id.jank_delete).visibility = View.GONE
                val intent = Intent(it.context, JankStackDetailActivity::class.java)
                    .apply {
                        putExtra("id",data.jankInfo.dbId)
                    }
                it.context?.startActivity(intent)
            }
            itemView.setOnLongClickListener {
                itemView.findViewById<View>(R.id.jank_delete).visibility = View.VISIBLE
                true
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fps_holder_jank_info, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    override fun getItemCount(): Int {
        return datas.size
    }
}