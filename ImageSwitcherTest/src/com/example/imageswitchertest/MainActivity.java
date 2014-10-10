package com.example.imageswitchertest;

import java.util.ArrayList;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageSwitcher;

public class MainActivity extends Activity {
	
	private static final String TAG = "liyang";
	ArrayList<Uri> imageUri = new ArrayList<Uri>();
	private ContentObserver cob = new ImageObserver(new Handler());
	private static final Uri uri = Uri.parse(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());
	private static final String[] STORE_IMAGES = {MediaStore.Images.Media._ID};
	private ImageSwitcher imageSwitcher;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imageSwitcher = (ImageSwitcher)findViewById(R.id.image_switcher);
		
		this.getContentResolver().registerContentObserver(uri, true, cob);
		getImageUri();
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
}
