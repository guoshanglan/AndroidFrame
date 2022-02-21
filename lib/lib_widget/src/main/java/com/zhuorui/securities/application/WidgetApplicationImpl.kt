package com.zhuorui.securities.application

import android.app.Application
import android.content.Context
import base2app.IAppInitProxy
import base2app.ex.text
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.zhuorui.commonwidget.R
import com.zhuorui.commonwidget.refresh.ClassicsFooter
import com.zhuorui.commonwidget.refresh.ClassicsHeader

/**
 * @descraption
 * @time  17:52
 */
class WidgetApplicationImpl : IAppInitProxy {

    override fun attachContext(context: Context?) {
    }

    override fun onAppCreate(application: Application) {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ ->
            ClassicsHeader.REFRESH_HEADER_PULLING = text(R.string.pull_down_refresh)
            ClassicsHeader.REFRESH_HEADER_REFRESHING = text(R.string.refresh_header_loading)
            ClassicsHeader.REFRESH_HEADER_RELEASE = text(R.string.release_refresh)
            ClassicsHeader.REFRESH_HEADER_FINISH = text(R.string.refresh_complete)
            ClassicsHeader.REFRESH_HEADER_FAILED = text(R.string.refresh_failed)

            //指定为经典Header，默认是 贝塞尔雷达Header
            ClassicsHeader(context)
        }
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
            ClassicsFooter.REFRESH_FOOTER_LOADING = text(R.string.footer_loading)
            ClassicsFooter.REFRESH_FOOTER_FINISH = ""
            ClassicsFooter.REFRESH_FOOTER_FAILED = text(R.string.footer_load_failed)
            ClassicsFooter.REFRESH_FOOTER_NOTHING = text(R.string.footer_all_loaded)

            //指定为经典Footer，默认是 BallPulseFooter
            ClassicsFooter(context)
        }
    }

    override fun onAppBackground() {
    }

    override fun onAppForeground() {
    }

    override fun onAppClose() {
    }

    /**
     * 主流程创建
     */
    override fun mainCreated() {
    }

    /**
     * 主流程结束
     */
    override fun mainDestroyed() {
    }

}