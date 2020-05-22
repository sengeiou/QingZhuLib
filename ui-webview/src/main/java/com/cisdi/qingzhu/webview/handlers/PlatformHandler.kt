package com.cisdi.qingzhu.webview.handlers

import android.content.Context
import com.cisdi.qingzhu.jsbridge.BridgeHandler
import com.cisdi.qingzhu.jsbridge.CallBackFunction
import com.cisdi.qingzhu.webview.data.callback.CallBackCreator
import com.cisdi.qingzhu.webview.data.protocol.HandlerPlatform

/**
 * 返回平台信息
 *
 * @author lh
 */
class PlatformHandler : BridgeHandler() {

    override fun handler(context: Context, data: String?, function: CallBackFunction?) {
        function?.onCallBack(CallBackCreator.createSuccess(HandlerPlatform()))
    }
}