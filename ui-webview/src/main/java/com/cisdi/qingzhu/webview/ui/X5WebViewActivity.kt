package com.cisdi.qingzhu.webview.ui

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.text.TextUtils
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.cisdi.qingzhu.jsbridge.CallBackFunction
import com.cisdi.qingzhu.qrcode.ui.QRScanActivity
import com.cisdi.qingzhu.webview.R
import com.cisdi.qingzhu.webview.constants.Constant
import com.cisdi.qingzhu.webview.constants.IntentArgs
import com.cisdi.qingzhu.webview.constants.JsConstant
import com.cisdi.qingzhu.webview.contract.WebViewContract
import com.cisdi.qingzhu.webview.data.callback.CallBackCreator
import com.cisdi.qingzhu.webview.data.protocol.HandlerPicture
import com.cisdi.qingzhu.webview.data.protocol.HandlerQrCode
import com.cisdi.qingzhu.webview.data.protocol.JsRequestData
import com.cisdi.qingzhu.webview.presenter.WebViewPresenter
import com.cisdi.qingzhu.webview.utils.Base64Util
import com.cisdi.qingzhu.webview.utils.OpenUrlUtil
import com.cisdi.qingzhu.webview.video.CompressCallback
import com.cisdi.qingzhu.webview.video.VideoCompress
import com.cisdi.qingzhu.webview.widgets.BridgeX5WebView
import com.cisdi.qingzhu.webview.widgets.GlideEngine
import com.cisdi.qingzhu.webview.widgets.X5WebView
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.constant.Type
import com.huantansheng.easyphotos.models.album.entity.Photo
import com.huantansheng.easyphotos.setting.Setting
import com.lcy.base.core.ui.activity.BaseActivity
import com.lcy.base.core.utils.UriUtils
import com.qiniu.pili.droid.shortvideo.PLErrorCode
import com.tencent.smtt.export.external.interfaces.JsResult
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import top.zibin.luban.Luban
import java.io.File
import java.util.*

