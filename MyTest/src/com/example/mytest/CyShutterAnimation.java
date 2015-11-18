package com.example.mytest;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;

public class CyShutterAnimation extends View {

    private float CENTER_X = 0.0F;
    private float CENTER_Y = 0.0F;
    private boolean mCapturingAnimStart = false;
    private Bitmap mShutterPiece;
    private Matrix mShutterMatrix;
    private float mShutterPieceHeight;
    private float mShutterPieceMargin;
    private float mCircleThick;
    private Paint mCirclePaint;
    private float mCircleRadius;
    private float mCircleRadiusMax;
    private float mCircleRadiusMin;
    private Paint mFlashPaint;
    private RectF mFlashRect;
    private float mFlashThick;
    private float mFlashThickMax;
    private Paint mLevelBgPaint;
    private Paint mLevelCenterPaint;
    private RectF mLevelCircleRectF;
    private boolean mLevelEnabled = true;
    private Paint mLevelFgPaint;
    private float mLevelLeanAngleCurrent;
    private float mLevelLeanAngleStart;
    private float mLevelLeanAngleTarget;
    private boolean mLevelLeanAnimClockwise;
    private long mLevelLeanAnimEndTime = 0L;
    private long mLevelLeanAnimStartTime = 0L;
    private float mLevelRadius;
    private float mLevelRadiusMax;
    private float mLevelRadiusMin;
    private int mLevelRotateAngleCurrent;
    private int mLevelRotateAngleStart;
    private int mLevelRotateAngleTarget;
    private boolean mLevelRotateAnimClockwise;
    private long mLevelRotateAnimEndTime = 0L;
    private long mLevelRotateAnimStartTime = 0L;
    private float mRefocusAngle;
    private boolean mRefocusAnimStart = false;
    private long mRefocusAnimationStartTime = -1L;
    private Paint mRefocusBgPaint;
    private Paint mRefocusFgPaint;
    private Paint mRefocusHintPaint;
    private String mRefocusHint;
    private String mRefocusHintHandling;
    private float mRefocusHintHeight;
    private float mRefocusHintCenterX;
    private float mRefocusHintCenterY;
    private float mRefocusRadius;
    private RectF mRefocusRectF;
    private float mScaleFactor;
    private boolean mShadowAnimStart = false;
    private long mShadowAnimStartTime = -1L;
    private float mShadowCenterX = 0.0F;
    private float mShadowCenterY = 0.0F;
    private Paint mShadowPaint;
    private float mShadowRadius = 200.0F;
    private static float SHADOW_RADIUS_MAX = 0.0F;
    private float mShutterAngle;
    private float mShutterRadius;
    private float mShutterRadiusMax;
    private float mShutterRadiusMin;
    private boolean mShutterSecondAnimStart = false;
    private boolean mShutterAnimStart = false;
    private boolean mShutterAnimDuringVideoRecording = false;
    private long mShutterAnimStartTime = -1L;
    private float mCaptureMaxAngle = 0.0F;
    private long mCaptureTime = 0L;
    private long mCaptureTimeRemain = 0L;
    private ShutterAnimListener mListener;
    
    public CyShutterAnimation(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        Resources localResources = paramContext.getResources();
        initShutterAnimStuff(localResources);
        initLevelStuff(localResources);
        //initRefocusStuff(localResources);
        initShadowPaint();
    }

    public void confirmShutterDimen(Context paramContext, int paramInt, float paramFloat) {
        
        if (CENTER_X == 0.0F) {
            CENTER_X = paramInt;
        }
        if (CENTER_Y == 0.0F) {          
            CENTER_Y = paramFloat;
            Log.d("liyang", "confirmShutterDimen CENTER_X: " + CENTER_X + ", CENTER_Y: " + CENTER_Y);
        }
        Resources localResources = paramContext.getResources();
        float f = localResources.getDrawable(R.drawable.mz_btn_shutter_default).getIntrinsicWidth();
        mScaleFactor = 0.8f;//(paramInt / f);
        initShutterStuffAfterShutterDimenConfirm(localResources);
        initLevelStuffAfterShutterDimenConfirm(localResources);
        initRefocusStuffAfterShutterDimenConfirm();
    }
    
