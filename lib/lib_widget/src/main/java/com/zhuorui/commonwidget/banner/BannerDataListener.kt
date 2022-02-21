package com.zhuorui.commonwidget.banner

import com.zhuorui.commonwidget.banner.model.BannerModel

/**

@author guoshanglan
@description:banner数据监听接口
@date : 2021/8/12 16:59
 */
interface BannerDataListener {

    /**
     * 获取banner数据成功
     */
   fun getBannerDataSuccessFul(bannerList: ArrayList<BannerModel>?)

    /**
     * 获取banner数据失败
     */
   fun getBannerDataFair()

}