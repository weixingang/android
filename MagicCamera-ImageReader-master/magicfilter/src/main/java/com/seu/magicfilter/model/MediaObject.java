//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.seu.magicfilter.model;

import com.seu.magicfilter.utils.FileUtils;
import com.seu.magicfilter.utils.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

public class MediaObject implements Serializable {
    public static final int MEDIA_PART_TYPE_RECORD = 0;
    public static final int MEDIA_PART_TYPE_IMPORT_VIDEO = 1;
    public static final int MEDIA_PART_TYPE_IMPORT_IMAGE = 2;
    public static final int MEDIA_PART_TYPE_RECORD_MP4 = 3;
    public static final int MEDIA_PART_TYPE_IMPORT_VIDEO_ONLINE = 4;
    public static final int DEFAULT_MAX_DURATION = 10000;
    public static final int DEFAULT_VIDEO_BITRATE = 800;
    public int mMediaType;
    public int mMaxDuration;
    public int mMaxTimeDuration;
    public String mOutputDirectory;
    public String mOutputObjectPath;
    public  String mediaPathFilter;
    public int mVideoBitrate;
    private String mOutputVideoPath;
    private String mOutputVideoThumbPath;
    private String mKey;
    public transient volatile MediaPart mCurrentPart;
    public LinkedList mediaList;
    public TextViewParams textViewParams;
    public LinkedList listTvParams;
    public int cropX;
    public int cropY;
    public int videoWidth;
    public int videoHeight;
    public int mVideoRotation;
    public float scale;
    public boolean mIsFitCenter;
    public boolean mIsWhiteBackground;
    public String outputTempVideoPath="";



    public TextViewParams getTextViewParams() {
        return textViewParams;
    }

    public void setTextViewParams(TextViewParams textViewParams) {
        this.textViewParams = textViewParams;
    }

    public void setOutputTempVideoPath(String outputTempVideoPath) {
            this.outputTempVideoPath = outputTempVideoPath;
    }

    public MediaObject() {
        this.mMediaType = 0;
        this.mMaxDuration = 120000;
        this.mediaList = new LinkedList();
        this.listTvParams = new LinkedList();
    }

    public MediaObject(String var1, String var2) {
        this(var1, var2, 800);
    }

    public MediaObject(String var1, String var2, int var3) {
        this.mMediaType = 0;
        this.mMaxDuration = 10000;
        this.mediaList = new LinkedList();
        this.listTvParams = new LinkedList();
        this.mKey = var1;
        this.mOutputDirectory = var2;
        this.mVideoBitrate = var3;
        this.mOutputObjectPath = this.mOutputDirectory + File.separator + this.mKey + ".obj";
        this.mOutputVideoPath = this.mOutputDirectory + ".mp4";
        this.mOutputVideoThumbPath = this.mOutputDirectory + ".jpg";
        this.mMaxDuration = 10000;
    }

    public MediaObject(String var1, String var2, int var3, int var4) {
        this.mMediaType = 0;
        this.mMaxDuration = 10000;
        this.mediaList = new LinkedList();
        this.listTvParams = new LinkedList();
        this.mKey = var2;
        this.mOutputDirectory = var1 + var2;
        this.mVideoBitrate = var3;
        this.mOutputObjectPath = this.mOutputDirectory + File.separator + this.mKey + ".obj";
        this.mOutputVideoPath = this.mOutputDirectory + ".mp4";
        this.mOutputVideoThumbPath = this.mOutputDirectory + ".jpg";
        this.mMaxDuration = 10000;
        this.mMediaType = var4;
    }

    public int getVideoBitrate() {
        return this.mVideoBitrate;
    }

    public int getMaxDuration() {
        return this.mMaxDuration;
    }

    public void setMaxDuration(int var1) {
        if(var1 >= 1000) {
            this.mMaxDuration = var1;
        }

    }

    public String getOutputDirectory() {
        return this.mOutputDirectory;
    }

    public String getOutputTempVideoPath() {
        if(outputTempVideoPath.equals("")) {
            return this.mOutputDirectory + File.separator + this.mKey + ".mp4";
        }else{
            return this.outputTempVideoPath;
        }

    }

