package com.device.ui.entity;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


public class BaseParser {

	private static BaseParser instance;

	public static BaseParser getInstance() {
		if (instance == null) {
			synchronized (BaseParser.class) {
				if (instance == null) {
					instance = new BaseParser();
				}
			}
		}
		return instance;
	}

	/**
	 * 解析json数据为对应model
	 * 
	 * @param data
	 *            二进制数据
	 * @param classOfT
	 *            转换类型
	 * @return
	 */
	public <T> T parser(byte[] data, Class<T> classOfT) {

		String jsonData = new String(data);
		//LogUtil.getInstance().debug("jsonData\n" + jsonData);

		return parser(jsonData, classOfT);
	}

	/**
	 * 解析json数据为对应model
	 * 
	 * @param jsonData
	 * @param classOfT
	 * @return
	 */
	public <T> T parserString(String jsonData, Class<T> classOfT) {

	//	LogUtil.getInstance().debug("jsonData\n" + jsonData);

		return parser(jsonData, classOfT);
	}

	/**
	 * 解析json数据为对应model
	 * 
	 * @param jsonData
	 *            字符串
	 * @param classOfT
	 *            转换类型
	 * @return
	 */
	public <T> T parser(String jsonData, Class<T> classOfT) {
		T obj = null;
		try {
			JSONObject ps = (JSONObject) new JSONTokener(jsonData).nextValue();
			try {
				obj = classOfT.newInstance();
				((BaseModel) obj).parseData(ps);
			} catch (InstantiationException e) {
				e.printStackTrace();
				Log.e("BaseParser" , "parser  json InstantiationException :" + e);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				Log.e("BaseParser" , "parser  json IllegalAccessException :" + e);
			}catch (ClassCastException e){
				e.printStackTrace();
				Log.e("BaseParser" , "parser  json ClassCastException :" + e);
			}catch (Exception e){
				e.printStackTrace();
				Log.e("BaseParser" , "parser  Exception  :" + e);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("BaseParser" , "parser  json JSONException :" + e);
		}catch (ClassCastException e){
			e.printStackTrace();
			Log.e("BaseParser" , "parser  ClassCastException:" + e);
		}catch (Exception e){
			e.printStackTrace();
			Log.e("BaseParser" , "parser  Exception:" + e);
		}
		return obj;
	}
}
