package com.cisdi.qingzhu.webview.handlers

import com.cisdi.qingzhu.webview.constants.JsConstant

/**
 * 录制视频
 *
 * @author lh
 */
class MediaVideoHandler : MediaHandler() {

    override fun mediaType(): Int = JsConstant.MEDIA_VIDEO

}