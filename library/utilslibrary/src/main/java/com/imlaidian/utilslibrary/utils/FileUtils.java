package com.imlaidian.utilslibrary.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.util.Log;

import com.imlaidian.utilslibrary.UtilsApplication;
import com.imlaidian.utilslibrary.utilsManager.ThreadPoolManager;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import static com.imlaidian.utilslibrary.config.PublicConstant.getSaveAppFolder;


public class FileUtils {
	private final static String TAG = FileUtils.class.getSimpleName();

	private String sdPath;

	public String getSDPath() {
		return sdPath;
	}

	public FileUtils() {
		sdPath = Environment.getExternalStorageDirectory() + "/";
	}
	
    /**
	 * 写文本文件, 先确保fileName 文件存在，否则打开会失败
	 */
	public static boolean write(Context context, String fileName, String content) {

		if (content == null) {
            content = "";
        }

		try {
			FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);

			fileOutputStream.write(content.getBytes());
			fileOutputStream.close();

            return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

        return false;
	}

	/**
	 * 读取文本文件
	 */
	public static String read(Context context, String fileName) {
		try {
			FileInputStream fileInputStream = context.openFileInput(fileName);
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int length = -1;

            while (-1 != (length = fileInputStream.read(buffer))) {
                byteOutputStream.write(buffer, 0, length);
            }

            byteOutputStream.close();
            fileInputStream.close();

            return byteOutputStream.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

    /// 创建文件
	public static File createFile(String dirPath, String fileName) {
		File destDir = new File(dirPath);

		if (!destDir.exists()) {
            if (!destDir.mkdir()) {
                return null;
            }
		}

		return new File(dirPath, fileName);
	}

	/**
	 * 向手机写图片
	 */
	public static boolean writeFile(byte[] buffer, String folder, String fileName) {
		boolean writeSuccess = false;
		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

		String folderPath = "";
		if (sdCardExist) {
			folderPath = Environment.getExternalStorageDirectory() + File.separator + folder + File.separator;
		} else {
			writeSuccess = false;
		}

		File fileDir = new File(folderPath);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

		File file = new File(folderPath + fileName);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			out.write(buffer);
			writeSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return writeSuccess;
	}

	/**
	 * 根据文件绝对路径获取文件名
	 */
	public static String getFileName(String filePath) {
		if (StringsUtils.isEmpty(filePath)) {
            return "";
        }

		if (-1 != filePath.lastIndexOf(".")) {
			String suffix = filePath.substring(filePath.lastIndexOf("."));

		   	String filename = MD5Util.MD5Lower16(filePath.substring(0, filePath.lastIndexOf("."))) + suffix;

		    return filename;
		}

		return "";
	}


	/**
	 * 根据文件的绝对路径获取文件名但不包含扩展名
	 */
	public static String getFileNameNoFormat(String filePath) {
		if (StringsUtils.isEmpty(filePath)) {
			return "";
		}

		int point = filePath.lastIndexOf('.');

		return filePath.substring(filePath.lastIndexOf(File.separator) + 1, point);
	}

	public static String getFileTotalName(String filePath){
		if (StringsUtils.isEmpty(filePath)) {
			return "";
		}

		return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
	}

	/**
	 * 获取文件扩展名
	 */
	public static String getFileFormat(String fileName) {
		if (StringsUtils.isEmpty(fileName)) {
            return "";
        }

		int point = fileName.lastIndexOf('.');

		return fileName.substring(point + 1);
	}


	public static  String getFilePath(String filePath){
		if (StringsUtils.isEmpty(filePath)) {
			return "";
		}

		int pathSerparator = filePath.lastIndexOf(File.separator);

		return filePath.substring(0 ,pathSerparator-1 );

	}

	/**
	 * 获取文件大小
	 */
	public static long getFileSize(String filePath) {
		long size = 0;

		File file = new File(filePath);
		if (file.exists()) {
			size = file.length();
		}

		return size;
	}

	/**
	 * 获取文件大小
	 */
	public static String getFileSizeString(long size) {
		if (size <= 0) {
            return "0";
        }

		java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
		float temp = (float) size / 1024;
		if (temp >= 1024) {
			return df.format(temp / 1024) + "M";
		} else {
			return df.format(temp) + "K";
		}
	}

	/**
	 * 转换文件大小
	 * 
	 * @return B/KB/MB/GB
	 */
	public static String formatFileSize(long fileSize) {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		String fileSizeString = "0B";
		if (fileSize == 0) {
			return fileSizeString;
		} else if (fileSize < 1024) {
			fileSizeString = df.format((double) fileSize) + "B";
		} else if (fileSize < 1048576) {
			fileSizeString = df.format((double) fileSize / 1024) + "KB";
		} else if (fileSize < 1073741824) {
			fileSizeString = df.format((double) fileSize / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileSize / 1073741824) + "G";
		}

		return fileSizeString;
	}

	/**
	 * 获取目录文件大小，递归获取子目录大小
	 * file的 list、listFiles 方法在部分有乱码文件的设备上会crash,且无法捕获,慎用
	 * 推荐用命令的方式获取
	 */
	@Deprecated
	public static long getDirectorySizeWithRecursion(File dir) {
		if (dir == null) {
			return 0;
		}

		if (!dir.isDirectory()) {
			return 0;
		}

		long dirSize = 0;
		File[] files = dir.listFiles();
		if (files == null) {
			return dirSize;
		}
		for (File file : files) {
			if (file.isFile()) {
				dirSize += file.length();
			} else if (file.isDirectory()) {
				dirSize += file.length();
				dirSize += getDirectorySizeWithRecursion(file); // 递归调用继续统计
			}
		}

		return dirSize;
	}

	/**
	 * 获取目录下文件个数，递归获取子目录个数
	 * file的 list、listFiles 方法在部分有乱码文件的设备上会crash,且无法捕获,慎用
	 * 推荐用命令的方式获取
	 */
	@Deprecated
	public static long getFileListCountWithRecursion(File dir) {
		File[] files = dir.listFiles();
		if(files == null){
			return 0L;
		}
		long count = files.length;

		for (File file : files) {
			if (file.isDirectory()) {
				count = count + getFileListCountWithRecursion(file);// 递归
				count--;
			}
		}

		return count;
	}

	/**
	 * 列出root目录下所有子目录
	 *  file的 list、listFiles 方法在部分有乱码文件的设备上会crash,且无法捕获,慎用
	 *  推荐用命令的方式获取
	 * @return 绝对路径
	 */
	@Deprecated
	public static List<String> listPath(String root) {
		List<String> allDir = new ArrayList<String>();
		SecurityManager checker = new SecurityManager();
		File path = new File(root);
		checker.checkRead(root);

		// 过滤掉以.开始的文件夹
		if (path.isDirectory()) {
			for (File f : path.listFiles()) {
				if (f.isDirectory() && !f.getName().startsWith(".")) {
					allDir.add(f.getAbsolutePath());
				}
			}
		}

		return allDir;
	}

	/**
	 * 获取一个文件夹下的所有文件
	 * file的 list、listFiles 方法在部分有乱码文件的设备上会crash,且无法捕获,慎用
	 * 推荐用命令的方式获取
	 */
	@Deprecated
	public static List<File> listPathFiles(String root) {
		List<File> allDir = new ArrayList<File>();
		SecurityManager checker = new SecurityManager();
		File path = new File(root);
		checker.checkRead(root);
		File[] files = path.listFiles();
		if(files == null){
			return  allDir;
		}
		for (File f : files) {
			if (f.isFile()) {
				allDir.add(f);
			} else {
				listPath(f.getAbsolutePath());
			}
		}

		return allDir;
	}


	public static byte[] toBytes(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int ch = 0;

		while ((ch = in.read()) != -1) {
			out.write(ch);
		}

		byte buffer[] = out.toByteArray();
		out.close();

		return buffer;
	}

	/**
	 * 检查文件是否存在
	 */
	public static boolean checkFileExists(String name) {
		boolean status = false;

		if (!name.equals("")) {
			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + name);

			status = newPath.exists();
		}

		return status;
	}

	/**
	 * 检查路径是否存在
	 */
	public static boolean checkFilePathExists(String path) {
		return new File(path).exists();
	}

	/**
	 * 计算SD卡的剩余空间
	 * 
	 * @return 返回-1，说明没有安装sd卡
	 */
	@SuppressWarnings("deprecation")
	public static long getFreeDiskSpace() {
		String status = Environment.getExternalStorageState();
		long freeSpace = 0;
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			try {
				File path = Environment.getExternalStorageDirectory();
				StatFs stat = new StatFs(path.getPath());
				long blockSize = stat.getBlockSize();
				long availableBlocks = stat.getAvailableBlocks();
				freeSpace = availableBlocks * blockSize / 1024;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return -1;
		}
		return (freeSpace);
	}

	/**
	 * 新建目录
	 */
	public static boolean createDirectory(String directoryName) {
		boolean status = false;

		if (!directoryName.equals("")) {
			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + directoryName);

			status = newPath.mkdir();
		}

		return status;
	}

