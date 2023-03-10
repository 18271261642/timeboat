package com.imlaidian.okhttp.download;

import java.io.File;

public interface DownloadTaskListener {


    /**
     * 开始下载
     *
     * @param downloadTask
     */
    void onDownloadBegin(DownloadTask downloadTask);

    /**
     * 下载中
     *
     * @param completedSize
     * @param totalSize
     * @param percent
     * @param downloadTask
     */
    void onDownloading(DownloadTask downloadTask, long completedSize, long totalSize, String percent);

    /**
     * 下载暂停
     *
     * @param downloadTask
     * @param completedSize
     * @param totalSize
     * @param percent
     */
    void onPause(DownloadTask downloadTask, long completedSize, long totalSize, String percent);

    /**
     * 下载取消
     *
     * @param downloadTask
     */
    void onCancel(DownloadTask downloadTask);

    /**
     * 下载成功
     *
     * @param file
     * @param downloadTask
     */
    void onDownloadSuccess(DownloadTask downloadTask, File file);

    /**
     * 下载失败
     *
     * @param downloadTask
     * @param errorCode    {@link DownloadStatus}
     */
    void onError(DownloadTask downloadTask, int errorCode, int repeatTimes);


}
