package com.alibaba.service;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

import redis.clients.jedis.Jedis;

public class RedisUtil {

	static {
		regist();
	}
	private static Jedis jedis;

	private static void regist() {
		jedis = new Jedis("127.0.0.1", 6379);
		jedis.auth("123456");
	}

	public static boolean ifCz(String urlSet,String url) {
		String urlBase64 = base64(url);
		if (jedis.sismember(urlSet, urlBase64)) {
			return true;
		} else {
			jedis.sadd(urlSet, urlBase64);
			return false;
		}
	}

	public static void delDatabase(String databasaeName) {
		jedis.del(databasaeName);
	}
	
	public static void insertIdSet(String urlSet,String id) {

		if (!jedis.sismember(urlSet, id)) {
			jedis.sadd("idSet", id);
		}
	}

	public static String base64(String url) {

		try {
			String asB64 = Base64.getEncoder().encodeToString(url.getBytes("utf-8"));
			return asB64;
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
}
