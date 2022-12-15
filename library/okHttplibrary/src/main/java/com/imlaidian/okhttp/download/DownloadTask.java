/*
 * Copyright 2014-2016 wjokhttp
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.imlaidian.okhttp.download;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.imlaidian.okhttp.https.HttpsUtils;
import com.imlaidian.utilslibrary.utils.LogUtil;
import com.imlaidian.utilslibrary.utils.MainBoardVersionTools;
import com.imlaidian.utilslibrary.utils.StringsUtils;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.imlaidian.okhttp.model.OkHttpLinkStatusModel.DNS_REQUEST_FAILED;
import static com.imlaidian.okhttp.model.OkHttpLinkStatusModel.DNS_REQUEST_SUCCESS;
import static com.imlaidian.okhttp.model.OkHttpLinkStatusModel.NETTY_DNS_ERROR_CLOSED;
import static com.imlaidian.utilslibrary.config.PublicConstant.DNS_REQUEST_TYPE;
import static com.imlaidian.utilslibrary.config.PublicConstant.NORMAL_REQUEST_TYPE;


/**
 * 下载线程
 */
public class DownloadTask implements Runnable {

    private final String TAG = DownloadTask.class.getSimpleName() ;
    private static String FILE_MODE = "rwd";
    private OkHttpClient mClient =null;

    private RandomAccessFile mDownLoadFile;
    private DownLoadSourceInfoModel mDownLoadInfo;
    private DownloadTaskListener mListener;

    private Builder mBuilder;
    private String taskId;// task id
    private long totalSize;// filesize
    private String md5 ;
    private long completedSize; //  Download section has been completed
    private String url="";// file url
    private String saveDirPath;// file save path
    private double updateSize; // The database is updated 100 times
    private String fileName; // File name when saving
    private int downloadStatus;
    private int repeatTimes = 0 ; // task downLoad repeat times
    private int errorCode;
    private String  ip="" ;
    private int type ;
    private String timestamp="" ;



    private DownloadTask(Builder builder) {
        mBuilder = builder;
        this.taskId = mBuilder.taskId;
        LogUtil.d(TAG ,"=====run start taskId ===== " + taskId);
        this.url = mBuilder.url;
        this.saveDirPath = mBuilder.saveDirPath;
        this.fileName = mBuilder.fileName;
        this.downloadStatus = mBuilder.downloadStatus;
        this.mListener = mBuilder.listener;
        this.md5 = mBuilder.md5 ;
        if(mBuilder.timestamp!=null && !mBuilder.timestamp.equals("")){
            this.timestamp = mBuilder.timestamp;
        }else{
            this.timestamp = "" + System.currentTimeMillis();
        }

        this.ip =mBuilder.ip;
        this.type = mBuilder.type ;
        // 以kb为计算单位
    }

