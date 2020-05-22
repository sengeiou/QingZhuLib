package com.cisdi.qingzhu.webview.data.protocol

/**
 * js 请求参数
 *
 * @author lh
 */
data class JsRequestData(
    /** 打开新页面时的状态值 **/
    val open: Boolean = false,
    /** 打开新页面时的url **/
    val url: String? = null,
    /** 身份证扫描,是否是正面 **/
    val isFront: Boolean = false
)