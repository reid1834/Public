package com.example.imageswitchertest;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher.ViewFactory;

public class MainActivity extends Activity implements OnClickListener, ViewFactory{
	
	private static final String TAG = "liyang";
	ArrayList<Uri> imageUri = new ArrayList<Uri>();
	private ContentObserver cob = new ImageObserver(new Handler());
	private static final Uri uri = Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
	private static final String[] STORE_IMAGES = {MediaStore.Images.Media._ID};
	private ImageSwitcher imageSwitcher;
	private boolean isRun=true;
	private static final int SHOW_NEXT=1;
	private AsynImageLoader asynImageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		asynImageLoader = new AsynImageLoader(this);
		imageSwitcher = (ImageSwitcher)findViewById(R.id.image_switcher);
		imageSwitcher.setFactory(this);
		getImageUri();
		//自动播放图片启动
		AutoShowImage.start();
		this.getContentResolver().registerContentObserver(uri, true, cob);
		
	}
	
	Thread AutoShowImage=new Thread(){
		
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
				showNextImage();
			}
		}
	};
	
	public void showNextImage() {
		int minIndex = 0;
		int maxIndex = imageUri.size();
		
		Random random = new Random();
		int index = random.nextInt(maxIndex)%(maxIndex - minIndex + 1) + minIndex;
		Log.d(TAG, "index = " + index);
		
		asynImageLoader.showImageAsyn(imageSwitcher, imageUri.get(index).toString(), R.drawable.ic_launcher);
		
	}
	
	public void getImageUri() {
		Cursor cursor = this.getContentResolver().query(uri, STORE_IMAGES, null, null, null);
		while (cursor.moveToNext()) {
			long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
			Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().
					  appendPath(Long.toString(id)).build();
			imageUri.add(uri);
		}
		Log.d(TAG, imageUri.toString());
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.getContentResolver().unregisterContentObserver(cob);
	}

	private class ImageObserver extends ContentObserver {
		
		public ImageObserver(Handler handler) {
			// TODO Auto-generated constructor stub
			super(handler);
		}
		
		public void onChange(boolean selfChange) {
			Log.d(TAG, "image uri change!!!");
			super.onChange(selfChange);
			getImageUri();
		}
	}

	@Override
	public View makeView() {
		// TODO Auto-generated method stub
		//将所有图片通过ImageView来
		return new ImageView(this);
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}
