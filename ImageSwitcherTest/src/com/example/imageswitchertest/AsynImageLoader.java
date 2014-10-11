package com.example.imageswitchertest;

import java.io.FileNotFoundException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageSwitcher;

public class AsynImageLoader {
	private static final String TAG = "AsynImageLoader";
	public static final String CACHE_DIR = "/DICM/Cache/";
	//�����ȡ����ͼƬ��Map
	private Map<String, SoftReference<Bitmap>> caches;
	//�������
	private List<Task> taskQueue;
	private boolean isRunning = false;
	private Context mContent;
	
	public AsynImageLoader(Context content) {
		//��ʼ������
		mContent = content;
		caches = new HashMap<String, SoftReference<Bitmap>>();
		taskQueue = new ArrayList<AsynImageLoader.Task>();
		//����ͼƬ�����߳�
		isRunning = true;
		new Thread(runnable).start();
	}
	
	/**
	 * param imageView ��Ҫ�ӳټ���ͼƬ�Ķ���
	 * param url ͼƬ��URL��ַ
	 * param resId ͼƬ���ع�������ʾ��ͼƬ��Դ
	 */
	public void showImageAsyn(ImageSwitcher imageSwitcher, String url, int resId) {
		imageSwitcher.setTag(url);
		Bitmap bitmap = loadImageAsyn(url, getImageCallback(imageSwitcher, resId));
		
		if (bitmap == null) {
			imageSwitcher.setImageResource(resId);
		} else {
			imageSwitcher.setImageDrawable(new BitmapDrawable(bitmap));
		}
	}
	
	/**
	 * @param path
	 * @param callback
	 * @return
	 */
	
	public Bitmap loadImageAsyn(String path, ImageCallback callback) {
		//�жϻ������Ƿ��Ѿ����ڸ�ͼƬ
		if (caches.containsKey(path)) {
			//ȡ��������
			SoftReference<Bitmap> rf = caches.get(path);
			//ͨ�������ã���ȡͼƬ
			Bitmap bitmap = rf.get();
			//�����ͼƬ�Ѿ����ͷţ��򽫸�path��Ӧ�ļ���Map���Ƴ���
			if (bitmap == null) {
				caches.remove(path);
			} else {
				//���ͼƬδ���ͷţ�ֱ�ӷ��ظ�ͼƬ
				Log.d(TAG, "return iamge in cache: " + path);
				return bitmap;
			}
		} else {
			//��������в����ڸ�ͼƬ���򴴽�ͼƬ��������
			Task task = new Task();
			task.path = path;
			task.callback = callback;
			Log.d(TAG, "new Task , " + path);
			if (!taskQueue.contains(task)) {
				taskQueue.add(task);
				//�����������ض���
				synchronized (runnable) {
					runnable.notify();
				}
			}
		}
		//������û��ͼƬ�򷵻�null
		return null;
	}
	
	/**
	 * @param iamgeView
	 * @param resId ͼƬ�������ǰ��ʾ��ͼƬ��ԴID
	 * @return
	 */
	private ImageCallback getImageCallback(final ImageSwitcher imageSwitcher, final int resId) {
		return new ImageCallback() {
			
			@Override
			public void loadImage(String path, Bitmap bitmap) {
				// TODO Auto-generated method stub
				if (path.equals(imageSwitcher.getTag().toString())) {
					imageSwitcher.setImageDrawable(new BitmapDrawable(bitmap));
				} else {
					imageSwitcher.setImageResource(resId);
				}
			}
		};
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			//���߳��з��صļ�����ɵ�����
			Task task = (Task)msg.obj;
			//����callback�����loadImage����������ͼƬ·����ͼƬ�ش���adapter
			task.callback.loadImage(task.path, task.bitmap);
		}
	};
	
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(isRunning) {
				//�������л���δ���������ʱ��ִ�м�������
				while(taskQueue.size() > 0) {
					byte[] mByteContent = null;
					//��ȡ��һ������,����֮�����������ɾ��
					Task task = taskQueue.remove(0);
					//��Ҫ���ص�ͼƬ��ӵ�����
					//task.bitmap = ImageUtil.getbitmap(task.path);
					try {
						mByteContent = ImageUtil.readInputStream(mContent.getContentResolver().openInputStream(Uri.parse(task.path)));
						task.bitmap = ImageUtil.getBitmapFromBytes(mByteContent, null);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					caches.put(task.path, new SoftReference<Bitmap>(task.bitmap));
					if (handler != null) {
						//������ϸ���󣬲�����ɵ�������ӵ���Ϣ������
						Message msg = handler.obtainMessage();
						msg.obj = task;
						handler.sendMessage(msg);
					}
				}
				
				//�������Ϊ�գ������̵߳ȴ�
				synchronized (this) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}
		
	};
	
	//�ص��ӿ�
	public interface ImageCallback {
		void loadImage(String path, Bitmap bitmap);
	}
	
	private class Task {
		//��������ļ���·��
		String path;
		//���ص�ͼƬ
		Bitmap bitmap;
		//�ص�����
		ImageCallback callback;
		
		public boolean equals(Object o) {
			Task task = (Task)o;
			return task.path.equals(path);
		}
	}
}
