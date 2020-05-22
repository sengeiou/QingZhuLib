package com.cisdi.qingzhu.webview.handlers

import com.cisdi.qingzhu.webview.constants.JsConstant

/**
 * 扫描二维码
 *
 * @author lh
 */
class QrCodeHandler : MediaHandler() {

    override fun mediaType(): Int = JsConstant.MEDIA_QR_CODE

//    override fun handler(context: Context, data: String?, function: CallBackFunction?) {
//        val currentActivity = CoreApplication.instance().currentActivity()
//        if (currentActivity is X5WebViewActivity) {
//            RxBus.post(
//                BaseEvent(
//                    JsConstant.EVENT_QR_SCAN,
//                    JsEvent(params = null, callback = function)
//                )
//            )
//        } else {
//            function?.onCallBack(CallBackCreator.createError(JsConstant.ERROR_WEB_ACTIVITY))
//        }
//    }
}