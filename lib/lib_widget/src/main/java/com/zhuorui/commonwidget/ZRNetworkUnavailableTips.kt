package com.zhuorui.commonwidget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.zrlib.lib_service.base.CommService

/**
 *    date   : 2021/6/23 17:08
 *    desc   : 网络状态不可用View
 */
class ZRNetworkUnavailableTips @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), Observer<Boolean> {

    /**
     * 显示高度
     */
    private val _heightLiveData = MutableLiveData<Int>().apply { value = height }
    val heightLiveData: LiveData<Int> = _heightLiveData

    /**
     * 手动关闭标记
     */
    private var mColse = false

    /**
     * 开启网络状态监听
     */
    fun observe(owner: LifecycleOwner) {
        CommService.instance.getNetworkConnectLiveData().observe(owner, this)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (_heightLiveData.value != h) {
            _heightLiveData.value = h
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (changedView == this) {
            val h = if (visibility == View.GONE) 0 else height
            if (_heightLiveData.value != h) {
                _heightLiveData.value = h
            }
        }
    }

    override fun onChanged(connect: Boolean) {
        when {
            connect -> {
                //网络连接上，重置关闭标记
                mColse = false
                removeAllViews()
            }
            mColse -> {
                removeAllViews()
            }
            else -> {
                addTips()
            }
        }
    }

    private fun addTips() {
        View.inflate(context, R.layout.layout_network_unavailable_tips, this)
        findViewById<View>(R.id.iv_close).setOnClickListener {
            mColse = true
            removeAllViews()
        }
        visibility = View.VISIBLE
    }

    override fun removeAllViews() {
        if (childCount > 0) {
            super.removeAllViews()
            visibility = View.GONE
        }
    }

}