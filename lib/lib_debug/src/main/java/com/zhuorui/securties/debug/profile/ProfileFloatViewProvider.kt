package com.zhuorui.securties.debug.profile

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import base2app.ex.mainThread
import com.zhuorui.securties.debug.Background
import com.zhuorui.securties.debug.R
import com.zhuorui.securties.debug.floatview.FloatViewProvider

/**
 * ProfileFloatViewProvider
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  9:54
 */
class ProfileFloatViewProvider : FloatViewProvider(), ProfileMonitor.ProfileListener {

    override val layout: Int
        get() = R.layout.profile_float_view

    override val isKetBack: Boolean
        get() = false

    override val isTouchDelegate: Boolean
        get() = true

    private var frame: TextView? = null

    private var cpu: TextView? = null

    private var memory: TextView? = null


    override fun onAttach(root: ViewGroup, container: View?) {
        ProfileMonitor.startProfile(true)
        ProfileMonitor.addProfileListener(this)
        container?.findViewById<View>(R.id.back)?.setOnClickListener {
            Background.unRegisterFloatViewProvider(this)
        }
        frame = container?.findViewById(R.id.frame)
        cpu = container?.findViewById(R.id.cpu)
        memory = container?.findViewById(R.id.memory)
    }

    override fun detached(root: ViewGroup) {
        super.detached(root)
        ProfileMonitor.startProfile(false)
    }

    @SuppressLint("SetTextI18n")
    override fun onProfile(rate: Int, cpuF: Float, memoryF: Float) {
        mainThread {
            frame?.text = rate.toString()

            cpu?.text = "${cpuF.toInt()}%"

            memory?.text = "${memoryF.toInt()}M"
        }
    }


}