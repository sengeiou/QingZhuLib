package com.cisdi.qingzhu.webview.data.protocol

/**
 * 身份证信息
 *
 * @author lh
 */
data class IdCardBack(
    /** 签发日期 **/
    val signDate: String? = null,
    /** 签发机关 **/
    val issueAuthority: String? = null,
    /** 过期时间 **/
    val expiryDate: String? = null,
    /** 身份证照片 **/
    val image: IdCardImage? = null
)