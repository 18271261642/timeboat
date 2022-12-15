package com.imlaidian.utilslibrary.utils;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import com.google.gson.Gson;
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class jsonUtil {
	
	public static <T> T getObject(String jsonString, Class<T> cls) {
        return JSON.parseObject(jsonString,cls);  
    }  
  
    public static <T> List<T> getObjects(String jsonString, Class<T> cls) {
        return JSON.parseArray(jsonString, cls);
    }  
    
    public static <T> HttpResponseModel<T> getObject(String json) {
        return JSON.parseObject(json, new TypeReference<HttpResponseModel<T>>() {
        });
    }


    public static <T> HttpResponseModel<List<T>> parseRespondListModel(String responseContent,
                                                             Class<T> clazz){
        Type type = new TypeReference<HttpResponseModel<List<T>>>(clazz) {}.getType();
        HttpResponseModel<List<T>> bean = JSON.parseObject(responseContent,type);
        return  bean;
    }


    // 将JSON字符串反序列化为泛型类型的JavaBean
    //ResultBean<StsTokenDataVO> respEntity = JSON.parseObject(responseContent, new TypeReference<ResultBean<StsTokenDataVO>>(StsTokenDataVO.class){});
    public static <T> HttpResponseModel<T> parseRespondModel(String responseContent,
                                                    Class<T> clazz){

        Type type = new TypeReference<HttpResponseModel<T>>(clazz) {}.getType();
        HttpResponseModel<T> bean = JSON.parseObject(responseContent,type);
        return  bean;
    }
    
    public static List<Map<String,String>> getKeyMapsList(String jsonString) {
        List<Map<String,String>> list;
        list = JSON.parseObject(jsonString, new TypeReference<List<Map<String, String>>>(){});
        return list;  
    }
    
    public static String getObject(Object object) {
        return JSON.toJSONString(object);  
    }
    
    public static String simpleMapToJsonStr(Map<String, Object> map){
        if(map==null||map.isEmpty()){  
            return "null";  
        }  
        String jsonStr = "{";
        Set<?> keySet = map.keySet();
        for (Object key : keySet) {
            jsonStr += "\""+key+"\":\""+map.get(key)+"\",";       
        }  
        jsonStr = jsonStr.substring(0, jsonStr.length()-1);  
        jsonStr += "}";  
        return jsonStr;  
    }

    public static String getJsonObjectByKey( String jsonStr, String key ){

        try {
            JSONObject json = new JSONObject(jsonStr);
            final String message = json.getString(key);
            return  message ;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  "";
    }




    /**
     * 将json对象中包含的null和JSONNull属性修改成""
     *
     * @param jsonObj
     */
    public static JSONObject filterNull(JSONObject jsonObj) {
        Iterator<String> it = jsonObj.keys();
        Object obj = null;
        String key = null;
        while (it.hasNext()) {
            key = it.next();
            try {
                obj = jsonObj.get(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (obj instanceof JSONObject) {
                filterNull((JSONObject) obj);
            }
            if (obj instanceof JSONArray) {
                JSONArray objArr = (JSONArray) obj;
                for (int i = 0; i < objArr.length(); i++) {
                    try {
                        filterNull(objArr.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (obj == null ) {
                try {
                    jsonObj.put(key, "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (obj.equals(null)) {
                try {
                    jsonObj.put(key, "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonObj;
    }

    public static <T> List<T> fromJsonToList(String json, Class<T[]> clazz) {
        if (TextUtils.isEmpty(json)) {
            return new ArrayList<>();
        }
        try {
            T[] arr = new Gson().fromJson(json, clazz);
            if (arr == null || arr.length <= 0) {
                return new ArrayList<>();
            }
            List<T> ts = Arrays.asList(arr);
            return new ArrayList<>(ts);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        Gson gson = new Gson();
        try {
            return gson.fromJson(json, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
