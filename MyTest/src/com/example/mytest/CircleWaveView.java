package com.example.mytest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class CircleWaveView extends View {
    private float mWidth;
    private float mHeight;

    private float centerX; // 圆心X
    private float centerY; // 圆心Y
    private float floatRadius; // 变化的半径
    private float maxRadius = -1; // 圆半径
    private volatile boolean started = false;
    private Paint mLinePaint;
    private Paint mSolidPaint;
    private int waveColor = Color.LTGRAY; // Color.argb(0, 0, 0, 0); //颜色
    private int waveInterval = 100; // 圆环的宽度
    private boolean centerAlign = true;// 居中
    private float bottomMargin = 0;// 底部margin
    private boolean fillCircle = true;// 是否填充成实心圆环

    public CircleWaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleWaveView(Context context) {
        this(context, null, 0);
    }

    public CircleWaveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        mLinePaint = new Paint();
        mSolidPaint = new Paint();
    }

    private void init() {
        mWidth = getWidth();
        mHeight = getHeight();

        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(1.0F);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(waveColor);

        if (fillCircle) {
            mSolidPaint.setStyle(Paint.Style.STROKE);
            mSolidPaint.setStrokeWidth(waveInterval);
            // 设置背景色的颜色
            mSolidPaint.setColor(Color.BLUE);
        }

        centerX = mWidth / 2.0F;
        if (centerAlign) {
            centerY = (mHeight / 2.0F);

        } else {
            centerY = mHeight - bottomMargin;
        }

        if (mWidth >= mHeight) {
            maxRadius = mHeight / 2.0F;
        } else {
            maxRadius = mWidth / 2.0F;
        }

        floatRadius = (maxRadius % waveInterval);

        start();
    }

    public void start() {
        if (!started) {
            started = true;
            new Thread(thread).start();
        }
    }

    public void stop() {
        // Thread.interrupted();
        started = true;
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        stop();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (maxRadius <= 0.0F) {
            Log.d("liyang", "onDraw maxRadius <= 0.0F");
            return;
        }
        float radius = floatRadius % waveInterval;
        Log.d("liyang", "onDraw radius: " + radius);
        while (true) {
            int alpha = (int) (255.0F * (1.0F - radius / maxRadius));
            Log.d("liyang", "onDraw alpha: " + alpha);
            if (alpha <= 0) {
                break;
            }
            // 设置背景色

            if (fillCircle) {
                mSolidPaint.setAlpha(alpha >> 2);
                canvas.drawCircle(centerX, centerY, radius - waveInterval / 2,
                        mSolidPaint);
                Log.d("liyang", "onDraw circle 1: " + (radius - waveInterval / 2));
            }
            mLinePaint.setAlpha(alpha);
            canvas.drawCircle(centerX, centerY, radius, mLinePaint);
            Log.d("liyang", "onDraw circle 2: " + radius);
            radius += waveInterval;
            Log.d("liyang", "onDraw radius: " + radius);
        }
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            Log.d("liyang", "onWindowFocusChanged init()");
            init();
        } else {
            Log.d("liyang", "onWindowFocusChanged stop()");
            stop();
        }
    }
//在外面定义一个方法把started设置为true就可以暂停了
    private Runnable thread = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (started) {
                floatRadius = 4.0F + floatRadius;
                Log.d("liyang", "thread floatRadius:" + floatRadius + ", maxRadius: " + maxRadius);
                if (floatRadius > maxRadius) {
                    floatRadius = (maxRadius % waveInterval);
                    postInvalidate();
                }
                postInvalidate();
                try {
                    Thread.sleep(50L);
                } catch (InterruptedException localInterruptedException) {
                    localInterruptedException.printStackTrace();
                }
            }
        }
    };

    public void setMaxRadius(float maxRadius) {
        this.maxRadius = maxRadius;
    }

    public void setWaveColor(int waveColor) {
        this.waveColor = waveColor;
    }

    public void setWaveInterval(int waveInterval) {
        this.waveInterval = waveInterval;
    }

    public void setCenterAlign(boolean centerAlign) {
        this.centerAlign = centerAlign;
    }

}