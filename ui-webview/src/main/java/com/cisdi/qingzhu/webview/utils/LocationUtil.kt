package com.cisdi.qingzhu.webview.utils

import android.content.Context
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener

/**
 * 高德地图定位工具
 *
 * @author lh
 */
class LocationUtil private constructor() {

    companion object {
        @JvmStatic
        val instance = SingletonHolder.holder
    }

    private object SingletonHolder {
        val holder = LocationUtil()
    }

    private var mLocationClient: AMapLocationClient? = null
    private var mLocationOption: AMapLocationClientOption? = null
    private var mLocationListener: AMapLocationListener? = null


    /**
     * 获取一次定位信息
     */
    fun startLocation(context: Context, locationListener: AMapLocationListener) {
        if (mLocationClient != null && mLocationOption != null) {
            if (mLocationListener != null) {
                mLocationClient?.unRegisterLocationListener(mLocationListener)
            }
            if (mLocationClient?.isStarted == true) {
                mLocationClient?.stopLocation()
            }
            mLocationClient?.onDestroy()
            mLocationClient = null
            mLocationOption = null
        }
        mLocationClient = AMapLocationClient(context)
        mLocationOption = defaultOption()
        mLocationClient?.setLocationOption(mLocationOption)
        mLocationListener = locationListener
        mLocationClient?.setLocationListener(locationListener)
        mLocationClient?.startLocation()
    }

    /**
     * 结束定位
     */
    fun stopLocation() {
        if (null != mLocationClient) {
            if (mLocationListener != null) {
                mLocationClient?.unRegisterLocationListener(mLocationListener)
            }
            mLocationClient?.stopLocation()
            mLocationClient?.onDestroy()
            mLocationClient = null
            mLocationOption = null
        }
    }

    private fun defaultOption(): AMapLocationClientOption {
        return AMapLocationClientOption().also {
            it.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            it.httpTimeOut = 10000
            it.isOnceLocation = true
            it.isNeedAddress = true
            it.isWifiScan = true
            it.isLocationCacheEnable = true
            it.isMockEnable = false
        }
    }

}