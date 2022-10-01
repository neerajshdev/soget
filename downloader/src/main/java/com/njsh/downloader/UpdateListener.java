package com.njsh.downloader;

public class UpdateListener {
    public void onUpdateSpeed(int bytesPerSec) {}
    public void onUpdateProgress(long complete, long total) {}
    public void onDownloadFished() {}
}
