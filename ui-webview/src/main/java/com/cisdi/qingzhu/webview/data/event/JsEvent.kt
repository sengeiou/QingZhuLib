package com.cisdi.qingzhu.webview.data.event

import com.cisdi.qingzhu.jsbridge.CallBackFunction

/**
 * js通知
 *
 * @author lh
 */
data class JsEvent<T>(
    var params: T? = null,
    var callback: CallBackFunction? = null
)
