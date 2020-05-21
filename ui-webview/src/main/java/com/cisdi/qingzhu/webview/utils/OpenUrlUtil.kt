package com.cisdi.qingzhu.webview.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.text.TextUtils
import android.widget.Toast
import com.cisdi.qingzhu.webview.R
import java.io.File

/**
 * 打开三方链接
 *
 * @author lh
 */
object OpenUrlUtil {
    /**
     * 用系统浏览器打开链接
     *
     * @param context context
     * @param url     链接
     */
    @JvmStatic
    fun openBrowser(context: Context, url: String) {
        try {
            openActivity(context, url)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "您还未安装相关客户端！", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun openActivity(
        context: Context,
        url: String,
        packageName: String? = null,
        activityClassName: String? = null
    ) {
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        val uri = Uri.parse(url)
        intent.data = uri
        if (null != packageName && null != activityClassName) {
            intent.setClassName(packageName, activityClassName)
        }
        context.startActivity(intent)
    }

    /**
     * 调用拨号界面
     *
     * @param phone 电话号码
     */
    @JvmStatic
    fun call(context: Context, phone: String) {
        call(context, Uri.parse("tel:$phone"))
    }

    @JvmStatic
    fun call(context: Context, phoneUri: Uri?) {
        val intent = Intent(Intent.ACTION_DIAL, phoneUri)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    /**
     * 检测该包名所对应的应用是否存在
     * * @param packageName
     */
    private fun checkPackage(
        context: Context,
        packageName: String?
    ): Boolean {
        return if (packageName.isNullOrEmpty()) false else try {
            context.packageManager.getApplicationInfo(
                packageName,
                PackageManager.GET_UNINSTALLED_PACKAGES
            )
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * 是否拦截请求地址
     *
     * @param context 上下文
     * @param url     请求链接
     * @return 返回true 则表示拦截,false则不处理,让后面逻辑处理
     */
    @JvmStatic
    fun shouldInterceptScheme(context: Context, url: String): Boolean {
        if (TextUtils.isEmpty(url)) return false
        // 处理常见的文档类型
        if (url.endsWith(".pdf") || url.endsWith(".doc") || url.endsWith(".docx")
            || url.endsWith(".xls") || url.endsWith(".xlsx") || url.endsWith(".ppt")
            || url.endsWith(".pptx")
        ) {
            openBrowser(context, url)
            return true
        }
        // 处理电话
        if (url.startsWith("tel:")) {
            call(context, Uri.parse(url))
            return true
        }
        return false
    }

}