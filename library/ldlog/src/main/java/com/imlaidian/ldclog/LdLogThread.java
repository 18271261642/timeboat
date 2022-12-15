
package com.imlaidian.ldclog;

import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Pattern;

import static com.imlaidian.ldclog.Util.deleteDirectory;
import static com.imlaidian.ldclog.Util.getCurrentTimeDate;
import static com.imlaidian.ldclog.Util.getCurrentTimeYMDate;
import static com.imlaidian.ldclog.Util.getDetailData;
import static com.imlaidian.ldclog.Util.getTimestampYYYYMMDDHH;
import static com.imlaidian.ldclog.Util.isYYYYMMddHH;
import static com.imlaidian.ldclog.Util.parseYmdHHData;
import static com.imlaidian.ldclog.Util.zipFileOrDir;
import static com.imlaidian.ldclog.Util.zipMultiFile;
import static com.imlaidian.ldclog.Util.zipSingleFile;

class LdLogThread extends Thread {

    private static final String TAG = "LdLogThread";
    private static final int MINUTE = 60 * 1000;
    private static final long HALF_HOUR = 30 * MINUTE;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;
    private static final long SAVE_MIN_DAYS = 5 * DAY; //默认删除天数
    // 间隔 6小时 生成一个文件，重新写日志，上一个日志打包
    private static final long  INTERVAL_CREATE_HOURS =  6 * HOUR ;

    private final Object sync = new Object();
    private final Object sendSync = new Object();
    private volatile boolean mIsRun = true;
    //当前最起始时间 比如 2021-01-23:00:00:00
    private long mCurrentDay;
    //当前最起始时间 比如 2021-01-23:01:00:00
    private long mCurrentDayHour;
    private volatile boolean mIsWorking;
    private File mFileDirectory;
    private boolean mIsSDCard;
    private long mLastTime;
    private LdLogProtocol mLdLogProtocol;
    private ConcurrentLinkedQueue<LdLogModel> mCacheLogQueue;
    private String mCachePath; // 缓存文件路径
    private String mPath; //文件路径
    private long mSaveTime; //存储时间
    private long mMaxLogFile;//最大文件大小
    private long mMinSDCard;
    private int mSendLogStatusCode;
    private String[] mWhitePath;
    // 发送缓存队列
    private ConcurrentLinkedQueue<LdLogModel> mCacheSendQueue = new ConcurrentLinkedQueue<>();
    private ExecutorService mSingleThreadExecutor;
    public static final String FILE_SUFFIX = ".zip";
    public static final String SAVE_FOLDER = "LaidianTerminal";

    public static final String CLIENT_SAVE_FOLDER = "LaidianClient";


    LdLogThread(
            ConcurrentLinkedQueue<LdLogModel> cacheLogQueue, String cachePath,
            String path, long saveTime, long maxLogFile, long minSDCard, String[] whitePath) {
        mCacheLogQueue = cacheLogQueue;
        mCachePath = cachePath;
        mPath = path;
        mSaveTime = saveTime;
        mMaxLogFile = maxLogFile;
        mMinSDCard = minSDCard;
        mWhitePath = whitePath;
    }

    void notifyRun() {
        if (!mIsWorking) {
            synchronized (sync) {
                sync.notify();
            }
        }
    }

    void quit() {
        mIsRun = false;
        if (!mIsWorking) {
            synchronized (sync) {
                sync.notify();
            }
        }
    }

