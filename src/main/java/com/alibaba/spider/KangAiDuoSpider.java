package com.alibaba.spider;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.spider.util.ErrorInfoUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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
import com.alibaba.fastjson.JSONObject;
import com.alibaba.service.ShareVar;

public class KangAiDuoSpider {

	public static BigDecimal getPrice(String priceId) {

		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet("http://www.360kad.com/product/getprice?wareskucode=" + priceId);
		httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
		httpGet.setConfig(ShareVar.opHttpClient.requestConfig);
		try {
			CloseableHttpResponse chResponse = null;
			try {
				chResponse = httpClient.execute(httpGet);
				HttpEntity entity = chResponse.getEntity();
				String urlContent = EntityUtils.toString(entity, "utf-8");
				BigDecimal price = JSONObject.parseObject(urlContent).getJSONObject("StyleInfo").getBigDecimal("Price");
				return price;
			} finally {
				if (httpClient != null)
					httpClient.close();
				if (chResponse != null)
					chResponse.close();
			}
		} catch (Exception e) {
			return null;
		}
	}

	private static ProductDetailPrice parseXq(String url, WebsiteRule websiteRule, ProductInfo productInfo) {
		String htmlSC = ShareVar.opHttpClient.clientDownloadHtml(url, "utf-8");
		if (htmlSC == null)
			return null;
		Document document = Jsoup.parse(htmlSC);

		String name = document.select("div.dtl-inf-top > div:nth-child(1) > div.dtl-inf-r").text();
		String scqy = document.select("div.dtl-inf-top > div:nth-child(3) > div.dtl-inf-r").text();

		Map<String, String> infoMap = new HashMap<String, String>();
		Elements infoElements = document.select("div#wrap990list1 > ul > li");
		for (Element infoElement : infoElements) {
			String infoText = infoElement.text();
			String[] split = infoText.split("：");
			if (split != null && split.length == 2) {
				infoMap.put(split[0], split[1]);
			}
		}
		String size = infoMap.get("规格");
		size = (size != null) ? size.replaceAll(" ", "") : null;
		if (name == null)
			name = infoMap.get("通用名称");

		name = (name != null) ? name.replaceAll(" ", "") : null;
		if (scqy == null)
			scqy = infoMap.get("生产企业");
		scqy = (scqy != null) ? scqy.replaceAll(" ", "") : null;

		String productId = ShareVar.getOnlyId(name, size, scqy, productInfo);
		if (productId == null)
			return null;
		String priceId = url.replaceAll("http://www.360kad.com/product/", "");
		priceId = priceId.substring(0, priceId.indexOf(".shtml"));
		BigDecimal price = getPrice(priceId);
		if (price == null)
			return null;

		ProductDetailPrice data = new ProductDetailPrice();
		data.setProduct_id(productId);
		data.setUrl(url);
		data.setWebsite_name(websiteRule.getWebsite());
		data.setWebsite_type(websiteRule.getWebsite_type());
		data.setProduct_minPrice(price);
		data.setProduct_maxPrice(price);
		data.setSpname(name);
		data.setGg(size);
		data.setScqy(scqy);
		return data;

	}

	public static List<ProductDetailPrice> parseKangAiDuo(WebsiteRule websiteRule, ProductInfo productInfo)
			throws IOException {
		List<ProductDetailPrice> productDetailPriceList = new ArrayList<ProductDetailPrice>();

		int pageNum = 1;
		for (int i = 1; i <= pageNum; i++) {
			String htmlSC = null;
			if (i == 1)
				htmlSC = ShareVar.opHttpClient.clientDownloadHtml(
						"http://search.360kad.com/?pageText=" + productInfo.getProduct_name(), "utf-8");
			else
				htmlSC = ShareVar.opHttpClient.clientDownloadHtml(
						"http://search.360kad.com/?pageText=" + productInfo.getProduct_name() + "pageIndex=" + i,
						"utf-8");
			if (htmlSC == null)
				continue;
			Document document = Jsoup.parse(htmlSC);
			if (i == 1) {
				try {
					pageNum = Integer.parseInt((document.select("div.countList > span").text().replaceAll("< 1 /", "")
							.replaceAll(" >", "").replaceAll(" ", "")));
				} catch (Exception e) {

				}
			}
			Elements lbElements = document.select("div.plist_li > div.plist_li_i");
			for (Element lbElement : lbElements) {
				String lbUrl = lbElement.select("p.t > a").attr("href").replaceAll(" ", "");
				ProductDetailPrice pdp = parseXq(lbUrl, websiteRule, productInfo);
				if (pdp != null)
					productDetailPriceList.add(pdp);
			}
		}

		return productDetailPriceList;
	}
}
