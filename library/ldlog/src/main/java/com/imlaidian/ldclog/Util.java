
package com.imlaidian.ldclog;


import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static android.text.TextUtils.isEmpty;
import static com.imlaidian.ldclog.LdLogThread.FILE_SUFFIX;

public class Util {

    private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd");

    private static final SimpleDateFormat yDateFormat = new SimpleDateFormat("yyyyMM");
    private static final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    private static final SimpleDateFormat hDateFormat = new SimpleDateFormat("yyyyMMddHH");
    private static final int BUFFER_SIZE = 64*1024;

    public final static Pattern yyyyMMddFomate = Pattern.compile("^[1-9]{1}\\d{3}[0-1]{1}\\d{1}\\d{2}$");
    public final static Pattern yyyyMMddHHFomate = Pattern.compile("^[1-9]{1}\\d{3}[0-1]{1}\\d{1}\\d{2}\\d{2}$");
    public final static Pattern yyyyMMFomate = Pattern.compile("^[1-9]{1}\\d{3}[0-1]{1}\\d{1}$");

    public static long getCurrentDayTime(long currentTime) {

        long tempTime = 0;
        try {
            String dataStr = sDateFormat.format(new Date(currentTime));
            tempTime = sDateFormat.parse(dataStr).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempTime;
    }

    public static long getCurrentDayHourTime() {
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.MINUTE,0);
        ca.set(Calendar.SECOND,0);
        ca.set(Calendar.MILLISECOND,0);
        return ca.getTime().getTime();
    }

