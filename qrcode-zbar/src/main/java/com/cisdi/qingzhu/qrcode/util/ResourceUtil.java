package com.cisdi.qingzhu.qrcode.util;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * 资源工具类
 * Created by lcy on 2019/1/7.
 * email:lcyzxin@gmail.com
 * version 1.0
 */
public class ResourceUtil {

    public static Drawable getDrawableFromRes(Context context, int drawableId) {
        Drawable drawable = context.getResources().getDrawable(drawableId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        return drawable;
    }

    public static int getVisiblePercent(View pView) {
        if (pView != null && pView.isShown()) {
            DisplayMetrics displayMetrics = pView.getContext().getResources().getDisplayMetrics();
            int displayWidth = displayMetrics.widthPixels;
            Rect rect = new Rect();
            pView.getGlobalVisibleRect(rect);
            if ((rect.top >= 0) && (rect.left < displayWidth)) {
                double areaVisible = rect.width() * rect.height();
                double areaTotal = pView.getWidth() * pView.getHeight();
                return (int) ((areaVisible / areaTotal) * 100);
            } else {
                return -1;
            }
        }
        return -1;
    }
}
