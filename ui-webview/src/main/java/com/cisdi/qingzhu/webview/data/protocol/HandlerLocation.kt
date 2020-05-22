package com.cisdi.qingzhu.webview.data.protocol

/**
 * 定位信息
 *
 * @author lh
 */
data class HandlerLocation(
    /** 地址信息 **/
    val location: Location,
    /** 经纬度信息 **/
    val coords: LatLon
)


data class Location(
    val address: String?
)

data class LatLon(
    val latitude: String?,
    val longitude: String?
)
