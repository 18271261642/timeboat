package com.imlaidian.utilslibrary.utils;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imlaidian.utilslibrary.dataModel.HttpResponseModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GsonUtils {

    public static String toJson(Object src) {
        if (src == null) {
            return "";
        }
        Gson gson = new Gson();
        return gson.toJson(src);
    }

    public static String toJson(Object src, Type typeOfSrc) {
        if (src == null) {
            return "";
        }
        Gson gson = new Gson();
        return gson.toJson(src, typeOfSrc);
    }

    // 将JSON字符串反序列化为泛型类型的JavaBean
    //    List<Person> ps = gson.fromJson(str, new TypeToken<List<Person>>(){}.getType());
    public static <T> HttpResponseModel<T> parseRespondModel(String responseContent,
                                                             Class<T> clazz){
        if (TextUtils.isEmpty(responseContent)) {
            return null;
        }

        Type type =  new TypeToken<HttpResponseModel<T>>(){}.getType();;
        Gson gson = new Gson();
        try {
            return gson.fromJson(responseContent, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

    public static <T> T fromJson(String json, Type type) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        Gson gson = new Gson();
        try {
            T result = gson.fromJson(json, type);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
}
