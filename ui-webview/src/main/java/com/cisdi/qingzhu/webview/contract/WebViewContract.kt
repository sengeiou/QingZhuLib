package com.cisdi.qingzhu.webview.contract

import com.cisdi.qingzhu.jsbridge.CallBackFunction
import com.lcy.base.core.presenter.IBasePresenter
import com.lcy.base.core.presenter.view.IBaseView
import com.tbruyelle.rxpermissions2.RxPermissions


interface WebViewContract {

    interface View : IBaseView {

        fun grantedPermission(granted: Boolean)

        /**
         * 相册,相机相关
         */
        fun onMediaEvent(type: Int, callback: CallBackFunction?)

        /**
         * 读取身份证
         */
        fun onIdCardEvent(front: Boolean = false, callback: CallBackFunction?)

        /**
         * 打开,关闭页面
         */
        fun onWindowEvent(url: String, callback: CallBackFunction?)

    }

    interface Presenter : IBasePresenter<View> {

        fun checkPermissions(rxPermissions: RxPermissions)

    }
}