    private void initLevelStuffAfterShutterDimenConfirm(Resources paramResources) {
        mLevelRadiusMin = (paramResources.getDimension(R.dimen.my_level_radius_min) / 2.0F * mScaleFactor);
        mLevelRadiusMax = (1.1F * mLevelRadiusMin);
        mLevelRadius = mLevelRadiusMin;
        mLevelCircleRectF = new RectF(CENTER_X - mLevelRadius, CENTER_Y - mLevelRadius, CENTER_X + mLevelRadius, CENTER_Y + mLevelRadius);
    }
    
    private void initRefocusStuffAfterShutterDimenConfirm(){
        mRefocusRadius = mLevelRadiusMin;
        mRefocusRectF = new RectF(mLevelCircleRectF);
    }
    
    private void initShutterStuffAfterShutterDimenConfirm(Resources paramResources) {
        mShutterPieceMargin = paramResources.getDimension(R.dimen.my_shutter_piece_margin);
        mShutterRadiusMax = paramResources.getDimension(R.dimen.my_shutter_radius_max);
        mShutterRadiusMin = paramResources.getDimension(R.dimen.my_shutter_radius_min);
        mShutterRadius = mShutterRadiusMax;
        mCircleRadiusMin = (paramResources.getDimension(R.dimen.my_circle_radius_min) / 2.0F * mScaleFactor);
        mCircleRadiusMax = (1.1F * mCircleRadiusMin);
    }
    
    private void initShutterAnimStuff(Resources paramResources) {
        mShutterPiece = BitmapFactory.decodeResource(paramResources, R.drawable.mz_shutter_piece);
        mShutterPieceHeight = mShutterPiece.getHeight();
        mCircleThick = paramResources.getDimension(R.dimen.my_circle_tick);
        mCirclePaint = new Paint();
        mCirclePaint.setColor(Color.WHITE);
        mCirclePaint.setAlpha(204);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setDither(true);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(mCircleThick);
        mFlashThickMax = paramResources.getDimension(R.dimen.my_flash_tick_max);
        mFlashThick = mFlashThickMax;
        mFlashPaint = new Paint(mCirclePaint);
        mFlashPaint.setAlpha(255);
        mFlashPaint.setStrokeWidth(mFlashThick);
        mShutterMatrix = new Matrix();
    }
    
    private void initLevelStuff(Resources paramResources) {
        float f = paramResources.getDimension(R.dimen.my_level_tick);
        mLevelFgPaint = new Paint();
        mLevelFgPaint.setColor(paramResources.getColor(R.color.ic_level_fg));
        mLevelFgPaint.setAntiAlias(true);
        mLevelFgPaint.setDither(true);
        mLevelFgPaint.setStyle(Paint.Style.STROKE);
        mLevelFgPaint.setStrokeWidth(f);
        mLevelBgPaint = new Paint(mLevelFgPaint);
        mLevelBgPaint.setColor(paramResources.getColor(R.color.ic_level_bg));
        mLevelCenterPaint = new Paint(mLevelFgPaint);
        mLevelCenterPaint.setColor(paramResources.getColor(R.color.ic_level_center));
    }
    
    /*private void initRefocusStuff(Resources paramResources) {
      float f = paramResources.getDimension(2131493380);
      mRefocusBgPaint = new Paint();
      mRefocusBgPaint.setColor(paramResources.getColor(2131361899));
      mRefocusBgPaint.setAntiAlias(true);
      mRefocusBgPaint.setDither(true);
      mRefocusBgPaint.setStyle(Paint.Style.STROKE);
      mRefocusBgPaint.setStrokeWidth(f);
      mRefocusFgPaint = new Paint(mRefocusBgPaint);
      mRefocusFgPaint.setColor(-1);
      mRefocusHintPaint = new Paint();
      mRefocusHintPaint.setColor(-1);
      mRefocusHintPaint.setTextSize(paramResources.getDimension(2131493236));
      mRefocusHintPaint.setTextAlign(Paint.Align.CENTER);
      mRefocusHintPaint.setAntiAlias(true);
      mRefocusHint = paramResources.getString(2131624300);
      mRefocusHintHandling = paramResources.getString(2131624301);
      mRefocusHintHeight = (-mRefocusHintPaint.ascent() - mRefocusHintPaint.descent());
      mRefocusHintCenterX = (CameraUtil.getScreenWidth() / 2);
      mRefocusHintCenterY = (CameraUtil.getScreenVisibleHeight() - paramResources.getDimension(2131493209));
    }*/
    
    private void initShadowPaint() {
        mShadowPaint = new Paint();
        mShadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        mShadowPaint.setAntiAlias(true);
    }

