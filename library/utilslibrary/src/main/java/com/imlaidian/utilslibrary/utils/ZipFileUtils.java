package com.imlaidian.utilslibrary.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static com.imlaidian.utilslibrary.config.PublicConstant.CLIENT_SAVE_FOLDER;
import static com.imlaidian.utilslibrary.config.PublicConstant.SAVE_FOLDER;


public class ZipFileUtils {
    private static final int BUFFER_SIZE = 64*1024;
    /**
     * 压缩单个文件
     * @param filepath  文件路径
     * @param ziPath   zip文件路径
     * @return true / false
     */
    public static boolean zipSingleFile(String filepath, String ziPath) {
        try {
            File file = new File(filepath);
            File zipFile = new File(ziPath);
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
                           LogUtil.d("ZipFileUtils" , "zipMultiFile init dirPath =" +  dirPath);
                           input = new FileInputStream(dirFile);
                           String multZipPath ="" ;
                           if(dirPath.contains(SAVE_FOLDER)){
//                               if(dirPath.contains(".zip")){
//                                   multZipPath = "serverLog" +File.separator+ dirPath.substring(dirPath.lastIndexOf("/")+1 , dirPath.length()-4 );
//                               }else{
                                   multZipPath = "serverLog" +File.separator+ dirPath.substring(dirPath.lastIndexOf("/")+1);
//                               }


                           }else if(dirPath.contains(CLIENT_SAVE_FOLDER)){
//                               if(dirPath.contains(".zip")){
//                                   multZipPath = "clientLog" +File.separator+ dirPath.substring(dirPath.lastIndexOf("/")+1 , dirPath.length()-4);
//                               }else{
                                   multZipPath = "clientLog" +File.separator+ dirPath.substring(dirPath.lastIndexOf("/")+1);
//                               }


                           }
                           LogUtil.d("ZipFileUtils" , "zipMultiFile  multZipPath=" +  multZipPath);
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
                           LogUtil.d("ZipFileUtils" , "zipMultiFile  input over" );
                       } else{
                           LogUtil.d("ZipFileUtils" , "zipMultiFile dirPath not exist" +  dirPath);
                       }

                    }


                zipOut.close();

                LogUtil.d("ZipFileUtils" , "zipMultiFile  input zipOut" );

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 解压缩（解压缩单个文件）
     * @param zippath       zip文件路径
     * @param outfilepath   输入文件路径
     * @param filename      解压的文件名
     */
    public static boolean unzipSingleFile(String zippath ,String outfilepath ,String filename) {
        try {
            File file = new File(zippath);//压缩文件路径和文件名
            File outFile = new File(outfilepath);//解压后路径和文件名
            ZipFile zipFile = new ZipFile(file);
            ZipEntry entry = zipFile.getEntry(filename);//所解压的文件名
            InputStream input = zipFile.getInputStream(entry);
            OutputStream output = new FileOutputStream(outFile);

            int readlen = 0;
            byte[] buffer = new byte[BUFFER_SIZE];

            while(true) {
                readlen = input.read(buffer);
                if (readlen <= 0) {
                    break;
                }

                output.write(buffer, 0, readlen);
            }

            input.close();
            output.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }




    /**
     * 一次性压缩多个文件，文件存放至一个文件夹中
     * @param dirpath  目录路径, 要被压缩的文件夹
     * @param zippath  zip文件路径
     * @return true / false
     */
    public static boolean zipMultiFile(String dirpath ,String zippath) {
        try {
            File file = new File(dirpath); // 要被压缩的文件夹
            File zipFile = new File(zippath);
            InputStream input = null;
            ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));

            if (file.isDirectory()) {
                File[] files = file.listFiles();

                for (int i=0; i<files.length; i++) {
                    input = new FileInputStream(files[i]);
                    String filepath = file.getName() + File.separator + files[i].getName();

                    ZipEntry zipEntry = new ZipEntry(filepath);
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
                }
            }

            zipOut.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean unzipMultiFile(String file ,String outzippath){
        File outFile  = new File(file);
        return  unzipMultiFile(outFile ,outzippath);
    }

    /**
     * 解压缩（压缩文件中包含多个文件）可代替上面的方法使用。
     *   ZipInputStream类
     *   当我们需要解压缩多个文件的时候，ZipEntry就无法使用了，
     *   如果想操作更加复杂的压缩文件，我们就必须使用ZipInputStream类
     * @param file zip文件路径
     * @param outzippath 输入目录路径
     * */
    public static boolean unzipMultiFile(File file ,String outzippath){
        try {
            File outFile = null;
            ZipFile zipFile = new ZipFile(file);
            ZipInputStream zipInput = new ZipInputStream(new FileInputStream(file));
            ZipEntry entry = null;
            InputStream input = null;
            OutputStream output = null;

            while (true) {
                entry = zipInput.getNextEntry();
                if (null == entry) {
                    break;
                }

                String filepath = outzippath + File.separator + entry.getName();

                outFile = new File(filepath);

                if (!outFile.getParentFile().exists()) {
                    outFile.getParentFile().mkdir();
                }

                if (!outFile.exists()) {
                    outFile.createNewFile();
                }

                input = zipFile.getInputStream(entry);
                output = new FileOutputStream(outFile);

                int readlen = 0;
                byte[] buffer = new byte[BUFFER_SIZE];

                while (true) {
                    readlen = input.read(buffer);
                    if (readlen <= 0) {
                        break;
                    }

                    output.write(buffer, 0, readlen);
                }

                input.close();
                output.close();
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
