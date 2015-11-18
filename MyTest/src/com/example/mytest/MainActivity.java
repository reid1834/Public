package com.example.mytest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends Activity implements OnClickListener, OnTouchListener {
    
    private static RectF ANIM_RECT_4TO3;
    private static RectF ANIM_RECT_FULL_SCREEN;
    private static final int MSG_ANIMATE_AFTER_SHUTTER = 100;
    private View mRoot;
    private View mShutterBg;
    private ImageView mButton;
    private CyShutterAnimation mShutterAnim;
    private boolean mIsFullScreen = true;
    private boolean mIsSquare;
    private Handler mhandler;
    
    private CyShutterAnimation.ShutterAnimListener mShutterAnimListener = new CyShutterAnimation.ShutterAnimListener() {
        
        @SuppressLint("NewApi") @Override
        public void onAnimStart() {
            // TODO Auto-generated method stub
            Log.d("liyang", "MainActivity onAnimStart");
            mButton.setVisibility(View.GONE);
        }
        
        @Override
        public void onAnimEnd() {
            // TODO Auto-generated method stub
            Log.d("liyang", "MainActivity onAnimaEnd");
            mButton.setVisibility(View.VISIBLE);
        }

        @Override
        public void onRefocusAnimEnd() {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onThumbAnimEnd() {
            // TODO Auto-generated method stub
            
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CameraUtil.initialize(this);
        init();
        mRoot = (View) findViewById(R.id.container);
        //mRoot.setOnTouchListener(this);
        
        mShutterBg = (View) findViewById(R.id.shutter_bg);
        mShutterBg.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            
            @Override
            public void onLayoutChange(View arg0, int arg1, int arg2, int arg3, int arg4, int arg5,
                    int arg6, int arg7, int arg8) {
                // TODO Auto-generated method stub
                if (mShutterAnim == null) {
                    throw new RuntimeException("init mShutterAnim before this !!!");
                }
                
                if (arg3 == arg7) {
                    return;
                }
                /*mShutterAnim.confirmShutterDimen(MainActivity.this, 
                        arg3 - arg1, arg2 + (arg4 - arg2) / 2 - CameraUtil.getControlbarHeight() / 2);*/
                mShutterAnim.confirmShutterDimen(MainActivity.this, 
                        arg1 + (arg3 - arg1) / 2, arg2 + (arg4 - arg2) / 2);
            }
        });
        mButton = (ImageView) findViewById(R.id.ic_shutter);
        mButton.setOnClickListener(this);
        
        mShutterAnim = (CyShutterAnimation) findViewById(R.id.shutter_anim);
        mShutterAnim.setListener(mShutterAnimListener);
 
        mhandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                switch (msg.what) {
                    case MSG_ANIMATE_AFTER_SHUTTER:
                        animateAfterShutter();
                        break;

                    default:
                        break;
                }
            }
            
        };
    }
    
    private void init() {
        ANIM_RECT_4TO3 = new RectF(0.0F, 0.0F, CameraUtil.getScreenWidth(), CameraUtil.getScreenVisibleHeight());
        ANIM_RECT_FULL_SCREEN = new RectF(0.0F, 0.0F, CameraUtil.getScreenWidth(), CameraUtil.getScreenHeight());
    }
            
    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.ic_shutter:
                animateBeforeShutter();
                mhandler.sendEmptyMessageDelayed(MSG_ANIMATE_AFTER_SHUTTER, 500);
                break;
                
            default:
                break;
        }
    }
    
    public void animateBeforeShutter() {
        mShutterAnim.startShutterAnimation();
    }
    
    public void animateAfterShutter() {
        RectF localRectF;
        //if (mIsSquare) {
        //    localRectF = ANIM_RECT_SQUARE;
        //} else {
            if (mIsFullScreen) {
                localRectF = ANIM_RECT_FULL_SCREEN;
            } else {
                localRectF = ANIM_RECT_4TO3;
            }
        //}
        mShutterAnim.startShadowAnimation(localRectF);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        // TODO Auto-generated method stub
        animateBeforeShutter();
        mhandler.sendEmptyMessageDelayed(MSG_ANIMATE_AFTER_SHUTTER, 500);
        return true;
    }
}
