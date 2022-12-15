package com.device.ui.entity;



import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;

public class BaseModel implements Serializable {

	private static final long serialVersionUID = 3488831424106545304L;

	public BaseModel() {

	}


	public BaseModel(String jsonString) {
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			parseData(jsonObject);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("BaseModel" , "parse json error :" +e.toString());
		}
	}

	public BaseModel(JSONObject jsonObject) {
		parseData(jsonObject);
	}

	public void parseData(JSONObject json) {
		if (null != json) {
			this.result = json.optInt("result");
			this.msg = json.optString("msg");
		}
	}

	private String msg;
	private int result;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	// 请求返回正确
	public static int SUCCESS = 1;
	// 请求返回错误
	public static int FAILURE = -1;
	// 已失效，已过期
	public static int INVALID = 2;

    /// 请求成功
    public boolean isResponseSuccess() {
        return (SUCCESS == result);
    }
}