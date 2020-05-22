package com.cisdi.qingzhu.webview.handlers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.amap.api.location.AMapLocationListener
import com.cisdi.qingzhu.jsbridge.BridgeHandler
import com.cisdi.qingzhu.jsbridge.CallBackFunction
import com.cisdi.qingzhu.webview.data.callback.CallBackCreator
import com.cisdi.qingzhu.webview.data.protocol.HandlerLocation
import com.cisdi.qingzhu.webview.data.protocol.LatLon
import com.cisdi.qingzhu.webview.data.protocol.Location
import com.cisdi.qingzhu.webview.utils.LocationUtil
import com.lcy.base.core.common.CoreApplication
import com.tbruyelle.rxpermissions2.RxPermissions


/**
 * 定位信息
 *
 * @author lh
 */
class LocationHandler : BridgeHandler() {

    @SuppressLint("CheckResult")
    override fun handler(context: Context, data: String?, function: CallBackFunction?) {
        val currentContext = CoreApplication.instance().currentActivity() ?: context
        val rxPermissions = RxPermissions(currentContext as AppCompatActivity)
        rxPermissions.request(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ).subscribe { granted ->
            if (granted) {
                LocationUtil.instance.startLocation(context,
                    AMapLocationListener { location ->
                        if (location.errorCode != 0) {
                            function?.onCallBack(
                                CallBackCreator.createError(location.errorInfo)
                            )
                        } else {
                            function?.onCallBack(
                                CallBackCreator.createSuccess(
                                    HandlerLocation(
                                        location = Location(
                                            location.address
                                        ),
                                        coords = LatLon(
                                            location.latitude.toString(),
                                            location.longitude.toString()
                                        )
                                    )
                                )
                            )
                        }
                    })
            } else {
                function?.onCallBack(
                    CallBackCreator.createError("location permission not granted")
                )
            }
        }

    }
}