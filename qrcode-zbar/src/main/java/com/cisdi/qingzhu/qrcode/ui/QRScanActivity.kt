package com.cisdi.qingzhu.qrcode.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.cisdi.qingzhu.qrcode.R
import com.cisdi.qingzhu.qrcode.core.QRCodeView
import com.cisdi.qingzhu.qrcode.util.ResourceUtil
import com.cisdi.qingzhu.qrcode.util.ServiceManager
import com.cisdi.qingzhu.qrcode.widget.ZBarView
import com.jakewharton.rxbinding2.view.RxView
import com.lcy.base.core.ui.activity.SimpleActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * 二维码扫描
 *
 * @author lcy
 */
class QRScanActivity : SimpleActivity(), QRCodeView.Delegate {

    /**
     * 二维码
     */
    private var mZBarView: ZBarView? = null

    /**
     * 闪光灯
     */
    private var mBrightnessBtn: TextView? = null

    private var mDialog: MaterialDialog? = null

    private var isFlashlightOpen = false


    override fun getLayout(): Int = R.layout.zbar_activity_zbar_scan

    override fun initEventAndData() {
        mZBarView = findViewById(R.id.zbar_view)
        mBrightnessBtn = findViewById(R.id.ambient_brightness_btn)
        mZBarView?.setDelegate(this)
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            showErrorDialog()
        }
    }

    override fun initListeners() {
        addSubscribe(
            RxView.clicks(mBrightnessBtn!!)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    //  打开或者关闭闪光灯
                    isFlashlightOpen = !isFlashlightOpen
                    mBrightnessBtn?.setText(if (isFlashlightOpen) R.string.zbar_ambient_brightness_tip_close else R.string.zbar_ambient_brightness_tip_open)
                    val drawable =
                        ResourceUtil.getDrawableFromRes(
                            mContext,
                            if (isFlashlightOpen) R.drawable.zbar_ic_ambient_brightness_open
                            else R.drawable.zbar_ic_ambient_brightness_close
                        )
                    mBrightnessBtn?.setCompoundDrawablesRelative(null, drawable, null, null)
                    if (isFlashlightOpen) {
                        mZBarView?.openFlashlight()
                    } else {
                        mBrightnessBtn?.visibility = View.GONE
                        mZBarView?.closeFlashlight()
                    }
                }
        )
    }

    private fun showErrorDialog() {
        mDialog = MaterialDialog.Builder(mContext)
            .content(R.string.zbar_camera_permission_needed)
            .cancelable(false)
            .negativeText(R.string.zbar_camera_permission_cancel)
            .onNegative { dialog, _ ->
                dialog.dismiss()
                finish()
            }.build()
        mDialog?.show()
    }

    override fun onStart() {
        super.onStart()
        startScan()
    }

    override fun onStop() {
        //关闭摄像头预览，并且隐藏扫描框
        mZBarView?.stopCamera()
        super.onStop()
    }

    override fun onDestroy() {
        //销毁二维码扫描控件
        mZBarView?.onDestroy()
        if (mDialog?.isShowing == true) {
            mDialog?.dismiss()
            mDialog = null
        }
        super.onDestroy()
    }

    /**
     * 震动200毫秒
     */
    @Suppress("DEPRECATION")
    private fun vibrate() {
        val vibrator = ServiceManager.getVibrator(mContext)
        vibrator.vibrate(200)
    }

    /**
     * 开启扫描
     */
    private fun startScan() {
        //打开后置摄像头开始预览，但是并未开始识别
        mZBarView?.startCamera()
        //显示扫描框，并开始识别
        mZBarView?.startSpotAndShowRect()
    }

    /**
     * 处理扫描结果
     *
     * @param result 摄像头扫码时只要回调了该方法 result 就一定有值，不会为 null。解析本地图片或 Bitmap 时 result 可能为 null
     */
    override fun onScanQRCodeSuccess(result: String?) {
        vibrate()
        //  扫描到数据后停止扫描
        val data = Intent()
        data.putExtra(ARGS_QR_CODE, result)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    /**
     * 摄像头环境亮度发生变化
     *
     * @param isDark 是否变暗
     */
    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {
        //这里是通过修改提示文案来展示环境是否过暗的状态
        var tipText = mZBarView?.scanBoxView?.tipText
        val ambientBrightnessTip = getString(R.string.zbar_ambient_brightness_tip)
        if (isDark) {
            if (tipText?.contains(ambientBrightnessTip) == false) {
                mZBarView?.scanBoxView?.tipText = tipText + ambientBrightnessTip
            }
            mBrightnessBtn?.visibility = View.VISIBLE
        } else {
            if (tipText?.contains(ambientBrightnessTip) == true) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip))
                mZBarView?.scanBoxView?.tipText = tipText
            }
            if (!isFlashlightOpen) {
                mBrightnessBtn?.visibility = View.GONE
            }
        }
    }

    /**
     * 处理打开相机出错
     */
    override fun onScanQRCodeOpenCameraError() {
        showErrorDialog()
    }

    companion object {
        /**
         * 返回结果
         */
        const val ARGS_QR_CODE = "QR_CODE"
    }
}