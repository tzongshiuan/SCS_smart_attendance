package com.gorilla.attendance.enterprise.datamodel;

/**
 * Created by ggshao on 2017/2/13.
 */

public class GetVideoModel {
    public static final String KEY_STATUS = "status";
    public static final String KEY_DATA = "data";

    private String status;
    private VideoDataModel videoData;

    private boolean isFromWebSocket = false;

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

    public boolean getIsFromWebSocket() {
        return isFromWebSocket;
    }
    public void setIsFromWebSocket(boolean isFromWebSocket) {
        this.isFromWebSocket = isFromWebSocket;
    }


}
