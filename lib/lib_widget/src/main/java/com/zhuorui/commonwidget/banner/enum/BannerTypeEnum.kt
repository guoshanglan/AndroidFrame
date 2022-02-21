package com.zhuorui.commonwidget.banner.enum

/**

@author guoshanglan
@description: banner类型的enum
@date : 2021/8/12 15:23
 */
enum class BannerTypeEnum(var TYPE: Int, var REQUEST: String) {   //request 标识请求数据
    UNKNOW(-1, "null"),//未知类型
    HOME_TOP_BANNER(1, "1-1"),   //首页头部海报banner
    HOME_MIDDLE_BANNER(2, "1-2"),  //首页中间横幅banner
    IPO_CENTER_BANNER(3, "1-3"),   //新股中心banner
    SIMULATION_BANNER(4, "1-7"),  //模拟交易banner
    ZHUORUI_LIVE_BANNER(5, "1-5"),  //卓锐直播banner
    STOCK_BANNER(6, "2-1"),        //自选股banner
    ME_BANNER(7, "5-1"),           //我的banner
    NEWS_BANNER(8, "8-1"),          //资讯banner
    NEWS_FOCUS_BANNER(9, "9-1")  ,    //资讯要闻banner
    FOUND_MARKET(10,"4-1")           //基金超市

}