package com.cisdi.qingzhu.webview.video;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.text.TextUtils;

import com.qiniu.pili.droid.shortvideo.PLShortVideoTranscoder;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class VideoCompress {

    private VideoCompress() {
    }

    /**
     * 提供单例获取入口
     */
    public static VideoCompress getInstance() {
        return SpLoginManagerHolder.holder;
    }

    private static class SpLoginManagerHolder {
        private static final VideoCompress holder = new VideoCompress();
    }

    /**
     * 当前进度条
     */
    private float mCurrentProgress;

    public void reflectQiniuSdk() {
        com.qiniu.pili.droid.shortvideo.b.t r = com.qiniu.pili.droid.shortvideo.b.t.a();
        Class cls = r.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                if (field.getType() == boolean.class) {
                    try {
                        field.setAccessible(true);
                        field.set(r, false);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 压缩视频
     *
     * @param context    上下文
     * @param sourcePath 要压缩文件的路径
     * @param callback   回调
     */
    public void compressVideoResource(Context context, String sourcePath, final CompressCallback callback) {
        mCurrentProgress = 0f;
        String savePath = getVideoCompressPath();
        if (TextUtils.isEmpty(sourcePath) || savePath == null || callback == null) {
            throw new IllegalArgumentException("sourcePath, callback must all not null");
        }
        MediaMetadataRetriever retriever = null;
        try {
            PLShortVideoTranscoder shortVideoTranscoder = new PLShortVideoTranscoder(context, sourcePath, savePath);
            retriever = new MediaMetadataRetriever();
            retriever.setDataSource(sourcePath);
            String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT); // 视频高度
            String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH); // 视频宽度
            int dstWidth = Integer.parseInt(width);
            int dstHeight = Integer.parseInt(height);
            if ((Math.max(dstWidth, dstHeight)) >= 720) {
                dstWidth = dstWidth >> 1;
                dstHeight = dstHeight >> 1;
            }
            int encodingBitrateLevel = 4;
            shortVideoTranscoder.transcode(dstWidth, dstHeight, getEncodingBitrateLevel(encodingBitrateLevel), new PLVideoSaveListener() {
                @Override
                public void onSaveVideoSuccess(String url) {
                    callback.onSuccess(url);
                }

                @Override
                public void onSaveVideoFailed(int errorCode) {
                    callback.onError(errorCode);
                }

                @Override
                public void onSaveVideoCanceled() {
                    callback.onCancel();
                }

                @Override
                public void onProgressUpdate(float percentage) {
                    if (percentage - mCurrentProgress > 0.01f) {
                        mCurrentProgress = percentage;
                        int percent = (int) (percentage * 100);
                        callback.onProgress(percent);
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            if (retriever != null) {
                retriever.release();
            }
        }
    }

    /**
     * 设置压缩质量
     */
    private int getEncodingBitrateLevel(int position) {
        return ENCODING_BITRATE_LEVEL_ARRAY[position];
    }

    /**
     * 选的越高文件质量越大，质量越好
     */
    private static final int[] ENCODING_BITRATE_LEVEL_ARRAY = {
            500 * 1000,
            800 * 1000,
            1000 * 1000,
            1200 * 1000,
            1600 * 1000,
            2000 * 1000,
            2500 * 1000,
            4000 * 1000,
            8000 * 1000,
    };

    /**
     * 获取文件路径
     */
    public static String getVideoCompressDir() {
        String sdPath = Environment.getExternalStorageDirectory().getPath();
        return sdPath + File.separator + "QingZhu" + File.separator;
    }

    /**
     * 生成抓图路径或录像存放路径
     */
    private static String getVideoCompressPath() {
        java.util.Date now = new java.util.Date();
        SimpleDateFormat tf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String path = getVideoCompressDir() + tf.format(now) + "_" + "video" + "_" + "compress" + ".mp4";
        boolean createSuccess = createFilePath(null, path);
        return createSuccess ? path : null;
    }

    /**
     * 创建文件路径
     *
     * @param file     文件
     * @param filePath 文件路径
     * @return 是否创建成功
     */
    public static boolean createFilePath(File file, String filePath) {
        int index = filePath.indexOf("/");
        if (index == -1) {
            return false;
        }
        if (index == 0) {
            filePath = filePath.substring(index + 1);
            index = filePath.indexOf("/");
        }
        String path = filePath.substring(0, index);
        File fPath;
        if (file == null) {
            fPath = new File(path);
        } else {
            fPath = new File(file.getPath() + "/" + path);
        }
        if (!fPath.exists()) {
            if (!fPath.mkdir()) {
                return false;
            }
        }
        if (index < (filePath.length() - 1)) {
            String exPath = filePath.substring(index + 1);
            createFilePath(fPath, exPath);
        }
        return true;
    }

}
