package com.cisdi.qingzhu.webview.handlers

import android.content.Context
import com.cisdi.qingzhu.jsbridge.BridgeHandler
import com.cisdi.qingzhu.jsbridge.CallBackFunction
import com.cisdi.qingzhu.webview.constants.IntentArgs
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
class WindowHandler : BridgeHandler() {

    override fun handler(context: Context, data: String?, function: CallBackFunction?) {
        if (data.isNullOrEmpty()) {
            function?.onCallBack(CallBackCreator.createError("data can not be null!"))
            return
        }
        try {
            val requestData = GsonConvertUtil.fromJson(data, JsRequestData::class.java)
            //开启新页面
            if (requestData.open) {
                if (requestData.url.isNullOrEmpty()) {
                    function?.onCallBack(CallBackCreator.createError("url is needed!"))
                } else {
                    context.startActivity<X5WebViewActivity>(IntentArgs.ARGS_URL to requestData.url)
                    function?.onCallBack(CallBackCreator.createSuccess(null))
                }
                return
            }
            //关闭页面
            val currentActivity = CoreApplication.instance().currentActivity()
            if (requestData.url.isNullOrEmpty() && currentActivity is X5WebViewActivity) {
                currentActivity.finish()
                function?.onCallBack(CallBackCreator.createSuccess(null))
                return
            }
            //发送Event事件,根据url关闭页面
            RxBus.post(
                BaseEvent(
                    JsConstant.EVENT_CLOSE_WINDOW,
                    JsEvent(
                        params = requestData.url,
                        callback = function
                    )
                )
            )
        } catch (e: Exception) {
            function?.onCallBack(CallBackCreator.createError(e.message ?: "json convert error!"))
        }
    }
}