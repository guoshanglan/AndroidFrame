package com.zhuorui.securties.debug.net

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import base2app.BaseApplication
import base2app.ex.gson
import base2app.ui.activity.AbsActivity
import base2app.util.StatusBarUtil
import base2app.viewbinding.viewBinding
import com.zhuorui.securties.debug.R
import com.zhuorui.securties.debug.databinding.DebugHttpNetDeatilActivityBinding
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit


class ZRNetDetailActivity : AbsActivity() {

    private val binding by viewBinding(DebugHttpNetDeatilActivityBinding::bind)

    override val layout: Int
        get() = R.layout.debug_http_net_deatil_activity

    override val acContentRootViewId: Int
        get() = R.id.root_layout

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val barHeight = StatusBarUtil.getStatusBarHeight(this)
        binding.back.setOnClickListener { finish() }
        (binding.titleBar.layoutParams as LinearLayoutCompat.LayoutParams).topMargin = barHeight

        val hooker = HttpMonitor.selecedHooker

        binding.url.text =
            BaseApplication.baseApplication.config.runMode.toString() + ":/" + hooker?.url?.let { it1 ->
                removeDomainAndPort(it1)
            }

        binding.tvMethod.text = hooker?.method

        binding.tvHeader.text = hooker?.requestHeader?.toMultimap()?.gson()

        try {
            binding.jsonBody.bindJson(hooker?.requestBody)
            binding.jsonBody.visibility = View.VISIBLE

        } catch (e: Exception) {
            e.printStackTrace()
            binding.jsonBody.visibility = View.GONE
        }

        binding.size.text = when (hooker) {
            null -> ""
            else -> when (hooker.responseTime) {
                null -> "loading..."
                else -> "ok : ${
                    hooker.responseSize?.let { it1 ->
                        getNetFileSizeDescription(
                            it1
                        )
                    }
                }"
            }
        }
        binding.cost.text = when (hooker) {
            null -> ""
            else -> when (hooker.responseTime) {
                null -> ""
                else -> "binding.cost : ${TimeUnit.NANOSECONDS.toMillis(hooker.responseTime!! - hooker.requestTime!!)} ms"
            }
        }

        binding.tvResponseHeader.text = hooker?.requestHeader?.toMultimap()?.gson()

        binding.contentType.text = hooker?.contetType
        try {
            binding.jsonRespondeBody.bindJson(hooker?.responseBody)
            binding.jsonRespondeBody.visibility = View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
            binding.jsonRespondeBody.visibility = View.GONE
            binding.respondeBodyText.visibility = View.VISIBLE
            binding.respondeBodyText.text = hooker?.responseBody
        }
    }

    private fun removeDomainAndPort(url: String): String {
        var urlBak = ""
        if (url.indexOf("//") != -1) {
            val splitTemp = url.split("//").toTypedArray()
            if (splitTemp.isNotEmpty() && splitTemp[1].indexOf("/") != -1) {
                val urlTemp2 = splitTemp[1].split("/").toTypedArray()
                if (urlTemp2.size > 1) {
                    for (i in 1 until urlTemp2.size) {
                        urlBak = urlBak + "/" + urlTemp2[i]
                    }
                }
            }
        }
        return urlBak
    }

    private fun getNetFileSizeDescription(size: Long): String {
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

    override fun onDestroy() {
        super.onDestroy()
        HttpConfig.onClick(this)
    }

}