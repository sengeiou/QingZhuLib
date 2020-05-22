package com.cisdi.qingzhu.webview.data.protocol

/**
 * 身份证信息
 *
 * @author lh
 */
data class IdCardFront(
    /** 地址 **/
    val address: String? = null,
    /** 出生日期 **/
    val birth: String? = null,
    /** 姓名 **/
    val name: String? = null,
    /** 身份证号 **/
    val idNumber: String? = null,
    /** 性别 **/
    val gender: String? = null,
    /** 民族 **/
    val ethnic: String? = null,
    /** 身份证照片 **/
    val image: IdCardImage? = null
)