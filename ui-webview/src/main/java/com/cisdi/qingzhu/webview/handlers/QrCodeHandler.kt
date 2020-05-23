package com.cisdi.qingzhu.webview.handlers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import com.cisdi.qingzhu.jsbridge.BridgeHandler
import com.cisdi.qingzhu.jsbridge.CallBackFunction
import com.cisdi.qingzhu.webview.constants.JsConstant
import com.cisdi.qingzhu.webview.data.callback.CallBackCreator
import com.cisdi.qingzhu.webview.data.event.BaseEvent
import com.cisdi.qingzhu.webview.data.event.JsEvent
import com.cisdi.qingzhu.webview.ui.X5WebViewActivity
import com.lcy.base.core.common.CoreApplication
import com.lcy.base.core.rx.RxBus
import com.tbruyelle.rxpermissions2.RxPermissions

/**
 * 扫描二维码
 *
 * @author lh
 */
class QrCodeHandler : BridgeHandler() {

    @SuppressLint("CheckResult")
    override fun handler(context: Context, data: String?, function: CallBackFunction?) {
        val currentContext = CoreApplication.instance().currentActivity() ?: context
        if (currentContext is X5WebViewActivity) {
            RxPermissions(currentContext)
                .request(Manifest.permission.CAMERA)
                .subscribe { granted ->
                    if (granted) {
                        RxBus.post(
                            BaseEvent(
                                JsConstant.EVENT_QR_SCAN,
                                JsEvent(params = null, callback = function)
                            )
                        )
                    } else {
                        function?.onCallBack(
                            CallBackCreator.createError("media permission not granted")
                        )
                    }
                }
        } else {
            function?.onCallBack(CallBackCreator.createError(JsConstant.ERROR_WEB_ACTIVITY))
        }
    }
}