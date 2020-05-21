package com.cisdi.qingzhu.webview.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.ViewGroup
import android.widget.FrameLayout
import com.cisdi.qingzhu.jsbridge.Bridge
import com.cisdi.qingzhu.jsbridge.BridgeHandler
import com.cisdi.qingzhu.webview.R
import com.cisdi.qingzhu.webview.constants.Handlers
import com.cisdi.qingzhu.webview.constants.IntentArgs
import com.cisdi.qingzhu.webview.entity.ThemeSpec
import com.cisdi.qingzhu.webview.handlers.ToastHandler
import com.cisdi.qingzhu.webview.widgets.BridgeX5WebView

class X5WebViewActivity : AppCompatActivity() {

    /**  X5内核浏览器 **/
    private var mX5WebView: BridgeX5WebView? = null

    /**  浏览器容器 **/
    private var mContainer: FrameLayout? = null

    /**  访问链接 **/
    private var mUrl: String? = null

    companion object {
        fun start(context: Context, url: String, title: String = "") {
            val starter = Intent(context, X5WebViewActivity::class.java)
            starter.putExtra(IntentArgs.ARGS_TITLE, title)
            starter.putExtra(IntentArgs.ARGS_URL, url)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeSpec.getInstance().themeId)
        setContentView(R.layout.activity_x5_web_view)

        val handlerMap = HashMap<String, BridgeHandler>()
        handlerMap[Handlers.TOAST] = ToastHandler()
        Bridge.instance.registerHandler(handlerMap)

        initEventAndData()
    }

    private fun initEventAndData() {
        mContainer = findViewById(R.id.web_container)
        val title = intent.getStringExtra(IntentArgs.ARGS_TITLE)
            ?: getString(R.string.title_activity_bridge_web_view)
        setTitle(title)
        mUrl = intent.getStringExtra(IntentArgs.ARGS_URL) ?: null
        if (TextUtils.isEmpty(mUrl)) {
            throw IllegalArgumentException("url can not be empty")
        }
        initX5WebView()
    }

    /**
     * 初始化X5WebView
     */
    private fun initX5WebView() {
        mX5WebView = BridgeX5WebView(this)
        mContainer?.addView(mX5WebView)
        val params = mX5WebView?.layoutParams
        params?.height = ViewGroup.LayoutParams.MATCH_PARENT
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT
        mX5WebView?.layoutParams = params
        mX5WebView?.loadUrl(mUrl)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mX5WebView != null) {
            mX5WebView!!.destroyWebView()
            mX5WebView = null
        }
    }

    override fun onBackPressed() {
        if (mX5WebView != null && mX5WebView!!.canGoBack()) {
            mX5WebView!!.goBack()
        } else {
            super.onBackPressed()
        }
    }

}
