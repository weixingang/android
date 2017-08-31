package com.seu.magicfilter.camera;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.seu.magicfilter.model.FilterMediaObject;
import com.seu.magicfilter.model.MediaObject;
import com.seu.magicfilter.utils.FileUtils;
import com.seu.magicfilter.utils.StringUtils;
import com.seu.magicfilter.utils.Util;

import java.io.File;
import java.io.IOException;

import static com.seu.magicfilter.camera.utils.CameraUtils.adaptFpsRange;
import static com.seu.magicfilter.camera.utils.CameraUtils.adaptPictureSize;
import static com.seu.magicfilter.camera.utils.CameraUtils.adaptPreviewSize;

public class CameraEngine {
    public static final int RECORD_WIDTH = 480, RECORD_HEIGHT = 640;
    private static long startTime;
    private boolean cameraOpened;
    /** 摄像头参数 */
    public static Camera camera = null;
    public static boolean iscamera = false;
    /** 摄像头参数 */
    protected static Parameters parameters = null;
    public static int cameraID = 0;
    private static SurfaceTexture surfaceTexture;
    private static SurfaceView surfaceView;
    public static MediaObject mMediaObject ;
    public static FilterMediaObject mFilterMediaObject;
    private static MediaObject.MediaPart result;
    public static int  filterType;   //滤镜选择
    public static  String outputFile = "";
    /** 视频码率 */
    protected static int mVideoBitrate = 2048;
    public static Camera getCamera() {
        return camera;
    }

    public static  boolean   isturn_sound =true;  //是否有声音视频
    public static Object objectLock = new Object();




    /**
     * 设置视频临时存储文件夹
     *
     * @param key 视频输出的名称，同目录下唯一，一般取系统当前时间
     * @param path 文件夹路径
     * @return 录制信息对象
     */
    public static MediaObject setOutputDirectory(String key, String path) {
        if (StringUtils.isNotEmpty(path)) {
            File f = new File(path);
            if (f != null) {
                if (f.exists()) {
                    //已经存在，删除
                    if (f.isDirectory())
                        FileUtils.deleteDir(f);
                    else
                        FileUtils.deleteFile(f);
                }

                if (f.mkdirs()) {
                    mMediaObject = new MediaObject(key, path, mVideoBitrate);
                }
            }
        }
        return mMediaObject;
    }
    /**
     * 设置视频临时存储文件夹
     *
     * @param key 视频输出的名称，同目录下唯一，一般取系统当前时间
     * @param path 文件夹路径
     * @return 录制信息对象
     */
    public static FilterMediaObject setFilterOutputDirectory(String key, String path) {
        if (StringUtils.isNotEmpty(path)) {
            File f = new File(path);
            if (f != null) {
                if (f.exists()) {
                    //已经存在，删除
                    if (f.isDirectory())
                        FileUtils.deleteDir(f);
                    else
                        FileUtils.deleteFile(f);
                }

                if (f.mkdirs()) {
                    mFilterMediaObject = new FilterMediaObject(key, path, mVideoBitrate);
                }
            }
        }
        return mFilterMediaObject;
    }
    /** 设置视频信息 */
    public void setMediaObject(MediaObject mediaObject) {
        this.mMediaObject = mediaObject;
    }

    /** 回删 */
    public static boolean backRemove() {
        if (mMediaObject != null && mMediaObject.mediaList != null) {
            int size = mMediaObject.mediaList.size();
            if (size > 0) {
                MediaObject.MediaPart part = (MediaObject.MediaPart) mMediaObject.mediaList.get(size - 1);
                mMediaObject.removePart(part, true);

                if (mMediaObject.mediaList.size() > 0)
                    mMediaObject.mCurrentPart = (MediaObject.MediaPart) mMediaObject.mediaList.get(mMediaObject.mediaList.size() - 1);
                else
                    mMediaObject.mCurrentPart = null;
                return true;
            }
        }
        if (mFilterMediaObject != null && mFilterMediaObject.mediaList != null) {
            int size = mFilterMediaObject.mediaList.size();
            if (size > 0) {
                FilterMediaObject.MediaPart part = (FilterMediaObject.MediaPart) mFilterMediaObject.mediaList.get(size - 1);
                mFilterMediaObject.removePart(part, true);

                if (mFilterMediaObject.mediaList.size() > 0)
                    mFilterMediaObject.mCurrentPart = (FilterMediaObject.MediaPart) mFilterMediaObject.mediaList.get(mFilterMediaObject.mediaList.size() - 1);
                else
                    mMediaObject.mCurrentPart = null;
                return true;
            }
        }
        return false;
    }

