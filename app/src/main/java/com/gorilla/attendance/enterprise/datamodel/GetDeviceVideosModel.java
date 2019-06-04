package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/3/20.
 */

public class GetDeviceVideosModel {
    public static final String KEY_STATUS = "status";
    public static final String KEY_DATA = "data";

    private String status;
    private VideoDataModel videoData;

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public VideoDataModel getVideoData() {
        return videoData;
    }
    public void setVideoData(VideoDataModel videoData) {
        this.videoData = videoData;
    }
}
