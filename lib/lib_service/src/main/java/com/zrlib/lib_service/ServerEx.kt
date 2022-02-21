package com.zrlib.lib_service

import com.alibaba.android.arouter.facade.template.IProvider

/**
 * author : Martin-huyuan
 * email : hy569835826@163.com
 * date : 2020/10/27
 * dest : RouterEx
 */

/**
 * 直接根据路由进行查询
 * 例:  Service<MarketService>(QuotesRouterPath.MARKET_EXPOSE_PATH)?.jumpStockPickStrategy(StockTsEnum.HK.market)
 * 也有几率会导致查找失败 , 所以一定要在判断 ;
 * 如果有需要添加失败后的操作可以进行额外的扩展处理 :
 *     Service<MarketService>(QuotesRouterPath.MARKET_EXPOSE_PATH) {
 *           ToastUtil.instance.toast("测试 测试")
 *      }?.jumpStockPickStrategy(StockTsEnum.HK.market)
 */
fun <T : IProvider> service(router: String, failure: (() -> Unit )?= {}): T? {
    val impl: T? = ZRServiceProvider.getInstance().getService(router)
    if (impl == null) {
        failure?.invoke()
    }
    return impl
}