    public void cleanTheme() {
        MediaPart var1;
        if(this.mediaList != null) {
            for(Iterator var2 = this.mediaList.iterator(); var2.hasNext(); var1.cutEndTime = var1.duration) {
                (var1 = (MediaPart)var2.next()).cutStartTime = 0;
            }
        }

    }

    public String getObjectFilePath() {
        if(StringUtils.isEmpty(this.mOutputObjectPath)) {
            File var1 = new File(this.mOutputVideoPath);
            String var2 = this.mOutputDirectory + File.separator + var1.getName() + ".obj";
            this.mOutputObjectPath = var2;
        }

        return this.mOutputObjectPath;
    }
    public String getObjectFilePathTs() {
    	 String var2 = this.mOutputDirectory + File.separator + this.mediaList.size() + ".ts";
        if(StringUtils.isEmpty(var2)) {
            File var1 = new File(this.mediaList.get(this.mediaList.size()).toString());
          //  String var2 = this.mOutputDirectory + File.separator + this.mediaList.size() + ".ts";
//            this.mOutputObjectPath = var2;
        }

        return var2;
    }
    public String getOutputVideoPath() {
        return this.mOutputVideoPath;
    }

    public String getOutputVideoThumbPath() {
        return this.mOutputVideoThumbPath;
    }

    public int getDuration() {
        int var1 = 0;
        MediaPart var2;
        if(this.mediaList != null) {
            for(Iterator var3 = this.mediaList.iterator(); var3.hasNext(); var1 += var2.getDuration()) {
                var2 = (MediaPart)var3.next();
            }
        }

        return var1;
    }
    public long getTimeDuration() {
        int var1 = 0;
        MediaPart var2;
        if(this.mediaList != null) {
            for(Iterator var3 = this.mediaList.iterator(); var3.hasNext(); var1 += var2.getTimeDuration()) {
                var2 = (MediaPart)var3.next();
            }
        }

        return var1;
    }

    public int getCutDuration() {
        int var1 = 0;
        int var4;
        if(this.mediaList != null) {
            for(Iterator var3 = this.mediaList.iterator(); var3.hasNext(); var1 += var4) {
                MediaPart var2;
                var4 = (var2 = (MediaPart)var3.next()).cutEndTime - var2.cutStartTime;
                if(var2.speed != 10) {
                    var4 = (int)((float)var4 * (10.0F / (float)var2.speed));
                }
            }
        }

        return var1;
    }

    public void removePart(MediaPart var1, boolean var2) {
        if(this.mediaList != null) {
            this.mediaList.remove(var1);
        }

        if(var1 != null) {
            var1.stop();
            if(var2) {
                var1.delete();
            }

            this.mediaList.remove(var1);
        }

    }

    public MediaPart buildMediaPart(int var1) {
        this.mCurrentPart = new MediaPart();
        this.mCurrentPart.position = this.getDuration();
        this.mCurrentPart.index = this.mediaList.size();
        this.mCurrentPart.mediaPath = this.mOutputDirectory + File.separator + this.mCurrentPart.index + ".v";
        this.mCurrentPart.audioPath = this.mOutputDirectory + File.separator + this.mCurrentPart.index + ".a";
        this.mCurrentPart.thumbPath = this.mOutputDirectory + File.separator + this.mCurrentPart.index + ".jpg";
        this.mCurrentPart.cameraId = var1;
        this.mCurrentPart.prepare();
        this.mCurrentPart.recording = true;
        this.mCurrentPart.startTime = System.currentTimeMillis();
        this.mCurrentPart.type = 1;
        this.mediaList.add(this.mCurrentPart);
        return this.mCurrentPart;
    }

