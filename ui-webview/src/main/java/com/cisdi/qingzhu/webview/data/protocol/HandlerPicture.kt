package com.cisdi.qingzhu.webview.data.protocol

/**
 * 照片信息
 *
 * @author lh
 */
data class HandlerPicture(
    /** 文件的绝对路径 **/
    val path: String?,
    /** 文件的宽度 **/
    val width: String?,
    /** 文件的高度 **/
    val height: String?,
    /** 文件的md5值 **/
    val data: String?
)