package com.example.myframe.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.view.ViewCompat
import base2app.MainAct
import base2app.ex.color
import base2app.ex.logd
import base2app.ui.activity.AbsActivity
import base2app.util.ToastUtil
import com.example.myframe.R
import com.zhuorui.securties.skin.ZRSkinManager

class MainActivity : AbsActivity(), MainAct {
    override val acContentRootViewId: Int
        get() = R.id.appAct

    override val layout: Int
        get() = R.layout.app_activity_main

    private var newIntentHandler: Handler? = null

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (newIntentHandler == null) {
            newIntentHandler = Handler(Looper.getMainLooper())
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logd("MainFragment onCreate startTime : " + System.currentTimeMillis())
    }

    override fun getStartDestinationArgs(): Bundle? {
        //将intent分发至MainFragment处理
        return intent.extras
    }

    /**
     * 获取开始目的地
     * @return calssName
     */
    override fun getStartDestination(): String {
        return MainFragment::class.java.name
    }

    override fun applyStatusBar() {
        val controller = ViewCompat.getWindowInsetsController(window.decorView)
        window.navigationBarColor = color(R.color.main_background)
        controller?.isAppearanceLightNavigationBars = !ZRSkinManager.instance.isNight()
        controller?.isAppearanceLightStatusBars = !ZRSkinManager.instance.isNight()
        super.applyStatusBar()
    }

    override fun statusBarLightMode(): Boolean {
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        newIntentHandler?.removeCallbacksAndMessages(null)
        ToastUtil.instance.cancel()
        ZRSkinManager.instance.clear()
    }

}