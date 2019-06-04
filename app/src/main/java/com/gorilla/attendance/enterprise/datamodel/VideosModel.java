package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/2/13.
 */

public class VideosModel {
    public static final String KEY_NAME = "name";
    public static final String KEY_URL = "url";
    public static final String KEY_THUMB_URL = "thumbUrl";
    public static final String KEY_PRIORITY = "priority";
    public static final String KEY_LENGTH = "length";
    public static final String KEY_FILE_SIZE = "fileSize";

    private String mName;
    private String mUrl;
    private String mThumbUrl;
    private String mPriority;
    private String mLength;
    private String mFileSize;


    public String getName() {
        return mName;
    }
    public void setName(String name) {
        this.mName = name;
    }

    public String getUrl() {
        return mUrl;
    }
    public void setUrl(String url) {
        this.mUrl = url;
    }

    public String getThumbUrl() {
        return mThumbUrl;
    }
    public void setThumbUrl(String thumbUrl) {
        this.mThumbUrl = thumbUrl;
    }

    public String getPriority() {
        return mPriority;
    }
    public void setPriority(String priority) {
        this.mPriority = priority;
    }

    public String getLength() {
        return mLength;
    }
    public void setLength(String length) {
        this.mLength = length;
    }

    public String getFileSize() {
        return mFileSize;
    }
    public void setFileSize(String fileSize) {
        this.mFileSize = fileSize;
    }
}
