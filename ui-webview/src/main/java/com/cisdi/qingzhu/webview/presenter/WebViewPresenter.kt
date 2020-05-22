package com.cisdi.qingzhu.webview.presenter

import android.Manifest
import com.cisdi.qingzhu.webview.constants.JsConstant
import com.cisdi.qingzhu.webview.contract.WebViewContract
import com.cisdi.qingzhu.webview.data.event.BaseEvent
import com.cisdi.qingzhu.webview.data.event.JsEvent
import com.lcy.base.core.presenter.RxPresenter
import com.lcy.base.core.rx.RxBus
import com.tbruyelle.rxpermissions2.RxPermissions
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class WebViewPresenter @Inject constructor() : RxPresenter<WebViewContract.View>(),
    WebViewContract.Presenter {

    override fun checkPermissions(rxPermissions: RxPermissions) {
        addSubscribe(
            rxPermissions.request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_WIFI_STATE
            ).subscribe { granted ->
                mView!!.grantedPermission(granted!!)
            }
        )
    }

    private fun registerEvent() {
        addSubscribe(
            RxBus.toFlowable(BaseEvent::class.java)
                //  10秒内只触发一次报错信息
                .filter { event ->
                    event.code == JsConstant.EVENT_MEDIA
                            || event.code == JsConstant.EVENT_ID_CARD ||
                            event.code == JsConstant.EVENT_CLOSE_WINDOW
                }
                .subscribe {
                    val eventValue = it.value as JsEvent<*>
                    when (it.code) {
                        JsConstant.EVENT_MEDIA -> {
                            mView?.onMediaEvent(eventValue.params as Int, eventValue.callback)
                        }
                        JsConstant.EVENT_ID_CARD -> {
                            mView?.onIdCardEvent(eventValue.params as Boolean, eventValue.callback)
                        }
                        JsConstant.EVENT_CLOSE_WINDOW -> {
                            mView?.onWindowEvent(eventValue.params as String, eventValue.callback)
                        }
                        else -> {

                        }
                    }
                })
    }
}