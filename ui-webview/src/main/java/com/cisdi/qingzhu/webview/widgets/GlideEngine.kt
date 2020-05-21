package com.cisdi.qingzhu.webview.widgets

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.huantansheng.easyphotos.engine.ImageEngine

class GlideEngine private constructor() : ImageEngine {

    companion object {
        val instance = GlideEngineHolder.holder
    }

    private object GlideEngineHolder {
        val holder = GlideEngine()
    }

    /**
     * 获取图片加载框架中的缓存Bitmap
     *
     * @param context 上下文
     * @param uri     图片路径
     * @param width   图片宽度
     * @param height  图片高度
     * @return Bitmap
     * @throws Exception 异常直接抛出，EasyPhotos内部处理
     */
    override fun getCacheBitmap(context: Context, uri: Uri, width: Int, height: Int): Bitmap {
        return Glide.with(context).asBitmap().load(uri).into(width, height).get()
    }

    /**
     * 加载gif动图到ImageView，gif动图动
     *
     * @param context   上下文
     * @param gifUri    gif动图路径Uri
     * @param imageView 加载动图的ImageView
     *
     *
     * 备注：不支持动图显示的情况下可以不写
     */
    override fun loadGif(context: Context, gifUri: Uri, imageView: ImageView) {
        Glide.with(context).asGif().load(gifUri).into(imageView)
    }

    /**
     * 加载图片到ImageView
     *
     * @param context   上下文
     * @param uri       图片Uri
     * @param imageView 加载到的ImageView
     */
    override fun loadPhoto(context: Context, uri: Uri, imageView: ImageView) {
        Glide.with(context).load(uri).into(imageView)
    }

    /**
     * 加载gif动图图片到ImageView，gif动图不动
     *
     * @param context   上下文
     * @param gifUri    gif动图路径Uri
     * @param imageView 加载到的ImageView
     *
     */
    override fun loadGifAsBitmap(context: Context, gifUri: Uri, imageView: ImageView) {
        Glide.with(context).asBitmap().load(gifUri).into(imageView)
    }
}