package base2app.network


/**
 * 统一请求错误处理
 */
class ErrorResponse(code: String, message: String) : BaseResponse() {

    var netException: Exception? = null

    val isNetworkBroken: Boolean
        get() = this.code == NETWORK_BROKEN_STATUS

    init {
        this.code = code
        this.msg = message
    }

    companion object {

        fun valueOf(response: BaseResponse?): ErrorResponse {
            return response?.let {
                ErrorResponse(it.code, it.msg)
            } ?: exceptionResponse(null)
        }

        fun exceptionResponse(exception: Exception?): ErrorResponse {
            return ErrorResponse(
                NETWORK_BROKEN_STATUS,
                getNetworkBrokenText()
            ).apply { netException = exception }
        }
    }
}