    public void enableLevel(boolean paramBoolean) {
        mLevelEnabled = paramBoolean;
        invalidate();
    }
    
    @Override
    protected void onDraw(Canvas paramCanvas) {
        // TODO Auto-generated method stub
        if (mLevelEnabled) {
            Log.d("liyang", "onDraw drawLevel");
            //drawLevel(paramCanvas);
        }
        if (mShadowAnimStart) {
            Log.d("liyang", "onDraw drawShadow");
            drawShadow(paramCanvas);
        }
        
        if (!mShutterAnimStart) {
            Log.d("liyang", "onDraw 000");
            return;
        }
        
        if (mCapturingAnimStart) {
            Log.d("liyang", "onDraw getCapturingAnimTransformation");
            getCapturingAnimTransformation(System.currentTimeMillis());
        } else {
            if (mShutterSecondAnimStart == true) {
                Log.d("liyang", "onDraw getShutterAnimSecondTransformation");
                getShutterAnimSecondTransformation(System.currentTimeMillis());
            } else {
                Log.d("liyang", "onDraw getShutterAnimFirstTransformation");
                getShutterAnimFirstTransformation(System.currentTimeMillis());
            }
        } 
        
        if (!mShutterAnimDuringVideoRecording) {
            Log.d("liyang", "onDraw drawShutter");
            drawShutter(paramCanvas);
            if (!mLevelEnabled) {
                Log.d("liyang", "onDraw drawCircle");
                drawCircle(paramCanvas);
            }
        }
        //invalidate();
       
        /*if (mCapturingAnimStart)
          drawCaptureHint(paramCanvas, mCaptureTooLongHint);*/
    }  
    
    private void getCapturingAnimTransformation(long paramLong) {
        if (mShutterAnimStartTime == -1L) {
            mShutterAnimStartTime = paramLong;
            mCaptureTimeRemain = mCaptureTime;
        }
        long l1 = mCaptureTime;
        long l2 = paramLong - mShutterAnimStartTime;
        float f = (float)l2 / (float)l1;
        if ((f >= 0.0F) && (f <= 1.0F)) {
            mCaptureTimeRemain = (l1 - l2);
            mShutterAngle = (70.0F + getInterpolation(f, 0.5F) * mCaptureMaxAngle);
        }
      
        mShutterAngle = 70.0F;
        mShutterAnimStartTime = -1L;
        mCapturingAnimStart = false;
        mShutterSecondAnimStart = true;
        mCaptureTime = 0L;
        mCaptureMaxAngle = 0.0F;
        mCaptureTimeRemain = 0L;
    }

    private void getShadowTransformation(long paramLong) {
        if (mShadowAnimStartTime == -1L) {
            mShadowAnimStartTime = paramLong;
        }
      
        float f = (float)(paramLong - mShadowAnimStartTime) / 750.0F;
        Log.d("liyang", "getShadowTransformation f: " + f);
        if ((f >= 0.0F) && (f <= 1.0F)) {
            mShadowRadius = (200.0F + getInterpolation(f, 1.5F) * (SHADOW_RADIUS_MAX - 200.0F));
        }
      
        mShadowAnimStartTime = -1L;
        mShadowRadius = (mFlashRect.height() / 2.0F);
        mShadowAnimStart = false;
        mShadowCenterX = 0.0F;
        mShadowCenterY = 0.0F;
    }
    
    private void getShutterAnimFirstTransformation(long paramLong) {
        if (mShutterAnimStartTime == -1L)
            mShutterAnimStartTime = paramLong;
        float f1 = (float)(paramLong - mShutterAnimStartTime) / 500.0F;
        if ((f1 >= 0.0F) && (f1 <= 1.0F)) {
            float f2 = getInterpolation(f1, 2.0F);
            mShutterRadius = (mShutterRadiusMax - f2 * (mShutterRadiusMax - mShutterRadiusMin));
            mShutterAngle = (70.0F * f2);
            mCircleRadius = (mCircleRadiusMin + f2 * (mCircleRadiusMax - mCircleRadiusMin));
            mFlashThick = (f2 * mFlashThickMax);
            mLevelRadius = (mLevelRadiusMin + f2 * (mLevelRadiusMax - mLevelRadiusMin));
            mListener.onAnimStart();
        }
      
        mShutterRadius = mShutterRadiusMin;
        mShutterAnimStartTime = -1L;
        mCircleRadius = mCircleRadiusMax;
        mFlashThick = mFlashThickMax;
        mLevelRadius = mLevelRadiusMax;
        mShutterAngle = 70.0F;
        if (mCaptureTime <= 0L) {
            mShutterSecondAnimStart = true;      
            return;
        } 
        mCapturingAnimStart = true;
        invalidate();
    }

