package com.alibaba.spider;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.spider.util.ErrorInfoUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.bean.ProductDetailPrice;
import com.alibaba.bean.ProductInfo;
import com.alibaba.bean.WebsiteRule;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.service.ShareVar;

public class AKangDaYaoFangSpider {

	private static ProductDetailPrice parseSon(String productUrl, BigDecimal price, WebsiteRule websiteRule,
			ProductInfo productInfo) {
		String productHtmlSC = ShareVar.opHttpClient.clientDownloadHtml(productUrl, "utf-8");
		if (productHtmlSC == null)
			return null;
		Document productDocument = Jsoup.parse(productHtmlSC);
		if (price == null) {
			String priceStr = productDocument.select("span.price.lFloat > em").text();
			try {
				price = new BigDecimal(priceStr);
			} catch (Exception e) {

			}
		}
		String scqy = productDocument.select("div.pro-r > div.pro-i > p.productFactory").text();
		String size = productDocument.select("div.pro-r > div.pro-i > button.gui").text();
		String name = productDocument.select("span.commonName").text();
		name = name.trim();
		scqy = scqy.trim();
		size = size.trim();
		String id = ShareVar.getOnlyId(name, size, scqy, productInfo);

		if (id != null) {
			ProductDetailPrice pdp = new ProductDetailPrice();
			pdp.setProduct_id(id);
			pdp.setProduct_maxPrice(price);
			pdp.setProduct_minPrice(price);
			pdp.setUrl(productUrl);
			pdp.setSpname(name);
			pdp.setGg(size);
			pdp.setScqy(scqy);
			pdp.setWebsite_name(websiteRule.getWebsite());
			pdp.setWebsite_type(websiteRule.getWebsite_type());
			return pdp;
		} else {
			return null;
		}
	}

	private static String getJson(String keyword, int page) {

		try {
			CloseableHttpClient httpClient = null;
			CloseableHttpResponse response = null;
			try {

				httpClient = HttpClients.createDefault();
				HttpPost httpPost = new HttpPost("https://www.ak1ak1.com/product/getProductList");
				httpPost.setHeader("Accept", "application/json, text/javascript, */*; q=0.01");
				httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
				httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
				httpPost.setHeader("Connection", "keep-alive");
				httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
				httpPost.setHeader("Referer", "https://www.ak1ak1.com/catalog.html?productName=" + keyword);
				httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
				String urlEN = "page=" + page + "&limit=20&productName=" + ShareVar.keyEncode(keyword, "utf-8")
						+ "&isImport=&productDrugType=&brandId=&lowestPrice=&highest=&productType_one=&productType_two=&sysNo=pc&prescriptionType=0";
				StringEntity se = new StringEntity(urlEN);
				httpPost.setEntity(se);
				RequestConfig requestConfig = RequestConfig.custom()
						// .setProxy(host)
						.setSocketTimeout(15000).setConnectTimeout(15000).setConnectionRequestTimeout(15000).build();
				httpPost.setConfig(requestConfig);
				// 执行请求
				String urlContent = "";
				response = httpClient.execute(httpPost);
				HttpEntity entity = response.getEntity();
				urlContent = EntityUtils.toString(entity, "utf-8");

				return urlContent;
			} finally {
				if (httpClient != null)
					httpClient.close();
				if (response != null)
					response.close();
			}
		} catch (Exception e) {
			return null;
		}

	}

	public static List<ProductDetailPrice> parseAKDYF(WebsiteRule websiteRule, ProductInfo productInfo) {
		List<ProductDetailPrice> productDetailPriceList = new ArrayList<ProductDetailPrice>();
		int pageNum = 1;
		for (int i = 1; i <= pageNum; i++) {
			String url = "https://www.ak1ak1.com/catalog.html?productName=" + productInfo.getProduct_name();
			if (i != 1) {
				String json = getJson(productInfo.getProduct_name(), i);
				JSONObject jsonObject = JSONObject.parseObject(json);
				JSONArray dataJsonArr = jsonObject.getJSONArray("data");
				for (Object dataJson : dataJsonArr) {
					JSONObject dataJsonObj = JSONObject.parseObject(JSON.toJSONString(dataJson));
					String productUrl = "https://www.ak1ak1.com/products" + dataJsonObj.getString("productNumber")  + ".html";
					BigDecimal price = dataJsonObj.getBigDecimal("dataJsonObj");
					ProductDetailPrice pdp = parseSon(productUrl, price, websiteRule, productInfo);
					if (pdp != null) {
						productDetailPriceList.add(pdp);
					}
				}
			} else {
				String htmlSC = ShareVar.opHttpClient.clientDownloadHtml(url, "utf-8");
				if (htmlSC == null)
					continue;
				Document document = Jsoup.parse(htmlSC);

				if (i == 1) {
					String pageNumText = document.select("div.page > span.tabCount").text().trim();
					try {
						pageNum = Integer.parseInt(pageNumText);
					} catch (NumberFormatException nfe) {

					}
				}
				Elements productListEmements = document.select("div.pro-wrap > div.item");
				for (Element productListEmement : productListEmements) {
					String priceStr = productListEmement.select("span.buy-pri").text().replaceAll("¥", "").trim();
					BigDecimal price = null;
					try {
						price = new BigDecimal(priceStr);
					} catch (Exception e) {

					}
					String productUrl = "https://www.ak1ak1.com" + productListEmement.select("a.product-info").attr("href");
					ProductDetailPrice pdp = parseSon(productUrl, price, websiteRule, productInfo);
					if (pdp != null) {
						productDetailPriceList.add(pdp);
					}
				}
			}
		}

		return productDetailPriceList;
	}
}