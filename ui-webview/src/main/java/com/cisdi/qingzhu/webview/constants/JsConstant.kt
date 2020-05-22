package com.cisdi.qingzhu.webview.constants

object JsConstant {

    /**成功回调code**/
    const val CALLBACK_SUCCESS = "0"

    /**失败回调code**/
    const val CALLBACK_ERROR = "-1"

    /** 失败消息**/
    const val ERROR_WEB_ACTIVITY = "can not call js when web is not front!"

    /** 相册 **/
    const val MEDIA_ALBUM: Int = 1

    /** 拍照 **/
    const val MEDIA_PHOTO: Int = 2

    /** 拍照 **/
    const val MEDIA_VIDEO: Int = 3

    /** 扫描二维码 **/
    const val MEDIA_QR_CODE: Int = 4

    /** 关闭窗口 **/
    const val EVENT_CLOSE_WINDOW: Long = 0x10001101

    /** 身份证扫描 **/
    const val EVENT_ID_CARD: Long = 0x10001102

    /** 资源请求 **/
    const val EVENT_MEDIA: Long = 0x10001103


}