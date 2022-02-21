package com.zhuorui.commonwidget.banner

import base2app.Cache
import base2app.network.IScope
import base2app.network.ZRCoroutineScope
import base2app.network.cancelJob
import base2app.network.cancelScope
import com.zhuorui.commonwidget.banner.enum.BannerTypeEnum
import com.zhuorui.commonwidget.banner.net.BannerRequest
import com.zhuorui.commonwidget.banner.net.IBannerNet
import kotlinx.coroutines.Job

/**

@author guoshanglan
@description: banner 数据管理者
@date : 2021/8/12 16:57
 */
class BannerDataManager(bannerListener: BannerDataListener?) : IScope by ZRCoroutineScope() {

    private var listener: BannerDataListener? = bannerListener
    private val list: ArrayList<String> = ArrayList()
    private var job: Job? = null  //协程job

    /**
     * 查询banner数据
     */
    fun queryBannerData(bannerType: Int?) {
        if (bannerType == null) return
        job?.cancelJob()
        val request = getRequest(bannerType)
        job = sendRequest(
            apiFun = { Cache[IBannerNet::class.java]?.getBannerList(request) },
            onResponse = { response ->
                listener?.getBannerDataSuccessFul(response.data)
            },
            onError = { _, _, _ ->
                listener?.getBannerDataFair()
                return@sendRequest true
            }
        )
    }

    /**
     * 根据不同的banner类型获取请求参数
     */
    private fun getRequest(bannerType: Int): BannerRequest {
        list.clear()
        when (bannerType) {
            BannerTypeEnum.HOME_TOP_BANNER.TYPE -> {
                list.add(BannerTypeEnum.HOME_TOP_BANNER.REQUEST)
            }
            BannerTypeEnum.HOME_MIDDLE_BANNER.TYPE -> {
                list.add(BannerTypeEnum.HOME_MIDDLE_BANNER.REQUEST)
            }
            BannerTypeEnum.IPO_CENTER_BANNER.TYPE -> {
                list.add(BannerTypeEnum.IPO_CENTER_BANNER.REQUEST)
            }
            BannerTypeEnum.SIMULATION_BANNER.TYPE -> {
                list.add(BannerTypeEnum.SIMULATION_BANNER.REQUEST)
            }
            BannerTypeEnum.ZHUORUI_LIVE_BANNER.TYPE -> {
                list.add(BannerTypeEnum.ZHUORUI_LIVE_BANNER.REQUEST)
            }
            BannerTypeEnum.STOCK_BANNER.TYPE -> {
                list.add(BannerTypeEnum.STOCK_BANNER.REQUEST)
            }
            BannerTypeEnum.ME_BANNER.TYPE -> {
                list.add(BannerTypeEnum.ME_BANNER.REQUEST)
            }
            BannerTypeEnum.FOUND_MARKET.TYPE->{
                list.add(BannerTypeEnum.FOUND_MARKET.REQUEST)
            }
        }
        return BannerRequest(list)
    }

    /**
     * 取消协程作用域
     */
    fun onDestory() {
        listener = null
        cancelScope()
    }
}