	/**
	 * 检查是否安装SD卡
	 */
	public static boolean checkSaveLocationExists() {
		String sdCardStatus = Environment.getExternalStorageState();
		boolean status = false;

		if (sdCardStatus.equals(Environment.MEDIA_MOUNTED)) {
			status = true;
		}

		return status;
	}
	
	/**
	 * 检查是否安装外置的SD卡
	 */
	public static boolean checkExternalSDExists() {
		Map<String, String> evn = System.getenv();

		return evn.containsKey("SECONDARY_STORAGE");
	}

	/**
	 * 删除目录(包括：目录里的所有文件)
	 */
	public static boolean deleteDirectory(String fileName) {
		boolean status = false;
		SecurityManager checker = new SecurityManager();
		
		if (!fileName.equals("")) {
			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + fileName);
			checker.checkDelete(newPath.toString());

			if (newPath.isDirectory()) {
				String[] listfile = newPath.list();
				// delete all files within the specified directory and then
				// delete the directory
				try {
					for (int i = 0; i < listfile.length; i++) {
						File deletedFile = new File(newPath.toString() + File.separator
								+ listfile[i].toString());

						deletedFile.delete();
					}

                    status = newPath.delete();
				} catch (Exception e) {
					e.printStackTrace();
					status = false;
				}
			}
		}

