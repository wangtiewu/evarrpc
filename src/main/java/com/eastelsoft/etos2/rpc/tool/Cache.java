package com.eastelsoft.etos2.rpc.tool;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * ����Ľӿڣ��ýӿڵĲ�ͬʵ�����ϵͳ����Ĳ�ͬ������ԡ� < p>Copyright: Copyright (c) 2005< /p> < p>Company:<
 * /p>
 * 
 * @author �����
 * @version 1.0
 */
public interface Cache {
	
	/**
	 * ��ʼ������
	 * 
	 */
	public void init();
    /**
	 * ��ݹؼ��ִӻ������ȡһ������
	 * 
	 * @param key
	 *            �ö���Ļ���ؼ���
	 * @return ������Ķ���
	 */
    public Object getFromCache(String key);
    
    /**
     * ��ݹؼ��ִӻ������ȡһ������
     * @param <T> 
     * @param key �ö���Ļ���ؼ���
     * @param clazz ��������
     * @return
     */
    public <T> T getFromCache(String key, Class<T> clazz);
    
    /**
     * ������ȡ������Ķ���
     * @param keys key�б�
     * @return
     */
    public Map<String, Object> getBulk(List<String> keys);

    /**
	 * ���һ�����󵽻�����
	 * 
	 * @param key
	 *            �ö���Ļ���ؼ���
	 * @param value
	 *            ������Ķ���
	 * @param expiry
	 *            ʧЧʱ�䣬��new Date(1000*10)����ʾ10���ʧЧ
	 * 
	 */
    public void putInCache(String key, Object value, Date expiry);
    
    
    /**
	 * ���һ�����󵽻�����
	 * 
	 * @param key
	 *            �ö���Ļ���ؼ���
	 * @param value
	 *            ������Ķ���
	 */
    public void putInCache(String key, Object value);

    /**
	 * �ӻ�����ɾ��һ������
	 * 
	 * @param key
	 *            �ö���Ļ���ؼ���
	 */
    public void removeFromCache(String key);

    /**
	 * ȡ������������
	 * 
	 * @return ������������
	 */
    public int size();

    /**
	 * ���������ж����������ʼ��
	 * 
	 */
    public void reset();

    /**
	 * �ƻ�����
	 * 
	 */
    public void destroy();

    /**
	 * ����Cache��ˢ��ʱ��
	 * 
	 * @param refreshPeriod
	 *            ˢ��ʱ��(��)
	 */
    public void setRefreshPeriod(int refreshPeriod);

    /**
	 * ����Cron
	 * 
	 * @param cron
	 */
    public void setCron(String cron);
}
