package com.zhuorui.securties.debug.net

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import base2app.BaseApplication
import base2app.ex.dp2px
import base2app.ex.mainThread
import com.zhuorui.securties.debug.Background
import com.zhuorui.securties.debug.R
import com.zhuorui.securties.debug.floatview.FloatViewProvider
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

/**
 * HttpFloatViewProvider
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  9:15
 */
class HttpFloatViewProvider : FloatViewProvider(), HttpMonitor.HttpListener {

    override val layout: Int
        get() = R.layout.debug_http_hook_list
    override val isKetBack: Boolean
        get() = true
    override val isTouchDelegate: Boolean
        get() = true

    private var rootLayout: LinearLayoutCompat? = null

    override fun onAttach(root: ViewGroup, container: View?) {
        container?.apply {
            layoutParams.height = 250.dp2px().toInt()
            layoutParams.width = ViewGroup.MarginLayoutParams.MATCH_PARENT
        }

        container?.findViewById<View>(R.id.back)?.setOnClickListener {
            Background.unRegisterFloatViewProvider(this)
        }
        rootLayout = container?.findViewById(R.id.root_layout)

        initScroll(25)

        notifyDatas()

        HttpMonitor.addHttpListener(this)
    }

    private fun notifyDatas() {
        rootLayout?.let {

            val datas = HttpMonitor.hookers.queue.reversed()
            for (i in 0 until it.childCount) {
                it.getChildAt(i).let { child ->
                    var hooker: RequestHooker? = null
                    if (i < HttpMonitor.hookers.size()) {
                        try {
                            hooker = datas[i]
                        }catch (e:Exception){

                        }
                    }
                    hooker?.let {
                        child.setOnClickListener {
                            val intent = Intent(child.context, ZRNetDetailActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            HttpMonitor.selecedHooker = hooker
                            child.context?.startActivity(intent)
                            Background.unRegisterFloatViewProvider(this)
                        }
                    }

                    child.findViewById<TextView>(R.id.status).text = when (hooker) {
                        null -> ""
                        else -> when (hooker.responseTime) {
                            null -> "loading..."
                            else -> "ok : ${hooker.responseSize?.let { it1 ->
                                getNetFileSizeDescription(
                                    it1
                                )
                            }}"
                        }
                    }
                    child.findViewById<TextView>(R.id.cost).text = when (hooker) {
                        null -> ""
                        else -> when (hooker.responseTime) {
                            null -> ""
                            else -> "cost : ${TimeUnit.NANOSECONDS.toMillis(hooker.responseTime!! - hooker.requestTime!!)} ms"
                        }
                    }
                    child.findViewById<TextView>(R.id.url).text = when (hooker) {
                        null -> ""
                        else -> BaseApplication.baseApplication.config.runMode.toString()+":/" + hooker.url?.let { it1 -> removeDomainAndPort(it1) }
                    }
                }
            }
        }
    }

    private fun removeDomainAndPort(url: String): String {
        var url_bak = ""
        if (url.indexOf("//") != -1) {
            val splitTemp = url.split("//").toTypedArray()
            if (splitTemp.isNotEmpty() && splitTemp[1].indexOf("/") != -1) {
                val urlTemp2 = splitTemp[1].split("/").toTypedArray()
                if (urlTemp2.size > 1) {
                    for (i in 1 until urlTemp2.size) {
                        url_bak = url_bak + "/" + urlTemp2[i]
                    }
                }
            }
        }
        return url_bak
    }

    private fun initScroll(size: Int) {
        rootLayout?.let {
            it.removeAllViews()
            for (i in 0 until size) {
                LayoutInflater.from(it.context).inflate(R.layout.debug_http_hook_item, it, true)
            }
        }
    }

    override fun notifyHookerChange() {
        mainThread {
            notifyDatas()
        }
    }

    private fun getNetFileSizeDescription(size: Long): String? {
        val bytes = StringBuffer()
        val format = DecimalFormat("###.0")
        if (size >= 1024 * 1024 * 1024) {
            val i = size / (1024.0 * 1024.0 * 1024.0)
            bytes.append(format.format(i)).append("GB")
        } else if (size >= 1024 * 1024) {
            val i = size / (1024.0 * 1024.0)
            bytes.append(format.format(i)).append("MB")
        } else if (size >= 1024) {
            val i = size / 1024.0
            bytes.append(format.format(i)).append("KB")
        } else if (size < 1024) {
            if (size <= 0) {
                bytes.append("0B")
            } else {
                bytes.append(size.toInt()).append("B")
            }
        }
        return bytes.toString()
    }
}