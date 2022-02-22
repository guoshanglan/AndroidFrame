package com.zhuorui.securties.debug.fps.view

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.zhuorui.securties.debug.R




/**
 * JankDetailAdapter
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  9:59
 */
class JankDetailAdapter : RecyclerView.Adapter<JankDetailAdapter.MyViewHolder>() {

    var datas = mutableListOf<JankDetailData>()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("NotifyDataSetChanged")
        fun bind(data: JankDetailData, position: Int) {
            itemView.findViewById<TextView>(R.id.count).text = data.count.toString()

            val span = SpannableString(data.stack)
            val rowStr: Array<String> = data.stack.split("\n").toTypedArray()
            var startIndex = 0
            for (s in rowStr) {
                if (s.contains("zhuorui")){
                    span.setSpan(
                        ForegroundColorSpan(Color.BLUE),
                        startIndex, startIndex + s.length + 1, Spannable.SPAN_PARAGRAPH
                    )
                }
                startIndex += s.length + 1
            }

            itemView.findViewById<TextView>(R.id.stack_detail).text = span

            itemView.setOnLongClickListener {
                val clipData = ClipData.newPlainText("", data.stack)
                (it.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(
                    clipData
                )
                Toast.makeText(it.context, "copied success", Toast.LENGTH_SHORT).show()
                return@setOnLongClickListener true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fps_holder_jank_detiles, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(datas[position], position)
    }

    override fun getItemCount(): Int {
        return datas.size
    }
}