    public static boolean openCamera() {
        if (camera == null) {
            try {
                camera = Camera.open(cameraID);
                setDefaultParameters();
                return true;
            } catch (RuntimeException e) {
                return false;
            }
        }
        return false;
    }

    public static boolean openCamera(int id) {
        if (camera == null) {
            try {
                camera = Camera.open(id);
                cameraID = id;
                setDefaultParameters();
                return true;
            } catch (RuntimeException e) {
                return false;
            }
        }
        return false;
    }

    public static void releaseCamera() {

        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public void resumeCamera() {
        openCamera();
    }

    public void setParameters(Parameters parameters) {
        camera.setParameters(parameters);
    }

    public Parameters getParameters() {
        if (camera != null)
            camera.getParameters();
        return null;
    }

    public static void switchCamera() {
        releaseCamera();
        cameraID = cameraID == 0 ? 1 : 0;
        openCamera(cameraID);
        startPreview(surfaceTexture);
    }

    private static void setDefaultParameters() {
        parameters = camera.getParameters();
        if (parameters.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        //设置帧Fps区间
        int[] range = adaptFpsRange(24, parameters);
        parameters.setPreviewFpsRange(range[0], range[1]);

        //设置大小
//        Size previewSize = adaptPreviewSize(parameters);
//        parameters.setPreviewSize(previewSize.width, previewSize.height);
        Size pictureSize = adaptPictureSize(parameters);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);

        parameters.setRotation(90);
        //设置放大倍数
//        parameters.setZoom(22);
        camera.setParameters(parameters);
    }

    private static Size getPreviewSize() {
        return camera.getParameters().getPreviewSize();
    }

    private static Size getPictureSize() {
        return camera.getParameters().getPictureSize();
    }

    public static void startPreview(SurfaceTexture surfaceTexture) {
        if (camera != null)
            try {
                camera.setPreviewTexture(surfaceTexture);
                CameraEngine.surfaceTexture = surfaceTexture;
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void startPreview() {
        if (camera != null)
            camera.startPreview();
    }

    public static void stopPreview() {
        camera.stopPreview();
        // stopChronometer();
        releaseMediaRecorder();
    }

    public static void setRotation(int rotation) {
        Camera.Parameters params = camera.getParameters();
        params.setRotation(rotation);
        camera.setParameters(params);
    }

    public static void takePicture(Camera.ShutterCallback shutterCallback, Camera.PictureCallback rawCallback,
                                   Camera.PictureCallback jpegCallback) {
        camera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }

    public static com.seu.magicfilter.camera.utils.CameraInfo getCameraInfo() {
        com.seu.magicfilter.camera.utils.CameraInfo info = new com.seu.magicfilter.camera.utils.CameraInfo();
        Size size = getPreviewSize();
        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(cameraID, cameraInfo);
        info.previewWidth = size.width;
        info.previewHeight = size.height;
        info.orientation = cameraInfo.orientation;
        info.isFront = cameraID == 1;
        size = getPictureSize();
        info.pictureWidth = size.width;
        info.pictureHeight = size.height;
        return info;
    }

    /**
     * 设置闪光灯
     *
     * @param value
     */
    private static boolean setFlashMode(String value) {
        if (parameters != null &&camera != null) {
            try {
                if (Camera.Parameters.FLASH_MODE_TORCH.equals(value) || Camera.Parameters.FLASH_MODE_OFF.equals(value)) {
                    parameters.setFlashMode(value);
                    camera.setParameters(parameters);
                }
                return true;
            } catch (Exception e) {
                Log.e("XBN", "setFlashMode", e);
            }
        }
        return false;
    }
    /**
     * 切换闪关灯，默认关闭
     */
    public static boolean toggleFlashMode() {
        if (parameters != null) {
            try {
                final String mode = parameters.getFlashMode();
                if (TextUtils.isEmpty(mode) || Camera.Parameters.FLASH_MODE_OFF.equals(mode))
                    setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                else
                    setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                return true;
            } catch (Exception e) {
                Log.e("XBN", "toggleFlashMode", e);
            }
        }
        return false;
    }
    /**
     * 判断是否前置摄像头
     */
    public static boolean isFrontCamera() {
        return cameraID == Camera.CameraInfo.CAMERA_FACING_FRONT;
    }
    /** 是否支持前置摄像头 */
    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean isSupportFrontCamera() {
        if (!hasGingerbread()) {
            return false;
        }
        int numberOfCameras = android.hardware.Camera.getNumberOfCameras();
        if (2 == numberOfCameras) {
            return true;
        }
        return false;
    }
    /** >=2.3 */
    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }
    /**
     * 远近焦距
     * @param progress
     */
    public static void setZoom(int progress) {
        if (parameters != null &&camera != null) {
            parameters.setZoom(progress);
            camera.setParameters(parameters);
            //(int) (progress * 1.0f / (40 * 100) * 40)
        }
    }
    /**
     * 曝光度
     * @param exposure
     */
    public static void setExposure(int exposure) {
        if (parameters != null &&camera != null) {
            parameters.setExposureCompensation(exposure);
            camera.setParameters(parameters);
        }
    }
    /**
     * 曝光度
     * @param seekBar
     */
    public static  void onStopTrackingTouch(SeekBar seekBar) {
        int seekProgress = seekBar.getProgress();
        if (parameters != null &&camera != null) {
            if (seekProgress < 13) {
                seekBar.setProgress(0);
                parameters.setExposureCompensation(-12);
                Log.d("exposure", "exposure -2");
            } else if (seekProgress >= 13 && seekProgress < 38) {
                seekBar.setProgress(25);
                parameters.setExposureCompensation(-6);
                Log.d("exposure", "exposure -1");
            } else if (seekProgress >= 38 && seekProgress < 63) {
                seekBar.setProgress(50);
                parameters.setExposureCompensation(0);
                Log.d("exposure", "exposure normal");
            } else if (seekProgress >= 63 && seekProgress < 88) {
                seekBar.setProgress(75);
                parameters.setExposureCompensation(+6);
                Log.d("exposure", "exposure +1");
            } else if (seekProgress >= 88) {
                seekBar.setProgress(100);
                parameters.setExposureCompensation(+12);
                Log.d("exposure", "exposure +2");
            }
            camera.setParameters(parameters);
        }
    }

    /**
     * 曝光度  -12  0  +12
     * @param seekProgress
     */
    public static void onStopTrackingTouch(int seekProgress) {
        Camera.Parameters mparameters = camera.getParameters();
        if (seekProgress < 13) {
            mparameters.setExposureCompensation(-12);
            Log.d("exposure", "exposure -2");
        } else if (seekProgress >= 13 && seekProgress < 38) {
            mparameters.setExposureCompensation(-6);
            Log.d("exposure", "exposure -1");
        } else if (seekProgress >= 38 && seekProgress < 63) {
            mparameters.setExposureCompensation(0);
            Log.d("exposure", "exposure normal");
        } else if (seekProgress >= 63 && seekProgress < 88) {
            mparameters.setExposureCompensation(+6);
            Log.d("exposure", "exposure +1");
        } else if (seekProgress >= 88) {
            mparameters.setExposureCompensation(+12);
            Log.d("exposure", "exposure +2");
        }
        camera.setParameters(mparameters);
    }
    public static void onStopTrackingTouchProgress(int seekProgress) {
        Camera.Parameters mparameters = camera.getParameters();
        if(seekProgress < 13){
            mparameters.setExposureCompensation(+2);
            Log.d("exposure","exposure -2");
        }else if(seekProgress >=13 && seekProgress < 38){
            mparameters.setExposureCompensation(+2);
            Log.d("exposure","exposure -1");
        }else if(seekProgress >=38 && seekProgress < 63){
            mparameters.setExposureCompensation(0);
            Log.d("exposure","exposure normal");
        }else if(seekProgress >=63 && seekProgress < 88){
            mparameters.setExposureCompensation(-2);
            Log.d("exposure","exposure +1");
        }else if(seekProgress >=88){
            mparameters.setExposureCompensation(-2);
            Log.d("exposure","exposure +2");
        }
        camera.setParameters(mparameters);
    }



    /**
     * 录制视频时间计时器
     */
    public static long countUp;
    public static long time_count;
    private static long time_dan;

    //计时器
    public static void startChronometer(final Chronometer textChrono,final ImageView chronoRecordingImage) {
        textChrono.setVisibility(View.VISIBLE);
        final long startTime = SystemClock.elapsedRealtime();
        textChrono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer arg0) {
                time_dan = (SystemClock.elapsedRealtime() - startTime) / 1000;
                countUp = (SystemClock.elapsedRealtime() - startTime) / 1000;
                countUp = (time_count+countUp);
                if (countUp % 2 == 0) {
                    chronoRecordingImage.setVisibility(View.VISIBLE);
                } else {
                    chronoRecordingImage.setVisibility(View.INVISIBLE);
                }

                String asText = String.format("%02d", countUp / 60) + ":" + String.format("%02d", countUp % 60);
                textChrono.setText(asText);
            }
        });
        textChrono.start();
    }

    public static void stopChronometer(Chronometer textChrono,ImageView chronoRecordingImage) {
        chronoRecordingImage.setVisibility(View.VISIBLE);
        time_count = countUp;
        textChrono.stop();
        if(countUp>1) {
            // 判断数据是否处理完，处理完了关闭输出流
            result = mMediaObject.buildMediaPart(cameraID, ".mp4");
            result.recording = false;
            result.startTime = startTime;
            result.endTime = System.currentTimeMillis();
            result.duration = (int) (result.endTime - result.startTime);
            result.cutStartTime = 0;
            result.cutEndTime = result.duration;
            result.time_duration = time_dan;
            result.mFilterType = filterType;
            result.cameraId = cameraID;
            result.mediaPathFilter = outputFile; //滤镜文件路径
            result.mediaPath = outputFile;
        }
    }


    /**
     * 录制视频
     */
    private static MediaRecorder mediaRecorder;
    private static boolean cameraFront = false;
    public static String url_file;
    private static boolean recording = false;

    public static boolean prepareMediaRecorder() {
        try {

            if (mediaRecorder == null) {
                mediaRecorder = new MediaRecorder();
            } else {
                mediaRecorder.reset();
            }
            camera.unlock();
            mediaRecorder.setCamera(camera);
            if(isturn_sound) {
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            }
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            if(camera!=null) {
                if (cameraID == CameraInfo.CAMERA_FACING_BACK) {
                    mediaRecorder.setOrientationHint(90);
                } else {
                    mediaRecorder.setOrientationHint(270);
                }
            }
//        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//            if (cameraFront) {
//                mediaRecorder.setOrientationHint(270);
//            } else {
//                mediaRecorder.setOrientationHint(90);
//            }
//        }
            final CamcorderProfile profile = CamcorderProfile.get(Camera.CameraInfo.CAMERA_FACING_BACK, CamcorderProfile.QUALITY_HIGH);
            mediaRecorder.setOutputFormat(profile.fileFormat);
            // 第3步:设置输出格式和编码格式(针对低于API Level 8版本)Stagefright
            // mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

            //设置视频输出的格式和编码
            CamcorderProfile mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
            //                mMediaRecorder.setProfile(mProfile);
            mediaRecorder.setVideoSize(640, 480);//after setVideoSource(),after setOutFormat()
            mediaRecorder.setAudioEncodingBitRate(44100);
            if (mProfile.videoBitRate > 2 * 1024 * 1024)
                mediaRecorder.setVideoEncodingBitRate(2 * 1024 * 1024);
            else
                mediaRecorder.setVideoEncodingBitRate(mProfile.videoBitRate);
            mediaRecorder.setVideoFrameRate(mProfile.videoFrameRate);//after setVideoSource(),after setOutFormat()

            if(isturn_sound) {
                //音频编码格式对应应为AAC
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            }
            //视频编码格式对应应为H264
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            File file1 = getOutputMediaFile();
            if (file1.exists()) {
                file1.delete();
            }
            mediaRecorder.setOutputFile(file1.toString());

            //    mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
      /*  mediaRecorder.setVideoSize(640, 480);// 视频尺寸
        mediaRecorder.setVideoFrameRate(30);// 视频帧频率
        mediaRecorder.setVideoEncodingBitRate(3 * 1024 * 1024);*/

            //   mediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            releaseRecorder();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            releaseRecorder();
            return false;
        }
        return true;

    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile() {

        result = mMediaObject.buildMediaPart(cameraID, ".mp4");
        url_file = result.mediaPath;
        Log.i("filePath", url_file);
        return new File(url_file);
    }

    /**
     * Create a File for saving an image or video
     */
    public static void saveBuildMediaPart() {
        startTime = System.currentTimeMillis();
    }


    private static void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            camera.lock();
            camera.startPreview();
        }
    }

    public static  void releaseRecorder() {
        if (mediaRecorder != null) {
            //if (mIsRecording) {
            try {
                mediaRecorder.stop();
            } catch (Throwable t) {
                //noinspection ResultOfMethodCallIgnored
                //new File(mOutputUri).delete();
                t.printStackTrace();
            }
            //mIsRecording = false;
            //}
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            camera.startPreview();
        }
    }
    public  static void  stopRecording(){
        mediaRecorder.stop(); //停止
        releaseMediaRecorder();
        recording = false;
    }

    /**
     * 開始錄製
     * @return
     */
    public static boolean startRecordingVideo() {
        try {
            mediaRecorder.start();
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return false;
    }
    public  void  startRecording(){

      /*  runOnUiThread(new Runnable() {
            public void run() {
                // If there are stories, add them to the table
                try {*/
        mediaRecorder.start(); //开始
               /* } catch (final Exception ex) {
                    Log.i("---", "Exception in thread");
                    releaseMediaRecorder();
                }
            }
        });*/
        recording = true;
    }
    public static void setAutoFocus() {
        if(parameters.getSupportedFocusModes().contains("continuous-video")) {
            parameters.setFocusMode("continuous-video");
        }

        camera.setParameters(parameters);
    }
    public static boolean onTouch(MotionEvent var1, Camera.AutoFocusCallback var2) {
        camera.cancelAutoFocus();
        parameters.setFocusMode("macro");
        camera.setParameters(parameters);
        camera.autoFocus(var2);
        return true;
    }

    public static void calculateTapArea(int var1, int var2, float var3, int var4, int var5, int var6, int var7, Rect var8) {
        var1 = (int)((float)var1 * var3);
        var2 = (int)((float)var2 * var3);
        int var9 = Util.clamp(var4 - var1 / 2, 0, var6 - var1);
        var4 = Util.clamp(var5 - var2 / 2, 0, var7 - var2);
        Util.rectFToRect(new RectF((float)var9, (float)var4, (float)(var9 + var1 > 1000?1000:var9 + var1), (float)(var4 + var2 > 1000?1000:var4 + var2)), var8);
    }

}