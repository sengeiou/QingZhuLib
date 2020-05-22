package com.cisdi.qingzhu.jsbridge

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.SkinAppCompatDelegateImpl
import com.cisdi.qingzhu.webview.constants.Handlers
import com.cisdi.qingzhu.webview.handlers.IdCardHandler
import com.cisdi.qingzhu.webview.handlers.WindowCloseHandler
import com.cisdi.qingzhu.webview.handlers.WindowOpenHandler
import com.cisdi.qingzhu.webview.ui.X5WebViewActivity

class MainActivity : AppCompatActivity() {

    private val url = "file:///android_asset/jsbridge/" + "demo.html"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        val handlerMap = HashMap<String, BridgeHandler>()
        handlerMap[Handlers.OPEN_WINDOW] = WindowOpenHandler()
        handlerMap[Handlers.CLOSE_WINDOW] = WindowCloseHandler()
        handlerMap[Handlers.SCAN_ID_CARD] = IdCardHandler()
        Bridge.instance.registerHandler(handlerMap)

        X5WebViewActivity.start(this, url)


        //startActivity(Intent(this, QRScanActivity::class.java))
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun getDelegate(): AppCompatDelegate {
        return SkinAppCompatDelegateImpl.get(this, this);
    }

}