    public MediaPart buildMediaPart(int var1, String var2) {
        this.mCurrentPart = new MediaPart();
        this.mCurrentPart.position = this.getDuration();
        this.mCurrentPart.index = this.mediaList.size();
        this.mCurrentPart.mediaPath = this.mOutputDirectory + File.separator + System.currentTimeMillis() + var2;
        this.mCurrentPart.mediaPathFilter = this.mOutputDirectory + File.separator + System.currentTimeMillis()+"_filter"+ var2;
        this.mCurrentPart.audioPath = this.mOutputDirectory + File.separator + System.currentTimeMillis() + ".a";
        this.mCurrentPart.thumbPath = this.mOutputDirectory + File.separator + System.currentTimeMillis() + ".jpg";
        this.mCurrentPart.recording = true;
        this.mCurrentPart.cameraId = var1;
        this.mCurrentPart.startTime = System.currentTimeMillis();
        this.mCurrentPart.type = 1;
        this.mediaList.add(this.mCurrentPart);
        return this.mCurrentPart;
    }

    public MediaPart buildMediaPart(String var1, int var2, int var3) {
        this.mCurrentPart = new MediaPart();
        this.mCurrentPart.position = this.getDuration();
        this.mCurrentPart.index = this.mediaList.size();
        this.mCurrentPart.mediaPath = this.mOutputDirectory + File.separator + System.currentTimeMillis() + ".v";
        this.mCurrentPart.audioPath = this.mOutputDirectory + File.separator + System.currentTimeMillis() + ".a";
        this.mCurrentPart.thumbPath = this.mOutputDirectory + File.separator + System.currentTimeMillis() + ".jpg";
        this.mCurrentPart.duration = var2;
        this.mCurrentPart.startTime = 0L;
        this.mCurrentPart.endTime = (long)var2;
        this.mCurrentPart.cutStartTime = 0;
        this.mCurrentPart.cutEndTime = var2;
        this.mCurrentPart.tempPath = var1;
        this.mCurrentPart.type = var3;
        this.mediaList.add(this.mCurrentPart);
        return this.mCurrentPart;
    }

    public String getConcatYUV() {
        StringBuilder var1 = new StringBuilder();
        if(this.mediaList != null && this.mediaList.size() > 0) {
            if(this.mediaList.size() == 1) {
                if(StringUtils.isEmpty(((MediaPart)this.mediaList.get(0)).tempMediaPath)) {
                    var1.append(((MediaPart)this.mediaList.get(0)).mediaPath);
                } else {
                    var1.append(((MediaPart)this.mediaList.get(0)).tempMediaPath);
                }
            } else {
                var1.append("concat:");
                int var2 = 0;

                for(int var3 = this.mediaList.size(); var2 < var3; ++var2) {
                    MediaPart var4;
                    if(StringUtils.isEmpty((var4 = (MediaPart)this.mediaList.get(var2)).tempMediaPath)) {
                        var1.append(var4.mediaPath);
                    } else {
                        var1.append(var4.tempMediaPath);
                    }

                    if(var2 + 1 < var3) {
                        var1.append("|");
                    }
                }
            }
        }

        return var1.toString();
    }

    public String getConcatPCM() {
        StringBuilder var1 = new StringBuilder();
        if(this.mediaList != null && this.mediaList.size() > 0) {
            if(this.mediaList.size() == 1) {
                if(StringUtils.isEmpty(((MediaPart)this.mediaList.get(0)).tempAudioPath)) {
                    var1.append(((MediaPart)this.mediaList.get(0)).audioPath);
                } else {
                    var1.append(((MediaPart)this.mediaList.get(0)).tempAudioPath);
                }
            } else {
                var1.append("concat:");
                int var2 = 0;

                for(int var3 = this.mediaList.size(); var2 < var3; ++var2) {
                    MediaPart var4;
                    if(StringUtils.isEmpty((var4 = (MediaPart)this.mediaList.get(var2)).tempAudioPath)) {
                        var1.append(var4.audioPath);
                    } else {
                        var1.append(var4.tempAudioPath);
                    }

                    if(var2 + 1 < var3) {
                        var1.append("|");
                    }
                }
            }
        }

        return var1.toString();
    }

