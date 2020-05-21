package com.cisdi.qingzhu.jsbridge

import android.app.Application
import com.lcy.base.core.common.CoreApplication
import skin.support.SkinCompatManager
import skin.support.app.SkinAppCompatViewInflater


class MyApplication : CoreApplication() {
    override fun initAppInjection() {

    }

    override fun onCreate() {
        super.onCreate()
        SkinCompatManager.withoutActivity(this)
            .addInflater(SkinAppCompatViewInflater())
            .setSkinWindowBackgroundEnable(false)
            .loadSkin()
    }
}