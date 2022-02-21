package com.zhuorui.commonwidget.banner.model

import base2app.NoProguardInterface


/**

@author guoshanglan
@description:banner的基础数据类型
@date : 2021/8/12 15:11
 */
class BannerModel(
    var imageUrl: String? = null, //图片地址
    val jumpType: Int?,  //跳转方式 1：原生 2：h5
    var url: String? = null, //H5链接
    var bannerId: Int? = null,   //banner的id
    var title: String? = null,  //标题名称
    var source: String? = null,  //来源
    var pubTime: Long? = null  //发布时间
) : NoProguardInterface