package org.autoswith.config;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassMethodStatusManager {
	
	private static final ClassMethodStatusManager instance = new ClassMethodStatusManager();
	
	private final ConcurrentHashMap<String, Integer> methodStatusMap = new ConcurrentHashMap<String,Integer>();
	
	private ClassMethodStatusManager(){}
	
	public static ClassMethodStatusManager getInstance(){
		return ClassMethodStatusManager.instance;
	}
	
	public  Map<String,Object> getSnapShot(){
		return new HashMap<String,Object>(methodStatusMap);
	}
	
	public boolean open(String method){
		return methodStatusMap.put(method, 1) != null;
	}
	
	public boolean close(String method){
		return methodStatusMap.remove(method) != null;
	}
	
	public boolean isOpen(String method){
		return methodStatusMap.containsKey(method) && methodStatusMap.get(method) == 1;
	}
}
