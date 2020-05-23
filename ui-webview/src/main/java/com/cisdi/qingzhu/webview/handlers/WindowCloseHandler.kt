package com.cisdi.qingzhu.webview.handlers

import android.content.Context
import com.cisdi.qingzhu.jsbridge.BridgeHandler
import com.cisdi.qingzhu.jsbridge.CallBackFunction
import com.cisdi.qingzhu.webview.constants.JsConstant
import com.cisdi.qingzhu.webview.data.event.JsEvent
import com.cisdi.qingzhu.webview.data.callback.CallBackCreator
import com.cisdi.qingzhu.webview.data.event.BaseEvent
import com.cisdi.qingzhu.webview.data.protocol.JsRequestData
import com.cisdi.qingzhu.webview.ui.X5WebViewActivity
import com.lcy.base.core.common.CoreApplication
import com.lcy.base.core.rx.RxBus
import com.lcy.base.core.utils.GsonConvertUtil
import org.jetbrains.anko.startActivity

/**
 * 操作窗口
 *
 * @author lh
 */
class WindowCloseHandler : BridgeHandler() {

    override fun handler(context: Context, data: String?, function: CallBackFunction?) {
        if (data.isNullOrEmpty()) {
            closeTopWebView(null, function)
            return
        }
        try {
            val requestData = GsonConvertUtil.fromJson(data, JsRequestData::class.java)
            if (closeTopWebView(requestData, function)) return
            //发送Event事件,根据url关闭页面
            RxBus.post(
                BaseEvent(
                    JsConstant.EVENT_CLOSE_WINDOW,
                    JsEvent(params = requestData.url, callback = function)
                )
            )
        } catch (e: Exception) {
            function?.onCallBack(CallBackCreator.createError(e.message ?: "json convert error!"))
        }
    }

    private fun closeTopWebView(requestData: JsRequestData?, function: CallBackFunction?): Boolean {
        //关闭页面
        val currentActivity = CoreApplication.instance().currentActivity()
        if (currentActivity is X5WebViewActivity) {
            if (requestData == null || requestData.url.isNullOrEmpty()) {
                currentActivity.finish()
                function?.onCallBack(CallBackCreator.createSuccess(null))
                return true
            }
        }
        return false
    }
}