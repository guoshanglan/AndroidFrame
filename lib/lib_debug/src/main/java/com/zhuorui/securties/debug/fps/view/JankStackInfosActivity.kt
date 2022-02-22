package com.zhuorui.securties.debug.fps.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.widget.LinearLayoutCompat
import base2app.ui.activity.AbsActivity
import base2app.util.StatusBarUtil
import base2app.viewbinding.viewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zhuorui.securties.debug.R
import com.zhuorui.securties.debug.databinding.DebugFpsInfosActivityBinding
import com.zhuorui.securties.debug.fps.FpsMonitor
import com.zhuorui.securties.debug.fps.JankInfo_
import io.objectbox.query.QueryBuilder


/**
 * JankStackInfosActivity
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  16:10
 */
class JankStackInfosActivity : AbsActivity() {

    private val binding by viewBinding(DebugFpsInfosActivityBinding::bind)

    var startTime = 0L

    private var mJankInfoAdapter: JankInfoAdapter? = null

    override val acContentRootViewId: Int
        get() = R.id.root_layout
    override val layout: Int
        get() = R.layout.debug_fps_infos_activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val barHeight = StatusBarUtil.getStatusBarHeight(this)
        binding.back.setOnClickListener { finish() }
        (binding.titleBar.layoutParams as LinearLayoutCompat.LayoutParams).topMargin = barHeight
        binding.traceNavigation.itemIconTintList = null
        binding.traceNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        mJankInfoAdapter = JankInfoAdapter()
        binding.recyclerview.adapter = mJankInfoAdapter
    }

    override fun onResume() {
        super.onResume()
        startTime = when (binding.traceNavigation.selectedItemId) {
            R.id.navigation_period -> {
                FpsMonitor.startTime ?: 0L
            }
            else -> {
                0L
            }
        }
        showJankInfos(startTime)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        startTime = when (item.itemId) {
            R.id.navigation_period -> {
                FpsMonitor.startTime ?: 0L
            }
            else -> {
                0L
            }
        }
        showJankInfos(startTime)
        true
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showJankInfos(startTime: Long) {
        var queryBuilder = FpsMonitor.mBox.query()?.greater(JankInfo_.occurredTime, startTime)
            queryBuilder = queryBuilder?.order(JankInfo_.frameCost, QueryBuilder.DESCENDING)
        mJankInfoAdapter?.datas = queryBuilder?.build()?.find()?.map { jankInfo->
            JankInfoData(jankInfo)
        } as MutableList<JankInfoData>
        mJankInfoAdapter?.notifyDataSetChanged()
    }
}