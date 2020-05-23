package com.cisdi.qingzhu.app

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.SkinAppCompatDelegateImpl
import com.cisdi.qingzhu.jsbridge.Bridge
import com.cisdi.qingzhu.jsbridge.BridgeHandler
import com.cisdi.qingzhu.webview.constants.Handlers
import com.cisdi.qingzhu.webview.handlers.*
import com.cisdi.qingzhu.webview.ui.X5WebViewActivity

class MainActivity : AppCompatActivity() {

    private val url = "file:///android_asset/jsbridge/" + "demo.html"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val handlerMap = HashMap<String, BridgeHandler>()
        handlerMap[Handlers.TOAST] = ToastHandler()
        handlerMap[Handlers.PLATFORM] = PlatformHandler()
        handlerMap[Handlers.TAKE_PICTURE] = MediaPhotoHandler()
        handlerMap[Handlers.PHOTO_PICKER] = MediaAlbumHandler()
        handlerMap[Handlers.RECORD_VIDEO] = MediaVideoHandler()
        handlerMap[Handlers.SCAN_QR_CODE] = QrCodeHandler()
        handlerMap[Handlers.SCAN_ID_CARD] = IdCardHandler()
        handlerMap[Handlers.CLOSE_WINDOW] = WindowCloseHandler()
        handlerMap[Handlers.OPEN_WINDOW] = WindowOpenHandler()
        handlerMap[Handlers.LOCATION] = LocationHandler()
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
