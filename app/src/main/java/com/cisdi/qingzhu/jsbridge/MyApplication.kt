package com.cisdi.qingzhu.jsbridge

import android.app.Application
import skin.support.SkinCompatManager
import skin.support.app.SkinAppCompatViewInflater


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        SkinCompatManager.withoutActivity(this)
            .addInflater(SkinAppCompatViewInflater())
            .setSkinWindowBackgroundEnable(false)
            .loadSkin()
    }
}