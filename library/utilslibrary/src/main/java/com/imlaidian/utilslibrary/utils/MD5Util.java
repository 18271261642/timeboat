package com.imlaidian.utilslibrary.utils;

import android.annotation.SuppressLint;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	/**
	 * @param str
	 * @return
	 * @Date: 2013-9-6
	 * @Author: lulei
	 * @Description: 32位小写MD5
	 */
	public static String parseStrToMd5L32(String str) {
		String reStr = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] bytes = md5.digest(str.getBytes());
			StringBuffer stringBuffer = new StringBuffer();
			for (byte b : bytes) {
				int bt = b & 0xff;
				if (bt < 16) {
					stringBuffer.append(0);
				}
				stringBuffer.append(Integer.toHexString(bt));
			}
			reStr = stringBuffer.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return reStr;
	}

	/**
	 * @param str
	 * @return
	 * @Date: 2013-9-6
	 * @Author: lulei
	 * @Description: 32位大写MD5
	 */
	public static String parseStrToMd5U32(String str) {
		String reStr = parseStrToMd5L32(str);
		if (reStr != null) {
			reStr = reStr.toUpperCase();
		}
		return reStr;
	}

	/**
	 * @param str
	 * @return
	 * @Date: 2013-9-6
	 * @Author: lulei
	 * @Description: 16位小写MD5
	 */
	public static String parseStrToMd5U16(String str) {
		String reStr = parseStrToMd5L32(str);
		if (reStr != null) {
			reStr = reStr.toUpperCase().substring(8, 24);
		}
		return reStr;
	}

	/**
	 * @param str
	 * @return
	 * @Date: 2013-9-6
	 * @Author: lulei
	 * @Description: 16位大写MD5
	 */
	public static String parseStrToMd5L16(String str) {
		String reStr = parseStrToMd5L32(str);
		if (reStr != null) {
			reStr = reStr.substring(8, 24);
		}
		return reStr;
	}


	@SuppressLint("DefaultLocale")
	public static String MD5Lower16(String text) {
		return MD5(text).toLowerCase().substring(8, 24);
	}

	@SuppressLint("DefaultLocale")
	public static String MD5Lower32(String text) {
		return MD5(text).toLowerCase();
	}

	@SuppressLint("DefaultLocale")
	public static String MD5Upper16(String text) {
		return MD5(text).toUpperCase().substring(8, 24);
	}

	@SuppressLint("DefaultLocale")
	public static String MD5Upper32(String text) {
		return MD5(text).toUpperCase();
	}

	private static String MD5(String text) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(text.getBytes("utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		String md5 =  new BigInteger(1, md.digest()).toString(16);
		return fillMD5(md5);
	}



	// 将16位数转为32位
	public static String fillMD5(String md5) {
		return md5.length() == 32 ? md5 : fillMD5("0" + md5);
	}

	public static byte[] md5Bytes(byte[] buffer, int offset, int len) {
		if (offset < 0 || len <= 0 || offset + len > buffer.length) {
			return null;
		}

		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] encodeBuffer = new byte[len];
			System.arraycopy(buffer, offset, encodeBuffer, 0, len);

			return md5.digest(encodeBuffer);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}
}
