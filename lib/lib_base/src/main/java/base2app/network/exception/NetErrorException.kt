package base2app.network.exception

import base2app.network.BaseResponse

/**
 * @date 2021/5/26 15:36
 * @desc App相关错误，errorCode!="000000"的错误，即后台有响应但是报错的exception
 */
class NetErrorException(
    val errorCode: String?,
    val errorMsg: String,
    val errorResponse: BaseResponse? = null
) : Exception(errorMsg)