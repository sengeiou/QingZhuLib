package com.cisdi.qingzhu.ui.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import androidx.multidex.MultiDex
import java.lang.ref.WeakReference
import java.util.*

/**
 * Application 基类
 *
 * @author lcy
 */
abstract class CoreApplication : Application() {

    /** 存储Activity栈 **/
    private val mActivityStack: Stack<WeakReference<Activity>> by lazy { Stack<WeakReference<Activity>>() }

    override fun onCreate() {
        super.onCreate()
        instance = this

        //  去掉Android P 私有API提示框
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            closeAndroidPDialog()
        }

    }

    /**
     * 自定义模块信息
     */
    protected abstract fun initAppInjection()


    /**
     * 全局伴生对象
     */
    companion object {

        private lateinit var instance: CoreApplication

        @JvmStatic
        fun instance() = instance
    }


    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    private fun closeAndroidPDialog() {
        try {
            val aClass = Class.forName("android.content.pm.PackageParser\$Package")
            val declaredConstructor = aClass.getDeclaredConstructor(String::class.java)
            declaredConstructor.isAccessible = true
        } catch (e: Exception) {
        }
        try {
            val cls = Class.forName("android.app.ActivityThread")
            val declaredMethod = cls.getDeclaredMethod("currentActivityThread")
            declaredMethod.isAccessible = true
            val activityThread = declaredMethod.invoke(null)
            val mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown")
            mHiddenApiWarningShown.isAccessible = true
            mHiddenApiWarningShown.setBoolean(activityThread, true)
        } catch (e: Exception) {
        }

    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}