package com.test.viewflipper;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

public class MainActivity extends Activity implements OnGestureListener, LoaderCallbacks<Cursor> {

	private static final String TAG = "liyang";
	private GestureDetector gestureDetector = null;
	private ViewFlipper viewFlipper = null;
	private LinearLayout linearLayout;
	private int SHOW_NEXT=1;
	private boolean isRun=true;
	private int cruneetImg=0;
	List<ImageView> imgviewList;
	ArrayList imageUri = new ArrayList();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getLoaderManager().initLoader(0, null, this);
		
		linearLayout=(LinearLayout)findViewById(R.id.linearLayout);
		linearLayout.getBackground().setAlpha(100);//设置背景透明

		viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
		
		//初始化图片数据
	    //int[] imgs = { R.drawable.a1, R.drawable.a2,
		//		  R.drawable.a3 };
	    
		gestureDetector =new GestureDetector(this);
		
		// 添加图片源
		/*for (int i = 0; i < imageSource.size(); i++) { 	
			ImageView iv = new ImageView(this);
			iv.setImageBitmap((Bitmap) imageSource.get(i));
			iv.setScaleType(ImageView.ScaleType.FIT_XY);
			viewFlipper.addView(iv, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}*/
		
		//添加小圆点
/*		imgviewList=new ArrayList<ImageView>();
		for (int i = 0; i < imgs.size(); i++) { 	
			ImageView iv = new ImageView(this);
			if(i==cruneetImg){
				iv.setImageResource(R.drawable.current_icon);
			}else{
				iv.setImageResource(R.drawable.for_icon);
			}
			iv.setScaleType(ImageView.ScaleType.FIT_XY);
			LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(8,8);
			lp.setMargins(4,4,4,4);
			iv.setLayoutParams(lp);
			linearLayout.addView(iv);
			imgviewList.add(iv);
		}*/
		
		//自动播放banner图片启动
		AutoFlip.start();
	}
	
	Thread AutoFlip=new Thread(){
		
		public void run(){
			while(isRun){
				try {
					Thread.sleep(5000);
					Message msg=new Message();
					msg.what=SHOW_NEXT;
					handler.sendMessage(msg);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					isRun=false;
					e.printStackTrace();
				}
			}
		}
	};
	
	
	Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(msg.what==SHOW_NEXT){
				showNextView();
			}
		}
	};

	public void showNextView(){
		//imgviewList.get(cruneetImg).setImageResource(R.drawable.for_icon);
		Animation lInAnim = AnimationUtils.loadAnimation(this,R.anim.push_left_in );		// 向左滑动左侧进入的渐变效果（alpha 0.1  -> 1.0）
		Animation lOutAnim = AnimationUtils.loadAnimation(this, R.anim.push_left_out); 	// 向左滑动右侧滑出的渐变效果（alpha 1.0  -> 0.1）
		viewFlipper.setInAnimation(lInAnim);
		viewFlipper.setOutAnimation(lOutAnim);
		viewFlipper.showNext();
		/*if(cruneetImg==imgviewList.size()-1){
			cruneetImg=0;
		}else{
			cruneetImg++;
		}
		imgviewList.get(cruneetImg).setImageResource(R.drawable.current_icon);*/
	}
	
	public void showPreviousView(){
		//imgviewList.get(cruneetImg).setImageResource(R.drawable.for_icon);
		Animation rInAnim = AnimationUtils.loadAnimation(this, R.anim.push_right_in); 	// 向右滑动左侧进入的渐变效果（alpha  0.1 -> 1.0）
		Animation rOutAnim = AnimationUtils.loadAnimation(this, R.anim.push_right_out); // 向右滑动右侧滑出的渐变效果（alpha 1.0  -> 0.1）
		viewFlipper.setInAnimation(rInAnim);
		viewFlipper.setOutAnimation(rOutAnim);
		viewFlipper.showPrevious();
		/*if(cruneetImg==0){
			cruneetImg=imgviewList.size()-1;
		}else{
			cruneetImg--;
		}
		imgviewList.get(cruneetImg).setImageResource(R.drawable.current_icon);*/
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return gestureDetector.onTouchEvent(event);
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		if (e2.getX() - e1.getX() > 120) {
			
			showPreviousView();
			
			return true;
		}else if (e2.getX() - e1.getX() < -120) {		 // 从右向左滑动（右进左出）
			
			showNextView();
			
			return true;
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private static final String[] STORE_IMAGES = {
        MediaStore.Images.Media._ID
	};
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		Log.d(TAG, "onCreateLoader");
		// TODO Auto-generated method stub
		// 为了查看信息，需要用到CursorLoader。
		CursorLoader cursorLoader = new CursorLoader(
				this,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
				STORE_IMAGES,
				null,
				null,
				null);
		
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		Log.d(TAG, "onLoadFinished");
		// TODO Auto-generated method stub
		while (cursor.moveToNext()) {
			long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
			Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().
					  appendPath(Long.toString(id)).build();
			imageUri.add(uri);
		}
		loadImageFromUri();
	}

	private void loadImageFromUri() {
		Log.d(TAG, "loadImageFromUri");
		Bitmap bitmap = null;
		byte[] mContent = null;
		
		FileUtil file = new FileUtil();
		ContentResolver resolver = getContentResolver();
				
		for (int i=0; i<imageUri.size(); i++) {
			Log.d(TAG, "imageUri.size(): " + imageUri.size());
			// 从Uri中读取图片资源
			try {
				ImageView iv = new ImageView(this);
				mContent = file.readInputStream(resolver.openInputStream(Uri.parse(imageUri.get(i).toString())));
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize=2;
				bitmap = file.getBitmapFromBytes(mContent, options);
				iv.setImageBitmap(bitmap);
				iv.setScaleType(ImageView.ScaleType.CENTER);
				viewFlipper.addView(iv, new LayoutParams(LayoutParams.MATCH_PARENT, 300));
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onLoaderReset");
	}

}
