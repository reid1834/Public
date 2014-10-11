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
	//缓存读取过的图片的Map
	private Map<String, SoftReference<Bitmap>> caches;
	//任务队列
	private List<Task> taskQueue;
	private boolean isRunning = false;
	private Context mContent;
	
	public AsynImageLoader(Context content) {
		//初始化变量
		mContent = content;
		caches = new HashMap<String, SoftReference<Bitmap>>();
		taskQueue = new ArrayList<AsynImageLoader.Task>();
		//启动图片加载线程
		isRunning = true;
		new Thread(runnable).start();
	}
	
	/**
	 * param imageView 需要延迟加载图片的对象
	 * param url 图片的URL地址
	 * param resId 图片加载过程中显示的图片资源
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
		//判断缓存中是否已经存在该图片
		if (caches.containsKey(path)) {
			//取出软引用
			SoftReference<Bitmap> rf = caches.get(path);
			//通过软引用，获取图片
			Bitmap bitmap = rf.get();
			//如果该图片已经被释放，则将该path对应的键从Map中移除掉
			if (bitmap == null) {
				caches.remove(path);
			} else {
				//如果图片未被释放，直接返回该图片
				Log.d(TAG, "return iamge in cache: " + path);
				return bitmap;
			}
		} else {
			//如果缓存中不存在该图片，则创建图片加载任务
			Task task = new Task();
			task.path = path;
			task.callback = callback;
			Log.d(TAG, "new Task , " + path);
			if (!taskQueue.contains(task)) {
				taskQueue.add(task);
				//唤醒任务下载队列
				synchronized (runnable) {
					runnable.notify();
				}
			}
		}
		//缓存中没有图片则返回null
		return null;
	}
	
	/**
	 * @param iamgeView
	 * @param resId 图片加载完成前显示的图片资源ID
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
			//子线程中返回的加载完成的任务
			Task task = (Task)msg.obj;
			//调用callback对象的loadImage方法，并将图片路径和图片回传给adapter
			task.callback.loadImage(task.path, task.bitmap);
		}
	};
	
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(isRunning) {
				//当队列中还有未处理的任务时，执行加载任务
				while(taskQueue.size() > 0) {
					byte[] mByteContent = null;
					//获取第一个任务,并将之从任务队列中删除
					Task task = taskQueue.remove(0);
					//将要加载的图片添加到缓存
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
						//创建纤细对象，并将完成的任务添加到消息对象中
						Message msg = handler.obtainMessage();
						msg.obj = task;
						handler.sendMessage(msg);
					}
				}
				
				//如果队列为空，则令线程等待
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
	
	//回调接口
	public interface ImageCallback {
		void loadImage(String path, Bitmap bitmap);
	}
	
	private class Task {
		//加载任务的加载路径
		String path;
		//加载的图片
		Bitmap bitmap;
		//回调对象
		ImageCallback callback;
		
		public boolean equals(Object o) {
			Task task = (Task)o;
			return task.path.equals(path);
		}
	}
}
