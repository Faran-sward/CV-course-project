package com.example.myapplication.utils;

import com.google.gson.Gson;


/**
 * 解析库的再封装
 */
public class JSONUtils {

	/**
	 * 获取jsonResult信息
	 *
	 * @param jsonString
	 * @return
	 */
	public static Result getJsonResult(String jsonString) {
		if (jsonString.equals("")) {
			return null;
		}
		Gson gson=new Gson();
		return gson.fromJson(jsonString,Result.class);
	}

	/**
	 * 把相对应节点的内容封装为对象
	 */
	public static <T> T parserObject(String jsonString,
                                     Class<T> beanClazz) {
		if (jsonString != null) {
			T object = new Gson().fromJson(jsonString,beanClazz);
			return object;
		}
		return null;
	}
}