    @Override
    public void run() {
        if(mClient ==null){
            mClient = getOkhttpClient();
        }

        LogUtil.d(TAG ,"=====run start download ===== " + totalSize);
        downloadStatus = DownloadStatus.DOWNLOAD_STATUS_BEGIN_DOWNLOADING;
        onCallBack();
        InputStream inputStream = null;
        BufferedInputStream bis = null;
        try {
            // 数据库中加载数据
            mDownLoadInfo = DownLoadDatabaseManager.getInstance().getInfoByTaskId(type ,taskId);
            if (mDownLoadInfo != null) {
                completedSize = mDownLoadInfo.getCompletedSize();
                totalSize = mDownLoadInfo.getLength();
            }else{
                // 查找url对应表类型下的最后一条记录
                mDownLoadInfo = DownLoadDatabaseManager.getInstance().getLastTaskInfo(type ,url);
                if(mDownLoadInfo !=null){
                    completedSize = mDownLoadInfo.getCompletedSize();
                    totalSize = mDownLoadInfo.getLength();
                    mDownLoadInfo.setTaskId(taskId);
                }
            }

            // 获得文件路径
            String filepath = getFilePath();
            // 获得下载保存文件
            mDownLoadFile = new RandomAccessFile(filepath, FILE_MODE);

            long fileLength = mDownLoadFile.length();
            LogUtil.d(TAG ,"=====fileLength= " + fileLength  + ",completedSize=" +completedSize + ",totalSize=" +totalSize + "========");
            if (fileLength < completedSize) {
                completedSize = mDownLoadFile.length();
            }


            // 下载完成，更新数据库数据
            if (fileLength != 0  && totalSize <= fileLength && totalSize >0) {
                downloadStatus = DownloadStatus.DOWNLOAD_STATUS_COMPLETED;
                totalSize = completedSize = fileLength;
                LogUtil.d(TAG ,"==finish===type= " + type  + ",taskId=" +taskId + ",url=" +url + ",repeatTimes=" +  repeatTimes+ "========");
                LogUtil.d(TAG ,"==finish====fileName= " + fileName  + ",saveDirPath=" +saveDirPath + ",downloadStatus=" +downloadStatus + "========");
                updateDownLoadInfo(type ,taskId ,totalSize ,url ,fileName ,saveDirPath ,downloadStatus ,repeatTimes );
                DownLoadDatabaseManager.getInstance().putInfoByTaskId(type ,taskId ,mDownLoadInfo );
                LogUtil.d(TAG ,"==finish====totalSize===== " + totalSize);
                // 执行finally中的回调
                return;
            }
            // 文件跳转到指定位置开始写入
            mDownLoadFile.seek(completedSize);
            // 开始下载
//            Request request = new Request.Builder().url(url).header("RANGE",
//                "bytes=" + completedSize + "-") // Http value set breakpoints RANGE
//                .build();
//
//            Response response = mClient.newCall(request).execute();
            Response response = getRespondByRequestType(NORMAL_REQUEST_TYPE) ;
            if(response!=null && response.isSuccessful()){
                ResponseBody responseBody = response.body();

                if (responseBody != null) {
                    downloadStatus = DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING;
                    //onCallBack();
                    if (totalSize <= 0) {
                        totalSize = responseBody.contentLength();
                    }

                    updateSize = totalSize / 100;

                    // 获得文件流
                    inputStream = responseBody.byteStream();
                    bis = new BufferedInputStream(inputStream);
                    byte[] buffer = new byte[2 * 1024];
                    int length = 0;
                    int buffOffset = 0;
                    // 开始下载数据库中插入下载信息
                    if (mDownLoadInfo == null) {
                        LogUtil.d(TAG ,"==start===type= " + type  + ",taskId=" +taskId + ",url=" +url + "========");
                        LogUtil.d(TAG ,"==start====ip= " + ip  + ",md5=" +md5 + ",repeatTimes=" +repeatTimes + "========");
                        LogUtil.d(TAG ,"==start====fileName= " + fileName  + ",saveDirPath=" +saveDirPath + ",downloadStatus=" +downloadStatus + "========");
                        mDownLoadInfo = new DownLoadSourceInfoModel(type ,taskId,
                                url ,
                                ip,
                                totalSize,
                                0L,
                                md5,
                                saveDirPath,
                                fileName,
                                downloadStatus ,
                                repeatTimes ,
                                (timestamp==null || timestamp.equals("") ) ?  ("" +System.currentTimeMillis()) :timestamp);
                        DownLoadDatabaseManager.getInstance().putInfoByTaskId(type ,taskId ,mDownLoadInfo);
                    }
                    while ((length = bis.read(buffer)) > 0 && downloadStatus != DownloadStatus.DOWNLOAD_STATUS_CANCEL
                            && downloadStatus != DownloadStatus.DOWNLOAD_STATUS_PAUSE) {
                        mDownLoadFile.write(buffer, 0, length);
                        completedSize += length;
                        buffOffset += length;

                        LogUtil.d(TAG ,"completedSize=" + completedSize + " ,totalSize=" +
                                totalSize + " ," + "" + "Status=" + downloadStatus);
                        // 下载多少触发一次回调，更新一次数据库
                        // 以kb计算
                        if (buffOffset >= updateSize) {
                            // Update download information database
                            buffOffset = 0;
                            // 支持断点续传时，在往数据库中保存下载信息
                            // 此处会频繁更新数据库
                            mDownLoadInfo.setCompletedSize(completedSize);
                            LogUtil.d(TAG ,"type=" +  type + ",taskId=" + taskId + " ,mDownLoadInfo taskId =" + taskId );

                            DownLoadDatabaseManager.getInstance().putInfoByTaskId(type ,taskId ,mDownLoadInfo);
                            //onDownloading 回调
                            onCallBack();
                        }
                    }

                    //onDownloading;
                    // 防止最后一次不足UPDATE_SIZE，导致percent无法达到100%
                    onCallBack();
                }else{
                    downloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
                    onCallBack();
                }
            }else{
                int code  = response.code();
                LogUtil.d(TAG , "respond code = " + code);
            }

        } catch (FileNotFoundException e) {
            // file not found
            e.printStackTrace();
            LogUtil.s(TAG , "FileNotFoundException  e="  , e);
            downloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
            errorCode = DownloadStatus.DOWNLOAD_ERROR_FILE_NOT_FOUND;
        } catch (IOException e) {
            // io exception
            LogUtil.s(TAG , "IOException  e="  , e);
            e.printStackTrace();
            downloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
            errorCode = DownloadStatus.DOWNLOAD_ERROR_IO_ERROR;
        } catch (Exception e){
            LogUtil.s(TAG , "Exception  e="  , e);
            e.printStackTrace();
            downloadStatus = DownloadStatus.DOWNLOAD_STATUS_ERROR;
            errorCode = DownloadStatus.DOWNLOAD_ERROR_IO_ERROR;
        }finally {
            if (isDownloadFinish()) {
                onCallBack();
            }
            LogUtil.d(TAG , "finish  ");
            // 下载后新数据库
            if (mDownLoadInfo != null) {
                mDownLoadInfo.setCompletedSize(completedSize);
                mDownLoadInfo.setDownloadStatus(downloadStatus);
                DownLoadDatabaseManager.getInstance().putInfoByTaskId(type ,taskId ,mDownLoadInfo);
            }

            // 回收资源
            if (bis != null) {
                close(bis);
            }
            if (inputStream != null) {
                close(inputStream);
            }
            if (mDownLoadFile != null) {
                close(mDownLoadFile);
            }
        }
    }

