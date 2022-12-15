package com.imlaidian.okhttp.download;

/**
 * Created by zbo on 17/7/12.
 */

public class DownLoadSourceInfoModel {
    private int id ;
    private String taskId ;
    private  String url;
    private  long length;
    private  String mime;
    private String md5 ;
    private  String timeStamp ;
    private  String adReleaseId ;
    private  String materialId ;
    private  String saveFilePath ;
    private  String saveFileName ;
    private  int downloadStatus ;
    private  long completedSize ;
    private  int repeatTime ;
    private  String ip ;
    private  int type ;
    private  String reserve1 ;
    private  String reserve2 ;
    private  String reserve3 ;
    private  String reserve4 ;

    public DownLoadSourceInfoModel() {

    }

    public DownLoadSourceInfoModel(int type ,String taskId, String url,String ip ,long length, long completedSize , String md5, String saveFilePath, String saveFileName, int downloadStatus ,int repeatTime ,String timeStamp) {
        this.type =type ;
        this.taskId = taskId;
        this.url = url;
        this.length = length;
        this.md5 = md5;
        this.saveFilePath = saveFilePath;
        this.saveFileName = saveFileName;
        this.downloadStatus = downloadStatus;
        this.completedSize = completedSize;
        this.repeatTime = repeatTime ;
        this.timeStamp =  timeStamp ;
        this.ip = ip;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getSaveFileName() {
        return saveFileName;
    }

    public void setSaveFileName(String saveFileName) {
        this.saveFileName = saveFileName;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public String getSaveFilePath() {
        return saveFilePath;
    }

    public void setSaveFilePath(String saveFilePath) {
        this.saveFilePath = saveFilePath;
    }

    public long getCompletedSize() {
        return completedSize;
    }

    public void setCompletedSize(long completedSize) {
        this.completedSize = completedSize;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getAdReleaseId() {
        return adReleaseId;
    }

    public void setAdReleaseId(String adReleaseId) {
        this.adReleaseId = adReleaseId;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public int getRepeatTime() {
        return repeatTime;
    }

    public void setRepeatTime(int repeatTime) {
        this.repeatTime = repeatTime;
    }

    public String getReserve1() {
        return reserve1;
    }

    public void setReserve1(String reserve1) {
        this.reserve1 = reserve1;
    }

    public String getReserve2() {
        return reserve2;
    }

    public void setReserve2(String reserve2) {
        this.reserve2 = reserve2;
    }

    public String getReserve3() {
        return reserve3;
    }

    public void setReserve3(String reserve3) {
        this.reserve3 = reserve3;
    }

    public String getReserve4() {
        return reserve4;
    }

    public void setReserve4(String reserve4) {
        this.reserve4 = reserve4;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
