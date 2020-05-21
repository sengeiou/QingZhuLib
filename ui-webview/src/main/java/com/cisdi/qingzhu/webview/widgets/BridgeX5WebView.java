package com.cisdi.qingzhu.webview.widgets;

import android.content.Context;
import android.util.AttributeSet;

import com.cisdi.qingzhu.jsbridge.BridgeTiny;
import com.cisdi.qingzhu.jsbridge.IWebView;
import com.cisdi.qingzhu.jsbridge.OnBridgeCallback;
import com.cisdi.qingzhu.webview.utils.OpenUrlUtil;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BridgeX5WebView extends X5WebView implements IWebView {

    private BridgeTiny bridgeTiny;

    public BridgeX5WebView(Context context) {
        this(context, null);
    }

    public BridgeX5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bridgeTiny = new BridgeTiny(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void evaluateJavascript(@Nullable String var1, @Nullable Object obj) {
        if (obj == null) {
            super.evaluateJavascript(var1, null);
            return;
        }
        super.evaluateJavascript(var1, (ValueCallback<String>) obj);
    }

    @Override
    public void callHandler(@Nullable String handlerName, @Nullable Object data, @Nullable OnBridgeCallback responseCallback) {
        if (bridgeTiny != null) {
            bridgeTiny.callHandler(handlerName, data, responseCallback);
        }
    }

    @NotNull
    @Override
    public Context context() {
        return getContext();
    }

    @Override
    public void destroy() {
        super.destroy();
        if (bridgeTiny != null) {
            bridgeTiny.freeMemory();
        }
    }

    @Override
    public boolean customOverrideUrlLoading(WebView view, String url) {
        return interceptRequest(view, url);
    }

    private boolean interceptRequest(WebView view, String url) {
        return OpenUrlUtil.shouldInterceptScheme(view.getContext(), url);
    }

    @Override
    public void customPageFinished(WebView webView, String url) {
        bridgeTiny.webViewLoadJs((IWebView) webView);
    }

}