    private Response getRespondByRequestType(int requestType){

        return getRespondByRequestType(false , requestType) ;
    }

    private Response getRespondByRequestType(boolean DirectionalSimCard ,int requestType){
        // 开始下载
        Response response = null ;
        boolean repeatDownload =false ;
        Request request = new Request.Builder().url(url).header("RANGE",
                "bytes=" + completedSize + "-") // Http value set breakpoints RANGE
                .build();
        //  8口的桌面机  使用http proxy 绑定 ip //
        try{
            if (DirectionalSimCard) {
                if (ip != null && !ip.equals("") && MainBoardVersionTools.isSmallNoneAM5400Rom() && !url.equals("")) {
                    String str[] = ip.split("\\.");
                    byte[] ip = new byte[str.length];
                    for(int i=0,len=str.length;i<len;i++){
                        ip[i] = (byte)(Integer.parseInt(str[i],10));
                        LogUtil.d(TAG , "ip=" + ip[i] + ",str= " + str[i]);
                    }
                    //绑定的ip地址，生成proxy代理对象，因为http底层是socket实现 80 端口
                    Proxy proxy = new Proxy(Proxy.Type.HTTP,
                            new InetSocketAddress(InetAddress.getByAddress(ip), 80));
                    mClient.newBuilder().proxy(proxy).build();
                    response = mClient.newCall(request).execute();
                } else {

                    response = mClient.newCall(request).execute();
                }
            }else{
                response = mClient.newCall(request).execute();
            }
        }catch (UnknownHostException e){

            if(requestType != DNS_REQUEST_TYPE){
                repeatDownload =true ;
                recordDnsError(NETTY_DNS_ERROR_CLOSED);
            }else{
                recordDnsError(DNS_REQUEST_FAILED);
            }

        }catch (Exception e) {
            LogUtil.s(TAG, "downloadPackage, read and write error" , e);
            e.printStackTrace();
        }finally {
            if(repeatDownload){
                response = getRespondByRequestType(DNS_REQUEST_TYPE);
                LogUtil.d(TAG, "downloadPackage, repeat execError");
                    recordDnsError(DNS_REQUEST_SUCCESS);
            }
        }
        return response;
    }


    private void  recordDnsError(int status){
//        OperatorLinkStatusStorageProvide.getInstance().recordDnsError(status);
    }

