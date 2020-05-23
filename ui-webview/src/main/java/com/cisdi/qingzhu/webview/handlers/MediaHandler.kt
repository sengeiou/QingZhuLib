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
import com.cisdi.qingzhu.webview.data.protocol.JsRequestData
import com.cisdi.qingzhu.webview.ui.X5WebViewActivity
import com.lcy.base.core.common.CoreApplication
import com.lcy.base.core.rx.RxBus
import com.lcy.base.core.utils.GsonConvertUtil
import com.tbruyelle.rxpermissions2.RxPermissions

/**
 * 相册信息
 *
 * @author lh
 */
abstract class MediaHandler : BridgeHandler() {

    abstract fun mediaType(): Int

    @SuppressLint("CheckResult")
    override fun handler(context: Context, data: String?, function: CallBackFunction?) {
        if (data.isNullOrEmpty() && (mediaType() == JsConstant.MEDIA_ALBUM || mediaType() == JsConstant.MEDIA_PHOTO)) {
            function?.onCallBack(CallBackCreator.createError("data can not be null!"))
            return
        }
        val currentContext = CoreApplication.instance().currentActivity() ?: context
        if (currentContext is X5WebViewActivity) {
            val rxPermissions = RxPermissions(currentContext)
            rxPermissions.request(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).subscribe { granted ->
                if (granted) {
                    checkDataInfo(data, function)
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

    private fun checkDataInfo(data: String?, function: CallBackFunction?) {
        if (data.isNullOrEmpty() && mediaType() == JsConstant.MEDIA_VIDEO) {
            RxBus.post(
                BaseEvent(
                    JsConstant.EVENT_MEDIA,
                    JsEvent(
                        params = JsRequestData(mediaType = JsConstant.MEDIA_VIDEO),
                        callback = function
                    )
                )
            )
            return
        }
        try {
            val requestData = GsonConvertUtil.fromJson(data, JsRequestData::class.java)
            requestData.mediaType = mediaType()
            RxBus.post(
                BaseEvent(
                    JsConstant.EVENT_MEDIA,
                    JsEvent(params = requestData, callback = function)
                )
            )
        } catch (e: Exception) {
            function?.onCallBack(
                CallBackCreator.createError(e.message ?: "json convert error!")
            )
        }
    }
}