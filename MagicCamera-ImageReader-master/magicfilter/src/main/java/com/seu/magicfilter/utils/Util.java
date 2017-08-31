package com.seu.magicfilter.utils;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class Util {
    public static final int ORIENTATION_HYSTERESIS = 5;
    public static final String REVIEW_ACTION = "com.android.camera.action.REVIEW";
    private static float a = 1.0F;

    private Util() {
    }

    public static void initialize(Context var0) {
        DisplayMetrics var1 = new DisplayMetrics();
        ((WindowManager)var0.getSystemService("window")).getDefaultDisplay().getMetrics(var1);
        a = var1.density;
    }

    public static int dpToPixel(int var0) {
        return Math.round(a * (float)var0);
    }

    public static Bitmap rotate(Bitmap var0, int var1) {
        return rotateAndMirror(var0, var1, false);
    }

    public static Bitmap rotateAndMirror(Bitmap var0, int var1, boolean var2) {
        if((var1 != 0 || var2) && var0 != null) {
            Matrix var3 = new Matrix();
            if(var2) {
                var3.postScale(-1.0F, 1.0F);
                if((var1 = (var1 + 360) % 360) != 0 && var1 != 180) {
                    if(var1 != 90 && var1 != 270) {
                        throw new IllegalArgumentException("Invalid degrees=" + var1);
                    }

                    var3.postTranslate((float)var0.getHeight(), 0.0F);
                } else {
                    var3.postTranslate((float)var0.getWidth(), 0.0F);
                }
            }

            if(var1 != 0) {
                var3.postRotate((float)var1, (float)var0.getWidth() / 2.0F, (float)var0.getHeight() / 2.0F);
            }

            try {
                Bitmap var5 = Bitmap.createBitmap(var0, 0, 0, var0.getWidth(), var0.getHeight(), var3, true);
                if(var0 != var5) {
                    var0.recycle();
                    var0 = var5;
                }
            } catch (OutOfMemoryError var4) {
                ;
            }
        }

        return var0;
    }

    public static int computeSampleSize(Options var0, int var1, int var2) {
        double var4 = (double)var0.outWidth;
        double var6 = (double)var0.outHeight;
        int var8 = var2 < 0?1:(int)Math.ceil(Math.sqrt(var4 * var6 / (double)var2));
        int var10000 = var1 < 0?128:(int)Math.min(Math.floor(var4 / (double)var1), Math.floor(var6 / (double)var1));
        int var3 = var10000;
        if((var8 = var10000 < var8?var8:(var2 < 0 && var1 < 0?1:(var1 < 0?var8:var3))) <= 8) {
            for(var1 = 1; var1 < var8; var1 <<= 1) {
                ;
            }
        } else {
            var1 = (var8 + 7) / 8 * 8;
        }

        return var1;
    }

    public static Bitmap makeBitmap(byte[] var0, int var1) {
        try {
            Options var2;
            (var2 = new Options()).inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(var0, 0, var0.length, var2);
            if(!var2.mCancel && var2.outWidth != -1 && var2.outHeight != -1) {
                var2.inSampleSize = computeSampleSize(var2, -1, var1);
                var2.inJustDecodeBounds = false;
                var2.inDither = false;
                var2.inPreferredConfig = Config.ARGB_8888;
                return BitmapFactory.decodeByteArray(var0, 0, var0.length, var2);
            } else {
                return null;
            }
        } catch (OutOfMemoryError var3) {
            Log.e("Util", "Got oom exception ", var3);
            return null;
        }
    }

    public static void closeSilently(Closeable var0) {
        if(var0 != null) {
            try {
                var0.close();
            } catch (Throwable var1) {
                ;
            }
        }
    }

    public static void Assert(boolean var0) {
        if(!var0) {
            throw new AssertionError();
        }
    }


    public static Object checkNotNull(Object var0) {
        if(var0 == null) {
            throw new NullPointerException();
        } else {
            return var0;
        }
    }

    public static boolean equals(Object var0, Object var1) {
        return var0 == var1 || var0 != null && var0.equals(var1);
    }

    public static int nextPowerOf2(int var0) {
        --var0;
        return (var0 | var0 >>> 16 | (var0 | var0 >>> 16) >>> 8 | (var0 | var0 >>> 16 | (var0 | var0 >>> 16) >>> 8) >>> 4 | (var0 | var0 >>> 16 | (var0 | var0 >>> 16) >>> 8 | (var0 | var0 >>> 16 | (var0 | var0 >>> 16) >>> 8) >>> 4) >>> 2 | (var0 | var0 >>> 16 | (var0 | var0 >>> 16) >>> 8 | (var0 | var0 >>> 16 | (var0 | var0 >>> 16) >>> 8) >>> 4 | (var0 | var0 >>> 16 | (var0 | var0 >>> 16) >>> 8 | (var0 | var0 >>> 16 | (var0 | var0 >>> 16) >>> 8) >>> 4) >>> 2) >>> 1) + 1;
    }

    public static float distance(float var0, float var1, float var2, float var3) {
        var0 -= var2;
        var1 -= var3;
        return (float) Math.sqrt(var0 * var0 + var1 * var1);
    }

    public static int clamp(int var0, int var1, int var2) {
        return var0 > var2?var2:(var0 < var1?var1:var0);
    }

    public static int getDisplayRotation(Activity var0) {
        switch(var0.getWindowManager().getDefaultDisplay().getRotation()) {
        case 0:
            return 0;
        case 1:
            return 90;
        case 2:
            return 180;
        case 3:
            return 270;
        default:
            return 0;
        }
    }

    public static int getDisplayOrientation(int var0, int var1) {
        CameraInfo var2 = new CameraInfo();
        Camera.getCameraInfo(var1, var2);
        if(var2.facing == 1) {
            var0 = (var2.orientation + var0) % 360;
            var0 = (360 - var0) % 360;
        } else {
            var0 = (var2.orientation - var0 + 360) % 360;
        }

        return var0;
    }

    public static int getCameraOrientation(int var0) {
        CameraInfo var1 = new CameraInfo();
        Camera.getCameraInfo(var0, var1);
        return var1.orientation;
    }

    public static int roundOrientation(int var0, int var1) {
        int var2;
        return (var1 == -1?true:Math.min(var2 = Math.abs(var0 - var1), 360 - var2) >= 50)?(var0 + 45) / 90 * 90 % 360:var1;
    }

   
    public static Size getOptimalVideoSnapshotPictureSize(List var0, double var1) {
        if(var0 == null) {
            return null;
        } else {
            Size var3 = null;
            Iterator var5 = var0.iterator();

            while(true) {
                Size var4;
                do {
                    do {
                        if(!var5.hasNext()) {
                            if(var3 == null) {
                                Log.w("Util", "No picture size match the aspect ratio");
                                var5 = var0.iterator();

                                while(true) {
                                    do {
                                        if(!var5.hasNext()) {
                                            return var3;
                                        }

                                        var4 = (Size)var5.next();
                                    } while(var3 != null && var4.width <= var3.width);

                                    var3 = var4;
                                }
                            }

                            return var3;
                        }
                    } while(Math.abs((double)(var4 = (Size)var5.next()).width / (double)var4.height - var1) > 0.001D);
                } while(var3 != null && var4.width <= var3.width);

                var3 = var4;
            }
        }
    }

    public static void dumpParameters(Parameters var0) {
        String var1 = var0.flatten();
        StringTokenizer var2 = new StringTokenizer(var1, ";");
        Log.d("Util", "Dump all camera parameters:");

        while(var2.hasMoreElements()) {
            Log.d("Util", var2.nextToken());
        }

    }

    public static boolean isMmsCapable(Context var0) {
        TelephonyManager var6;
        if((var6 = (TelephonyManager)var0.getSystemService("phone")) == null) {
            return false;
        } else {
            try {
                Class[] var1 = new Class[0];
                Method var7 = TelephonyManager.class.getMethod("isVoiceCapable", var1);
                Object[] var2 = new Object[0];
                return ((Boolean)var7.invoke(var6, var2)).booleanValue();
            } catch (InvocationTargetException var3) {
                ;
            } catch (IllegalAccessException var4) {
                ;
            } catch (NoSuchMethodException var5) {
                ;
            }

            return true;
        }
    }

    public static int getCameraFacingIntentExtras(Activity var0) {
        var0.getIntent().getIntExtra("android.intent.extras.CAMERA_FACING", -1);
        return -1;
    }



    public static boolean isUriValid(Uri var0, ContentResolver var1) {
        if(var0 == null) {
            return false;
        } else {
            try {
                ParcelFileDescriptor var3;
                if((var3 = var1.openFileDescriptor(var0, "r")) == null) {
                    Log.e("Util", "Fail to open URI. URI=" + var0);
                    return false;
                } else {
                    var3.close();
                    return true;
                }
            } catch (IOException var2) {
                return false;
            }
        }
    }

    public static void viewUri(Uri var0, Context var1) {
        if(!isUriValid(var0, var1.getContentResolver())) {
            Log.e("Util", "Uri invalid. uri=" + var0);
        } else {
            try {
                var1.startActivity(new Intent("com.android.camera.action.REVIEW", var0));
            } catch (ActivityNotFoundException var3) {
                try {
                    var1.startActivity(new Intent("android.intent.action.VIEW", var0));
                } catch (ActivityNotFoundException var2) {
                    Log.e("Util", "review image fail. uri=" + var0, var2);
                }
            }
        }
    }

    public static void dumpRect(RectF var0, String var1) {
        Log.v("Util", var1 + "=(" + var0.left + "," + var0.top + "," + var0.right + "," + var0.bottom + ")");
    }

    public static void rectFToRect(RectF var0, Rect var1) {
        var1.left = Math.round(var0.left);
        var1.top = Math.round(var0.top);
        var1.right = Math.round(var0.right);
        var1.bottom = Math.round(var0.bottom);
    }

    public static void prepareMatrix(Matrix var0, boolean var1, int var2, int var3, int var4) {
        var0.setScale((float)(var1?-1:1), 1.0F);
        var0.postRotate((float)var2);
        var0.postScale((float)var3 / 2000.0F, (float)var4 / 2000.0F);
        var0.postTranslate((float)var3 / 2.0F, (float)var4 / 2.0F);
    }

 
    public static void broadcastNewPicture(Context var0, Uri var1) {
        var0.sendBroadcast(new Intent("android.hardware.action.NEW_PICTURE", var1));
        var0.sendBroadcast(new Intent("com.android.camera.NEW_PICTURE", var1));
    }

    public static void fadeIn(View var0, float var1, float var2, long var3) {
        if(var0.getVisibility() != 0) {
            var0.setVisibility(0);
            AlphaAnimation var5;
            (var5 = new AlphaAnimation(var1, var2)).setDuration(var3);
            var0.startAnimation(var5);
        }
    }

    public static void fadeIn(View var0) {
        fadeIn(var0, 0.0F, 1.0F, 400L);
        var0.setEnabled(true);
    }

    public static void fadeOut(View var0) {
        if(var0.getVisibility() == 0) {
            var0.setEnabled(false);
            AlphaAnimation var1;
            (var1 = new AlphaAnimation(1.0F, 0.0F)).setDuration(400L);
            var0.startAnimation(var1);
            var0.setVisibility(8);
        }
    }

    public static int getJpegRotation(int var0, int var1) {
        return 0;
    }

    public static void setGpsParameters(Parameters var0, Location var1) {
        var0.removeGpsData();
        var0.setGpsTimestamp(System.currentTimeMillis() / 1000L);
        if(var1 != null) {
            double var2 = var1.getLatitude();
            double var4 = var1.getLongitude();
            if(var2 != 0.0D || var4 != 0.0D) {
                Log.d("Util", "Set gps location");
                var0.setGpsLatitude(var2);
                var0.setGpsLongitude(var4);
                var0.setGpsProcessingMethod(var1.getProvider().toUpperCase());
                if(var1.hasAltitude()) {
                    var0.setGpsAltitude(var1.getAltitude());
                } else {
                    var0.setGpsAltitude(0.0D);
                }

                if(var1.getTime() != 0L) {
                    long var6 = var1.getTime() / 1000L;
                    var0.setGpsTimestamp(var6);
                }
            }
        }

    }
    
    public static  ContentValues videoContentValues = null;

    public static String getRecordingTimeFromMillis(long millis)
	{
		String strRecordingTime = null;
		int seconds = (int) (millis / 1000);
		int minutes = seconds / 60;
		int hours = minutes / 60;

		if(hours >= 0 && hours < 10)
			strRecordingTime = "0" + hours + ":";
		else
			strRecordingTime = hours + ":";

		if(hours > 0)
			minutes = minutes % 60;

		if(minutes >= 0 && minutes < 10)
			strRecordingTime += "0" + minutes + ":";
		else
			strRecordingTime += minutes + ":";

		seconds = seconds % 60;

		if(seconds >= 0 && seconds < 10)
			strRecordingTime += "0" + seconds ;
		else
			strRecordingTime += seconds ;

		return strRecordingTime;

	}


	public static int determineDisplayOrientation(Activity activity, int defaultCameraId) {
		int displayOrientation = 0;
		if(Build.VERSION.SDK_INT >  Build.VERSION_CODES.FROYO)
		{
			CameraInfo cameraInfo = new CameraInfo();
			Camera.getCameraInfo(defaultCameraId, cameraInfo);

			int degrees  = getRotationAngle(activity);


			if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
				displayOrientation = (cameraInfo.orientation + degrees) % 360;
				displayOrientation = (360 - displayOrientation) % 360;
			} else {
				displayOrientation = (cameraInfo.orientation - degrees + 360) % 360;
			}
		}
		return displayOrientation;
	}

	public static int getRotationAngle(Activity activity)
	{
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int degrees  = 0;

		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;

		case Surface.ROTATION_90:
			degrees = 90;
			break;

		case Surface.ROTATION_180:
			degrees = 180;
			break;

		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}
		return degrees;
	}

	public static int getRotationAngle(int rotation)
	{
		int degrees  = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;

		case Surface.ROTATION_90:
			degrees = 90;
			break;

		case Surface.ROTATION_180:
			degrees = 180;
			break;

		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}
		return degrees;
	}
}
