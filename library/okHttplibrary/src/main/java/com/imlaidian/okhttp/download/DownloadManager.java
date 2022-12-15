
package com.imlaidian.okhttp.download;


import com.imlaidian.okhttp.https.HttpsUtils;
import com.imlaidian.utilslibrary.utils.LogUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;

/**
 * 下载管理器
 */
public class DownloadManager {

    private final String TAG= DownloadManager.class.getSimpleName();
    /**
     * download type
     */

    public static final int NORMAL_DOWNLOAD_TYPE = 0 ;

    public static final int MEDIA_DOWNLOAD_TYPE = 1 ;

    public static final int ZIP_DOWNLOAD_TYPE = 2 ;

    public static final int OTHER_DOWNLOAD_TYPE = 3 ;

    public static final int PATCH_DOWNLOAD_TYPE = 4 ;

    private OkHttpClient mClient;
    private int mPoolSize = 5;
    // 将执行结果保存在future变量中
    private Map<String, Future> mFutureMap;
    private ExecutorService mExecutor;
    private Map<String, DownloadTask> mCurrentTaskList;

    private static DownloadManager instance;

    public static int mMaxRepeatTimes = 6;

    /**
     * 获得当前对象实例
     *
     * @return 当前实例对象
     */
    public  static DownloadManager getInstance() {
        if (instance == null) {
            synchronized (DownLoadDatabaseManager.class){
                if(instance ==null){
                    instance = new DownloadManager();
                }
            }
        }
        return instance;
    }

    public DownloadManager(){
        DownLoadDatabaseManager.getInstance() ;
        //数据库初始化
        initOkhttpClient();

        // 初始化线程池
        mExecutor = Executors.newFixedThreadPool(mPoolSize);
        mFutureMap = new HashMap<>();
        mCurrentTaskList = new HashMap<>();
    }

//    public DownloadManager() {
//        initOkhttpClient();
//
//        // 数据库初始化
//        DaoMaster.OpenHelper openHelper = new DaoMaster.DevOpenHelper(mContext, "downloadDB", null);
//        DaoMaster daoMaster = new DaoMaster(openHelper.getWritableDatabase());
//        mDownloadDao = daoMaster.newSession().getDownloadDao();
//
//        // 初始化线程池
//        mExecutor = Executors.newFixedThreadPool(mPoolSize);
//        mFutureMap = new HashMap<>();
//        mCurrentTaskList = new HashMap<>();
//    }

    /**
     * 初始化okhttp
     */
    private void initOkhttpClient() {

        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
//        CookieJarImpl cookieJar = new CookieJarImpl(new MemoryCookieStore());
        mClient = new OkHttpClient.Builder()
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
    }



