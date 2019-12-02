package com.alibaba.service;

import java.io.FileReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


public class Encryption {
	private static RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(15000).setConnectTimeout(15000)
			.setConnectionRequestTimeout(15000).build();

	public static String getProductListSign(String product, int pageNum, String time) {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("js");
		String str = null;
		try {
			String filePath = System.getProperty("user.dir") + "\\js\\encryption.js";
			engine.eval(new FileReader(filePath));
			if (engine instanceof Invocable) {
				Invocable invocable = (Invocable) engine;
				SignEncryption executeMethod = invocable.getInterface(SignEncryption.class);
//				String s = "ddky.cms.h5.search.get.newbrandIdcity北京市lat39.91488908lng116.40387397methodddky.cms.h5.search.get.neworderTypeId3pageNo"
//						+ pageNum + "pageSize20pharmacyTypeplatH5platformH5searchPanel1searchTypeo2oshopIdsuite1t"
//						+ time + "unique5D45E932ACAEB89A180B542200FBB7F6v1.0versionName5.3.0wd" + product
//						+ " 6C57AB91A1308E26B797F4CD382AC79D";
				String s = "ddky.cms.h5.search.get.newbrandIdcity北京市lat39.91488908lng116.40387397methodddky.cms.h5.search.get.neworderTypeId0pageNo"
						+ pageNum + "pageSize20pharmacyTypeplatH5platformH5searchPanel1searchTypeo2oshopIdsuite1t"
						+ time + "unique5D45E932ACAEB89A180B542200FBB7F6v1.0versionName5.3.0wd" + product
						+ "6C57AB91A1308E26B797F4CD382AC79D";
				str = executeMethod.getSign(s);
			}
			return str;
		} catch (Exception e) {
			return null;
		}
	}

	public static String getProductListSign(int pageNum, String time) {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("js");
		String str = null;
		try {
			String filePath = System.getProperty("user.dir") + "\\js\\encryption.js";
			engine.eval(new FileReader(filePath));
			if (engine instanceof Invocable) {
				Invocable invocable = (Invocable) engine;
				SignEncryption executeMethod = invocable.getInterface(SignEncryption.class);
				String s = "ddky.cms.all.search.spells.blend.by530.recipeb2cDirectoryId730,731,733,812,805,1169,1170,1171,1172,1173,1174,1175,1176,1165,1166,1167,1168,960,1080,1220,739,742,805,1203,804,809,1180,725,728,1201,1202,811,1200,1165,1166,1167,1168methodddky.cms.all.search.spells.blend.by530.recipeorderTypeId3pageNo"
						+ pageNum + "pageSize20platH5platformH5searchPanel2searchTypeb2cshopId-1suite2t" + time
						+ "unique5D45E932ACAEB89A180B542200FBB7F6v1.0versionName5.3.06C57AB91A1308E26B797F4CD382AC79D";
				str = executeMethod.getSign(s);

			}
			return str;
		} catch (Exception e) {
			return null;
		}
	}

	public static String getProductSign(String productId, String time, String shopId, String suite) {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("js");
		String str = null;
		try {
			String filePath = System.getProperty("user.dir") + "\\js\\encryption.js";
			engine.eval(new FileReader(filePath));
			if (engine instanceof Invocable) {
				Invocable invocable = (Invocable) engine;
				SignEncryption executeMethod = invocable.getInterface(SignEncryption.class);
				String s = "ddky.cms.product.detailfps.getcity北京市lat39.91488908lng116.40387397methodddky.cms.product.detailfps.getplatH5platformH5shopId"
						+ shopId + "skuId" + productId + "suite" + suite + "t" + time
						+ "v1.0versionName5.3.06C57AB91A1308E26B797F4CD382AC79D";
				str = executeMethod.getSign(s);//

			}
			return str;
		} catch (Exception e) {
			return null;
		}
	}

