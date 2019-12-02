package com.alibaba.spider;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.spider.util.ErrorInfoUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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
import com.alibaba.service.ShareVar;

public class QuanYuanTangSpider {

	public static String postGetHtmlSc(String keyWord, int pageNum) {

		try {
			CloseableHttpClient httpClient = null;
			CloseableHttpResponse response = null;
			try {

				httpClient = HttpClients.createDefault();
				HttpPost httpPost = new HttpPost("http://www.qyt1902.com/gallery-ajax_get_goods.html");// 创建get请求
				httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
				StringEntity se = new StringEntity("cat_id=&virtual_cat_id=&scontent=n," + keyWord
						+ "&orderBy=view_count desc&showtype=grid&page=" + pageNum);
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

	public static List<ProductDetailPrice> parseQYT(WebsiteRule websiteRule, ProductInfo productInfo) {
		List<ProductDetailPrice> productDetailPriceList = new ArrayList<ProductDetailPrice>();

		int pageNum = 1;
		for (int i = 1; i <= pageNum; i++) {
			String url = "http://www.qyt1902.com/gallery.html?scontent=n," + productInfo.getProduct_name();
			String htmlSC = null;
			if (i == 1) {
				htmlSC = ShareVar.opHttpClient.clientDownloadHtml(url, "utf-8");
			} else {
				htmlSC = postGetHtmlSc(productInfo.getProduct_name(), i);
			}
			if (htmlSC == null)
				continue;
			Document document = Jsoup.parse(htmlSC);

			if (i == 1) {
				Elements pageElements = document.select("div.pageview > a.flip");
				if (pageElements.size() > 0) {
					String pageNumText = pageElements.get(pageElements.size() - 1).text();
					try {
						pageNum = Integer.parseInt(pageNumText);
					} catch (NumberFormatException nfe) {

					}
				}
			}
			Elements productListEmements = document.select("div.gallery-grid > ul > li");
			for (Element productListEmement : productListEmements) {
				String priceStr = productListEmement.select("div.goods-price > ins.price").text().replaceAll("￥", "")
						.trim();
				BigDecimal price = null;
				try {
					price = new BigDecimal(priceStr);
				} catch (Exception e) {

				}
				String productUrl = "http://www.qyt1902.com"
						+ productListEmement.select("div.goods-pic > a").attr("href") + "#instructions";
				String productHtmlSC = ShareVar.opHttpClient.clientDownloadHtml(productUrl, "utf-8");
				if (productHtmlSC == null)
					continue;
				Document productDocument = Jsoup.parse(productHtmlSC);
				if (price == null) {
					priceStr = productDocument.select("span.detail > b.price > ins.action-price").text()
							.replaceAll("￥", "").trim();
					try {
						price = new BigDecimal(priceStr);
					} catch (Exception e) {

					}
				}
				String name = null;
				String scqy = null;

				Elements infoElements = productDocument.select("div.product-concerns > ul > li");
				for (Element infoElement : infoElements) {
					String key = infoElement.select("span.label").text();
					if (key.contains("通用名称")) {
						name = infoElement.select("span.detail").text();
					} else if (key.contains("生产企业")) {
						scqy = infoElement.select("span.detail").text();
					}
				}

				String size = null;
				Elements smsElements = productDocument.select("div#product_instructions > div > textarea");
				String smsText = smsElements.text().replaceAll("&lt;", "<").replaceAll("&gt;", ">");
				smsElements = Jsoup.parse(smsText).select("p");
				for (Element smsElement : smsElements) {
					String value = smsElement.text().trim();
					if (value.contains("规格")) {
						size = value.replaceAll("\\【", "").replaceAll("\\】", "").replaceAll("规格", "");
					}
				}

				String id = null;
				if (name != null && scqy != null && size != null) {
					name = name.trim();
					scqy = scqy.trim();
					size = size.trim();
					id = ShareVar.getOnlyId(name, size, scqy, productInfo);
				}

				if (id != null) {
					ProductDetailPrice pdp = new ProductDetailPrice();
					pdp.setProduct_id(id);
					pdp.setProduct_maxPrice(price);
					pdp.setProduct_minPrice(price);
					pdp.setUrl(productUrl);
					pdp.setWebsite_name(websiteRule.getWebsite());
					pdp.setWebsite_type(websiteRule.getWebsite_type());
					pdp.setSpname(name);
					pdp.setGg(size);
					pdp.setScqy(scqy);
					productDetailPriceList.add(pdp);
				}
			}
		}

		return productDetailPriceList;
	}
}
