package com.alibaba.service;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * httpclient操作类
 * 
 * @author mayn
 *
 */
public class OperationHttpClient {

	public RequestConfig requestConfig = RequestConfig.custom()
			// .setProxy(host)
			.setSocketTimeout(15000).setConnectTimeout(15000).setConnectionRequestTimeout(15000).build();

	/**
	 * 根据HttpClient下载网页
	 * 
	 * @param url 要下载网页的url
	 * @return 返回该网页的源代码
	 */
	public String clientDownloadHtml(String httpUrl, String charset) {

		try {
			CloseableHttpClient httpClient = null;
			CloseableHttpResponse response= null;
			try {
				httpClient = HttpClients.createDefault();
				HttpGet httpGet = new HttpGet(httpUrl);// 创建get请求
				httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
				httpGet.setConfig(requestConfig);
				// 执行请求
				String urlContent = "";
				response = httpClient.execute(httpGet);
				HttpEntity entity = response.getEntity();
				urlContent = EntityUtils.toString(entity, charset);
			
				return urlContent;
			} finally {
				if(httpClient != null)
					httpClient.close();
				if(response != null)
					response.close();
			}				
		} catch (Exception e) {
			return null;
		}

	
	}
}
