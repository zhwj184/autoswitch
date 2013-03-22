package org.autoswitch.complier;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class SimpleReturnObjectComplier {
	
	private static Map<String,String> sourceMap = new ConcurrentHashMap<String, String>(1000);
	
	private static Map<String,Object> instanceMap = new ConcurrentHashMap<String, Object>(1000);

	public static boolean addJsonRet(String classMethod,String source){
		if(classMethod == null || source == null){
			return false;
		}
		instanceMap.remove(classMethod);
		return sourceMap.put(classMethod, source) != null;
	}
	public static Object getRetInstance(String classMethod, Class retClass){
		String source = sourceMap.get(classMethod);
		if(classMethod == null || source == null){
			return null;
		}
		if(instanceMap.containsKey(classMethod)){
			return instanceMap.get(classMethod);
		}
		
		Object ret = null;
		if(retClass.isArray() || retClass.isAssignableFrom(List.class)){
			JSONArray jsonArray = (JSONArray) JSONSerializer.toJSON(source);
			ret = (List) JSONSerializer.toJava(jsonArray);
		}else{
			JSONObject jsonObject = JSONObject.fromObject(source);
			ret = (Object) JSONObject.toBean(jsonObject, retClass);	
		}
		instanceMap.put(classMethod, ret);
		return ret;
	}
	
	public static boolean remove(String classMethod){
		instanceMap.remove(classMethod);
		return sourceMap.remove(classMethod) != null;
	}
}