    private void getShutterAnimSecondTransformation(long paramLong) {
        if (mShutterAnimStartTime == -1L) {
            mShutterAnimStartTime = paramLong;
        }
        float f1 = (float)(paramLong - mShutterAnimStartTime) / 500.0F;
        if ((f1 >= 0.0F) && (f1 <= 1.0F)) {
            float f2 = getInterpolation(f1, 2.0F);
            mShutterRadius = (mShutterRadiusMin + f2 * (mShutterRadiusMax - mShutterRadiusMin));
            mShutterAngle = (70.0F - 70.0F * f2);
            mCircleRadius = (mCircleRadiusMax - f2 * (mCircleRadiusMax - mCircleRadiusMin));
            mFlashThick = (mFlashThickMax - f2 * mFlashThickMax);
            mLevelRadius = (mLevelRadiusMax - f2 * (mLevelRadiusMax - mLevelRadiusMin));
        }
      
        mShutterAngle = 0.0F;
        mShutterRadius = mShutterRadiusMax;
        mShutterAnimStartTime = -1L;
        mShutterSecondAnimStart = false;
        mCircleRadius = mCircleRadiusMin;
        mFlashThick = 0.0F;
        mLevelRadius = mLevelRadiusMin;
        mShutterAnimStart = false;
        mListener.onAnimEnd();
    }
    
    public float getInterpolation(float paramFloat1, float paramFloat2) {
        if (paramFloat2 == 1.0F) {
            return 1.0F - (1.0F - paramFloat1) * (1.0F - paramFloat1);
        } 
        return (float)(1.0D - Math.pow(1.0F - paramFloat1, 2.0F * paramFloat2));
    }
    
    private void drawCircle(Canvas paramCanvas) {
        paramCanvas.drawCircle(CENTER_X, CENTER_Y, mCircleRadius, mCirclePaint);
    }

    private void drawFlash(Canvas paramCanvas) {
        mFlashPaint.setStrokeWidth(mFlashThick);
        mFlashRect.inset(mFlashThick / 2.0F, mFlashThick / 2.0F);
        paramCanvas.drawRect(mFlashRect, mFlashPaint);
        mFlashRect.inset(-mFlashThick / 2.0F, -mFlashThick / 2.0F);
    }

    private void drawLevel(Canvas paramCanvas) {
        float f1 = mLevelRadius - mLevelRadiusMin;
        if (f1 != 0.0F)
            mLevelCircleRectF.inset(-f1, -f1);
        int j;
        int m;
        int n;
        int i;
        float f3;
        float f4;
        if (mLevelRotateAngleCurrent != mLevelRotateAngleTarget) {
            long l2 = AnimationUtils.currentAnimationTimeMillis();
            if (l2 >= mLevelRotateAnimEndTime) {
                mLevelRotateAngleCurrent = mLevelRotateAngleTarget;
            }
            j = (int)(l2 - mLevelRotateAnimStartTime);
            int k = mLevelRotateAngleStart;
            if (mLevelRotateAnimClockwise) {
                m = k + j * 100 / 1000;
                if (m < 0) {
                    n = 360 + m % 360;
                }
                n = m % 360;
                mLevelRotateAngleCurrent = n;
                invalidate();
            }
        } else {
            paramCanvas.save();
            paramCanvas.rotate(-mLevelRotateAngleCurrent, CENTER_X, CENTER_Y);
            paramCanvas.drawCircle(CENTER_X, CENTER_Y, mLevelRadius, mLevelBgPaint);
            paramCanvas.drawArc(mLevelCircleRectF, 0.0F, 180.0F, false, mLevelCenterPaint);
            if (Math.abs(mLevelLeanAngleTarget - mLevelLeanAngleCurrent) > 10.0F) {
                long l1 = AnimationUtils.currentAnimationTimeMillis();
                if (l1 >= mLevelLeanAnimEndTime) {
                    mLevelLeanAngleCurrent = mLevelLeanAngleTarget;
                }
                i = (int)(l1 - mLevelLeanAnimStartTime);
                float f2 = mLevelLeanAngleStart;
                if (!mLevelLeanAnimClockwise) {
                    i = -i;
                }
                f3 = f2 + i * 100 / 1000;
                if (f3 < 0.0F) {
                    f4 = 360.0F + f3 % 360.0F;
                }
                f4 = f3 % 360.0F;
                mLevelLeanAngleCurrent = f4;
                invalidate();
            }
        }

        paramCanvas.drawArc(mLevelCircleRectF, -mLevelLeanAngleCurrent, 180.0F, false, mLevelFgPaint);
        paramCanvas.restore();
        if (f1 != 0.0F) {
            mLevelCircleRectF.inset(f1, f1);
        }
    }
    
