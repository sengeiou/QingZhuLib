package com.cisdi.qingzhu.webview.video;

public interface CompressCallback {
    /**
     * 压缩进度
     *
     * @param progress 整数
     */
    void onProgress(int progress);

    /**
     * 压缩错误
     *
     * @param errorCode 错误码
     */
    void onError(int errorCode);

    /**
     * 取消回调
     */
    void onCancel();

    /**
     * 压缩成功
     *
     * @param fileUrl 成功回调
     */
    void onSuccess(String fileUrl);
}