    public static String getTimestampYYYYMMDDHH(long timestamp) {
        Date date = new Date(timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        String monthString = (month < 10) ? ("0" + month) : ("" + month);
        String dayString = (day < 10) ? ("0" + day) : ("" + day);
        String hourString = (hour < 10) ? ("0" + hour) : ("" + hour);

        return year + monthString + dayString  +  hourString;
    }


    public static String getCurrentTimeDate(long currentTime) {

       return sDateFormat.format(new Date(currentTime));

    }

    public static String getDateStr(long time) {
        return sDateFormat.format(new Date(time));
    }

    public static String getCurrentTimeYMDate(long currentTime) {
        return yDateFormat.format(new Date(currentTime));
    }




    public static String getDetailData(long time){

        return mDateFormat.format(new Date(time));
    }

    public static boolean zipFileOrDir(String serverPath,String zipOutPath )  {
        boolean isZipSuccess = false ;
        try {
            if(LdLog.sDebug){
                Log.d("Util" , "zipFileOrDir  zipOutPath=" +  zipOutPath + ",serverPath=" +serverPath);
            }

            if(serverPath !=null && !serverPath.equals("")){
                File serverFile  = new File(serverPath);

                if (!serverFile.exists()) {
                    if (new File(serverPath + FILE_SUFFIX).exists()) {
                        serverPath = serverPath + FILE_SUFFIX;
                        if(LdLog.sDebug){
                             Log.d("Util" , "zipFileOrDir  zipOutPath=" +  zipOutPath + ",serverPath=" +serverPath);
                        }
                        LdLog.d("Util", "zipFileOrDir serverPath =" + serverPath + " exist");
                        isZipSuccess =true ;
                    } else {
                        if(LdLog.sDebug) {
                            Log.d("Util", "zipMultiLogFile serverPath =" + serverPath + FILE_SUFFIX + "not exist");
                        }
                        serverPath = "";
                        LdLog.d("Util", "zipMultiLogFile serverPath =" + serverPath + FILE_SUFFIX + "not exist");
                    }
                }else{


                    if (serverFile.isDirectory()) {
                        if ( !serverPath.endsWith(File.separator) )//serverPath不以分隔符结尾则自动添加分隔符
                        {
                            serverPath = serverPath + File.separator;
                        }
                        if(LdLog.sDebug){
                            Log.d("Util", "delete file status = " + serverPath + " is Directory");
                        }
                        LdLog.d("Util", "delete file status = " + serverPath + " is Directory");
                        serverFile = new File(serverPath);//根据指定的文件名创建File对象

                        String[] files = serverFile.list();
                        // 文件夹路径下没被压缩等文件需要压缩下文件
                        if(files!=null && files.length>0){
                            int updateSize= 0 ;
                            for(int i =0 ; i< files.length ; i++){
                                File dirFile = new File(serverFile, files[i]);
                                String filePathName = dirFile.getAbsolutePath();
                                LdLog.d("LdUtil", "zip file  Directory  file =" + filePathName  );

                                boolean isExist =dirFile.exists() ;
                                boolean isFile = dirFile.isFile() ;
                                boolean isZip = filePathName.endsWith(FILE_SUFFIX);

                                if(LdLog.sDebug) {
                                    Log.d("LdUtil", "zip file  Directory  file =" + filePathName  + ",dirFile.exists()=" +isExist + ",isFile ="+isFile +",isZip=" +isZip);
                                }
                                if( isExist && isFile  && !isZip){
                                   boolean zip =zipSingleFile(filePathName,filePathName +FILE_SUFFIX );
                                   if(zip){
                                      boolean del = new File(filePathName).delete() ;
                                      Log.d("LdUtil", "zip file  Directory file del =" + del  );
                                   }
                                    updateSize ++ ;
                                    Log.d("LdUtil", "zip file  Directory file zip =" + zip  );
                                }else{
                                    Log.d("LdUtil", "not need zip "  );
                                }
                            }

                            if(updateSize>0){
                                files = serverFile.list();
                            }

                        }


                        if(files!=null && files.length>0){
                            InputStream input = null;
                            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipOutPath));
                            for(int i =0 ; i< files.length ; i++){

                                File dirFile = new File(serverFile, files[i]);
                                if(LdLog.sDebug){
                                    Log.d("Util" , "zipFileOrDir  dirFile name=" +  dirFile.getAbsolutePath());
                                }
                                if(dirFile.exists()){
                                    input = new FileInputStream(dirFile);
                                    String name =dirFile.getName();
                                    if(LdLog.sDebug){
                                        Log.d("Util" , "zipFileOrDir  name=" +  name);
                                    }
                                    LdLog.d("Util" , "zipFileOrDir  name=" +  name);
                                    ZipEntry zipEntry = new ZipEntry(name);
                                    zipOut.putNextEntry(zipEntry);

                                    int readlen = 0;
                                    byte[] buffer = new byte[BUFFER_SIZE];

                                    while(true) {
                                        readlen = input.read(buffer);
                                        if (readlen <= 0) {
                                            break;
                                        }

                                        zipOut.write(buffer, 0, readlen);
                                    }

                                    input.close();
                                    if(LdLog.sDebug){
                                        Log.d("Util", "zipFileOrDir  input over");
                                    }
                                    LdLog.d("Util" , "zipFileOrDir  input over" );
                                } else{
                                    LdLog.d("Util" , "zipFileOrDir dirPath not exist =" +  dirFile);
                                }

                            }
                            zipOut.close();
                            isZipSuccess = true ;
                        }else{
                            // 又是文件夹删除不了，已打包的文件会被删除了
                            if(new File(zipOutPath).exists()){
                                isZipSuccess = true ;
                            }
                        }


                    } else {
                        InputStream input = null;
                        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipOutPath));
                        input = new FileInputStream(serverFile);
                        ZipEntry zipEntry = new ZipEntry(serverFile.getName());
                        zipOut.putNextEntry(zipEntry);

                        int readlen = 0;
                        byte[] buffer = new byte[BUFFER_SIZE];

                        while(true) {
                            readlen = input.read(buffer);
                            if (readlen <= 0) {
                                break;
                            }
                            zipOut.write(buffer, 0, readlen);
                        }

                        input.close();
                        zipOut.close();
                        buffer = null;

                        isZipSuccess = true;

                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            LdLog.e("Util" , "zipFileOrDir dirPath not exist =" +  e);
        }

        return isZipSuccess ;
    }
    /**
     * 压缩单个文件或者文件夹
     * @param serverPath  源文件
     * param  zipOutPath 压缩文件
     * @return true / false
     */
    public static boolean zipSingleFile(String serverPath ,String zipOutPath) {
        try {

            File file = new File(serverPath);

            File zipFile = new File(zipOutPath);
            InputStream input = new FileInputStream(file);
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));

            ZipEntry zipEntry = new ZipEntry(file.getName());
            zipOut.putNextEntry(zipEntry);

            int readlen = 0;
            byte[] buffer = new byte[BUFFER_SIZE];

            while(true) {
                readlen = input.read(buffer);
                if (readlen <= 0) {
                    break;
                }
                zipOut.write(buffer, 0, readlen);
            }

            input.close();
            zipOut.close();

