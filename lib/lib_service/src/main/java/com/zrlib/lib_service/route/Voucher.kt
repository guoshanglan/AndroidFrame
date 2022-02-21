package com.zrlib.lib_service.route

import android.os.Bundle
import com.alibaba.android.arouter.facade.Postcard

/**
 * author : Martin-huyuan
 * email : hy569835826@163.com
 * date : 2020/11/5
 * dest : Voucher 路由凭证
 */
class Voucher(var postcard: Postcard, var bundle: Bundle? = null, var befor: ((priority: Int, action: () -> Unit) -> Boolean)? = null)