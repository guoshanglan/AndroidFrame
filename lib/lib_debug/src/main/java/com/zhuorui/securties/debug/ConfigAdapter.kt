package com.zhuorui.securties.debug

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import base2app.ex.drawable
import base2app.ex.text


/**
 * ConfigAdapter
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  9:59
 */
class ConfigAdapter(val activity: DebugConfigListProvider, val root: ViewGroup) : RecyclerView.Adapter<ConfigAdapter.MyViewHolder>() {

    val datas = mutableListOf<IDebugKit>()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(iDebugKit: IDebugKit, position: Int) {
            itemView.findViewById<TextView>(R.id.name).text = text(iDebugKit.name)
            itemView.findViewById<ImageView>(R.id.icon).setImageDrawable(drawable(iDebugKit.icon))
            itemView.setOnClickListener {
                activity.detached(root)
                iDebugKit.onClick(it.context)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.debug_config_item, parent,false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(datas[position], position)
    }

    override fun getItemCount(): Int {
        return datas.size
    }
}