	public static String getSearchProductListJson(String productName, int pageNum) {
		try {
			URIBuilder uriBuilder = new URIBuilder("https://api.ddky.com/cms/rest.htm");
			uriBuilder.addParameter("method", "ddky.cms.h5.search.get.new");
			uriBuilder.addParameter("pageNo", "" + pageNum);
			uriBuilder.addParameter("pageSize", "20");
			uriBuilder.addParameter("shopId", null);
			uriBuilder.addParameter("brandId", null);
			uriBuilder.addParameter("pharmacyType", null);
			uriBuilder.addParameter("suite", "1");
			uriBuilder.addParameter("searchType", "o2o");
			uriBuilder.addParameter("orderTypeId", "0");
			uriBuilder.addParameter("searchPanel", "1");
			uriBuilder.addParameter("wd", productName);
			uriBuilder.addParameter("lat", "39.91488908");

			uriBuilder.addParameter("lng", "116.40387397");
			uriBuilder.addParameter("city", "北京市");
			uriBuilder.addParameter("unique", "5D45E932ACAEB89A180B542200FBB7F6");
			uriBuilder.addParameter("versionName", "5.3.0");
			uriBuilder.addParameter("plat", "H5");
			LocalDateTime ldt = LocalDateTime.now();
			String time = ldt.format(DateTimeFormatter.ofPattern("yyyy-M-d H:m:ss"));
			uriBuilder.addParameter("platform", "H5");
			uriBuilder.addParameter("t", time);
			uriBuilder.addParameter("v", "1.0");
			String sign = getProductListSign(productName, pageNum, time);
			uriBuilder.addParameter("sign", sign);
			CloseableHttpClient httpClient = null;
			try {
				httpClient = HttpClients.createDefault();
				HttpGet httpGet = new HttpGet(uriBuilder.build());
				httpGet.setHeader("Accept", "*/*");
				httpGet.setHeader("Accept-Encoding", "gzip, deflate, br");
				httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
				httpGet.setHeader("Connection", "keep-alive");
				httpGet.setHeader("Host", "api.ddky.com");
				httpGet.setHeader("Referer", "https://m.ddky.com/list.html?searchkey=" + productName + "&from=o2o");
				httpGet.setHeader("accept-language", "zh-CN,zh;q=0.8");
				// httpGet.setHeader("cookie","Hm_lvt_b2bf749885c83e6182ad66079206ecf5=1572834294;
				// wxType=0; LAT=39.91488908; LNG=116.40387397;
				// shopInfo=%7B%22city%22%3A%22%E5%8C%97%E4%BA%AC%E5%B8%82%22%2C%22shopId%22%3A%22100012%22%2C%22shopName%22%3A%22%E5%8F%AE%E5%BD%93%E6%99%BA%E6%85%A7%E8%8D%AF%E6%88%BF%EF%BC%88%E5%8C%97%E4%BA%AC%EF%BC%89%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8%E7%AC%AC%E5%9B%9B%E5%8D%81%E4%BA%94%E5%BA%97(%E6%80%BB%E9%83%A8%E5%BA%97)%22%2C%22lng%22%3A116.40387397%2C%22lat%22%3A39.91488908%2C%22suite%22%3A1%7D");
				httpGet.setHeader("User-Agent",
						"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3947.100 Safari/537.36");
				httpGet.setConfig(requestConfig);
				CloseableHttpResponse chResponse = httpClient.execute(httpGet);
				HttpEntity entity = chResponse.getEntity();
				String urlContent = EntityUtils.toString(entity, "utf-8");
				return urlContent;
			} finally {
				if (httpClient != null)
					httpClient.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public static String getProductListJson(int pageNum) {
		try {
			URIBuilder uriBuilder = new URIBuilder("https://api.ddky.com/cms/rest.htm");
			uriBuilder.addParameter("method", "ddky.cms.all.search.spells.blend.by530.recipe");
			uriBuilder.addParameter("b2cDirectoryId",
					"730,731,733,812,805,1169,1170,1171,1172,1173,1174,1175,1176,1165,1166,1167,1168,960,1080,1220,739,742,805,1203,804,809,1180,725,728,1201,1202,811,1200,1165,1166,1167,1168");
			uriBuilder.addParameter("pageNo", "" + pageNum);
			uriBuilder.addParameter("pageSize", "20");
			uriBuilder.addParameter("orderTypeId", "3");
			uriBuilder.addParameter("suite", "2");
			uriBuilder.addParameter("searchPanel", "2");
			uriBuilder.addParameter("searchType", "b2c");
			uriBuilder.addParameter("shopId", "-1");
			uriBuilder.addParameter("unique", "5D45E932ACAEB89A180B542200FBB7F6");
			uriBuilder.addParameter("versionName", "5.3.0");
			uriBuilder.addParameter("plat", "H5");
			uriBuilder.addParameter("platform", "H5");
			LocalDateTime ldt = LocalDateTime.now();
			String time = ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			uriBuilder.addParameter("t", time);
			uriBuilder.addParameter("v", "1.0");
			String sign = getProductListSign(pageNum, time);
			uriBuilder.addParameter("sign", sign);
			CloseableHttpClient httpClient = null;
			try {
				httpClient = HttpClients.createDefault();
				HttpGet httpGet = new HttpGet(uriBuilder.build());
				httpGet.setHeader("Accept", "*/*");
				httpGet.setHeader("Accept-Encoding", "gzip, deflate, br");
				httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
				httpGet.setHeader("Connection", "keep-alive");
				httpGet.setHeader("Host", "api.ddky.com");
				httpGet.setHeader("Referer", "https://m.ddky.com/findDrug_list.html?id=402");
				httpGet.setHeader("accept-language", "zh-CN,zh;q=0.8");
				httpGet.setHeader("User-Agent",
						"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3947.100 Safari/537.36");
				httpGet.setConfig(requestConfig);
				CloseableHttpResponse chResponse = httpClient.execute(httpGet);
				HttpEntity entity = chResponse.getEntity();
				String urlContent = EntityUtils.toString(entity, "utf-8");
				return urlContent;
			} finally {
				if (httpClient != null)
					httpClient.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public static String getProductJson(String skuId, String shopId, String suite) {
		try {
			URIBuilder uriBuilder = new URIBuilder("https://api.ddky.com/cms/rest.htm");
			uriBuilder.addParameter("method", "ddky.cms.product.detailfps.get");
			uriBuilder.addParameter("versionName", "5.3.0");
			uriBuilder.addParameter("skuId", skuId);
			uriBuilder.addParameter("shopId", shopId);
			uriBuilder.addParameter("suite", suite);
			uriBuilder.addParameter("city", "北京市");
			uriBuilder.addParameter("lat", "39.91488908");
			uriBuilder.addParameter("lng", "116.40387397");
			uriBuilder.addParameter("plat", "H5");
			uriBuilder.addParameter("platform", "H5");
			LocalDateTime ldt = LocalDateTime.now();
			String time = ldt.format(DateTimeFormatter.ofPattern("yyyy-M-d H:m:s"));
			uriBuilder.addParameter("t", time);
			uriBuilder.addParameter("v", "1.0");
			String sign = getProductSign(skuId, time, shopId, suite);
			uriBuilder.addParameter("sign", sign);
			CloseableHttpClient httpClient = null;
			try {
				httpClient = HttpClients.createDefault();
				HttpGet httpGet = new HttpGet(uriBuilder.build());
				httpGet.setHeader("Accept", "*/*");
				httpGet.setHeader("Accept-Encoding", "gzip, deflate, br");
				httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
				httpGet.setHeader("Connection", "keep-alive");
				httpGet.setHeader("Host", "api.ddky.com");
				httpGet.setHeader("Referer", "https://m.ddky.com/findDrug_list.html?id=402");
				httpGet.setHeader("accept-language", "zh-CN,zh;q=0.8");
				httpGet.setHeader("User-Agent",
						"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3947.100 Safari/537.36");
				httpGet.setConfig(requestConfig);
				CloseableHttpResponse chResponse = httpClient.execute(httpGet);
				HttpEntity entity = chResponse.getEntity();
				String urlContent = EntityUtils.toString(entity, "utf-8");
				return urlContent;
			} finally {
				if (httpClient != null)
					httpClient.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}