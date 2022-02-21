package com.zhuorui.commonwidget.banner.net

import base2app.network.BaseRequest


/**

@author guoshanglan
@description: banner数据请求参数
@date : 2021/8/18 9:35
 */
class BannerRequest(
    /**
     * banner位置，具体参考 {@see BannerTypeEnum}
     */
    var position: List<String>? = null,
) : BaseRequest() {
    init {
        generateSign()
    }
}