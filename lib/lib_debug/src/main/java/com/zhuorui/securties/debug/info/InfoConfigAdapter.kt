package com.zhuorui.securties.debug.info

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zhuorui.securties.debug.R

/**
 * ConfigAdapter
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  9:59
 */
class InfoConfigAdapter() : RecyclerView.Adapter<InfoConfigAdapter.MyViewHolder>() {

    val datas = mutableListOf<SysInfoItem>()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(data: SysInfoItem, position: Int) {
            itemView.findViewById<TextView>(R.id.name).text = data.name
            itemView.findViewById<TextView>(R.id.value).text = data.value
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.debug_info_config_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(datas[position], position)
    }

    override fun getItemCount(): Int {
        return datas.size
    }
}