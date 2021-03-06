package com.cisdi.qingzhu.webview.handlers

import android.content.Context
import com.cisdi.qingzhu.jsbridge.BridgeHandler
import com.cisdi.qingzhu.jsbridge.CallBackFunction
import com.cisdi.qingzhu.webview.data.callback.CallBackCreator
import org.jetbrains.anko.toast

/**
 * 支持弹出吐司
 *
 * @author lh
 */
class ToastHandler : BridgeHandler() {

    override fun handler(context: Context, data: String?, function: CallBackFunction?) {
        if (!data.isNullOrEmpty()) {
            context.toast(data)
        }
        function?.onCallBack(CallBackCreator.createSuccess(null))
    }
}