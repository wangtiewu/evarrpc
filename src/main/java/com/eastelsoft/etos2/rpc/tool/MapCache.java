package com.eastelsoft.etos2.rpc.tool;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 缓存的接口的Map方式实现。采用简单的Map结构来存储对象。 < p>Copyright: Copyright (c) 2005< /p> < p>Company:
 * Sunny虚拟开发组< /p>
 * 
 * @author 高雁冰
 * @version 1.0
 */
public class MapCache implements Cache {
	private static final Logger logger = LoggerFactory
			.getLogger(MapCache.class);
	private java.util.Map map = new java.util.HashMap();// 存放缓存的key-value的容器

	public MapCache() {
	}

	/**
	 * 清除缓存里所有东西，缓存初始化
	 */
	public void reset() {
		if (logger.isDebugEnabled()) {
			logger.debug("reset MapCache.");
		}
		map.clear();
	}

	/**
	 * 根据关键字从缓存里获取一个对象
	 * 
	 * @param key
	 *            该对象的缓存关键字
	 * @return 被缓存的对象
	 */
	public Object getFromCache(String key) {
		// TODO Auto-generated method stub
		if (logger.isDebugEnabled()) {
			logger.debug("get object form MapCache by key:" + key);
		}
		return map.get(key);
	}
	
	

	public <T> T getFromCache(String key, Class<T> clazz) {
		// TODO Auto-generated method stub
		if (logger.isDebugEnabled()) {
			logger.debug("get object form MapCache by key:" + key);
		}
		return (T)map.get(key);
	}

	/**
	 * 存放一个对象到缓存里
	 * 
	 * @param key
	 *            该对象的缓存关键字
	 * @param value
	 *            被缓存的对象
	 */
	public void putInCache(String key, Object value) {
		// TODO Auto-generated method stub
		if (logger.isDebugEnabled()) {
			logger.debug("put object to MapCache by key:" + key);
		}
		map.put(key, value);

	}

	/**
	 * 从缓存里删除一个对象
	 * 
	 * @param key
	 *            该对象的缓存关键字
	 */
	public void removeFromCache(String key) {
		// TODO Auto-generated method stub
		if (logger.isDebugEnabled()) {
			logger.debug("remove object from MapCache by key:" + key);
		}
		map.remove(key);

	}

	public int size() {
		return map.size();
	}

	/**
	 * 摧毁缓存,缓存不可再用
	 */
	public void destroy() {
		// TODO Auto-generated method stub
		if (logger.isDebugEnabled()) {
			logger.debug("destroy MapCache.");
		}
		map.clear();
		map = null;
	}

	/**
	 * 垃圾回收
	 */
	protected void finalize() {
		destroy();
	}

	/**
	 * not implements this method
	 */
	public void setRefreshPeriod(int refreshPeriod) {
		// TODO Auto-generated method stub

	}

	/**
	 * not implements this method
	 */
	public void setCron(String cron) {
		// TODO Auto-generated method stub

	}

	public void init() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @deprecated
	 */
	public void putInCache(String key, Object value, Date expiry) {
		// TODO Auto-generated method stub
		
	}

	public Map<String, Object> getBulk(List<String> keys) {
		// TODO Auto-generated method stub
		if (logger.isDebugEnabled()) {
			logger.debug("get object form MapCache by key list");
		}
		Map<String, Object> values = new HashMap<String, Object>();
		for (String key : keys) {
			values.put(key, map.get(key));
		}
		return values;
	}
	
}
