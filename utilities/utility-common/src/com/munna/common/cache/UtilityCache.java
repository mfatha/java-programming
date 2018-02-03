package com.munna.common.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UtilityCache {

	private static UtilityCache utilityCache;

	private static Map<String, Object> cache;

	private UtilityCache() {
		cache = new ConcurrentHashMap<String, Object>();
	}

	public static UtilityCache getInstance() {
		if (utilityCache == null) {
			synchronized (UtilityCache.class) {
				if (utilityCache == null) {
					utilityCache = new UtilityCache();
				}
			}
		}
		return utilityCache;
	}

	public void add(String key, Object value) {
		cache.put(key, value);
	}

	public Object get(String key) {
		return cache.get(key);
	}

	public void remove(String key) {
		cache.remove(key);
	}

	public void clearCache() {
		cache.clear();
	}
	
	public Map<String, Object> getEntireCache() {
		return cache;
	}

}
