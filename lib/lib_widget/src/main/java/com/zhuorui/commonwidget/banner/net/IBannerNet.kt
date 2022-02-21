package com.zhuorui.commonwidget.banner.net

import retrofit2.http.Body
import retrofit2.http.POST

/**

@author guoshanglan
@description: banner 请求
@date : 2021/8/18 9:41
 */
interface IBannerNet {
    /**
     * banner数据
     */
    @POST(BannerApi.BannerList)
    suspend fun getBannerList(@Body bannerRequest: BannerRequest): BannerResponse
}