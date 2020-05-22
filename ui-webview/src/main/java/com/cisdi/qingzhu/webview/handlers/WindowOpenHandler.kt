package com.cisdi.qingzhu.webview.handlers

import android.content.Context
import com.cisdi.qingzhu.jsbridge.BridgeHandler
import com.cisdi.qingzhu.jsbridge.CallBackFunction
import com.cisdi.qingzhu.webview.constants.IntentArgs
import com.cisdi.qingzhu.webview.data.callback.CallBackCreator
import com.cisdi.qingzhu.webview.data.protocol.JsRequestData
import com.cisdi.qingzhu.webview.ui.X5WebViewActivity
import com.lcy.base.core.utils.GsonConvertUtil
import org.jetbrains.anko.startActivity

/**
 * 操作窗口
 *
 * @author lh
 */
class WindowOpenHandler : BridgeHandler() {

    override fun handler(context: Context, data: String?, function: CallBackFunction?) {
        if (data.isNullOrEmpty()) {
            function?.onCallBack(CallBackCreator.createError("data can not be null!"))
            return
        }
        try {
            val requestData = GsonConvertUtil.fromJson(data, JsRequestData::class.java)
            if (requestData.url.isNullOrEmpty()) {
                function?.onCallBack(CallBackCreator.createError("url is needed!"))
                return
            }
            context.startActivity<X5WebViewActivity>(IntentArgs.ARGS_URL to requestData.url)
            function?.onCallBack(CallBackCreator.createSuccess(null))
        } catch (e: Exception) {
            function?.onCallBack(CallBackCreator.createError(e.message ?: "json convert error!"))
        }
    }
}