    public MediaPart getCurrentPart() {
        if(this.mCurrentPart != null) {
            return this.mCurrentPart;
        } else {
            if(this.mediaList != null && this.mediaList.size() > 0) {
                this.mCurrentPart = (MediaPart)this.mediaList.get(this.mediaList.size() - 1);
            }

            return this.mCurrentPart;
        }
    }

    public int getCurrentIndex() {
        MediaPart var1;
        return (var1 = this.getCurrentPart()) != null?var1.index:0;
    }

    public MediaPart getPart(int var1) {
        return this.mCurrentPart != null && var1 < this.mediaList.size()?(MediaPart)this.mediaList.get(var1):null;
    }

    public void delete() {
        if(this.mediaList != null) {
            Iterator var1 = this.mediaList.iterator();

            while(var1.hasNext()) {
                ((MediaPart)var1.next()).stop();
            }
        }

        FileUtils.deleteDir(this.mOutputDirectory);
    }

    public LinkedList getMedaParts() {
        return this.mediaList;
    }

    public static void preparedMediaObject(MediaObject var0) {
        if(var0 != null && var0.mediaList != null) {
            int var1 = 0;

            MediaPart var3;
            for(Iterator var2 = var0.mediaList.iterator(); var2.hasNext(); var1 += var3.duration) {
                (var3 = (MediaPart)var2.next()).startTime = (long)var1;
                var3.endTime = var3.startTime + (long)var3.duration;
            }
        }

    }

    public String toString() {
        StringBuffer var1 = new StringBuffer();
        if(this.mediaList != null) {
            var1.append("[" + this.mediaList.size() + "]");
            Iterator var3 = this.mediaList.iterator();

            while(var3.hasNext()) {
                MediaPart var2 = (MediaPart)var3.next();
                var1.append(var2.mediaPath + ":" + var2.duration + "\n");
            }
        }

        return var1.toString();
    }

    public void cancel() {
        if(this.mediaList != null) {
            Iterator var1 = this.mediaList.iterator();

            while(var1.hasNext()) {
                ((MediaPart)var1.next()).stop();
            }

            FileUtils.deleteDir(this.mOutputDirectory);
        }

    }

    public String getKey() {
        if(StringUtils.isEmpty(this.mKey)) {
            this.mKey = (new File(this.mOutputDirectory)).getName();
        }

        return this.mKey;
    }

    /*public static boolean writeFile(MediaObject var0) {
        try {
            if(StringUtils.isNotEmpty(var0.getObjectFilePath())) {
                FileOutputStream var1 = new FileOutputStream(var0.getObjectFilePath());
                Gson var2 = new Gson();
                var1.write(var2.toJson(var0).getBytes());
                var1.flush();
                var1.close();
                return true;
            }
        } catch (Exception var3) {
            Log.e("VCamera", "writeFile", var3);
        }

        return false;
    }*/
   /* public static boolean writeFileTs(MediaObject var0) {
        try {
            if(StringUtils.isNotEmpty(var0.getObjectFilePathTs())) {
                FileOutputStream var1 = new FileOutputStream(var0.getObjectFilePathTs());
                Gson var2 = new Gson();
                var1.write(var2.toJson(var0).getBytes());
                var1.flush();
                var1.close();
                return true;
            }
        } catch (Exception var3) {
            Log.e("VCamera", "writeFile", var3);
        }

        return false;
    }*/
    public MediaPart getLastPart() {
        return this.mediaList != null && this.mediaList.size() > 0?(MediaPart)this.mediaList.get(this.mediaList.size() - 1):null;
    }

    public MediaPart buildMediaPartOnline(long var1) {
        this.mCurrentPart = new MediaPart();
        this.mCurrentPart.position = this.getDuration();
        this.mCurrentPart.index = this.mediaList.size();
        this.mCurrentPart.mediaPath = this.mOutputDirectory + File.separator + this.mCurrentPart.index + ".ts";
        this.mCurrentPart.audioPath = this.mOutputDirectory + File.separator + this.mCurrentPart.index + ".a";
        this.mCurrentPart.thumbPath = this.mOutputDirectory + File.separator + this.mCurrentPart.index + ".jpg";
        this.mCurrentPart.recording = true;
        this.mCurrentPart.startTime = var1;
        this.mCurrentPart.type = 4;
        this.mCurrentPart.timestamp = System.currentTimeMillis();
        this.mediaList.add(this.mCurrentPart);
        return this.mCurrentPart;
    }

