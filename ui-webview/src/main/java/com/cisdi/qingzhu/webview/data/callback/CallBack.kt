package com.cisdi.qingzhu.webview.data.callback

import com.cisdi.qingzhu.webview.constants.JsConstant
import com.lcy.base.core.utils.GsonConvertUtil

class CallBack<out T>(
    val code: String = JsConstant.CALLBACK_SUCCESS,
    val message: String = "success",
    val data: T? = null
)

object CallBackCreator {

    fun createError(errorMsg: String = ""): String {
        return GsonConvertUtil.toJson(
            CallBack<String>(
                code = JsConstant.CALLBACK_ERROR,
                message = errorMsg
            )
        )
    }

    fun <T> createSuccess(t: T): String {
        return GsonConvertUtil.toJson(CallBack<T>(data = t))
    }
}