    private void drawShadow(Canvas paramCanvas) {
        Log.d("liyang", "drawShadow");
        getShadowTransformation(System.currentTimeMillis());
        paramCanvas.saveLayer(mFlashRect, null, 31);
        paramCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC);
        if (mShadowCenterX == 0.0F) {
            mShadowCenterX = mFlashRect.centerX();
            mShadowCenterY = mFlashRect.centerY();
        }
        
        if (mShadowRadius > 0.0F) {
            float f1 = 1.01F * mShadowRadius;
            float f2 = mShadowCenterX;
            float f3 = mShadowCenterY;
            float f4 = f1 + mShadowRadius;
            int[] arrayOfInt = { Color.TRANSPARENT, 0xCCFFFFFF, Color.TRANSPARENT };
            float[] arrayOfFloat = new float[3];
            arrayOfFloat[0] = 0.0F;
            arrayOfFloat[1] = (mShadowRadius / (f1 + mShadowRadius));
            arrayOfFloat[2] = 1.0F;
            RadialGradient localRadialGradient = new RadialGradient(f2, f3, f4, arrayOfInt, arrayOfFloat, Shader.TileMode.MIRROR);
            mShadowPaint.setShader(localRadialGradient);
            Log.d("liyang", "f1: " + f1 + ", mShadowRadius: " + mShadowRadius);
            paramCanvas.drawCircle(mShadowCenterX, mShadowCenterY, f1 + mShadowRadius, mShadowPaint);
        } 
        paramCanvas.restore();
        invalidate();
    }

    private void drawShutter(Canvas paramCanvas) {
        paramCanvas.save();
        paramCanvas.rotate(mShutterAngle, CENTER_X, CENTER_Y);
        paramCanvas.scale(mScaleFactor, mScaleFactor, CENTER_X, CENTER_Y);
        mShutterMatrix.reset();
        mShutterMatrix.preTranslate(CENTER_X - mShutterPieceMargin, CENTER_Y + mShutterRadius - mShutterPieceHeight);
        paramCanvas.drawBitmap(mShutterPiece, mShutterMatrix, null);
        mShutterMatrix.postRotate(60.0F, CENTER_X, CENTER_Y);
        paramCanvas.drawBitmap(mShutterPiece, mShutterMatrix, null);
        mShutterMatrix.postRotate(60.0F, CENTER_X, CENTER_Y);
        paramCanvas.drawBitmap(mShutterPiece, mShutterMatrix, null);
        mShutterMatrix.postRotate(60.0F, CENTER_X, CENTER_Y);
        paramCanvas.drawBitmap(mShutterPiece, mShutterMatrix, null);
        mShutterMatrix.postRotate(60.0F, CENTER_X, CENTER_Y);
        paramCanvas.drawBitmap(mShutterPiece, mShutterMatrix, null);
        mShutterMatrix.postRotate(60.0F, CENTER_X, CENTER_Y);
        paramCanvas.drawBitmap(mShutterPiece, mShutterMatrix, null);
        paramCanvas.restore();
    }
    
    public void setListener(ShutterAnimListener paramShutterAnimListener) {
        mListener = paramShutterAnimListener;
    }
    
    public void startShutterAnimation() {
        mShutterAnimStart = true;
        invalidate();
    }
    
    public void startShadowAnimation(RectF paramRectF) {
        if ((mFlashRect == null) || (mFlashRect.height() != paramRectF.height())) {
            mFlashRect = new RectF(paramRectF);
        }
        mShadowRadius = 200.0F;
        SHADOW_RADIUS_MAX = mFlashRect.height() / 2.0F;
        mShadowAnimStart = true;
        invalidate();
    }
    
    public static abstract interface ShutterAnimListener {
        public abstract void onAnimEnd();
        public abstract void onAnimStart();
        public abstract void onRefocusAnimEnd();
        public abstract void onThumbAnimEnd();
    }
}
