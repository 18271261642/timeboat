package com.imlaidian.ldclog;

import java.util.HashMap;
import java.util.Map;

public class LdUploadItem {

    boolean isMultipleApp =false; // 是否 多个app :c/s 模式的
    // 是否删除源文件
    boolean delSourceFile =false ;
    //是否删除要上传的文件
    boolean delUploadFile =false ;
    //是否立即上传
    boolean uploadNow = true;
    UploadFileType uploadFileType;
    String uploadPath ;
    //上传的文件名
    String fileName ;
    //需要上传的文件路径
    Map<String,String> filePathList = new HashMap<String,String>();
    int uploadTaskId ;

    public UploadFileType getUploadFileType() {
        return uploadFileType;
    }

    public void setUploadFileType(UploadFileType uploadFileType) {
        this.uploadFileType = uploadFileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Map<String,String>  getFilePathList() {
        return filePathList;
    }

    public void setFilePathList(Map<String,String>  filePath) {
        this.filePathList = filePath;
    }

    public int getUploadTaskId() {
        return uploadTaskId;
    }

    public void setUploadTaskId(int uploadTaskId) {
        this.uploadTaskId = uploadTaskId;
    }

    public boolean isMultipleApp() {
        return isMultipleApp;
    }

    public void setMultipleApp(boolean multipleApp) {
        isMultipleApp = multipleApp;
    }

    public boolean isDelUploadFile() {
        return delUploadFile;
    }

    public void setDelUploadFile(boolean delUploadFile) {
        this.delUploadFile = delUploadFile;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public boolean isUploadNow() {
        return uploadNow;
    }

    public void setUploadNow(boolean uploadNow) {
        this.uploadNow = uploadNow;
    }
}
