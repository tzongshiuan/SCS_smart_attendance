package com.gorilla.attendance.enterprise.datamodel;

import java.util.ArrayList;

/**
 * Created by ggshao on 2017/2/13.
 */

public class VideoDataModel {
    public static final String KEY_TOTAL_COUNTS = "totalCounts";
    public static final String KEY_VIDEOS = "videos";

    private int totalCounts;
    private ArrayList<VideosModel> videos = new ArrayList<VideosModel>();

    public int getTotalCounts() {
        return totalCounts;
    }
    public void setTotalCounts(int totalCounts) {
        this.totalCounts = totalCounts;
    }

    public ArrayList<VideosModel> getVideos() {
        return videos;
    }
    public void setVideos(ArrayList<VideosModel> videos) {
        this.videos = videos;
    }

    public void addVideos(VideosModel videos) {
        this.videos.add(videos);
    }
}