   /* public static MediaObject readFile(String var0) {
        try {
            var0 = FileUtils.readFile(new File(var0));
            MediaObject var2;
            if((var2 = (MediaObject)(new Gson()).fromJson(var0.toString(), MediaObject.class)) != null) {
                var2.mCurrentPart = var2.getLastPart();
            }

            preparedMediaObject(var2);
            return var2;
        } catch (Exception var1) {
            Log.e("VCamera", "readFile", var1);
            return null;
        }
    }*/
    

	public static class MediaPart implements Serializable {
		  public long timestamp;
		    public int index;
		    public String mediaPath;
            public String mediaPathFilter;
            public String reversifyPath="";
            public String reversifyPathFilter="";
		    public String audioPath;
		    public String tempMediaPath;
		    public String tempAudioPath;
		    public String thumbPath;
		    public String tempPath;

		    public int type = 0;
		    public int cutStartTime;
		    public int cutEndTime;
            public int ishighlight=-1;
            public int  isreversify = -1;
            public int  isreversifyFilter = -1;

		    public int duration;
		    public int position;
            public long time_duration=0;
            public int mFilterType=0;  //选择滤镜
		    public int speed = 10;
		    public int cameraId;
		    public int yuvWidth;
		    public int yuvHeight;
		    public transient boolean remove;
		    public transient long startTime;
		    public transient long endTime;
		    public transient FileOutputStream mCurrentOutputVideo;
		    public transient FileOutputStream mCurrentOutputAudio;
		    public transient volatile boolean recording;
		    public transient float scale;
		    public transient float cropX;
		    public transient float cropY;
		    public transient float width;
		    public transient float height;

		    public MediaPart() {
		    }

		    public void delete() {
		        FileUtils.deleteFile(this.mediaPath);
		        FileUtils.deleteFile(this.audioPath);
		        FileUtils.deleteFile(this.thumbPath);
		        FileUtils.deleteFile(this.tempMediaPath);
		        FileUtils.deleteFile(this.tempAudioPath);
                FileUtils.deleteFile(this.mediaPathFilter);
		    }

		    public void writeAudioData(byte[] var1) {
		        if(this.mCurrentOutputAudio != null) {
		            try {
						this.mCurrentOutputAudio.write(var1);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }

		    }

		    public void writeVideoData(byte[] var1) {
		        if(this.mCurrentOutputVideo != null) {
		            try {
						this.mCurrentOutputVideo.write(var1);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }

		    }

		    public void prepare() {
		        try {
		            this.mCurrentOutputVideo = new FileOutputStream(this.mediaPath);
		        } catch (IOException var1) {
		            var1.printStackTrace();
		        }

		        this.prepareAudio();
		    }

		    public void prepareAudio() {
		        try {
		            this.mCurrentOutputAudio = new FileOutputStream(this.audioPath);
		        } catch (IOException var1) {
		            var1.printStackTrace();
		        }
		    }

		    public int getDuration() {
		        return this.duration > 0?this.duration:(int)(System.currentTimeMillis() - this.startTime);
		    }
        public long getTimeDuration() {
            return this.time_duration;
        }
		    public void stop() {
		        if(this.mCurrentOutputVideo != null) {
		            try {
		                this.mCurrentOutputVideo.flush();
		                this.mCurrentOutputVideo.close();
		            } catch (IOException var2) {
		                var2.printStackTrace();
		            }

		            this.mCurrentOutputVideo = null;
		        }

		        if(this.mCurrentOutputAudio != null) {
		            try {
		                this.mCurrentOutputAudio.flush();
		                this.mCurrentOutputAudio.close();
		            } catch (IOException var1) {
		                var1.printStackTrace();
		            }

		            this.mCurrentOutputAudio = null;
		        }

		    }

	}


}
