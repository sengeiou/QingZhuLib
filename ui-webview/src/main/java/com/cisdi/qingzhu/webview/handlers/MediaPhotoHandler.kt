package com.cisdi.qingzhu.webview.handlers

import com.cisdi.qingzhu.webview.constants.JsConstant

/**
 * 拍照信息
 *
 * @author lh
 */
class MediaPhotoHandler : MediaHandler() {

    override fun mediaType(): Int = JsConstant.MEDIA_PHOTO

}