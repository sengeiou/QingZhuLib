package com.cisdi.qingzhu.webview.handlers

import com.cisdi.qingzhu.webview.constants.JsConstant

/**
 * 相册信息
 *
 * @author lh
 */
class MediaAlbumHandler : MediaHandler() {

    override fun mediaType(): Int = JsConstant.MEDIA_ALBUM
}