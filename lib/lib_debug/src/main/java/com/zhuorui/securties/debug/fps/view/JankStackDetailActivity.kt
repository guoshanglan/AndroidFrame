package com.zhuorui.securties.debug.fps.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.widget.LinearLayoutCompat
import base2app.ui.activity.AbsActivity
import base2app.util.StatusBarUtil
import base2app.viewbinding.viewBinding
import com.zhuorui.securties.debug.R
import com.zhuorui.securties.debug.databinding.DebugFpsDetailActivityBinding
import com.zhuorui.securties.debug.fps.FpsMonitor
import com.zhuorui.securties.debug.fps.JankInfo_
import com.zhuorui.securties.debug.fps.ms2Date


/**
 * JankStackDetailActivity
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  16:10
 */
class JankStackDetailActivity : AbsActivity() {

    private val binding by viewBinding(DebugFpsDetailActivityBinding::bind)

    val mJankDetailAdapter = JankDetailAdapter()

    override val acContentRootViewId: Int
        get() = R.id.root_layout
    override val layout: Int
        get() = R.layout.debug_fps_detail_activity

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getLongExtra("id", 0L)
        val info = FpsMonitor.mBox.query().equal(JankInfo_.dbId,id).build().findFirst()
        if (info == null) finish()

        binding.recyclerview.adapter = mJankDetailAdapter

        info?.let {jankInfo->
            if(jankInfo.resolved){
                binding.resolveStatus.setImageResource(R.drawable.selected)
            }else {
                binding.resolveStatus.setImageResource(R.drawable.fps_alarm)
                binding.resolveStatus.setOnClickListener {
                    FpsMonitor.mBox.put(info.apply {
                        resolved = true
                    })
                    binding.resolveStatus.setImageResource(R.drawable.selected)
                }
            }

            jankInfo.occurredTime?.let { occurTime->
                binding.occurTimeTv.text = getString(R.string.occurrence_time,ms2Date(occurTime))
            }
            binding.costTimeTv.text = getString(R.string.cost_time,jankInfo.frameCost.toString())

            mJankDetailAdapter.datas = jankInfo.stackCountEntries?.map { pair->
                JankDetailData(pair.second, pair.first)
            } as MutableList<JankDetailData>
            mJankDetailAdapter.notifyDataSetChanged()
        }


        val barHeight = StatusBarUtil.getStatusBarHeight(this)
        binding.back.setOnClickListener { finish() }
        (binding.titleBar.layoutParams as LinearLayoutCompat.LayoutParams).topMargin = barHeight

    }

}