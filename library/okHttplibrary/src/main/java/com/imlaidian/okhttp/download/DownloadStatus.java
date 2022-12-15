package com.imlaidian.okhttp.download;

public class DownloadStatus {
    /**
     * init download
     */
    public static final int DOWNLOAD_STATUS_INIT = 1;

    /**
     * begin download
     */
    public static final int DOWNLOAD_STATUS_BEGIN_DOWNLOADING = 2;

    /**
     * downloading
     */
    public static final int DOWNLOAD_STATUS_DOWNLOADING = 3;
    /**
     * download cancel
     */
    public static final int DOWNLOAD_STATUS_CANCEL = 4;
    /**
     * download error
     */
    public static final int DOWNLOAD_STATUS_ERROR = 5;
    /**
     * download finish
     */
    public static final int DOWNLOAD_STATUS_COMPLETED = 6;
    /**
     * download pause
     */
    public static final int DOWNLOAD_STATUS_PAUSE = 7;
    /**
     * download error file not found
     */
    public static final int DOWNLOAD_ERROR_FILE_NOT_FOUND = 8;
    /**
     * download error io exception
     */
    public static final int DOWNLOAD_ERROR_IO_ERROR = 9;


}
