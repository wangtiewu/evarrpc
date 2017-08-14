package com.eastelsoft.etos2.rpc.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.bson.types.ObjectId;

/**
 * 
 * 
 * 
 * @author Administrator
 *
 */

public class IdGenerator {
	/**
	 * mongodb object id hex string
	 * 
	 * @return
	 */
	public static String createObjectIdHex() {
		return ObjectId.get().toHexString();
	}

	/**
	 * 获取 mongodb object id hex string yyyyMMdd格式的时间字符串
	 * 
	 * @param objectIdHex
	 * @return
	 */
	public static String getDayFromObjectIdHex(String objectIdHex) {
		if (ObjectId.isValid(objectIdHex)) {
			return DateFormat.toFormatString(
					1000L * Integer.parseInt(objectIdHex.substring(0, 8), 16),
					"yyyyMMdd");
		} else {
			return "";
		}
	}

	/**
	 * 获取 mongodb object id hex string yyyyMMddHH格式的时间字符串
	 * 
	 * @param objectIdHex
	 * @return
	 */
	public static String getHourFromObjectIdHex(String objectIdHex) {
		if (ObjectId.isValid(objectIdHex)) {
			return DateFormat.toFormatString(
					1000L * Integer.parseInt(objectIdHex.substring(0, 8), 16),
					"yyyyMMddHH");
		} else {
			return "";
		}
	}

	/**
	 * 获取 mongodb object id hex string yyyyMMddHHmmss 格式的时间字符串
	 * 
	 * @param objectIdHex
	 * @return 非objectIdHex，返回null，否则返回 yyyyMMddHHmmss 格式的时间字符串
	 */
	public static String getSecondFromObjectIdHex(String objectIdHex) {
		if (ObjectId.isValid(objectIdHex)) {
			return DateFormat.toFormatString(
					1000L * Integer.parseInt(objectIdHex.substring(0, 8), 16),
					"yyyyMMddHHmmss");
		} else {
			return "";
		}
	}

	/**
	 * 1970年以来的秒数
	 * 
	 * @param objectIdHex
	 * @return
	 */
	public static int getTimestampFromObjectIdHex(String objectIdHex) {
		if (ObjectId.isValid(objectIdHex)) {
			return Integer.parseInt(objectIdHex.substring(0, 8), 16);
		} else {
			return -1;
		}
	}
}
