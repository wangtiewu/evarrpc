package com.eastelsoft.etos2.rpc.tool;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Redis {

	void init();

	void destroy();

	void setServers(String[] var1);

	String[] getServers();

	Set keys(String var1);

	boolean exists(Serializable var1);

	boolean expire(Serializable var1, long var2);

	public boolean expire(final String[] keys, final long[] milliseconds);

	long incrby(Serializable var1, long var2);

	void delete(Serializable var1);

	void set(Serializable var1, Serializable var2);

	void set(Serializable var1, Serializable var2, long var3);

	public boolean mset(final String... keysvalues);

	public List<String> mget(final String... keys);

	public long mdelete(final String... keys);

	public <T> T get(Serializable var1);

	public <T> T get(Serializable var1, Class<T> clazz);

	long setsSize(Serializable var1);

	boolean setsExists(Serializable var1, Serializable var2);

	void setsAdd(Serializable var1, Serializable... var2);

	public <T> Set<T> setsGet(Serializable var1, long var2);

	public <T> Set<T> setsGet(Serializable var1, long var2, Class clazz);

	public <T> T setsPop(Serializable var1);

	public <T> T setsPop(Serializable var1, Class clazz);

	public <T> Set<T> setsGetAll(Serializable var1);

	public <T> Set<T> setsGetAll(Serializable var1, Class clazz);

	void setsRemove(Serializable var1, Serializable... var2);

	long sortedSetsSize(Serializable var1);

	boolean sortedSetsExists(Serializable var1, Serializable var2);

	void sortedSetsAdd(Serializable var1, double var2, Serializable var4);

	long sortedSetsAdd(Serializable var1, Map<String, Double> values);

	public Set<String> zrangeByScore(String key, double min, double max);

	public <T> T sortedSetsGetFirst(Serializable var1);

	public <T> T sortedSetsGetFirst(Serializable var1, Class clazz);

	public <T> T sortedSetsGetLast(Serializable var1);

	public <T> T sortedSetsGetLast(Serializable var1, Class clazz);

	public <T> Set<T> sortedSetsGet(Serializable var1, long var2, long var4);

	public <T> Set<T> sortedSetsGet(Serializable var1, long var2, long var4,
			Class clazz);

	public <T> Set<T> sortedSetsGetRev(Serializable var1, long var2, long var4);

	public <T> Set<T> sortedSetsGetRev(Serializable var1, long var2, long var4,
			Class clazz);

	/**
	 * 获取成员的位置
	 * 
	 * @param var1
	 * @param var2
	 * @return
	 */
	long sortedSetsGetScore(Serializable var1, Serializable var2);

	/**
	 * score 值在 min 和 max 之间(默认包括 score 值等于 min 或 max )的成员的数量
	 * 
	 * @param var1
	 * @param min
	 * @param max
	 * @return
	 */
	long sortedSetCount(Serializable var1, long min, long max);

	public <T> Set<T> sortedSetsGetAll(Serializable var1);

	public <T> Set<T> sortedSetsGetAll(Serializable var1, Class clazz);

	void sortedSetsRemove(Serializable var1, Serializable var2);

	long sortedSetsRemove(String var1, String... var2);

	/**
	 * sortedSets 保留有序集尾部指定数量的元素
	 * 
	 * @param var1
	 * @param size
	 */
	void sortedSetsTrim(Serializable var1, long size);

	long queueSize(Serializable var1);

	long queueIn(Serializable var1, Serializable... var2);
	
	long queueIn(Serializable var1, Serializable value);

	public <T> T queueGet(Serializable var1);

	public <T> T queueOut(Serializable var1);

	public <T> T queueOut(Serializable var1, Class clazz);

	void queueRemove(Serializable var1, Serializable var2, int var3);

	long stackSize(Serializable var1);

	void stackIn(Serializable var1, Serializable... var2);

	public <T> T stackGet(Serializable var1);

	public <T> T stackOut(Serializable var1);

	void stackRemove(Serializable var1, Serializable var2, long var3);

	long mapSize(Serializable var1);

	public <HK> long mapIncrement(Serializable var1, HK var2, long var3);

	public void mapSet(Serializable var1, Serializable var2, Serializable var3);

	public <HK, HV> void mapSet(Serializable key,
			Map<? extends HK, ? extends HV> m);

	boolean mapExistsByHashKey(Serializable var1, Serializable var2);

	public <T> T mapGetByHashKey(Serializable var1, Serializable var2);

	public <T> T mapGetByHashKey(Serializable var1, Serializable var2,
			Class clazz);

	public <K, V> Map<K, V> mapGetAll(Serializable var1);

	public <K, V> Map<K, V> mapGetAll(Serializable key, Class clazz);

	/**
	 * 获取map中所有的key
	 * 
	 * @param key
	 * @return
	 */
	public <T> Set<T> mapKeys(String key);

	void mapRemoveByHashKey(Serializable var1, Serializable... var2);

	/**
	 * 设置bitset
	 * 
	 * @param key
	 * @param i
	 * @param b
	 */
	void setbit(String key, int i, boolean b);

	/**
	 * Pipeline方式设置bitset
	 * 
	 * @param key
	 * @param offset
	 * @param b
	 */
	public void setbit(String key, int[] offset, boolean b);

	/**
	 * 获取bitset指定位置的值
	 * 
	 * @param key
	 * @param i
	 * @return
	 */
	boolean getbit(String key, int i);

	/**
	 * 获取bitset指定位置的值，有任何一个位置的值为false，返回false；否则返回true
	 * 
	 * @param key
	 * @param i
	 * @return
	 */
	boolean getbit(String key, int[] i);

	/**
	 * PFADD 操作
	 * 
	 * @param key
	 * @param element
	 * @return
	 */
	int pfadd(String key, String... element);

	/**
	 * PFCOUNT操作
	 * 
	 * @param key
	 * @return
	 */
	long pfcount(String key);

	/**
	 * value 值按从左到右的顺序依次插入到表头
	 * 
	 * @param key
	 * @param values
	 * @return 操作后的列表长度
	 */
	long lpush(Serializable key, Serializable... values);

	/**
	 * 列表的长度
	 * 
	 * @param key
	 * @return
	 */
	long llen(Serializable key);

	/**
	 * 保留指定区间内的元素
	 * 
	 * @param key
	 * @param start
	 *            起始元素
	 * @param end
	 *            结束元素
	 * @return
	 */
	int ltrim(Serializable key, long start, long end);

	/**
	 * 内存使用率
	 * 
	 * @return
	 */
	List<Float> memRatio();

	/**
	 * TTL in seconds
	 * 
	 * @param key
	 * @return
	 */
	long ttl(String key);
}