    private void  updateDownLoadInfo(int type ,String taskId ,long totalSize ,String url ,String fileName ,String saveDirPath ,int downloadStatus ,int repeatTimes){

        mDownLoadInfo.setTaskId(taskId);
        mDownLoadInfo.setLength(totalSize);
        mDownLoadInfo.setUrl(url);
        mDownLoadInfo.setSaveFileName(fileName);
        mDownLoadInfo.setSaveFilePath(saveDirPath);
        mDownLoadInfo.setDownloadStatus(downloadStatus);
        mDownLoadInfo.setRepeatTime(repeatTimes);
        mDownLoadInfo.setType(type);
    }

    private boolean isDownloadFinish() {
        boolean finish = false;
        if (totalSize > 0 && completedSize > 0 && totalSize == completedSize) {
            downloadStatus = DownloadStatus.DOWNLOAD_STATUS_COMPLETED;
            finish = true;
        }
        return finish;
    }

    /**
     * 删除数据库文件和已经下载的文件
     */
    public void cancel() {
        mListener.onCancel(DownloadTask.this);
        if (mDownLoadInfo != null) {
            DownLoadDatabaseManager.getInstance().delete(type ,mDownLoadInfo.getTaskId());
            File temp = new File(getFilePath());
            if (temp.exists()) {
                temp.delete();
            }
        }
    }

    /**
     * 分发回调事件到ui层
     */
    private void onCallBack() {
        mHandler.sendEmptyMessage(downloadStatus);
        // 同步manager中的task信息
        DownloadManager.getInstance().updateDownloadTask(this);
    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            int code = msg.what;
            switch (code) {
                // start download
                case DownloadStatus.DOWNLOAD_STATUS_INIT:
                    mListener.onDownloadBegin(DownloadTask.this);
                // 下载失败
                case DownloadStatus.DOWNLOAD_STATUS_ERROR:
                    mListener.onError(DownloadTask.this, errorCode ,repeatTimes);
                    break;
                // 正在下载
                case DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING:
                    mListener.onDownloading(DownloadTask.this, completedSize, totalSize, getDownLoadPercent());
                    break;
                // 取消
                case DownloadStatus.DOWNLOAD_STATUS_CANCEL:
                    cancel();
                    break;
                // 完成
                case DownloadStatus.DOWNLOAD_STATUS_COMPLETED:
                    mListener.onDownloadSuccess(DownloadTask.this, new File(getFilePath()));
                    break;
                // 停止
                case DownloadStatus.DOWNLOAD_STATUS_PAUSE:
                    mListener.onPause(DownloadTask.this, completedSize, totalSize, getDownLoadPercent());
                    break;

            }
        }
    };

    private String getDownLoadPercent() {
        String baifenbi = "0";// 接受百分比的值
        double baiy = completedSize * 1.0;
        double baiz = totalSize * 1.0;
        // 防止分母为0出现NoN
        if (baiz > 0) {
            double fen = (baiy / baiz) * 100;
            //NumberFormat nf = NumberFormat.getPercentInstance();
            //nf.setMinimumFractionDigits(2); //保留到小数点后几位
            // 百分比格式，后面不足2位的用0补齐
            //baifenbi = nf.format(fen);
            //注释掉的也是一种方法
            DecimalFormat df1 = new DecimalFormat("0");//0.00
            baifenbi = df1.format(fen);
        }
        return baifenbi;
    }

    private String getFilePath() {
        // 获得文件名
        if (!TextUtils.isEmpty(fileName)) {
            LogUtil.d(TAG ,"getFilePath fileName=" +fileName);
        }else {

            fileName = getFileNameFromUrl(url);
            LogUtil.d(TAG ,"getFilePath url last name fileName=" +fileName);
        }

        if (!TextUtils.isEmpty(saveDirPath)) {
            if (!saveDirPath.endsWith("/")) {
                saveDirPath = saveDirPath + "/";
            }
        }
        else {
            saveDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/downLoad/";
        }

        File file = new File(saveDirPath);
        if (!file.exists()) {
            boolean isSuccess = file.mkdirs();
            LogUtil.d(TAG ,"mkdirs result:" + isSuccess+",path:"+saveDirPath);
        }

        String filepath = saveDirPath + fileName;
        LogUtil.d(TAG ,"getFilePath filepath=" + filepath + ",saveDirPath=" + saveDirPath+ ",fileName=" +fileName);
        checkSavePackageFile(filepath);
        return filepath;
    }

    /// 检查文件
    private void checkSavePackageFile(String filepath) {
        File file = new File(filepath);
        if (!file.exists()) {
            try {
                LogUtil.d(TAG, "file createNewFile():");
                if (file.createNewFile()) {
                    LogUtil.d(TAG, "mkdir new file success!");
                }
            } catch (Exception e) {
                LogUtil.d(TAG, "checkSavePackageFile exception:"+e.getMessage());
                e.printStackTrace();
            }
        }


    }


    private String getFileNameFromUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            LogUtil.d(TAG, "getFileNameFromUrl url =" +url);
            return url.substring(url.lastIndexOf("/") + 1);
        }
        return System.currentTimeMillis() + "";
    }

    private void close(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void setDownloadDao(DownloadDao mDownloadDao) {
//        this.mDownloadDao = mDownloadDao;
//    }
//
    public void setClient(OkHttpClient mClient) {
        this.mClient = mClient;
    }

    private OkHttpClient getOkhttpClient() {

        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
//        CookieJarImpl cookieJar = new CookieJarImpl(new MemoryCookieStore());
        OkHttpClient mClient = new OkHttpClient.Builder()
                .connectTimeout(20000L, TimeUnit.MILLISECONDS)
                .readTimeout(20000L, TimeUnit.MILLISECONDS)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true ;
                    }
                })