class X5WebViewActivity : BaseActivity<WebViewContract.View, WebViewPresenter>(),
    WebViewContract.View {

    /**  X5内核浏览器 **/
    private var mX5WebView: BridgeX5WebView? = null

    /**  浏览器容器 **/
    private var mContainer: FrameLayout? = null

    /**  访问链接 **/
    private var mUrl: String? = null

    //  For Android 5.0
    /** 图片选择意图回调 **/
    private var mValueCallbackArray: ValueCallback<Array<Uri>>? = null

    /** 身份证 **/
    private var mIdCardCallBack: CallBackFunction? = null

    /** 二维码回调 **/
    private var mQrCodeCallBack: CallBackFunction? = null

    /** 相机相关 **/
    private var mMediaCallBack: CallBackFunction? = null

    /** 请求信息 **/
    private var mRequestData: JsRequestData? = null

    companion object {
        fun start(context: Context, url: String, title: String = "") {
            val starter = Intent(context, X5WebViewActivity::class.java)
            starter.putExtra(IntentArgs.ARGS_TITLE, title)
            starter.putExtra(IntentArgs.ARGS_URL, url)
            context.startActivity(starter)
        }
    }

    override fun initWindowFlags() {
        window.setFormat(PixelFormat.TRANSLUCENT)
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                    or WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
        )
    }

    override fun getLayout(): Int = R.layout.activity_x5_web_view

    override fun initInject() {
        mPresenter = WebViewPresenter()
    }

    override fun initEventAndData() {
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

    //region + 设置监听器
    override fun initListeners() {
        mX5WebView?.webChromeListener = object : X5WebView.WebChrome {
            override fun onJsAlert(
                webView: WebView?, url: String?, message: String?, jsResult: JsResult
            ): Boolean {
                jsResult.cancel()
                return false
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                setTitle(title)
            }

            override fun onShowFileChooser(
                webView: WebView?, valueCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: WebChromeClient.FileChooserParams
            ): Boolean {
                mValueCallbackArray = valueCallback
                return onInterceptFileChooser(fileChooserParams.acceptTypes, fileChooserParams.mode)
            }
        }

        mX5WebView?.setDownloadListener { url, _, _, _, _ ->
            //默认直接打开系统浏览器
            OpenUrlUtil.openBrowser(mContext, url)
        }
    }
    //endregion

    //region + file文件拦截器
    private fun onInterceptFileChooser(acceptTypes: Array<String>?, mode: Int): Boolean {
        if (acceptTypes.isNullOrEmpty() || acceptTypes[0].isEmpty()) {
            chooseFile()
            return true
        }
        val acceptType = acceptTypes[0]
        return when {
            acceptType.contains("image") -> {
                chooseImage(
                    false,
                    if (mode == WebChromeClient.FileChooserParams.MODE_OPEN_MULTIPLE) Constant.MAX_PICKER_NUM
                    else Constant.MIN_PICKER_NUM
                )
                true
            }
            acceptType.contains("video") -> {
                chooseVideo()
                true
            }
            else -> {
                chooseFile()
                true
            }
        }
    }
    //endregion

    //region + 选择文件
    private fun chooseFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, IntentArgs.FILE_PICKER)
    }
    //endregion

    //region + 选择图片
    private fun chooseImage(onlyCamera: Boolean = false, count: Int = Constant.MIN_PICKER_NUM) {
        (if (onlyCamera) {
            EasyPhotos.createCamera(this)
        } else {
            EasyPhotos.createAlbum(this, true, GlideEngine.instance)
        }).setFileProviderAuthority("${packageName}.fileprovider")
            .setCount(count)
            .setCleanMenu(false)
            .setPuzzleMenu(false)
            .setCameraLocation(Setting.LIST_FIRST)
            .start(IntentArgs.IMAGE_PICKER)
    }
    //endregion

    //region + 选择视频
    private fun chooseVideo(data: JsRequestData? = null) {

//        if (data != null) {
//            if (!data.onlyCamera && data.filterMinSecond > data.filterMaxSecond) {
//                // 触发错误
//                return
//            }
//        }

        data?.let {
            if (!it.onlyCamera && it.filterMinSecond > it.filterMaxSecond) {
                // 触发错误
                callbackWithData(mMediaCallBack, CallBackCreator.createError("param error!"))
                return
            }
            // 边界值校验
            if (it.maxRecordSecond > Constant.MAX_RECORD_SECOND || it.maxRecordSecond < 0) {
                it.maxRecordSecond = Constant.MAX_RECORD_SECOND
            }
            if (it.filterMaxSecond > Constant.MAX_RECORD_SECOND) {
                it.filterMaxSecond = Constant.MAX_RECORD_SECOND
            }
            if (it.filterMaxSecond < 1) {
                it.filterMaxSecond = 1
            }
            if (it.filterMinSecond < 0) {
                it.filterMinSecond = 0
            }
        }
        if (data == null || data.onlyCamera) {
            EasyPhotos.createCamera(this)
        } else {
            EasyPhotos.createAlbum(this, true, GlideEngine.instance)
        }.setFileProviderAuthority("${packageName}.fileprovider")
            .setCount(1)
            .filter(Type.VIDEO)
            .setCameraLocation(Setting.LIST_FIRST)
            .setVideoRecordLimitTime(data?.maxRecordSecond ?: 300)
            .setVideoMaxSecond(data?.filterMaxSecond ?: 300)
            .setVideoMinSecond(data?.filterMinSecond ?: 0)
            .start(IntentArgs.VIDEO_PICKER)

    }
    //endregion

    private fun setValueCallback(list: MutableList<Photo>?) {
        if (mValueCallbackArray != null) {
            mValueCallbackArray?.onReceiveValue(list?.map { it.uri }?.toTypedArray())
            mValueCallbackArray = null
            return
        }
        if (mMediaCallBack != null) {
            if (list.isNullOrEmpty()) {
                callbackWithData(mMediaCallBack, CallBackCreator.createError("选择媒体文件失败!"))
            } else {
                convertPhotos(mRequestData?.includeBase64, list)
            }
        }
    }

    /**
     * 将文件转成base64字符串
     */
    private fun convertPhotos(convertBase64: Boolean? = false, list: MutableList<Photo>) {
        addSubscribe(
            Observable.fromIterable(list)
                .map {
                    val base64String: String? =
                        if (convertBase64 == false || it.type.contains(Type.VIDEO)) {
                            null
                        } else {
                            val bitmap = Glide.with(mContext).asBitmap().load(it.uri).submit().get()
                            Base64Util.bitmapToBase64(bitmap)
                        }
                    HandlerPicture(
                        path = it.path,
                        width = it.width.toString(),
                        height = it.height.toString(),
                        data = base64String
                    )
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toList()
                .subscribe(
                    {
                        callbackWithData(mMediaCallBack, CallBackCreator.createSuccess(it))

                    },
                    {
                        callbackWithData(
                            mMediaCallBack,
                            CallBackCreator.createError(it.message ?: "convert photo fail!")
                        )
                        mMediaCallBack = null
                        mRequestData = null
                    }
                )
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            IntentArgs.IMAGE_PICKER, IntentArgs.VIDEO_PICKER -> {
                if (resultCode != RESULT_OK || data == null || !data.hasExtra(EasyPhotos.RESULT_PHOTOS)) {
                    setValueCallback(null)
                    return
                }
                val list: ArrayList<Photo> =
                    data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS) ?: ArrayList()
                if (requestCode == IntentArgs.IMAGE_PICKER) {
                    lubanCompress(list)
                } else {
                    videoCompress(list)
                }
            }
            IntentArgs.QR_SCAN -> {
                if (resultCode == RESULT_OK && data?.hasExtra(QRScanActivity.ARGS_QR_CODE) == true) {
                    val result = data.getStringExtra(QRScanActivity.ARGS_QR_CODE)
                    callbackWithData(
                        mQrCodeCallBack,
                        CallBackCreator.createSuccess(HandlerQrCode(result))
                    )
                } else {
                    callbackWithData(
                        mQrCodeCallBack,
                        CallBackCreator.createError(errorMsg = "qrcode result null")
                    )
                }
            }
            else -> {

            }
        }
    }

    private fun videoCompress(list: ArrayList<Photo>) {
        if (list.size > 0) {
            val photo = list[0]
            val path = photo.path
            //反射
            VideoCompress.getInstance().reflectQiniuSdk()
            //进行视频压缩
            VideoCompress.getInstance()
                .compressVideoResource(mContext, path, object : CompressCallback {
                    override fun onSuccess(fileUrl: String) {
                        onProgress(100)
                        photo.path = fileUrl
                        photo.uri = UriUtils.file2Uri(File(fileUrl))
                        setValueCallback(arrayListOf(photo))
                    }

                    override fun onCancel() {
                        onProgress(100)
                        setValueCallback(null)
                    }

                    override fun onProgress(progress: Int) {
                        mX5WebView?.post {
                            mX5WebView?.showProgress(progress)
                        }
                    }

                    override fun onError(errorCode: Int) {
                        onProgress(100)
                        setValueCallback(null)
                        toast(
                            when (errorCode) {
                                PLErrorCode.ERROR_NO_VIDEO_TRACK -> "该文件没有视频信息！"
                                PLErrorCode.ERROR_SRC_DST_SAME_FILE_PATH -> "源文件路径和目标路径不能相同！"
                                PLErrorCode.ERROR_LOW_MEMORY -> "手机内存不足，无法对该视频进行压缩！"
                                else -> "压缩视频错误！"
                            }
                        )
                    }
                })
        }
    }

    /**
     * 启动鲁班压缩
     */
    private fun lubanCompress(list: ArrayList<Photo>) {
        if (list.size > 0) {
            addSubscribe(
                Observable.fromIterable(list)
                    .map { photo: Photo ->
                        val file = Luban.with(mContext).ignoreBy(100).load(photo.path).get()[0]
                        photo.also {
                            it.path = file.absolutePath
                            it.uri = UriUtils.file2Uri(file)
                        }
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .toList()
                    .subscribe({ setValueCallback(it) }, { setValueCallback(null) })
            )
        }
    }

    override fun grantedPermission(granted: Boolean) {

    }

    /**
     * 相册,相机相关
     */
    override fun onMediaEvent(data: JsRequestData, callback: CallBackFunction?) {
        mRequestData = data
        mMediaCallBack = callback
        when (data.mediaType) {
            JsConstant.MEDIA_VIDEO -> {
                chooseVideo(data)
            }
            JsConstant.MEDIA_PHOTO -> {
                chooseImage(onlyCamera = true)
            }
            JsConstant.MEDIA_ALBUM -> {
                chooseImage(count = if (data.multiple) data.maxCount else Constant.MIN_PICKER_NUM)
            }
            else -> {

            }
        }
    }

    /**
     * 扫描二维码
     */
    override fun onQrScanEvent(callback: CallBackFunction?) {
        mQrCodeCallBack = callback
        startActivityForResult<QRScanActivity>(IntentArgs.QR_SCAN)
    }

    /**
     * 读取身份证
     */
    override fun onIdCardEvent(front: Boolean, callback: CallBackFunction?) {
        // TODO 启动扫描身份证
        mIdCardCallBack = callback
        // TODO 启动扫描身份证
    }

    /**
     * 打开,关闭页面
     */
    override fun onWindowEvent(url: String, callback: CallBackFunction?) {
        //优先判断页面信息
        if (mUrl.equals(url)) {
            callbackWithData(callback, CallBackCreator.createSuccess(null))
            finish()
        }
    }

    /**
     * 调用回调
     */
    private fun callbackWithData(callback: CallBackFunction?, data: String?) {
        callback?.onCallBack(data)
        if (mMediaCallBack != null) {
            mMediaCallBack = null
            mRequestData = null
        }
        if (mQrCodeCallBack != null) {
            mQrCodeCallBack = null
        }
        if (mIdCardCallBack != null) {
            mIdCardCallBack = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mX5WebView?.destroyWebView()
        mX5WebView = null
    }

    override fun onBackPressedSupport() {
        if (mX5WebView?.canGoBack() == true) {
            mX5WebView?.goBack()
        } else {
            super.onBackPressedSupport()
        }
    }
}
