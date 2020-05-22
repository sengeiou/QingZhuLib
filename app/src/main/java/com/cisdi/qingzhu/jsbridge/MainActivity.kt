package com.cisdi.qingzhu.jsbridge

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.SkinAppCompatDelegateImpl
import com.cisdi.qingzhu.qrcode.ui.QRScanActivity

class MainActivity : AppCompatActivity() {

    private val url = "file:///android_asset/jsbridge/" + "demo.html"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        // X5WebViewActivity.start(this, url)
        startActivity(Intent(this, QRScanActivity::class.java))
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun getDelegate(): AppCompatDelegate {
        return SkinAppCompatDelegateImpl.get(this, this);
    }

}
