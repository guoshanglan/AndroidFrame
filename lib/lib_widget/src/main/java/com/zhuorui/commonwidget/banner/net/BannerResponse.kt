package com.zhuorui.commonwidget.banner.net

import base2app.network.BaseResponse
import com.zhuorui.commonwidget.banner.model.BannerModel


/**

@author guoshanglan
@description: banner 数据响应类
@date : 2021/8/18 9:43
 */
class BannerResponse(
    var data: ArrayList<BannerModel>?
) : BaseResponse()