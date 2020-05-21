package com.cisdi.qingzhu.webview.handlers

import android.content.Context
import android.widget.Toast
import com.cisdi.qingzhu.jsbridge.BridgeHandler
import com.cisdi.qingzhu.jsbridge.CallBackFunction

/**
 * 支持弹出吐司
 */
class ToastHandler : BridgeHandler() {

    override fun handler(context: Context, data: String?, function: CallBackFunction?) {
        Toast.makeText(context, "datadatadata---data:$data", Toast.LENGTH_SHORT).show()
        function?.onCallBack("{\"status\":\"0\",\"msg\":\"toast success\"}")
    }
}