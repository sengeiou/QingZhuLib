package com.cisdi.qingzhu.webview.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class X5WebView extends WebView {

    //  进度条的矩形（进度线）
    private WebViewProgressBar progressBar;
    private Handler handler;

    private WebClient webClientListener;
    private WebChrome webChromeListener;

    public WebClient getWebClientListener() {
        return webClientListener;
    }

    public void setWebClientListener(WebClient webClientListener) {
        this.webClientListener = webClientListener;
    }

    public WebChrome getWebChromeListener() {
        return webChromeListener;
    }

    public void setWebChromeListener(WebChrome webChromeListener) {
        this.webChromeListener = webChromeListener;
    }

    public X5WebView(Context context) {
        this(context, null);
    }

    public X5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        clearCache(true);
        //实例化进度条
        progressBar = new WebViewProgressBar(context);
        //设置进度条的size
        progressBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        //刚开始时候进度条不可见
        progressBar.setVisibility(GONE);
        //把进度条添加到webView里面
        addView(progressBar);
        //初始化handle
        handler = new Handler();
        initSettings();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initSettings() {
        //初始化设置
        WebSettings settings = this.getSettings();
        setWebChromeClient(new MyWebChromeClient());
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowContentAccess(true);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setDomStorageEnabled(true);
        //设置支持文件流
        settings.setAllowFileAccess(true);
        settings.setDatabaseEnabled(true);
        //页面加载好以后，再放开图片
        settings.setBlockNetworkImage(false);
        settings.setLoadWithOverviewMode(true);
        setHorizontalScrollBarEnabled(false);//水平不显示
        setVerticalScrollBarEnabled(false); //垂直不显示
        //缩放比例 1
        setInitialScale(1);
        //是否应该支持使用其屏幕缩放控件和手势缩放
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        //WebView是否支持多个窗口。
        settings.setSupportMultipleWindows(true);
        //设置此属性，可任意比例缩放
        settings.setUseWideViewPort(true);
        //屏幕自适应网页,如果没有这个，在低分辨率的手机上显示可能会异常
        settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        setDrawingCacheEnabled(true);
        //保存表单数据
        settings.setSaveFormData(true);
        //启动应用缓存
        settings.setAppCacheEnabled(true);
        //开启混合加载
        settings.setAllowUniversalAccessFromFileURLs(true);
        //设置缓存模式
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        // 排版适应屏幕
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        WebView.setWebContentsDebuggingEnabled(true);
        //点击链接继续在当前browser中响应
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return customOverrideUrlLoading(view, url) || super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
                sslErrorHandler.proceed();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //  自定义方法
                customPageFinished(view, url);
                if (getWebClientListener() != null) {
                    getWebClientListener().onPageFinished(view, url);
                }
            }

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                if (getWebClientListener() != null) {
                    getWebClientListener().onPageStarted(webView, s, bitmap);
                }
            }
        });

    }

    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            showProgress(newProgress);
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult jsResult) {
            if (getWebChromeListener() != null) {
                return getWebChromeListener().onJsAlert(webView, url, message, jsResult);
            }
            jsResult.cancel();
            return super.onJsAlert(webView, url, message, jsResult);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (getWebChromeListener() != null) {
                getWebChromeListener().onReceivedTitle(view, title);
            }
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (getWebChromeListener() != null) {
                return getWebChromeListener().onShowFileChooser(webView, filePathCallback, fileChooserParams);
            }
            return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
        }
    }

    public void showProgress(int newProgress) {
        if (newProgress == 100) {
            progressBar.setProgress(100);
            handler.postDelayed(runnable, 200);//0.2秒后隐藏进度条
        } else if (progressBar.getVisibility() == GONE) {
            progressBar.setVisibility(VISIBLE);
        }
        //设置初始进度5，这样会显得效果真一点，总不能从1开始吧
        if (newProgress < 5) {
            newProgress = 5;
        }
        //不断更新进度
        progressBar.setProgress(newProgress);
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            progressBar.setVisibility(View.GONE);
        }
    };

    public interface WebClient {

        void onPageFinished(WebView webView, String s);

        void onPageStarted(WebView webView, String s, @Nullable Bitmap bitmap);

    }

    public interface WebChrome {

        boolean onJsAlert(WebView webView, String url, String message, JsResult jsResult);

        void onReceivedTitle(WebView view, String title);

        boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, WebChromeClient.FileChooserParams fileChooserParams);

    }

    /**
     * 销毁WebView时调用
     */
    public void destroyWebView() {
        if (webChromeListener != null) {
            webChromeListener = null;
        }
        clearHistory();
        ((ViewGroup) getParent()).removeView(this);
        loadUrl("about:blank");
        stopLoading();
        setWebChromeClient(null);
        setWebViewClient(null);
        setWebClientListener(null);
        removeAllViews();
        destroy();
    }

    /**
     * 钩子方法
     *
     * @param view webView
     * @param url  链接
     * @return 默认为不拦截
     */
    public boolean customOverrideUrlLoading(WebView view, String url) {
        return false;
    }

    /**
     * 钩子方法 ,空实现
     *
     * @param view webView
     * @param url  链接
     */
    public void customPageFinished(WebView view, String url) {
    }
}