//                .cookieJar(cookieJar)
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .build();

        return mClient ;
    }

    public Builder getBuilder() {
        return mBuilder;
    }

    public void setBuilder(Builder builder) {
        this.mBuilder = builder;
    }

    /**
     * 如果 taskId 不存在 值返回 url
     * 返回id
     * @return
     */
    public String getTaskId() {
        if (!TextUtils.isEmpty(taskId)) {
        }
        else {
            taskId = StringsUtils.getRequestId();
        }
        return taskId;
    }

    public String getUrl() {
        return url;
    }

    public String getMd5(){
        return md5 ;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSaveDirPath() {
        return saveDirPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public void setCompletedSize(long completedSize) {
        this.completedSize = completedSize;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getRepeatTimes() {
        return repeatTimes;
    }

    public void setRepeatTimes(int repeatTimes) {
        this.repeatTimes = repeatTimes;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static class Builder {
        private String taskId;// task id
        private String url;// file url
        private String saveDirPath;// file save path
        private String fileName; // File name when saving
        private int downloadStatus = DownloadStatus.DOWNLOAD_STATUS_INIT;
        private String md5 ;
        private String timestamp ;
        private String ip;
        private int type ;
        private DownloadTaskListener listener;

        /**
         * 作为下载task开始、删除、停止的key值，如果为空则默认是url
         *
         * @param taskId
         * @return
         */
        public Builder setTaskId(String taskId) {
            this.taskId = taskId;
            LogUtil.d("DownloadTask" , "setTaskId= " +taskId);
            return this;
        }

        /**
         * 下载url（not null）
         *
         * @param url
         * @return
         */
        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }


        public Builder setIp(String ip) {
            this.ip = ip;
            return this;
        }

        /**
         * 设置保存地址
         *
         * @param saveDirPath
         * @return
         */
        public Builder setSaveDirPath(String saveDirPath) {
            this.saveDirPath = saveDirPath;
            return this;
        }

        /**
         * 设置下载状态
         *
         * @param downloadStatus
         * @return
         */
        public Builder setDownloadStatus(int downloadStatus) {
            this.downloadStatus = downloadStatus;
            return this;
        }

        /**
         * 设置文件名
         *
         * @param fileName
         * @return
         */
        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        /**
         * 设置下载回调
         *
         * @param listener
         * @return
         */
        public Builder setListener(DownloadTaskListener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * 设置文件的md5
         * @param  md5
         * @return
         */

        public Builder setMd5(String md5){
            this.md5 = md5 ;
            return  this ;
        }

        /**
         * 设置文件下载的时间戳
         * @param  timestamp
         * @return
         */
        public Builder setTimestamp(String timestamp) {
            this.timestamp = timestamp;
            return  this ;
        }


        public Builder setType(int type) {
            this.type = type;
            return  this ;
        }

        /**
         * DownloadTask 实例
         * @return
         */
        public DownloadTask build() {
            return new DownloadTask(this);
        }



    }

}