            buffer = null;

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean copyFile(String src, String des) {
        boolean back = false;
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(new File(src));
            outputStream = new FileOutputStream(new File(des));
            byte[] buffer = new byte[BUFFER_SIZE];
            int i;
            while ((i = inputStream.read(buffer)) >= 0) {
                outputStream.write(buffer, 0, i);
                outputStream.flush();
            }
            back = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return back;
    }

    /// 获取路径最后的部分
    public static String getLastPathComponent(String path) {
        if (null != path && path.length() > 0) {
            int index = path.lastIndexOf(File.separatorChar);

            if (index >= 0) {
                return path.substring(index + 1);
            }
        }

        return "";
    }

    // 区分c/s 文件路径
    public static boolean zipMultiFile(ArrayList<String> dirPathList, String zipPath) {
        try {
            File zipFile = new File(zipPath);
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
            InputStream input = null;
            for(int i =0 ; i< dirPathList.size() ; i++){
                String dirPath = dirPathList.get(i);
                File dirFile = new File(dirPath);
                if(dirFile.exists()){
                    Log.d("Util" , "zipMultiFile init dirPath =" +  dirPath);
                    input = new FileInputStream(dirFile);
                    String multZipPath ="" ;

                    multZipPath = "logFile_"  + i + File.separator+ dirPath.substring(dirPath.lastIndexOf("/")+1);

                    Log.d("Util" , "zipMultiFile  multZipPath=" +  multZipPath);
                    ZipEntry zipEntry = new ZipEntry(multZipPath);
                    zipOut.putNextEntry(zipEntry);

                    int readlen = 0;
                    byte[] buffer = new byte[BUFFER_SIZE];

                    while(true) {
                        readlen = input.read(buffer);
                        if (readlen <= 0) {
                            break;
                        }

                        zipOut.write(buffer, 0, readlen);
                    }

                    input.close();
                    Log.d("Util" , "zipMultiFile  input over" );
                } else{
                    Log.d("Util" , "zipMultiFile dirPath not exist" +  dirPath);
                }

            }


            zipOut.close();

            Log.d("Util" , "zipMultiFile  input zipOut" );

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    /**
     * 判断是不是一个yyyyMM 格式明
     */
    public static boolean isYYYYMM(String  date) {
        if (isEmpty(date))
            return false;
        return yyyyMMFomate.matcher(date).matches();
    }

    public static boolean isYYYYMMdd(String date){
        if (isEmpty(date))
            return false;
        return yyyyMMddFomate.matcher(date).matches();

    }

    public static boolean isYYYYMMddHH(String date){
        if (isEmpty(date))
            return false;
        return yyyyMMddHHFomate.matcher(date).matches();

    }

    public static long parseYYYYMMData(String date) {
        long tempTime = 0;
        try {
            tempTime = yDateFormat.parse(date).getTime();
        }catch (Exception e){
            e.printStackTrace();
        }
        return  tempTime ;
    }

    public static long parseYmdData(String date){
        long tempTime = 0;
        try {
            tempTime = sDateFormat.parse(date).getTime();
        }catch (Exception e){
            e.printStackTrace();
        }
        return  tempTime ;
    }

    public static long parseYmdHHData(String date){
        long tempTime = 0;
        try {
            tempTime = hDateFormat.parse(date).getTime();
        }catch (Exception e){
            e.printStackTrace();
        }
        return  tempTime ;
    }


    /**
     * 删除指定的目录以及目录下的所有子文件
     * @param dirPath is 目录路径
     * @return true or false 成功返回true，失败返回false
     */
    public static boolean deleteDirectory(String dirPath){

        boolean isSuccess =false ;
        if (!dirPath.endsWith(File.separator) ){
            //dirPath不以分隔符结尾则自动添加分隔符
            dirPath = dirPath + File.separator;
        }


        File dirFile = new File(dirPath);//根据指定的文件名创建File对象

        if ( !dirFile.exists() || ( !dirFile.isDirectory() ) ){ //目录不存在或者
            return false;
        }

        try {
            if (dirFile.exists()) {
                if (dirFile.isDirectory()) {
                    String[] children = dirFile.list();
                    if(children!=null && children.length>0){
                        for (int i = 0; i < children.length; i++) {
                            boolean success = new File(dirFile, children[i]).delete();
                            if (!success) {
                                return false;
                            }
                        }
                    }
                    // 目录此时为空，可以删除
                    isSuccess = dirFile.delete();
                    Log.d("Utils" , "deleteDirectory isSuccess=" +  isSuccess);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return isSuccess;

    }



}
