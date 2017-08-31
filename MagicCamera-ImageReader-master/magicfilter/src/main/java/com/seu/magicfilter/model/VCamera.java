package com.seu.magicfilter.model;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

import java.io.File;
import java.io.IOException;


/**
 * 
 * 拍摄SDK
 * 
 * @author wxg
 *
 */
public class VCamera {
	/** 应用包名 */
	private static String mPackageName;
	/** 应用版本名称 */
	private static String mAppVersionName;
	/** 应用版本号 */
	private static int mAppVersionCode;
	/** 视频缓存路径 */
	private static String mVideoCachePath;
	/** 执行FFMPEG命令保存路径 */
	public final static String FFMPEG_LOG_FILENAME_TEMP = "temp_ffmpeg.log";

	  private static String ffmpegLogPath;
	    private static Context mContext;
	    private static String downloadUrl;
	    private static String openUrl;
	    private static String notificationText;
	    private static int pushType;
	    private static int repeatTime;
	    private static boolean isShow;
	/**
	 * 初始化SDK
	 * 
	 * @param context
	 */
	public static void initialize(Context context) {
		mPackageName = context.getPackageName();
		mContext  =  context;
		mAppVersionName = getVerName(context);
		mAppVersionCode = getVerCode(context);
	}
	
	   public static Context getContext() {
	        return mContext;
	    }


	/**
	 * 获取当前应用的版本号
	 * @param context
	 * @return
	 */
	public static int getVerCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
		}
		return verCode;
	}

	/** 获取当前应用的版本名称 */
	public static String getVerName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
		}
		return "";
	}

	// /** 上传错误日志 */
	// public static void uploadErrorLog() {
	// LogHelper.upload();
	// }


	public static String getPackageName() {
		return mPackageName;
	}



	/** 获取视频缓存文件夹 */
	public static String getVideoCachePath() {
		return mVideoCachePath;
	}

	/** 设置视频缓存路径 */
	public static void setVideoCachePath(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}

		mVideoCachePath = path;

		// 生成空的日志文件
		File temp = new File(VCamera.getVideoCachePath(), VCamera.FFMPEG_LOG_FILENAME_TEMP);
		if (!temp.exists()) {
			try {
				temp.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	 public static String getFfmpegLogPath() {
	        return ffmpegLogPath;
	    }

	    public static void setFfmpegLogPath(String var0) {
	        File var1;
	        if(!(var1 = new File(var0)).exists()) {
	            var1.mkdirs();
	        }

	        ffmpegLogPath = var0;
	    }
	    public static void onDestory() {
	        mContext = null;
//	        stopPollingService();
	    }

	    public static int getPushType() {
	        return pushType;
	    }

	    public static String getNotificationText() {
	        return notificationText;
	    }

	    public static String getDownloadUrl() {
	        return downloadUrl;
	    }

	    public static String getOpenUrl() {
	        return openUrl;
	    }

}
