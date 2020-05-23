package com.cisdi.qingzhu.webview.data.protocol

/**
 * js 请求参数
 *
 * @author lh
 */
data class JsRequestData(
    /** 打开新页面时的url **/
    val url: String? = null,
    /** 身份证扫描,是否是正面 **/
    val isFront: Boolean = false,
    /** 资源信息类型 **/
    var mediaType: Int = -1,
    /** 是否需要返回base64格式 **/
    val includeBase64: Boolean = false,
    /** 是否可以选择多张 **/
    val multiple: Boolean = false,
    /** 默认选择张数 **/
    val maxCount: Int = 3,
    /** 只使用相机 **/
    val onlyCamera: Boolean = true,
    /** 最大录制时常 **/
    var filterMinSecond: Int = 0,
    /** 最大录制时常 **/
    var filterMaxSecond: Int = 300,
    /** 最大录制时常 **/
    var maxRecordSecond: Int = 300
)