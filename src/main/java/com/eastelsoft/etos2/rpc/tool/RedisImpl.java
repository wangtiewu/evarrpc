package com.eastelsoft.etos2.rpc.tool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.ShardedJedisPool;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RedisImpl implements Redis {
	private static final Logger logger = LoggerFactory
			.getLogger(RedisImpl.class);
	public static final int DEFAULT_PORT = 6379;
	private final Pattern p = Pattern
			.compile("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$");
	private String[] servers = new String[0];
	private List shards = new ArrayList();
	private ShardedJedisPool jedisPool = null;
	ObjectMapper mapper = new ObjectMapper();

	// private JedisDynamicShardsProvider provider = null;

	public void setServers(String[] servers) {
		if (servers == null) {
			this.servers = new String[0];
		} else {
			this.servers = servers;
		}
	}

	public String[] getServers() {
		return this.servers;
	}

	public void init() {
		String[] var4 = this.servers;
		int redisProperties = 0;

		Properties var8 = this.getRedisProperties();
		int timeout = 0;
		if (var8 != null) {
			timeout = Integer.parseInt(var8.getProperty("timeout", "20000"));
		}
		List<String> svrs = new ArrayList<String>();
		for (int var3 = var4.length; redisProperties < var3; ++redisProperties) {
			String config = var4[redisProperties];
			if (config.startsWith("${") && var8 != null) {
				config = var8.getProperty(config.substring(
						config.indexOf("{") + 1, config.indexOf("}")));
			}
			String[] ips = StringDeal.split(config, ",");
			for (String ip : ips) {
				String[] ipAndPort = StringDeal.split(ip, ":");
				JedisShardInfo si = null;
				if (ipAndPort.length == 3) {
					si = new JedisShardInfo(ipAndPort[0],
							Integer.parseInt(ipAndPort[1]), timeout);
					si.setPassword(ipAndPort[2]);
				} else if (ipAndPort.length == 2) {
					si = new JedisShardInfo(ipAndPort[0],
							Integer.parseInt(ipAndPort[1]), timeout);
				} else {
					si = new JedisShardInfo(ipAndPort[0], 6379, timeout);
				}
				this.shards.add(si);
				svrs.add(ip);
			}
		}
		this.servers = new String[svrs.size()];
		for (int i = 0; i < servers.length; i++) {
			servers[i] = svrs.get(i);
		}

		GenericObjectPoolConfig var7 = new GenericObjectPoolConfig();
		if (var8 != null) {
			var7.setTestOnBorrow(Boolean.parseBoolean(var8.getProperty(
					"test.on.borrow", "false")));
			var7.setTestOnReturn(Boolean.parseBoolean(var8.getProperty(
					"test.on.return", "false")));
			var7.setTestWhileIdle(Boolean.parseBoolean(var8.getProperty(
					"test.while.idle", "true")));
			var7.setMaxIdle(Integer.parseInt(var8.getProperty("max.idle", "16")));
			var7.setMinIdle(Integer.parseInt(var8.getProperty("min.idle", "4")));
			var7.setMaxTotal(Integer.parseInt(var8.getProperty("max.active",
					"128")));
			var7.setMaxWaitMillis((long) Integer.parseInt(var8.getProperty(
					"max.wait", "10000")));
			var7.setNumTestsPerEvictionRun(Integer.parseInt(var8.getProperty(
					"num.tests.per.eviction.run", "-1")));
			var7.setTimeBetweenEvictionRunsMillis((long) Integer.parseInt(var8
					.getProperty("time.between.eviction.runs.millis", "60000")));
			var7.setMinEvictableIdleTimeMillis((long) Integer.parseInt(var8
					.getProperty("min.evictable.idle.time.millis", "120000")));
		} else {
			var7.setTestOnBorrow(false);
			var7.setTestOnReturn(false);
			var7.setTestWhileIdle(true);
			var7.setMaxIdle(2);
			var7.setMinIdle(2);
			var7.setMaxTotal(128);
			var7.setMaxWaitMillis(10000L);
			var7.setNumTestsPerEvictionRun(-1);
			var7.setTimeBetweenEvictionRunsMillis(60000L);
			var7.setMinEvictableIdleTimeMillis(120000L);
		}
		System.out.println("show redis properties");
		System.out.println("timeout:" + timeout);
		System.out.println("testOnBorrow:" + var7.getTestOnBorrow());
		System.out.println("testOnReturn:" + var7.getTestOnReturn());
		System.out.println("minIdle:" + var7.getMinIdle());
		System.out.println("maxIdle:" + var7.getMaxIdle());
		System.out.println("maxTotal:" + var7.getMaxTotal());
		System.out.println("maxWait:" + var7.getMaxWaitMillis());
		System.out.println("numTestsPerEvictionRun:"
				+ var7.getNumTestsPerEvictionRun());
		System.out.println("timeBetweenEvictionRunsMillis:"
				+ var7.getTimeBetweenEvictionRunsMillis());
		System.out.println("minEvictableIdleTimeMillis:"
				+ var7.getMinEvictableIdleTimeMillis());
		System.out.println("end redis properties");
		// this.provider = new JedisDynamicShardsProvider(this.shards);
		this.jedisPool = new ShardedJedisPool(var7, this.shards);
	}

	public void destroy() {
		this.jedisPool.destroy();
	}

	private boolean isPrimitiveWrapClass(Class clz) {
		return ClassUtils.isWrapClass(clz);
	}

	public byte[] serialize1(Object object) {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;

		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			byte[] e = baos.toByteArray();
			return e;
		} catch (Exception var5) {
			var5.printStackTrace();
			return null;
		}
	}

	private String serializeJson(Object object) {
		if (object.getClass().equals(String.class)) {
			return object.toString();
		} else if (this.isPrimitiveWrapClass(object.getClass())) {
			return String.valueOf(object);
		} else {
			// ObjectMapper mapper = new ObjectMapper();
			StringWriter writer = null;
			JsonGenerator gen = null;

			try {
				writer = new StringWriter();
				gen = (new JsonFactory()).createJsonGenerator(writer);
				mapper.writeValue(gen, object);
				String e = writer.toString();
				String var7 = e;
				return var7;
			} catch (Exception var19) {
				var19.printStackTrace();
			} finally {
				try {
					if (gen != null) {
						gen.close();
					}
				} catch (IOException var18) {
					var18.printStackTrace();
				}

				try {
					if (writer != null) {
						writer.close();
					}
				} catch (IOException var17) {
					var17.printStackTrace();
				}

			}

			return null;
		}
	}

	private boolean isBase64String(String source) {
		if (source == null) {
			return false;
		} else {
			Matcher m = this.p.matcher(source);
			return m.find();
		}
	}

	private Object unserialize1(byte[] bytes) {
		ByteArrayInputStream bais = null;

		try {
			bais = new ByteArrayInputStream(bytes);
			ObjectInputStream e = new ObjectInputStream(bais);
			return e.readObject();
		} catch (Exception var4) {
			var4.printStackTrace();
			return null;
		}
	}

	private Object unserializeJson(String string) {
		if (string == null) {
			return null;
		} else {
			// ObjectMapper mapper = new ObjectMapper();

			try {
				Object e = mapper.readValue(string, Object.class);
				return this.isPrimitiveWrapClass(e.getClass()) ? e : (this
						.isBase64String(string) ? this.unserialize1(Base64
						.decode(string.getBytes())) : string);
			} catch (Exception var6) {
				if (this.isBase64String(string)) {
					try {
						return this.unserialize1(Base64.decode(string
								.getBytes()));
					} catch (RuntimeException var5) {
						var5.printStackTrace();
					}
				}

				return string;
			}
		}
	}

	private <T> T unserializeJson(String string, Class<T> clazz) {
		if (string == null) {
			return null;
		} else {
			if (clazz.equals(Long.class)) {
				return (T) new Long(string);
			} else if (clazz.equals(Character.class)) {
				return (T) new Character(string.charAt(0));
			} else if (clazz.equals(Byte.class)) {
				return (T) new Byte(string);
			} else if (clazz.equals(Short.class)) {
				return (T) new Short(string);
			} else if (clazz.equals(Integer.class)) {
				return (T) new Integer(string);
			} else if (clazz.equals(Float.class)) {
				return (T) new Float(string);
			} else if (clazz.equals(Double.class)) {
				return (T) new Double(string);
			} else if (clazz.equals(Boolean.class)) {
				return (T) new Boolean(string);
			} else if (clazz.equals(String.class)) {
				return (T) string;
			}
			if (!string.startsWith("{") && !string.startsWith("[")) {
				Object o = (T) string;
				return (T) o;
			}
			// ObjectMapper mapper = new ObjectMapper();
			try {
				T e = mapper.readValue(string, clazz);
				return e;
			} catch (Exception var6) {
				var6.printStackTrace();
				return (T) string;
			}
		}
	}

	private Properties getRedisProperties() {
		InputStream is = null;

		try {
			is = this.getClass().getResourceAsStream("/redis.properties");
			if (is == null) {
				System.out.println("redis.propertiesδ�ҵ���ʹ��Ĭ������");
				return null;
			}
			Properties redisPs = new Properties();
			try {
				redisPs.load(is);
			} catch (IOException var17) {
				var17.printStackTrace();
			}
			Properties var6 = redisPs;
			return var6;
		} catch (Exception var19) {
			var19.printStackTrace();
			System.out.println("redis.propertiesδ�ҵ���ʹ��Ĭ������");
			return null;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException var16) {
					var16.printStackTrace();
				}
			}

		}
	}

	public static void main(String[] args) {
		RedisImpl redis = new RedisImpl();
		redis.setServers(new String[] { "${redis.server}" });
		redis.init();
		String qName = "test_q1";
		redis.mdelete(qName);
		redis.mset(new String[] { "a", "b" });
		redis.queueIn("abcd", "a", "b");
		redis.delete(qName);
		Integer intVal = Integer.valueOf(100);
		redis.queueIn(qName, new Serializable[] { intVal });
		System.out.println(redis.queueOut(qName));

		try {
			Thread.sleep(10000L);
		} catch (InterruptedException var9) {
			var9.printStackTrace();
		}

		intVal = Integer.valueOf(100);
		redis.queueIn(qName, new Serializable[] { intVal });
		System.out.println(redis.queueOut(qName));

		try {
			Thread.sleep(10000L);
		} catch (InterruptedException var8) {
			var8.printStackTrace();
		}

		intVal = Integer.valueOf(100);
		redis.queueIn(qName, new Serializable[] { intVal });
		System.out.println(redis.queueOut(qName));

		try {
			Thread.sleep(10000L);
		} catch (InterruptedException var7) {
			var7.printStackTrace();
		}

		String abc = "billing_stop_acctitem";

		for (int e = 0; e < 100000; ++e) {
			redis.queueIn(qName, new Serializable[] { Integer.valueOf(e) });
			System.out.println(redis.queueOut(qName));
		}

		redis.destroy();
		System.out.println("RedisQueue destroyed");

		try {
			Thread.sleep(10000L);
		} catch (InterruptedException var6) {
			var6.printStackTrace();
		}

	}

	public void delete(Serializable key) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			resource.del(this.serializeJson(key));
		} catch (RuntimeException var7) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var7.getMessage(), (Throwable) var7);
			throw var7;
		} finally {
			if (resource != null) {
				// resource.close();
				resource.close();
				resource = null;
			}

		}

	}

	public boolean exists(Serializable key) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();

		try {
			boolean var5 = resource.exists(this.serializeJson(key))
					.booleanValue();
			return var5;
		} catch (RuntimeException var8) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}

			logger.error(var8.getMessage(), (Throwable) var8);
			throw var8;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}

		}
	}

	public boolean expire(Serializable key, long milliseconds) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			resource.expire(this.serializeJson(key),
					(int) (milliseconds / 1000L));
			return true;
		} catch (RuntimeException var9) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}

			logger.error(var9.getMessage(), (Throwable) var9);
			throw var9;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public boolean expire(final String[] keys, final long[] milliseconds) {
		if (keys.length != milliseconds.length) {
			return false;
		}
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		Map<Jedis, Map<String, Long>> map = new HashMap<Jedis, Map<String, Long>>();
		try {
			for (int i = 0; i < keys.length; i++) {
				Jedis jedis = resource.getShard(keys[i]);
				Map<String, Long> list = map.get(jedis);
				if (list == null) {
					list = new HashMap<String, Long>();
					map.put(jedis, list);
				}
				list.put(keys[i], milliseconds[i]);
			}
			boolean success = true;
			for (Map.Entry<Jedis, Map<String, Long>> entry : map.entrySet()) {
				Pipeline pl = entry.getKey().pipelined();
				for (Map.Entry<String, Long> entry1 : entry.getValue()
						.entrySet()) {
					pl.expire(entry1.getKey(),
							(int) (entry1.getValue() / 1000L));
				}
				pl.sync();
			}
			return success;
		} catch (RuntimeException var11) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var11.getMessage(), (Throwable) var11);
			throw var11;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public Object get(Serializable key) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();

		try {
			Object var5 = this.unserializeJson(resource.get(this
					.serializeJson(key)));
			return var5;
		} catch (RuntimeException var8) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var8.getMessage(), (Throwable) var8);
			throw var8;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}

		}
	}

	public long incrby(Serializable key, long value) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			return resource.incrBy(this.serializeJson(key), value);
		} catch (RuntimeException var8) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var8.getMessage(), (Throwable) var8);
			throw var8;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public Set keys(String pattern) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			Collection exp = resource.getAllShards();
			Set result = null;
			Iterator var6 = exp.iterator();

			while (var6.hasNext()) {
				Jedis jedis = (Jedis) var6.next();
				if (result == null) {
					result = jedis.keys(pattern);
				} else {
					result.addAll(jedis.keys(pattern));
				}
			}

			Set var8 = result;
			return var8;
		} catch (RuntimeException var11) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}

			logger.error(var11.getMessage(), (Throwable) var11);
			throw var11;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public boolean mapExistsByHashKey(Serializable key, Serializable hashKey) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			return resource.hexists(this.serializeJson(key),
					this.serializeJson(hashKey));
		} catch (RuntimeException var8) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var8.getMessage(), (Throwable) var8);
			throw var8;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public Map mapGetAll(Serializable key) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			return resource.hgetAll(this.serializeJson(key));
		} catch (RuntimeException var8) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var8.getMessage(), (Throwable) var8);
			throw var8;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public Map mapGetAll(Serializable key, Class clazz) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			Map<String, String> alls = resource
					.hgetAll(this.serializeJson(key));
			Map allNew = new HashMap(alls.size());
			Iterator<Entry<String, String>> iterator = alls.entrySet()
					.iterator();
			Entry<String, String> entry;
			while (iterator.hasNext()) {
				entry = iterator.next();
				allNew.put(entry.getKey(),
						this.unserializeJson(entry.getValue(), clazz));
			}
			alls.clear();
			return allNew;
		} catch (RuntimeException var8) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var8.getMessage(), (Throwable) var8);
			throw var8;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public Set mapKeys(String key) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			Set<String> allKeys = resource.hkeys(key);
			return allKeys;
		} catch (RuntimeException e) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(e.getMessage(), (Throwable) e);
			throw e;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public Object mapGetByHashKey(Serializable key, Serializable hashKey) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			return resource.hget(this.serializeJson(key),
					this.serializeJson(hashKey));
		} catch (RuntimeException var8) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var8.getMessage(), (Throwable) var8);
			throw var8;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public Object mapGetByHashKey(Serializable key, Serializable hashKey,
			Class clazz) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			return this.unserializeJson(
					resource.hget(this.serializeJson(key),
							this.serializeJson(hashKey)), clazz);
		} catch (RuntimeException var8) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var8.getMessage(), (Throwable) var8);
			throw var8;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public long mapIncrement(Serializable key, Object hashKey, long num) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			return resource.hincrBy(this.serializeJson(key),
					this.serializeJson(hashKey), num);
		} catch (RuntimeException var8) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var8.getMessage(), (Throwable) var8);
			throw var8;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public void mapRemoveByHashKey(Serializable key, Serializable... hashKeys) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			for (Serializable serializable : hashKeys) {
				resource.hdel(this.serializeJson(key),
						this.serializeJson(serializable));
			}
		} catch (RuntimeException var8) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var8.getMessage(), (Throwable) var8);
			throw var8;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public void mapSet(Serializable key, Serializable hashKey,
			Serializable hashValue) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			resource.hset(this.serializeJson(key), this.serializeJson(hashKey),
					this.serializeJson(hashValue));
		} catch (RuntimeException var8) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var8.getMessage(), (Throwable) var8);
			throw var8;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public void mapSet(Serializable key, Map m) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			resource.hmset(this.serializeJson(key), m);
		} catch (RuntimeException var8) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var8.getMessage(), (Throwable) var8);
			throw var8;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public long mapSize(Serializable key) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			return resource.hlen(this.serializeJson(key));
		} catch (RuntimeException var8) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var8.getMessage(), (Throwable) var8);
			throw var8;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public Object queueGet(Serializable key) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();

		try {
			List exp = resource.lrange(this.serializeJson(key), 0L, 0L);
			if (exp != null && exp.size() == 1) {
				Object var5 = this.unserializeJson((String) exp.get(0));
				return var5;
			}
			return null;
		} catch (RuntimeException var8) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var8.getMessage(), (Throwable) var8);
			throw var8;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}

		}
	}

	public long queueIn(Serializable key, Serializable value) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();

		try {
			return resource.rpush(this.serializeJson(key),
					this.serializeJson(value));
		} catch (RuntimeException var11) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var11.getMessage(), (Throwable) var11);
			throw var11;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public long queueIn(Serializable key, Serializable... value) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();

		try {
			String[] list = new String[value.length];
			for (int i = 0; i < value.length; i++) {
				list[i] = this.serializeJson(value[i]);
			}
			return resource.rpush(this.serializeJson(key), list);
		} catch (RuntimeException var11) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}

			logger.error(var11.getMessage(), (Throwable) var11);
			throw var11;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}

		}

	}

	public <T> T queueOut(Serializable var1, Class clazz) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();

		try {
			String exp = resource.lpop(this.serializeJson(var1));
			return (T) unserializeJson(exp, clazz);
		} catch (RuntimeException var8) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var8.getMessage(), (Throwable) var8);
			throw var8;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}

	}

	public Object queueOut(Serializable key) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();

		try {
			String exp = resource.lpop(this.serializeJson(key));
			Object var5 = this.unserializeJson(exp);
			return var5;
		} catch (RuntimeException var8) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var8.getMessage(), (Throwable) var8);
			throw var8;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}

		}
	}

	public void queueRemove(Serializable key, Serializable value, int count) {
		throw new UnsupportedOperationException();
	}

	public long queueSize(Serializable key) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();

		try {
			long var5 = resource.llen(this.serializeJson(key)).longValue();
			return var5;
		} catch (RuntimeException var9) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var9.getMessage(), (Throwable) var9);
			throw var9;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}

		}
	}

	public void set(Serializable key, Serializable value) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			resource.set(this.serializeJson(key), this.serializeJson(value));
		} catch (RuntimeException var11) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var11.getMessage(), (Throwable) var11);
			throw var11;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public void set(Serializable key, Serializable value, long milliseconds) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			int seconds = (int) (milliseconds / 1000L);
			if (seconds > 0) {
				resource.setex(this.serializeJson(key), seconds,
						this.serializeJson(value));
			} else {
				resource.set(this.serializeJson(key), this.serializeJson(value));
			}
			/*
			 * resource.set(this.serializeJson(key), this.serializeJson(value));
			 * int seconds = (int) (milliseconds / 1000L); if (seconds > 0) {
			 * resource.expire(this.serializeJson(key), seconds); }
			 */
		} catch (RuntimeException var11) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var11.getMessage(), (Throwable) var11);
			throw var11;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public long mdelete(final String... keys) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		long deledCount = 0;
		try {
			Iterator iter = resource.getAllShards().iterator();
			while (iter.hasNext()) {
				Jedis jedis = (Jedis) iter.next();
				deledCount += jedis.del(keys);
			}
			return deledCount;
		} catch (RuntimeException var11) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var11.getMessage(), (Throwable) var11);
			throw var11;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public List<Float> memRatio() {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		long deledCount = 0;
		List<Float> meRatios = new ArrayList<Float>();
		try {
			Iterator iter = resource.getAllShards().iterator();
			while (iter.hasNext()) {
				Jedis jedis = (Jedis) iter.next();
				long maxMemory = Long.valueOf(jedis.configGet("maxmemory").get(
						1));
				if (maxMemory == 0) {
					// δ��������ڴ棬����
					logger.warn("redis {0} δ��������ڴ�", servers);
					meRatios.add(50F);
					continue;
				}
				String memory = jedis.info("memory");
				String[] strList = StringDeal.split(memory, "\r\n");
				long usedMemory = 0l;
				for (String t : strList) {
					if (t.startsWith("used_memory:")) {
						usedMemory = Long.valueOf(StringDeal.split(t, ":")[1]);
						break;
					}
				}
				DecimalFormat df = new DecimalFormat("0.00");
				float radio = Float.valueOf(df.format((float) usedMemory
						/ (float) maxMemory)) * 100;
				meRatios.add(radio);
			}
			return meRatios;
		} catch (RuntimeException var11) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var11.getMessage(), (Throwable) var11);
			throw var11;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public boolean mset(final String... keysvalues) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		Map<Jedis, List<String>> map = new HashMap<Jedis, List<String>>();
		try {
			for (int i = 0; i < keysvalues.length; i++) {
				if (i % 2 == 0) {
					Jedis jedis = resource.getShard(keysvalues[i]);
					List<String> list = map.get(jedis);
					if (list == null) {
						list = new ArrayList<String>();
						map.put(jedis, list);
					}
					list.add(keysvalues[i]);
					list.add(keysvalues[i + 1]);
				}
			}
			boolean success = true;
			for (Map.Entry<Jedis, List<String>> entry : map.entrySet()) {
				String[] strings = new String[entry.getValue().size()];
				entry.getValue().toArray(strings);
				if (!entry.getKey().mset(strings).equals("OK")) {
					success = false;
				}
			}
			return success;
		} catch (RuntimeException var11) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var11.getMessage(), (Throwable) var11);
			throw var11;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public List<String> mget(final String... keys) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		Map<Jedis, List<String>> map = new HashMap<Jedis, List<String>>();
		List<String> values = new ArrayList<String>();
		Map<String, String> valMap = new HashMap<String, String>();
		try {
			for (int i = 0; i < keys.length; i++) {
				Jedis jedis = resource.getShard(keys[i]);
				List<String> list = map.get(jedis);
				if (list == null) {
					list = new ArrayList<String>();
					map.put(jedis, list);
				}
				list.add(keys[i]);
			}
			for (Map.Entry<Jedis, List<String>> entry : map.entrySet()) {
				String[] strings = new String[entry.getValue().size()];
				entry.getValue().toArray(strings);
				List<String> vals = entry.getKey().mget(strings);
				for (int i = 0; i < strings.length; i++) {
					valMap.put(strings[i], vals.get(i));
				}
			}
			for (int i = 0; i < keys.length; i++) {
				values.add(valMap.get(keys[i]));
			}
			map.clear();
			valMap.clear();
			return values;
		} catch (RuntimeException var11) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var11.getMessage(), (Throwable) var11);
			throw var11;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public void setsAdd(Serializable key, Serializable... value) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			String[] jsons = new String[value.length];
			for (int i = 0; i < value.length; i++) {
				jsons[i] = this.serializeJson(value[i]);
			}
			resource.sadd(this.serializeJson(key), jsons);
		} catch (RuntimeException var11) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var11.getMessage(), (Throwable) var11);
			throw var11;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}

		}

	}

	public boolean setsExists(Serializable key, Serializable value) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();

		try {
			boolean var6 = resource.sismember(this.serializeJson(key),
					this.serializeJson(value)).booleanValue();
			return var6;
		} catch (RuntimeException var9) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var9.getMessage(), (Throwable) var9);
			throw var9;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}

		}
	}

	public Set setsGet(Serializable key, long count) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			Set exp = new LinkedHashSet();
			for (int i = 0; (long) i < count; ++i) {
				exp.add(this.unserializeJson(resource.srandmember(this
						.serializeJson(key))));
			}
			return exp;
		} catch (RuntimeException var11) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var11.getMessage(), (Throwable) var11);
			throw var11;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}

		}
	}

	@Override
	public <T> Set<T> setsGet(Serializable key, long count, Class clazz) {
		// TODO Auto-generated method stub
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			Set exp = new LinkedHashSet();
			for (int i = 0; (long) i < count; ++i) {
				exp.add(this.unserializeJson(
						resource.srandmember(this.serializeJson(key)), clazz));
			}
			return exp;
		} catch (RuntimeException var11) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var11.getMessage(), (Throwable) var11);
			throw var11;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}

		}
	}

	public Set setsGetAll(Serializable key) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();

		try {
			Set exp = new LinkedHashSet();
			Set all = resource.smembers(this.serializeJson(key));
			Iterator var6 = all.iterator();

			while (var6.hasNext()) {
				String member = (String) var6.next();
				exp.add(this.unserializeJson(member));
			}

			return exp;
		} catch (RuntimeException var11) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var11.getMessage(), (Throwable) var11);
			throw var11;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}

		}
	}

	@Override
	public <T> Set<T> setsGetAll(Serializable var1, Class clazz) {
		// TODO Auto-generated method stub
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();

		try {
			Set exp = new LinkedHashSet();
			Set all = resource.smembers(this.serializeJson(var1));
			Iterator var6 = all.iterator();

			while (var6.hasNext()) {
				String member = (String) var6.next();
				exp.add(this.unserializeJson(member, clazz));
			}

			return exp;
		} catch (RuntimeException var11) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var11.getMessage(), (Throwable) var11);
			throw var11;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}

		}
	}

	public Object setsPop(Serializable key) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();

		try {
			Object var5 = this.unserializeJson(resource.spop(this
					.serializeJson(key)));
			return var5;
		} catch (RuntimeException var8) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var8.getMessage(), (Throwable) var8);
			throw var8;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}

		}
	}

	@Override
	public <T> T setsPop(Serializable key, Class clazz) {
		// TODO Auto-generated method stub
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();

		try {
			Object var5 = this.unserializeJson(
					resource.spop(this.serializeJson(key)), clazz);
			return (T) var5;
		} catch (RuntimeException var8) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var8.getMessage(), (Throwable) var8);
			throw var8;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}

		}
	}

	public void setsRemove(Serializable key, Serializable... value) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();

		try {
			String[] jsons = new String[value.length];
			for (int i = 0; i < value.length; i++) {
				jsons[i] = this.serializeJson(value[i]);
			}
			resource.srem(this.serializeJson(key), jsons);
		} catch (RuntimeException var8) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var8.getMessage(), (Throwable) var8);
			throw var8;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}

		}

	}

	public long setsSize(Serializable key) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();

		try {
			long var5 = resource.scard(this.serializeJson(key)).longValue();
			return var5;
		} catch (RuntimeException var9) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var9.getMessage(), (Throwable) var9);
			throw var9;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}

		}
	}

	public void sortedSetsAdd(Serializable key, double score, Serializable value) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			resource.zadd(this.serializeJson(key), score,
					this.serializeJson(value));
		} catch (RuntimeException var9) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var9.getMessage(), (Throwable) var9);
			throw var9;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public long sortedSetsAdd(Serializable key, Map<String, Double> values) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			return resource.zadd(this.serializeJson(key), values);
		} catch (RuntimeException var9) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var9.getMessage(), (Throwable) var9);
			throw var9;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public Set<String> zrangeByScore(String key, double min, double max) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			return resource.zrangeByScore(this.serializeJson(key), min, max);
		} catch (RuntimeException var9) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var9.getMessage(), (Throwable) var9);
			throw var9;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public boolean sortedSetsExists(Serializable key, Serializable value) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();

		try {
			Double score = resource.zscore(this.serializeJson(key),
					this.serializeJson(value));
			return score != null ? true : false;
		} catch (RuntimeException var9) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var9.getMessage(), (Throwable) var9);
			throw var9;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public Set sortedSetsGet(Serializable key, long start, long end) {
		return sortedSetsGet(key, start, end, null);
	}

	public Set sortedSetsGet(Serializable key, long start, long end, Class clazz) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			Set exp = new LinkedHashSet();
			Set<String> vals = null;
			if (start >= 0) {
				vals = resource.zrange(this.serializeJson(key), start, end);
			} else {
				vals = resource.zrevrange(this.serializeJson(key),
						Math.abs(start) - 1, Math.abs(end) - 1);
			}
			for (String val : vals) {
				if (clazz != null) {
					exp.add(this.unserializeJson(val, clazz));
				} else {
					exp.add(this.unserializeJson(val));
				}
			}
			return exp;
		} catch (RuntimeException var9) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var9.getMessage(), (Throwable) var9);
			throw var9;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public Set sortedSetsGetRev(Serializable key, long start, long end) {
		return sortedSetsGetRev(key, start, end, null);
	}

	public Set sortedSetsGetRev(Serializable key, long start, long end,
			Class clazz) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			Set exp = new LinkedHashSet();
			Set<String> vals = null;
			vals = resource.zrevrange(this.serializeJson(key), start, end);
			for (String val : vals) {
				if (clazz != null) {
					exp.add(this.unserializeJson(val, clazz));
				} else {
					exp.add(this.unserializeJson(val));
				}
			}
			return exp;
		} catch (RuntimeException var9) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var9.getMessage(), (Throwable) var9);
			throw var9;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public Set sortedSetsGetAll(Serializable key) {
		return sortedSetsGetAll(key, null);
	}

	public Set sortedSetsGetAll(Serializable key, Class clazz) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			Set exp = new LinkedHashSet();
			Set<String> vals = resource.zrange(this.serializeJson(key), 0, -1);
			for (String val : vals) {
				if (clazz != null) {
					exp.add(this.unserializeJson(val, clazz));
				} else {
					exp.add(this.unserializeJson(val));
				}
			}
			return exp;
		} catch (RuntimeException var9) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var9.getMessage(), (Throwable) var9);
			throw var9;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public Object sortedSetsGetFirst(Serializable key, Class clazz) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			Set<String> vals = resource.zrange(this.serializeJson(key), 0, 0);
			for (String val : vals) {
				if (clazz != null) {
					return this.unserializeJson(val, clazz);
				} else {
					return this.unserializeJson(val);
				}
			}
			return null;
		} catch (RuntimeException var9) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var9.getMessage(), (Throwable) var9);
			throw var9;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public Object sortedSetsGetFirst(Serializable key) {
		return sortedSetsGetFirst(key, null);
	}

	public Object sortedSetsGetLast(Serializable key, Class clazz) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			Set<String> vals = resource.zrange(this.serializeJson(key), -1, -1);
			for (String val : vals) {
				if (clazz != null) {
					return this.unserializeJson(val, clazz);
				} else {
					return this.unserializeJson(val);
				}
			}
			return null;
		} catch (RuntimeException var9) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var9.getMessage(), (Throwable) var9);
			throw var9;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public Object sortedSetsGetLast(Serializable key) {
		return sortedSetsGetLast(key, null);
	}

	public long sortedSetCount(Serializable var1, long min, long max) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			return resource.zcount(this.serializeJson(var1), min, max);
		} catch (RuntimeException var9) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var9.getMessage(), (Throwable) var9);
			throw var9;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public long sortedSetsGetScore(Serializable key, Serializable value) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			return resource.zrank(this.serializeJson(key),
					this.serializeJson(value));
		} catch (RuntimeException var9) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var9.getMessage(), (Throwable) var9);
			throw var9;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public void sortedSetsRemove(Serializable key, Serializable value) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			resource.zrem(this.serializeJson(key), this.serializeJson(value));
		} catch (RuntimeException var9) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var9.getMessage(), (Throwable) var9);
			throw var9;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public long sortedSetsRemove(String key, String... vals) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			return resource.zrem(key, vals);
		} catch (RuntimeException var9) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var9.getMessage(), (Throwable) var9);
			throw var9;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public void sortedSetsTrim(Serializable key, long size) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			long total = resource.zcard(this.serializeJson(key));
			if (total <= size) {
				return;
			} else {
				resource.zremrangeByRank(this.serializeJson(key), 0, total
						- size - 1);
			}
		} catch (RuntimeException var9) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var9.getMessage(), (Throwable) var9);
			throw var9;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public long sortedSetsSize(Serializable key) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			return resource.zcard(this.serializeJson(key));
		} catch (RuntimeException var9) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var9.getMessage(), (Throwable) var9);
			throw var9;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	public Object stackGet(Serializable key) {
		return null;
	}

	public void stackIn(Serializable key, Serializable... value) {
	}

	public Object stackOut(Serializable key) {
		return null;
	}

	public void stackRemove(Serializable key, Serializable value, long count) {
	}

	public long stackSize(Serializable key) {
		return 0L;
	}

	@Override
	public <T> T get(Serializable key, Class<T> clazz) {
		// TODO Auto-generated method stub
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();

		try {
			T var5 = this.unserializeJson(
					resource.get(this.serializeJson(key)), clazz);
			return var5;
		} catch (RuntimeException var8) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(var8.getMessage(), (Throwable) var8);
			throw var8;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}

		}
	}

	@Override
	public void setbit(String key, int i, boolean b) {
		// TODO Auto-generated method stub
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();

		try {
			resource.setbit(key, i, b);
		} catch (RuntimeException e) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(e.getMessage(), (Throwable) e);
			throw e;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}

		}
	}

	@Override
	public void setbit(String key, int[] offset, boolean b) {
		// TODO Auto-generated method stub
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();

		try {
			ShardedJedisPipeline pipeline = resource.pipelined();
			for (int i : offset) {
				pipeline.setbit(key, i, b);
			}
			pipeline.sync();
		} catch (RuntimeException e) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(e.getMessage(), (Throwable) e);
			throw e;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}

		}
	}

	@Override
	public boolean getbit(String key, int i) {
		// TODO Auto-generated method stub
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();

		try {
			return resource.getbit(key, i);
		} catch (RuntimeException e) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(e.getMessage(), (Throwable) e);
			throw e;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}

		}
	}

	@Override
	public boolean getbit(String key, int[] offset) {
		// TODO Auto-generated method stub
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();

		try {
			ShardedJedisPipeline pipeline = resource.pipelined();
			for (int i : offset) {
				pipeline.getbit(key, i);
			}
			List<Object> responses = pipeline.syncAndReturnAll();
			if (responses.size() != offset.length) {
				return false;
			}
			for (Object object : responses) {
				if (object instanceof Boolean) {
					Boolean contains = (Boolean) object;
					if (!contains) {
						return false;
					}
				}
			}
			return true;
		} catch (RuntimeException e) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(e.getMessage(), (Throwable) e);
			throw e;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}

		}
	}

	@Override
	public int pfadd(String key, String... elements) {
		// TODO Auto-generated method stub
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			return resource.pfadd(key, elements).intValue();
		} catch (RuntimeException e) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(e.getMessage(), (Throwable) e);
			throw e;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	@Override
	public long pfcount(String key) {
		// TODO Auto-generated method stub
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			return resource.pfcount(key);
		} catch (RuntimeException e) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(e.getMessage(), (Throwable) e);
			throw e;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	/**
	 * value ֵ�������ҵ�˳�����β��뵽��ͷ
	 * 
	 * @param key
	 * @param values
	 * @return ��������б?��
	 */
	public long lpush(Serializable key, Serializable... values) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			String[] strVals = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				strVals[i] = this.serializeJson(values[i]);
			}
			return resource.lpush(this.serializeJson(key), strVals);
		} catch (RuntimeException e) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(e.getMessage(), (Throwable) e);
			throw e;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	/**
	 * �б�ĳ���
	 * 
	 * @param key
	 * @return
	 */
	public long llen(Serializable key) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			return resource.llen(this.serializeJson(key));
		} catch (RuntimeException e) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(e.getMessage(), (Throwable) e);
			throw e;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	/**
	 * ����ָ������ڵ�Ԫ��
	 * 
	 * @param key
	 * @param start
	 *            ��ʼԪ��
	 * @param end
	 *            ����Ԫ��
	 * @return
	 */
	public int ltrim(Serializable key, long start, long end) {
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			resource.ltrim(this.serializeJson(key), start, end);
			return 0;
		} catch (RuntimeException e) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(e.getMessage(), (Throwable) e);
			throw e;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}

	@Override
	public long ttl(String key) {
		// TODO Auto-generated method stub
		ShardedJedis resource = (ShardedJedis) this.jedisPool.getResource();
		try {
			Long seconds = resource.ttl(key);
			return seconds == null ? 0 : seconds;
		} catch (RuntimeException e) {
			if (resource != null) {
				this.jedisPool.returnBrokenResource(resource);
				resource = null;
			}
			logger.error(e.getMessage(), (Throwable) e);
			throw e;
		} finally {
			if (resource != null) {
				resource.close();
				resource = null;
			}
		}
	}
}