    @Override
    public void run() {
        super.run();
        while (mIsRun) {
            synchronized (sync) {
                mIsWorking = true;
                try {
                    LdLogModel model = mCacheLogQueue.poll();
                    if (model == null) {
                        mIsWorking = false;
                        sync.wait();
                        mIsWorking = true;
                    } else {
                        action(model);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mIsWorking = false;
                }
            }
        }
    }

    private void action(LdLogModel model) {
        if (model == null || !model.isValid()) {
            return;
        }
        if (mLdLogProtocol == null) {
            mLdLogProtocol = LdLogProtocol.newInstance();
            mLdLogProtocol.setOnLogProtocolStatus(new OnLdLogProtocolStatus() {
                @Override
                public void logProtocolStatus(String cmd, int code) {
                    LdLog.onListenerLogWriteStatus(cmd, code);
                }
            });
            //初始化log
            long currentTime = System.currentTimeMillis();

            String ymDate = getCurrentTimeYMDate(currentTime);

            String ymdDate = getCurrentTimeDate(currentTime);

            String subPath = ymDate + File.separator + ymdDate;
            // 兼容上一个版本  /sdcard/LaidianTerminal/LogFile/202103/20210308（文件）
            ///sdcard/LaidianTerminal/LogFile/202103/20210308（文件夹） 不能同时创建的问题
            checkSubFileExist(mPath ,subPath,ymdDate);
            //outputType 0 :日志字段组合，用逗号隔开 1:日志字段通过json格式化后输出
            mLdLogProtocol.log_init(mCachePath, mPath, subPath, (int) mMaxLogFile,0);

            mLdLogProtocol.log_debug(LdLog.sDebug);
        }

        if (model.action == LdLogModel.Action.WRITE) {
            doWriteLog2File(model.writeAction);
        } else if (model.action == LdLogModel.Action.SEND) {
            if (model.sendAction.sendLogRunnable != null) {
                // 是否正在发送
                synchronized (sendSync) {
                    if (mSendLogStatusCode == SendLogRunnable.SENDING) {
                        mCacheSendQueue.add(model);
                    } else {
                        doSendLog2Net(model.sendAction);
                    }
                }
            }
        } else if (model.action == LdLogModel.Action.FLUSH) {
            doFlushLog2File();
        }

    }

    private void checkSubFileExist(String mPath ,String subPath,String ymdFileName){
        //sdcard/LaidianTerminal/LogFile/202103/20210308（文件）
        ///sdcard/LaidianTerminal/LogFile/202103/20210308（文件夹） 不能同时创建的问题
        //20210308 文件重命名为2021030800 ，并放到/sdcard/LaidianTerminal/LogFile/202103/20210308/2021030800
        File subFile =  new File(mPath + subPath);
        if(subFile.exists()&&subFile.isFile()){
            String newFileName = mPath + subPath + "00";
            File newFile= new File(newFileName);
            if(subFile.renameTo(newFile)){


                if(new File(mPath + subPath).mkdirs()){

                    String newFilePathName = mPath + subPath + File.separator +  ymdFileName +"00";

                    boolean isZip= zipSingleFile(newFileName ,newFilePathName + ".zip");
                    boolean isDel =newFile.delete();
                    if (LdLog.sDebug) {
                        Log.d(TAG, "checkSubFileExist newFile isZip = " + isZip + ",isDel=" +isDel);
                    }
                }else{
                    boolean isDel =newFile.delete();
                    if (LdLog.sDebug) {
                        Log.d(TAG, "checkSubFileExist newFile isDel = " + isDel );
                    }
                }

            }else{
                boolean isSuccess =subFile.delete();
                if (LdLog.sDebug) {
                    Log.d(TAG, "checkSubFileExist isSuccess = " + isSuccess );
                }
            }
        }
    }

    private void doFlushLog2File() {
        if (LdLog.sDebug) {
            Log.d(TAG, "Log flush start");
        }
        if (mLdLogProtocol != null) {
            mLdLogProtocol.log_flush();
        }
    }

    //在当天，默认间隔小时有效时间内
    private boolean isHour() {
        long currentTime = System.currentTimeMillis();
        return mCurrentDayHour < currentTime && mCurrentDayHour + INTERVAL_CREATE_HOURS > currentTime;
    }

    //当天的有效时间内
    private boolean isDay() {
        long currentTime = System.currentTimeMillis();
        return mCurrentDay < currentTime && mCurrentDay + DAY > currentTime;
    }

    private boolean isVailDayHour() {
        return isDay() && isHour();
    }

    private boolean isExpireFile(long deleteTime, String fileName) {
        boolean isExpire = false;
        try {
            if (TextUtils.isEmpty(fileName)) {
                return false;
            }
            String tempName = fileName;
            if (tempName.endsWith(FILE_SUFFIX)) {
                tempName = tempName.substring(0, tempName.length() - FILE_SUFFIX.length());
            }
            //后缀未zip的压缩文件需要处理下
            String[] longStrArray = tempName.split("\\.");
            if (longStrArray.length > 0) {  //小于时间就删除
                try {
                    String date = longStrArray[0];

                    long longItem = Util.parseYmdData(date);
                    if (longItem <= deleteTime ) {
                        isExpire = true;

                    }
                    if (LdLog.sDebug) {
                        Log.d(TAG, "isExpireFile isExpire = " + isExpire );
                    }
                } catch (Exception e) {
                    isExpire = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isExpire;
    }

    private boolean isFilePathInWhiteList(String subPath) {
        String filename = Util.getLastPathComponent(subPath);
        for (String name : mWhitePath) {
            if (name.equalsIgnoreCase(filename)) {
                return true;
            }
        }
        return false;
    }

    private void deleteExpireLogFile(long deleteTime, String directoryPath) {
        LdLog.d(TAG, "delete expire log file handle directoryPath = " + directoryPath + "，deleteTime=" +deleteTime);

        if (null == directoryPath || directoryPath.length() <= 0) {
            return;
        }

        if (!directoryPath.endsWith(File.separator)) {
            directoryPath = directoryPath + File.separator;
        }

        try {
            File file = new File(directoryPath);
            if (file.isDirectory()) {
                File[] files = file.listFiles();

                if (null != files && files.length > 0) {
                    for (File subFile : files) {
                        String fileName = subFile.getName();
                        String subPath = directoryPath + fileName;

                        Log.d(TAG, "handle path = " + subPath);
                        if (subFile.isDirectory()) {

                            if (LdLog.sDebug) {
                                Log.d(TAG, "handle path = " + subPath + ", is isDirectory" + ",fileName=" + fileName);
                            }
                            LdLog.d(TAG, "handle path = " + subPath + ", is directory" + ",fileName=" + fileName);

                            checkYmdDirectoryOrFile(deleteTime, subPath, fileName);
                            deleteExpireLogFile(deleteTime, subPath);


                        } else if (!subFile.isHidden() && subFile.isFile()) {
                            if (LdLog.sDebug) {
                                Log.d(TAG, "file  path = " + subPath + ", is normal file" + ",fileName=" + fileName);
                            }

                            LdLog.d(TAG, "file path = " + subPath + ", is normal file" + ",fileName=" + fileName);

                            if (!checkYmdDirectoryOrFile(deleteTime, subPath, fileName)) {
                                if (LdLog.sDebug) {
                                    Log.d(TAG, "start  checkYmdHHFile file  path  = " + subPath + ", is normal file" + ",fileName=" + fileName);
                                }
                                checkYmdHHFile(deleteTime, subPath, fileName);
                            }
                        } else {
                            LdLog.d(TAG, "handle path = " + subPath + ", is unknown file");
                        }
                    }
                }else{
                    // 删除文件夹
                    try {
                        if(file!=null && file.exists()){
                            boolean isDel = file.delete();
                        }

                    }catch (Exception  e){
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            LdLog.d(TAG, "deleteExpireLogFile  error= " + e);

            e.printStackTrace();
        }
    }


    private void checkYmdHHFile(long deleteTime, String subPath, String fileName) {
        // 此处如果是.zip 格式文件需要截取下判断，否者.zip 文件会被删除
        String checkFileName = fileName;
        if (fileName.endsWith(FILE_SUFFIX)) {
            checkFileName = fileName.substring(0, fileName.length() - FILE_SUFFIX.length());
        }

        if (isYYYYMMddHH(checkFileName)) {
            if (LdLog.sDebug) {
                Log.d(TAG, "checkYmdHHFile handle path = " + subPath + ", file name valid");
            }
            LdLog.d(TAG, "checkYmdHHFile handle path = " + subPath + ", file name valid");
            long fileTimestamp = getYYYYmmDDHHTime(fileName);
            long currentTime = System.currentTimeMillis();
            long twoHourAgo = currentTime - 2 * HOUR;
            if (fileTimestamp > 0 && fileTimestamp < twoHourAgo) {
                if (!isZipFileName(fileName)) {
                    if (LdLog.sDebug) {
                        Log.d(TAG, "checkYmdHHFile handle subPath = " + subPath + ",fileName=" + fileName + ", file not zip file");
                    }

                    //压缩前两个小时的日志
                    //zip, delete source file
                    zipLogFile(subPath, true);
                }
            }
        } else {
            if (isFilePathInWhiteList(subPath)) {
                LdLog.d(TAG, " file is in white list.");
            } else {
                LdLog.d(TAG, "This file will be deleted.");
                try {
                    boolean isSuccess = new File(subPath).delete(); //删除文件
                    LdLog.d(TAG, "checkYmdHHFile  File subPath= " + subPath + ", del isSuccess=" + isSuccess);
                } catch (Exception e) {
                    e.printStackTrace();
                    LdLog.d(TAG, "checkYmdHHFile error = " + e.toString());
                }
            }

        }

    }

    private boolean checkYmdDirectoryOrFile(long deleteTime, String subPath, String fileName) {
        boolean isYmd = false;
        if (isYMDFileNameValid(fileName) || isErrorYMDFileNameValid(fileName)) {
            isYmd = true;
            if (LdLog.sDebug) {
                Log.d(TAG, "checkYmdDirectoryOrFile handle path = " + subPath + ", file name valid");
            }
            LdLog.d(TAG, "checkYmdDirectoryOrFile handle path = " + subPath + ", file name valid");

            if (isExpireFile(deleteTime, fileName)) {

                LdLog.d(TAG, "checkYmdDirectoryOrFile handle path = " + subPath + ", file expire and delete");
                boolean isSuccess = false;
                File subFile = new File(subPath);
                //需处理下文件夹
                if (subFile.isDirectory()) {
                    isSuccess = deleteDirectory(subPath);
                } else {
                    isSuccess = subFile.delete(); //删除文件
                }


                LdLog.d(TAG, "isFileNameValid Expire File date= " + getDetailData(deleteTime) + ", del isSuccess=" + isSuccess);

                if (LdLog.sDebug) {
                    Log.d(TAG, "handle path = " + subPath + ", file expire and delete");
                    Log.d(TAG, "isFileNameValid Expire File date= " + getDetailData(deleteTime) + ", del isSuccess=" + isSuccess);
                }

            } else {
                if (LdLog.sDebug) {
                    Log.d(TAG, "handle path = " + subPath + ", file not expire");
                }
                LdLog.d(TAG, "handle path = " + subPath + ", file not expire");

                if (!isZipFileName(fileName)) {
                    if (LdLog.sDebug) {
                        Log.d(TAG, "handle subPath = " + subPath + ",fileName=" + fileName + ", file not zip file");
                    }
                    LdLog.d(TAG, "handle subPath = " + subPath + ", file not zip file");
                    long currentTime = System.currentTimeMillis();
                    String outFileName = generateYMDLogFileName(currentTime);
                    if (null != outFileName && 0 != subPath.compareToIgnoreCase(outFileName)) {
                        if (LdLog.sDebug) {
                            Log.d(TAG, "handle path = " + subPath + ", file not today file, outFileName = " + outFileName);
                        }
                        LdLog.d(TAG, "handle path = " + subPath + ", file not today file, outFileName = " + outFileName);

                        //不是今天的日期文件名
                        //zip, delete source file
                        zipLogFile(subPath, true);

                        LdLog.d(TAG, "handle path = " + subPath + ", file not today file over, outFileName = " + outFileName);

                    }
                }
            }

        }

        return isYmd;


    }

    private long getYYYYmmDDHHTime(String fileName) {
        long currentTime = 0;
        String checkFileName = fileName;
        if (fileName.endsWith(FILE_SUFFIX)) {
            checkFileName = fileName.substring(0, fileName.length() - FILE_SUFFIX.length());
        }

        if (isYYYYMMddHH(checkFileName)) {
            currentTime = parseYmdHHData(checkFileName);
            if (LdLog.sDebug) {
                Log.d(TAG, "getYYYYmmDDHHTime currentTime = " + currentTime);
            }
        }

        return currentTime;
    }


    private boolean isYMDHFileNameValid(String fileName) {
        boolean isValid = false;
        final String mYearMonthDayHourString = "yyyyMMddHH";
        String checkFileName = fileName;
        if (fileName.endsWith(FILE_SUFFIX)) {
            checkFileName = fileName.substring(0, fileName.length() - FILE_SUFFIX.length());
        }

        if (checkFileName.length() == mYearMonthDayHourString.length()) {
            for (int i = 0; i < checkFileName.length(); i++) {
                int chr = checkFileName.charAt(i);
                if (0 == i) {
                    isValid = (chr == '2');
                } else if (1 == i) {
                    isValid = (chr == '0');
                } else {
                    isValid = (chr >= '0' && chr <= '9');
                }

                if (!isValid) {
                    break;
                }
            }
        }

        if (LdLog.sDebug) {
            Log.d(TAG, "isFileNameValid isValid = " + isValid);
        }
        LdLog.d(TAG, "isFileNameValid isValid = " + isValid);

        return isValid;
    }


    private boolean isYMDFileNameValid(String fileName) {
        boolean isValid = false;
        final String mYearMonthDayString = "yyyyMMdd";
        String checkFileName = fileName;
        if (fileName.endsWith(FILE_SUFFIX)) {
            checkFileName = fileName.substring(0, fileName.length() - FILE_SUFFIX.length());
        }

        if (checkFileName.length() == mYearMonthDayString.length()) {
            for (int i = 0; i < checkFileName.length(); i++) {
                int chr = checkFileName.charAt(i);
                if (0 == i) {
                    isValid = (chr == '2');
                } else if (1 == i) {
                    isValid = (chr == '0');
                } else {
                    isValid = (chr >= '0' && chr <= '9');
                }

                if (!isValid) {
                    break;
                }
            }
        }

        if (LdLog.sDebug) {
            Log.d(TAG, "isYMDFileNameValid isValid = " + isValid);
        }
        LdLog.d(TAG, "isYMDFileNameValid isValid = " + isValid);

        return isValid;
    }

    private boolean isErrorYMDFileNameValid(String fileName) {
        final String mYearMonthDayString = "yyyyMMdd";
        boolean isValid = false;
        String checkFileName = fileName;
        if (fileName.endsWith(FILE_SUFFIX)) {
            checkFileName = fileName.substring(0, fileName.length() - FILE_SUFFIX.length());
        }
        if (checkFileName.length() == mYearMonthDayString.length()) {
            for (int i = 0; i < checkFileName.length(); i++) {
                int chr = checkFileName.charAt(i);
                if (0 == i) {
                    isValid = (chr >= '1' && chr <= '9');
                } else if (1 == i) {
                    isValid = (chr >= '0' && chr <= '9');
                } else {
                    isValid = (chr >= '0' && chr <= '9');
                }

                if (!isValid) {
                    break;
                }
            }
        }

        if (LdLog.sDebug) {
            Log.d(TAG, "isErrorDataFileNameValid isValid = " + isValid);
        }
        LdLog.d(TAG, "isErrorDataFileNameValid isValid = " + isValid);

        return isValid;
    }


    private void doWriteLog2File(WriteAction action) {
//        if (LdLog.sDebug) {
//            Log.d(TAG, "Log write start");
//        }
        if (mFileDirectory == null) {
            mFileDirectory = new File(mPath);
            if (!mFileDirectory.exists()) {
                boolean isSuccess = mFileDirectory.mkdirs();
                LdLog.d(TAG, "doWriteLog2File isSuccess" + isSuccess);
            }
        }

        if (!isVailDayHour()) {
            long currentTime = System.currentTimeMillis();
            long tempCurrentDay = Util.getCurrentDayTime(currentTime);
            //需要删除的文件开始时间
            long deleteTime = tempCurrentDay - mSaveTime;

            mCurrentDay = tempCurrentDay;
            mCurrentDayHour = Util.getCurrentDayHourTime();
            String ymDate = getCurrentTimeYMDate(currentTime);
            String ymdDate = getCurrentTimeDate(currentTime);
            String ymdHDate = getTimestampYYYYMMDDHH(currentTime);
            String subFilePath = ymDate + File.separator + ymdDate;
            if (LdLog.sDebug) {
                Log.d(TAG, "doWriteLog2File subPath=" + subFilePath + ",fileName=" + ymdHDate);
            }

            checkSubFileExist(mPath,subFilePath,ymdDate);

            mLdLogProtocol.log_open(ymdHDate, subFilePath);
//            mLdLogProtocol.log_open(getCurrentTimeDate(currentTime), getCurrentTimeYMDate(currentTime));

            deleteExpireLogFile(deleteTime, mPath);
        }

        long currentTime = System.currentTimeMillis(); //每隔半小时判断一次
        if (currentTime - mLastTime > HALF_HOUR) {
            mIsSDCard = isCanWriteSDCard();

            if(!mIsSDCard){
                mSaveTime = SAVE_MIN_DAYS ;
            }
            mLastTime = System.currentTimeMillis();

            LdLog.d(TAG, "doWriteLog2File mIsSDCard =" + mIsSDCard + "，mLastTime=" +mLastTime);
        }


        if (!mIsSDCard) { //如果小于1G 不让再次写入
            if (LdLog.sDebug) {
                Log.d(TAG, "doWriteLog2File mIsSDCard =" + mIsSDCard);
            }
            return;
        }
        mLdLogProtocol.log_write(action.tag, getDetailData(action.localTime), action.lever, action.type, action.threadName, action.log);
    }

    private void doSendLog2Net(SendAction action) {
        if (LdLog.sDebug) {
            Log.d(TAG, "Log send start");
        }
        if (TextUtils.isEmpty(mPath) || action == null || !action.isValid()) {
            return;
        }
        boolean success = prepareLogFile(action);
        if (!success) {
            if (LdLog.sDebug) {
                LdLog.d(TAG, "Log prepare log file failed, can't find log file");
            }
        }
        action.sendLogRunnable.setSendAction(action);
        action.sendLogRunnable.setCallBackListener(
                new SendLogRunnable.OnSendLogCallBackListener() {
                    @Override
                    public void onCallBack(int statusCode) {
                        synchronized (sendSync) {
                            mSendLogStatusCode = statusCode;
                            if (statusCode == SendLogRunnable.FINISH) {
                                if (LdLog.sDebug) {
                                    Log.d(TAG, "send file FINISH");
                                }
                                LdLog.d(TAG, "send file FINISH");
                                mCacheLogQueue.addAll(mCacheSendQueue);
                                mCacheSendQueue.clear();
                                notifyRun();
                            }
                        }
                    }
                });
        mSendLogStatusCode = SendLogRunnable.SENDING;
        if (mSingleThreadExecutor == null) {
            mSingleThreadExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    // Just rename Thread
                    Thread t = new Thread(Thread.currentThread().getThreadGroup(), r,
                            "log-thread-send-log", 0);
                    if (LdLog.sDebug) {
                        Log.d(TAG, "send file execute");
                    }
                    LdLog.d(TAG, "send file execute");
                    if (t.isDaemon()) {
                        t.setDaemon(false);
                    }
                    if (t.getPriority() != Thread.NORM_PRIORITY) {
                        t.setPriority(Thread.NORM_PRIORITY);
                    }
                    return t;
                }
            });
        }
        mSingleThreadExecutor.execute(action.sendLogRunnable);
    }


    /**
     * 发送日志前的预处理操作
     */
    // rambo  当天的文件需要下把内容flush 下，在压缩 文件
    private boolean prepareLogFile(SendAction action) {
        boolean isSuccess = false;
        if (LdLog.sDebug) {
            Log.d(TAG, "prepare log file");
        }

        if (action.uploadItem == null || TextUtils.isEmpty(action.uploadItem.fileName)) {
            return false;
        }

        LdUploadItem uploadItem = action.uploadItem;
        setUploadFile(uploadItem);
        isSuccess = zipUploadFileByType(uploadItem);

        return isSuccess;
    }


    private void setUploadFile(LdUploadItem uploadItem) {
        Map<String, String> filePathList = uploadItem.getFilePathList();
        if (filePathList != null && filePathList.size() >= 1) {
            if (!uploadItem.isMultipleApp) {
                String uploadFile = filePathList.get(SAVE_FOLDER);
                if (uploadFile != null && !uploadFile.equals("")) {
                    uploadItem.setUploadPath(setUploadPath(uploadItem, uploadFile));
                }
            } else {
                String uploadServerFile = filePathList.get(SAVE_FOLDER);
                String uploadClientFile = filePathList.get(CLIENT_SAVE_FOLDER);
                if (uploadServerFile != null && !uploadServerFile.equals("")) {
                    uploadItem.setUploadPath(setUploadPath(uploadItem, uploadServerFile));
                } else if (uploadClientFile != null && !uploadClientFile.equals("")) {
                    uploadItem.setUploadPath(setUploadPath(uploadItem, uploadClientFile));
                }
            }
        }
    }

    private boolean zipUploadFileByType(LdUploadItem uploadItem) {
        boolean isSuccess = false;
        try {

            UploadFileType uploadType = uploadItem.uploadFileType;
            switch (uploadType) {
                case DateLogYmdFile: {
                    if (uploadItem.fileName.equals(getCurrentTimeDate(System.currentTimeMillis()))) {
                        doFlushLog2File();
                    }
                    isSuccess = zipUploadFile(uploadItem);
                    break;
                }
                case DateLogYmdHHFile: {
                    //yyyyMMddHH 格式日志
                    if (uploadItem.fileName.equals(getTimestampYYYYMMDDHH(System.currentTimeMillis()))) {
                        doFlushLog2File();
                    }
                    isSuccess = zipUploadFile(uploadItem);
                    break;
                }
                case OtherFile:
                case HeapDump:
                case ScreenShot: {
                    // 只有server 端保存
                    String uploadServerFile = uploadItem.filePathList.get(SAVE_FOLDER);
                    isSuccess = zipFileOrDir(uploadServerFile, uploadItem.getUploadPath());
                    break;
                }

                case TerminalLog:
                case StartApp:
                case SerialLog: {
                    isSuccess = zipUploadFile(uploadItem);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isSuccess;
    }

    private boolean zipUploadFile(LdUploadItem item) {

        if (item.isMultipleApp) {
            return zipMultiLogFile(item);
        } else {
            String uploadServerFile = item.filePathList.get(SAVE_FOLDER);
            return zipFileOrDir(uploadServerFile, item.getUploadPath());
        }
    }

    private boolean zipMultiLogFile(LdUploadItem item) {
        boolean isZipSuccess = false;
        String severFilePath = item.getFilePathList().get(SAVE_FOLDER);
        String clientFilePath = item.getFilePathList().get(CLIENT_SAVE_FOLDER);
        String zipFilePath = item.getUploadPath();


        LdLog.d(TAG, "zipMultiLogFile file clientFilePath ="
                + clientFilePath + ",severOutFilePath=" + severFilePath + ",zipFilePath=" + zipFilePath);
        File serverFile = new File(severFilePath);
        //查看 要上传的文件是否存在，不存在 再检查zip 文件是否存在，存在，则需要进行压缩
        if (!serverFile.exists()) {
            if (new File(severFilePath + FILE_SUFFIX).exists()) {
                severFilePath = severFilePath + FILE_SUFFIX;
                LdLog.d(TAG, "zipMultiLogFile severFilePath =" + severFilePath + " exist");
            } else {
                severFilePath = "";
                LdLog.d(TAG, "zipMultiLogFile severFilePath =" + severFilePath + FILE_SUFFIX + "not exist");
            }
        } else {

            if (serverFile.isDirectory()) {
                if (LdLog.sDebug) {
                    Log.d(TAG, "serverFile= " + serverFile + "exist");
                }
                String serverZipFilePath = severFilePath + FILE_SUFFIX;
                if (zipFileOrDir(severFilePath, serverZipFilePath)) {
                    severFilePath = serverZipFilePath;
                    if (LdLog.sDebug) {
                        Log.d(TAG, "serverFile= " + serverFile + ",zip dir success");
                    }
                } else {
                    severFilePath = "";
                    if (LdLog.sDebug) {
                        Log.d(TAG, "serverFile= " + serverFile + ",zip dir faile");
                    }
                }

            } else {
                LdLog.d(TAG, "zipMultiLogFile severFilePath= " + severFilePath + ",is file exist");
            }


        }

        File clientFile = new File(clientFilePath);
        if (!clientFile.exists()) {
            if (new File(clientFilePath + FILE_SUFFIX).exists()) {
                LdLog.d(TAG, "clientFilePath= " + clientFilePath + ".zip exist");
                clientFilePath = clientFilePath + FILE_SUFFIX;
            } else {
                clientFilePath = "";
                LdLog.d(TAG, "zipMultiLogFile clientFilePath =" + clientFilePath + FILE_SUFFIX + "not exist");
            }
        } else {
            if (LdLog.sDebug) {
                Log.d(TAG, "clientFilePath= " + clientFilePath + "exist");
            }
            if (clientFile.isDirectory()) {
                if (zipFileOrDir(clientFilePath, clientFilePath + FILE_SUFFIX)) {
                    clientFilePath = clientFilePath + FILE_SUFFIX;
                } else {
                    clientFilePath = "";
                    if (LdLog.sDebug) {
                        Log.d(TAG, "clientFilePath= " + clientFilePath + ", zip   dir false");
                    }
                }
                if (LdLog.sDebug) {
                    Log.d(TAG, "clientFilePath= " + clientFilePath + ", is  dir exist");
                }
            } else {
                if (LdLog.sDebug) {
                    Log.d(TAG, "clientFilePath= " + clientFilePath + ", is File exist");
                }
            }

            LdLog.d(TAG, "clientFilePath= " + clientFilePath + "exist");
        }

        if ((severFilePath != null && !severFilePath.equals("")) || (clientFilePath != null && !clientFilePath.equals(""))) {
            isZipSuccess = zipCSFile(clientFilePath, severFilePath, zipFilePath);

            if (isZipSuccess) {
                if (item.delSourceFile) {
                    ArrayList<String> fileList = new ArrayList<String>();
                    fileList.add(severFilePath);
                    fileList.add(clientFilePath);
                    for (String path : fileList) {
                        boolean isSuccessDel = new File(path).delete();
                        LdLog.d(TAG, "delete file status = " + isSuccessDel + " path = " + path);
                    }
                }
            } else {
                boolean isSuccess = new File(zipFilePath).delete();
                LdLog.d(TAG, "delete file status = " + isSuccess + " path = " + zipFilePath);
            }
        } else {
            LdLog.d(TAG, "clientFilePath=" + clientFilePath + ",severFilePath=" + severFilePath);
        }

        return isZipSuccess;
    }

    // 区分c/s 文件路径
    public boolean zipCSFile(String clientPath, String serverPath, String zipOutPath) {
        try {
            ArrayList<String> dirPathList = new ArrayList<String>();
            if (!serverPath.equals("")) {
                dirPathList.add(serverPath);
                LdLog.d(TAG, "zipCSFile  serverPath= " + serverPath + "exist");
            }
            if (!clientPath.equals("")) {
                dirPathList.add(clientPath);
                LdLog.d(TAG, "zipCSFile  clientPath= " + clientPath + "exist");
            }


            if (dirPathList.size() > 0) {
                return zipMultiFile(dirPathList, zipOutPath);
            }


        } catch (Exception e) {
            e.printStackTrace();
            LdLog.e(TAG, "zipCSFile error =" + e);
        }

        return false;
    }

    private boolean isCanWriteSDCard() {
        boolean item = false;
        try {
            StatFs stat = new StatFs(mPath);
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            long total = availableBlocks * blockSize;
            if (total > mMinSDCard) { //判断存储空间是否可以继续写入
                item = true;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return item;
    }


    public String setUploadPath(LdUploadItem uploadItem, String outPath) {
        String outNewFile = "";
        boolean isMulApp = uploadItem.isMultipleApp;
        if (isMulApp) {
            if (outPath.contains(FILE_SUFFIX)) {
                outNewFile = outPath.substring(0, outPath.length() - 4) + "_cs" + ".zip";
            } else {
                outNewFile = outPath + FILE_SUFFIX;
                outNewFile = outNewFile.substring(0, outNewFile.length() - 4) + "_cs" + ".zip";
            }
            uploadItem.setDelUploadFile(true);
        } else {
            outNewFile = outPath + FILE_SUFFIX;
            uploadItem.setDelUploadFile(false);
        }

        return outNewFile;

    }

    /// 压缩Log文件
    private String zipLogFile(String filePath, boolean deleteSourceFile) {
        if (null != filePath) {
            if (LdLog.sDebug) {
                Log.d(TAG, "zipLogFile handle path = " + filePath);
            }
            File file = new File(filePath);
            if (file.exists()) {
                String zipFilePath = filePath + FILE_SUFFIX;
                if (LdLog.sDebug) {
                    Log.d(TAG, "zipLogFile handle zipFilePath = " + zipFilePath);
                }
                return checkZipFile(filePath, zipFilePath, deleteSourceFile);
            }
        }


        return null;
    }

    private String checkZipFile(String filePath, String zipFilePath, boolean deleteSourceFile) {
        if (LdLog.sDebug) {
            Log.d(TAG, "checkZipFile filePath = " + filePath + " ,zipFilePath = " + zipFilePath);
        }
        if (zipFileOrDir(filePath, zipFilePath)) {
            if (deleteSourceFile) {
                boolean isSuccess = false;
                File subFile = new File(filePath);
                if (subFile.isDirectory()) {
                    isSuccess = deleteDirectory(filePath);
                } else {
                    isSuccess = subFile.delete(); //删除文件
                }
                if (LdLog.sDebug) {
                    Log.d(TAG, "delete file status = " + isSuccess + " path = " + filePath);
                }
                LdLog.d(TAG, "delete file status = " + isSuccess + " path = " + filePath);
            }

            return zipFilePath;
        } else {
            if (LdLog.sDebug) {
                Log.d(TAG, "checkZipFile  false zipFilePath = " + zipFilePath);
            }
            File zipFile = new File(zipFilePath);
            if (zipFile.exists()) {
                boolean isSuccess = zipFile.delete();
                if (LdLog.sDebug) {
                    Log.d(TAG, "checkZipFile delete file status = " + isSuccess + " path = " + filePath);
                }
                LdLog.d(TAG, "checkZipFile delete file status = " + isSuccess + " path = " + filePath);
            }


            return null;
        }
    }

    /// 获取日志文件名
    private String generateYMDLogFileName(long currentTime) {
        String logPath = mPath;
        if (null != logPath) {

            String detailDate = getCurrentTimeDate(currentTime);

            String fileSubPath = getCurrentTimeYMDate(currentTime);

            String directoryPath = logPath + fileSubPath;
            File file = new File(directoryPath);
            if (!file.exists()) {
                if (file.mkdirs()) {

                }
            }

            return directoryPath + File.separator + detailDate;
        }

        return null;
    }

    /// 获取带小时的日志文件名
    private String generateYmdhLogFileName(long currentTime) {
        String ymdPath = generateYMDLogFileName(currentTime);
        if (null != ymdPath) {

            String ymdhDate = getTimestampYYYYMMDDHH(currentTime);

            File file = new File(ymdPath);
            if (!file.exists()) {
                if (file.mkdirs()) {

                }
            }

            return ymdPath + File.separator + ymdhDate;
        }

        return null;
    }


    private boolean isZipFileName(String fileName) {
        return fileName.endsWith(FILE_SUFFIX);
    }


    private boolean isFileOrDir(Collection<String> fileList) {
        boolean isExist = false;
        if (fileList != null) {
            for (String filePath : fileList) {
                if (filePath != null) {
                    File file = new File(filePath);
                    if (file.exists() && (file.isFile() || file.isDirectory())) {
                        isExist = true;
                        break;
                    }
                }
            }
        }
        return isExist;
    }

}