    /**
     * 添加下载任务
     *需要添加 url MD5  type 等重要字段
     * @param downloadTask
     */
    public void addDownloadTask(DownloadTask downloadTask) {
        if (downloadTask != null && !isDownloading(downloadTask)) {
//            downloadTask.setDownloadDao(mDownloadDao);
            downloadTask.setClient(mClient);
            downloadTask.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_INIT);
            // 保存下载task列表
            mCurrentTaskList.put(downloadTask.getTaskId(), downloadTask);
            Future future = mExecutor.submit(downloadTask);
            mFutureMap.put(downloadTask.getTaskId(), future);
        }else {
            LogUtil.d(TAG , " addDownloadTask");
        }
    }

    private boolean isDownloading(DownloadTask task) {
        if (task != null) {
            if (task.getDownloadStatus() == DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING) {
                LogUtil.d(TAG , " isDownloading  task downloading");
                return true;
            }else{
                LogUtil.d(TAG , " isDownloading  task not downloading");
            }
        }else{
            LogUtil.d(TAG , " isDownloading task null");
        }
        return false;
    }

    /**
     * 暂停下载任务
     *@param type 下载类型
     * @param id 任务id
     */
    public void pause(int type  ,String id) {
        DownloadTask task = getDownloadTask(type ,id);
        if (task != null) {
            task.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_PAUSE);
        }
    }

    /**
     * 重新开始已经暂停的下载任务
     *
     * @param type 下载类型
     * @param taskId 任务id
     * @param time 重复次数
     */
    public void repeatDownLoad(int type ,String taskId ,int time) {
        DownloadTask task = getDownloadTask(type ,taskId);
        if (task != null ) {
            if(time < mMaxRepeatTimes){
                task.setRepeatTimes( time + 1);
                addDownloadTask(task);
            }else{
                LogUtil.d(TAG , "repeatDownLoad task time  beyond 10");
            }

        }else{
            LogUtil.d(TAG , "repeatDownLoad task null");
        }
    }



    /**
     * 取消下载任务(同时会删除已经下载的文件，和清空数据库缓存)
     * @param type 下载类型
     * @param id 任务id
     */
    public void cancel(int type ,String id) {
        DownloadTask task = getDownloadTask(type ,id);
        if (task != null) {
            mCurrentTaskList.remove(id);
            mFutureMap.remove(id);
            task.cancel();
            task.setDownloadStatus(DownloadStatus.DOWNLOAD_STATUS_CANCEL);
        }
    }

    /**
     * 实时更新manager中的task信息
     *
     * @param task
     */
    public void updateDownloadTask(DownloadTask task) {
        if (task != null) {
            DownloadTask currTask = getDownloadTask(task.getType(),task.getTaskId());
            if (currTask != null) {
                mCurrentTaskList.put(task.getTaskId(), task);
            }
        }
    }

    /**
     * 获得指定的task
     *
     * @param taskId task id
     * @param type 下载类型
     * @return
     */
    public DownloadTask getDownloadTask(int type ,String taskId) {
        DownloadTask currTask = mCurrentTaskList.get(taskId);
        if (currTask == null) {
            // 从数据库中取出为完成的task
            DownLoadSourceInfoModel entity = DownLoadDatabaseManager.getInstance().getInfoByTaskId(type ,taskId);
            if (entity != null) {
                if (entity.getDownloadStatus() != DownloadStatus.DOWNLOAD_STATUS_COMPLETED) {
                    currTask = parseEntity2Task(new DownloadTask.Builder().build(), entity);
                    // 放入task list中
                    mCurrentTaskList.put(taskId, currTask);
                }
            }
        }
        return currTask;
    }


    /**
     * 获得所有的task
     *
     * @return
     */
    public Map<String, DownloadTask> getAllDownloadTasks(int type) {
        if (mCurrentTaskList != null && mCurrentTaskList.size() <= 0) {
            List<DownLoadSourceInfoModel> entitys = DownLoadDatabaseManager.getInstance().getAllDownloadInfo(type );
            for (DownLoadSourceInfoModel entity : entitys) {
                DownloadTask currTask = parseEntity2Task(new DownloadTask.Builder().build(), entity);
                mCurrentTaskList.put(entity.getTaskId(), currTask);
            }
        }

        return mCurrentTaskList;
    }

    private DownloadTask parseEntity2Task(DownloadTask currTask, DownLoadSourceInfoModel entity) {
        if (entity != null && currTask != null) {
            DownloadTask.Builder builder = new DownloadTask.Builder()//
                .setDownloadStatus(entity.getDownloadStatus())//
                .setFileName(entity.getSaveFileName())//
                .setSaveDirPath(entity.getSaveFilePath())//
                .setUrl(entity.getUrl())//
                .setIp(entity.getIp())
                .setType(entity.getType())
                .setTimestamp(entity.getTimeStamp())
                .setTaskId(entity.getTaskId());//

            currTask.setBuilder(builder);
            currTask.setCompletedSize(entity.getCompletedSize());//
            currTask.setTotalSize(entity.getLength());
            currTask.setTimestamp(entity.getTimeStamp());
            currTask.setMd5(entity.getTimeStamp());
            currTask.setUrl(entity.getUrl());
            currTask.setIp(entity.getIp());
            currTask.setType(entity.getType());
            currTask.setRepeatTimes(entity.getRepeatTime());
        }
        return currTask;
    }
}