		return status;
	}


	/**
	 * 删除目录(包括：目录里的所有文件 )
	 */
	public static boolean deleteDirectoryByPath(String fileName) {
		boolean status = false;
		SecurityManager checker = new SecurityManager();

		if (!fileName.equals("")) {
			File newPath = new File(fileName);
			checker.checkDelete(newPath.toString());

			if (newPath.isDirectory()) {
				String[] listfile = newPath.list();
				// delete all files within the specified directory and then
				// delete the directory
				try {
					for (int i = 0; i < listfile.length; i++) {
						File deletedFile = new File(newPath.toString() + File.separator + listfile[i].toString());

						deletedFile.delete();
					}

					status = newPath.delete();
				} catch (Exception e) {
					e.printStackTrace();
					status = false;
				}
			}
		}

		return status;
	}

	/**
	 *  删除目录(包括：目录里的所有文件 )
	 * @param fileName
	 * @param isNeedDeleteDir 是否需要删除目录
	 * @return
	 */
	public static boolean deleteDirectoryByPath(String fileName, boolean isNeedDeleteDir) {
		boolean status = false;
		SecurityManager checker = new SecurityManager();
		if (StringsUtils.isEmpty(fileName)) {
			return status;
		}
		File newPath = new File(fileName);
		checker.checkDelete(newPath.toString());

		if (newPath.isDirectory()) {
			String[] listfile = newPath.list();
			if (listfile == null) {
				return status;
			}
			try {
				int deleteCount = 0;
				for (int i = 0; i < listfile.length; i++) {
					File deletedFile = new File(newPath.toString() + File.separator + listfile[i].toString());
					if (deletedFile.delete()) {
						deleteCount++;
					}
				}
				//如果删除了所有的子文件,则status置为true
				if (deleteCount == listfile.length) {
					status = true;
				}
				if (isNeedDeleteDir) {
					status = newPath.delete();
				}
			} catch (Exception e) {
				LogUtil.d(TAG, "deleteDirectoryByPath error:" + e);
				e.printStackTrace();
				status = false;
			}
		}
		return status;
	}

	public static boolean deleteWholeFile(String fileName){

		boolean status = false;
		SecurityManager checker = new SecurityManager();

		if (!fileName.equals("")) {
			File newPath = new File(fileName);
			if(newPath!=null&& newPath.exists()){
				checker.checkDelete(newPath.toString());

				if (newPath.isDirectory()) {
					String[] listfile = newPath.list();
					// delete all files within the specified directory and then
					// delete the directory
					try {
						for (int i = 0; i < listfile.length; i++) {
							File deletedFile = new File(newPath.toString() + File.separator + listfile[i].toString());

							boolean deleted = deletedFile.delete();
							Log.d("fileUtils" , "newPath=" + newPath + ",deleted=" +deleted);
						}
						if(!File.separator.equals(fileName.substring(fileName.length()-1))){
							status = newPath.delete();
						}

						Log.d("fileUtils" , "status=" +status);
					} catch (Exception e) {
						e.printStackTrace();
						status = false;
					}
				}else{
					status = newPath.delete();
					Log.d("fileUtils" , "status=" +status);
				}
			}

		}

		return status;
	}

	/**
	 * 删除文件
	 */
	public static boolean deleteFile(String fileName) {
		boolean status = false;
		SecurityManager checker = new SecurityManager();

		if (!fileName.equals("")) {
			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + fileName);
			checker.checkDelete(newPath.toString());

			if (newPath.isFile()) {
				try {
                    status = newPath.delete();
				} catch (SecurityException se) {
					se.printStackTrace();
				}
			}
		}

		return status;
	}

	/**
	 * 删除文件
	 * @param filePath 文件路径
	 * @return true / false
     */
	public static boolean deleteFileFromPath(String filePath) {
		SecurityManager checker = new SecurityManager();

		if (null != filePath && filePath.length() > 0) {
			checker.checkDelete(filePath);
			File file = new File(filePath);

			if (file.isFile()) {
				try {
					return file.delete();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return false;
	}

	public static boolean deleteDirectoryPath(String directoryPath) {
        SecurityManager checker = new SecurityManager();

        if (null != directoryPath && directoryPath.length() > 0) {
            if (!directoryPath.endsWith(File.separator)) {
                directoryPath = directoryPath + File.separator;
            }

            checker.checkDelete(directoryPath);
            File file = new File(directoryPath);

            if (file.isFile()) {
                return file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                boolean isSuccess = true;
				if(files !=null&&files.length>0){
					for (File subFile : files) {
						String subFullPath = subFile.getAbsolutePath();

						if (subFile.isFile()) {
							isSuccess = subFile.delete();
						} else if (subFile.isDirectory()
								&& !subFile.getName().equals(".")
								&& !subFile.getName().equals("..")) {
							isSuccess = deleteDirectoryPath(subFullPath);
							if (isSuccess) {
								isSuccess = subFile.delete();
							}
						}

						if (!isSuccess) {
							break;
						}
					}
				}
				// 删除文件夹
				try {
					File[] newFiles = file.listFiles();
					if(newFiles !=null && newFiles.length==0 &&file!=null && file.exists()){
						boolean isDel = file.delete();
					}

				}catch (Exception  e){
					 e.printStackTrace();
				}




                return isSuccess;
            }
        }

        return false;
    }

	/**
	 * 删除空目录
	 * 
	 * 返回 0代表成功 ,1 代表没有删除权限, 2代表不是空目录,3 代表未知错误
	 */
	public static int deleteBlankPath(String path) {
		File f = new File(path);
		if (!f.canWrite()) {
			return 1;
		}

		if (f.list() != null && f.list().length > 0) {
			return 2;
		}

		if (f.delete()) {
			return 0;
		}

		return 3;
	}

	/**
	 * 重命名
	 */
	public static boolean renameFile(String oldName, String newName) {
		File f = new File(oldName);
		return f.renameTo(new File(newName));
	}

	/**
	 * 删除文件
	 */
	public static boolean deleteFileWithPath(String filePath) {
		String newFilePath = filePath + System.currentTimeMillis() ;
		SecurityManager checker = new SecurityManager();
		File f = new File(filePath);
		File newFile =  new File(newFilePath);
		f.renameTo(newFile);
		checker.checkDelete(newFilePath);

        return newFile.isFile() && newFile.delete();
	}
	
	/**
	 * 清空一个文件夹
	 */
	public static void clearFileWithPath(String filePath) {
		List<File> files = FileUtils.listPathFiles(filePath);

		if (files.isEmpty()) {
			return;
		}

		for (File f : files) {
			if (f.isDirectory()) {
				clearFileWithPath(f.getAbsolutePath());
			} else {
				f.delete();
			}
		}
	}

	/**
	 * 获取SD卡的根目录
	 */
	public static String getSDRootPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	
	/**
	 * 获取手机外置SD卡的根目录
	 */
	public static String getExternalSDRoot() {
		Map<String, String> evn = System.getenv();

		return evn.get("SECONDARY_STORAGE");
	}

	public enum PathStatus {
		SUCCESS, EXITS, ERROR
	}

	/**
	 * 创建目录
	 */
	public static PathStatus createPath(String newPath) {
		File path = new File(newPath);
		if (path.exists()) {
			return PathStatus.EXITS;
		}

		if (path.mkdir()) {
			return PathStatus.SUCCESS;
		} else {
			return PathStatus.ERROR;
		}
	}

	/**
	 * 截取路径名
	 */
	public static String getPathName(String absolutePath) {
		int start = absolutePath.lastIndexOf(File.separator) + 1;
		int end = absolutePath.length();

		return absolutePath.substring(start, end);
	}
	
	/**
	 * 获取应用程序缓存文件夹下的指定目录
	 */
	public static String getAppCache(Context context, String dir) {
		String savePath = context.getCacheDir().getAbsolutePath() + File.separator + dir + File.separator;
		File savedir = new File(savePath);

		if (!savedir.exists()) {
			savedir.mkdirs();
		}

		savedir = null;

		return savePath;
	}
	
	/**
	 * 读取语音数据并缓存在本地
	 * @param responseBody
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static int saveArm(byte[] responseBody) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File dir = new File(Environment.getExternalStorageDirectory(), "yishihu/amr");
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File file = new File(dir, "/amr_" + new SimpleDateFormat("_yyyy_MM_dd_hh_mm_ss").format(new Date()) + ".amr");
			ByteArrayInputStream in = new ByteArrayInputStream(responseBody);
			BufferedOutputStream out = null;

			try {
				out = new BufferedOutputStream(new FileOutputStream(file));
				int len = 0;
				byte[] buffer = new byte[1024 * 4];
				while ((-1 != (len = in.read(buffer)))) {
					out.write(buffer, 0, len);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (out != null) {
						out.flush();
						out.close();
					}
					if (in != null) {
						in.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				if (file != null && file.exists() && file.length() >= responseBody.length) {
					MediaPlayer player = new MediaPlayer();
					player.setDataSource(file.getPath());
					player.prepare();
					player.start();
					int second = Math.round((player.getDuration() / 1000.0f));
					return second;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return -1;
	}
	
	/**
	 * 在SD卡上创建文件
	 * 
	 * @throws IOException
	 */
	public File creatSDFile(String fileName) throws IOException {
		File file = new File(sdPath + fileName);
		file.createNewFile();

		return file;
	}

	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirName
	 */
	public File creatSDDir(String dirName) {
		File dir = new File(sdPath + dirName);
		dir.mkdir();

		return dir;
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 */
	public boolean isFileExist(String fileName) {
		File file = new File(sdPath + fileName);
		return file.exists();
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 */
	public File write2SDFromInput(String path, String fileName, InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			creatSDDir(path);
			file = creatSDFile(path + fileName);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[4 * 1024];
			while ((input.read(buffer)) != -1) {
				output.write(buffer);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * 将文件保存到本地
	 */
	public static void saveFileCache(byte[] fileData, String folderPath,
									 String fileName) {
		File folder = new File(folderPath);
		folder.mkdirs();
		File file = new File(folderPath, fileName);
		ByteArrayInputStream is = new ByteArrayInputStream(fileData);
		OutputStream os = null;
		if (!file.exists()) {
			try {
				file.createNewFile();
				os = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while (-1 != (len = is.read(buffer))) {
					os.write(buffer, 0, len);
				}
				os.flush();
			} catch (Exception e) {
				throw new RuntimeException(
						FileUtils.class.getClass().getName(), e);
			} finally {
				closeIO(is, os);
			}
		}
	}

	/**
	 * 从指定文件夹获取文件
	 *
	 * @return 如果文件不存在则创建,如果如果无法创建文件或文件名为空则返回null
	 */
	public static File getSaveFile(String folderPath, String filename) {
		String path = getSavePath(folderPath) + File.separator + filename;

		return getSaveFile(path);
	}

	public static File getSaveFile(String path) {
		File file = new File(path);
		try {
            if (file.createNewFile()) {
                Log.d("FileUtils", "create file succss path" +path);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}

		return file;
	}


	/**
	 * 获取SD卡下指定文件夹的绝对路径
	 *
	 * @return 返回SD卡下的指定文件夹的绝对路径
	 */
	public static String getSavePath(String folderName) {
		return getSaveFolder(folderName).getAbsolutePath();
	}


	/**
	 * 从指定文件夹获取文件
	 *
	 * @return 如果文件不存在则创建,如果如果无法创建文件或文件名为空则返回null
	 */
	public static File getSaveCacheFile(String folderPath, String filename) {
		String path = saveAppCacheFolder(folderPath).getAbsolutePath() + File.separator + filename;

		return getSaveFile(path);
	}

	public static File saveAppCacheFolder(String folderName) {
		File file = new File(getAppCacheFile() + File.separator + folderName
				+ File.separator);

		if(file.mkdirs()){
			LogUtil.d(TAG ,"folderName = " +folderName + "create success");
		}
		return file;
	}

	/**
	 * 获取文件夹对象
	 *
	 * @return 返回SD卡下的指定文件夹对象，若文件夹不存在则创建
	 */
	public static File getSaveFolder(String folderName) {
		File file = new File(getSDCardPath() + File.separator + folderName
				+ File.separator);

		if(file.mkdirs()){
			LogUtil.d(TAG ,"folderName = " +folderName + "create success");
		}
		return file;
	}

	public static String getAppCacheFile(){
		return UtilsApplication.getInstance().getExternalCacheDir().getAbsolutePath();
	}

	public static String getSDCardPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	/**
	 * 输入流转byte[]<br>
	 */
	public static final byte[] input2byte(InputStream inStream) {
		if (inStream == null) {
			return null;
		}
		byte[] in2b = null;
		BufferedInputStream in = new BufferedInputStream(inStream);
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		int rc = 0;
		try {
			while ((rc = in.read()) != -1) {
				swapStream.write(rc);
			}
			in2b = swapStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeIO(inStream, in, swapStream);
		}
		return in2b;
	}

	/**
	 * 把uri转为File对象
	 */
	public static File uri2File(Activity aty, Uri uri) {
		if (SystemTool.getSDKVersion() < 11) {
			// 在API11以下可以使用：managedQuery
			String[] proj = { MediaStore.Images.Media.DATA };
			@SuppressWarnings("deprecation")
			Cursor actualimagecursor = aty.managedQuery(uri, proj, null, null,
					null);
			int actual_image_column_index = actualimagecursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			actualimagecursor.moveToFirst();
			String img_path = actualimagecursor
					.getString(actual_image_column_index);
			return new File(img_path);
		} else {
			// 在API11以上：要转为使用CursorLoader,并使用loadInBackground来返回
			String[] projection = { MediaStore.Images.Media.DATA };
			CursorLoader loader = new CursorLoader(aty, uri, projection, null,
					null, null);
			Cursor cursor = loader.loadInBackground();
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return new File(cursor.getString(column_index));
		}
	}

	/**
	 * 复制文件
	 *
	 * @param from
	 * @param to
	 */
	public static void copyFile(File from, File to) {
		if (null == from || !from.exists()) {
			return;
		}
		if (null == to) {
			return;
		}
		FileInputStream is = null;
		FileOutputStream os = null;
		try {
			is = new FileInputStream(from);
			if (!to.exists()) {
				to.createNewFile();
			}
			os = new FileOutputStream(to);
			copyFileFast(is, os);
		} catch (Exception e) {
			throw new RuntimeException(FileUtils.class.getClass().getName(), e);
		} finally {
			closeIO(is, os);
		}
	}

	/**
	 * 快速复制文件（采用nio操作）
	 *
	 * @param is
	 *            数据来源
	 * @param os
	 *            数据目标
	 * @throws IOException
	 */
	public static void copyFileFast(FileInputStream is, FileOutputStream os)
			throws IOException {
		FileChannel in = is.getChannel();
		FileChannel out = os.getChannel();
		in.transferTo(0, in.size(), out);
	}

	/**
	 * 关闭流
	 *
	 * @param closeables
	 */
	public static void closeIO(Closeable... closeables) {
		if (null == closeables || closeables.length <= 0) {
			return;
		}
		for (Closeable cb : closeables) {
			try {
				if (null == cb) {
					continue;
				}
				cb.close();
			} catch (IOException e) {
				throw new RuntimeException(
						FileUtils.class.getClass().getName(), e);
			}
		}
	}

	/**
	 * 图片写入文件
	 *
	 * @param bitmap
	 *            图片
	 * @param filePath
	 *            文件路径
	 * @return 是否写入成功
	 */
	public static boolean bitmapToFile(Bitmap bitmap, String filePath) {
		boolean isSuccess = false;
		if (bitmap == null) {
			return isSuccess;
		}
		File file = new File(filePath.substring(0,
				filePath.lastIndexOf(File.separator)));
		if (!file.exists()) {
			file.mkdirs();
		}

		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(filePath),
					8 * 1024);
			isSuccess = bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			closeIO(out);
		}
		return isSuccess;
	}

	/**
	 * 从文件中读取文本
	 *
	 * @param filePath
	 * @return
	 */
	public static String readFile(String filePath) {
		InputStream is = null;
		try {
			is = new FileInputStream(filePath);
		} catch (Exception e) {
			throw new RuntimeException(FileUtils.class.getName()
					+ "readFile---->" + filePath + " not found");
		}
		return inputStream2String(is);
	}

	/**
	 * 从assets中读取文本
	 *
	 * @param name
	 * @return
	 */
	public static String readFileFromAssets(Context context, String name) {
		InputStream is = null;
		try {
			is = context.getResources().getAssets().open(name);
		} catch (Exception e) {
			throw new RuntimeException(FileUtils.class.getName()
					+ ".readFileFromAssets---->" + name + " not found");
		}
		return inputStream2String(is);
	}

	/**
	 * 输入流转字符串
	 *
	 * @param is
	 * @return 一个流中的字符串
	 */
	public static String inputStream2String(InputStream is) {
		if (null == is) {
			return null;
		}
		StringBuilder resultSb = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			resultSb = new StringBuilder();
			String len;
			while (null != (len = br.readLine())) {
				resultSb.append(len);
			}
		} catch (Exception ex) {
		} finally {
			closeIO(is);
		}
		return null == resultSb ? null : resultSb.toString();
	}

    public static String getFileMD5(String filePath) {
        if (null == filePath || filePath.length() <= 0) {
            return null;
        }

        File file = new File(filePath);

        return getFileMD5(file);
    }

    /// 对文件进行md5
    public static String getFileMD5(File file) {
        if (null == file) {
            return null;
        }

        if (!file.isFile()) {
            return null;
        }

        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024 * 16];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
        } catch (Exception e) {
			try {
				if (null != in) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}

            e.printStackTrace();
            return null;
        }

		try {
			if (null != in) {
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

        BigInteger bigInt = new BigInteger(1, digest.digest());
		String hexString = bigInt.toString(16);

        if (0 != hexString.length() % 2) {
            hexString = "0" + hexString;
        }

		int totalLen = 32;
		if (totalLen != hexString.length()) {
			int tempLen = totalLen - hexString.length();
            StringBuilder prefix = new StringBuilder();
            while (tempLen > 0) {
				prefix.append("0");
                tempLen--;
            }

            hexString = prefix.toString() + hexString;
		}

		LogUtil.d("FileUtils" , "Md5 hexString =" + hexString);
        return hexString;
    }

    public static boolean isExist(String path) {
		if (null != path && path.length() > 0) {
			File file = new File(path);
			return file.exists();
		}

		return false;
	}

	/**处理缓存**/

	/**
	 * 清除应用所有的数据
	 */
	public static void clearInternalDataOfApplication( ) {
		clearInternalCache();
		clearDatabases();
 // 暂时不清除sharePerence
//		clearSharedPreference();

	}
	/*
	 *清除外部和内部缓存
	 */
	public static void clearAllCache() {
		clearExternalCache();
		clearInternalCache();
	}

	/**
	 * 清除内部缓存(/data/data/包名/cache)
	 */
	public static void clearInternalCache() {
		Context context = UtilsApplication.getInstance().getApplicationContext() ;
		deleteFilesByDirectory(context.getCacheDir());
	}

	/**
	 * 清除应用SharedPreference(/data/data/包名/shared_prefs)
	 */
	public static void clearSharedPreference() {
		Context context = UtilsApplication.getInstance().getApplicationContext() ;
		deleteFilesByDirectory(new File("/data/data/"
				+ context.getPackageName() + "/shared_prefs"));
	}

	/**
	 * *清除所有数据库(/data/data/包名/databases)
	 */
	public static void clearDatabases() {
		Context context = UtilsApplication.getInstance().getApplicationContext() ;
		deleteFilesByDirectory(new File("/data/data/"
				+ context.getPackageName() + "/databases"));
	}

	/**
	 * 按名字清除数据库
	 */
	public static void clearDatabaseBydbName(String dbName) {
		Context context = UtilsApplication.getInstance().getApplicationContext() ;
		context.deleteDatabase(dbName);
	}

	/**
	 * 清除/data/data/包名/files下的内容
	 */
	public static void clearFiles() {
		Context context = UtilsApplication.getInstance().getApplicationContext() ;
		deleteFilesByDirectory(context.getFilesDir());
	}

	/**
	 * 清除自定义路径下的文件(只支持目录下的文件删除,不支持目录)
	 */
	public static void clearCustomCache(String filePath) {
		deleteFilesByDirectory(new File(filePath));
	}

	/**
	 * 清除外部SDcard下的cache内容(/mnt/sdcard/android/data/包名/cache)
	 */
	public static void clearExternalCache() {
		Context context = UtilsApplication.getInstance().getApplicationContext() ;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			deleteFilesByDirectory(context.getExternalCacheDir());
		}
	}

	/**
	 * 只删除文件夹下的文件
	 *
	 */
	private static void deleteFilesByDirectory(File directory) {
		if (directory != null && directory.exists() && directory.isDirectory()) {
			for (File item : directory.listFiles()) {
				item.delete();
			}
		}
	}


}