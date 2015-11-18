package com.example.mytest;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class CameraUtil {

    private static float sPixelDensity = 1.0F;
    private static int sScreenWidth;
    private static int sScreenHeight;
    private static int sScreenVisibleHeight;
    private static int sSmartbarHeight;
    private static int sControlbarHeight;

    public static void initialize(Context paramContext){
      DisplayMetrics localDisplayMetrics = new DisplayMetrics();
      ((WindowManager)paramContext.getSystemService("window")).getDefaultDisplay().getMetrics(localDisplayMetrics);
      sPixelDensity = localDisplayMetrics.density;
      sScreenWidth = localDisplayMetrics.widthPixels;
      sScreenHeight = localDisplayMetrics.heightPixels;
      sSmartbarHeight = 0;//paramContext.getResources().getDimensionPixelSize(R.dimen.my_smartbar_height);
      sControlbarHeight = 0;//sScreenHeight - (int)(4.0D * sScreenWidth / 3.0D) - sSmartbarHeight;
      sScreenVisibleHeight = sScreenHeight - sSmartbarHeight - sControlbarHeight;
    }
    
    public static int getScreenWidth() {
      return sScreenWidth;
    }
    
    public static int getScreenHeight()
    {
      return sScreenHeight;
    }
    
    public static int getScreenVisibleHeight() {
      return sScreenVisibleHeight;
    }
    
    public static int getControlbarHeight() {
      return sControlbarHeight;
    }
}
