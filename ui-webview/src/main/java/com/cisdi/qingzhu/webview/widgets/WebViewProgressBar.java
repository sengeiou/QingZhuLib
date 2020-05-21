package com.cisdi.qingzhu.webview.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.cisdi.qingzhu.webview.R;

public class WebViewProgressBar extends View {
    private int progress = 1;
    private final static int LINE_SIZE = 4;
    private Paint paint;

    public WebViewProgressBar(Context context) {
        this(context, null);
    }

    public WebViewProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebViewProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint(context);
    }

    private void initPaint(Context context) {
        paint = new Paint(Paint.DITHER_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(LINE_SIZE);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(context.getResources().getColor(R.color.colorWebProgress));
    }

    /**
     * 设置进度
     *
     * @param progress 进度值
     */
    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();//刷新画笔
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, (float) (getWidth() * progress / 100), LINE_SIZE, paint);
    }
}
