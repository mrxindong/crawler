package com.alibaba.spider;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.spider.util.ErrorInfoUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.bean.ProductDetailPrice;
import com.alibaba.bean.ProductInfo;
import com.alibaba.bean.WebsiteRule;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.service.ShareVar;

public class MiaoShouYishengSpider {

	public static List<ProductDetailPrice> parseMSYS(WebsiteRule websiteRule, ProductInfo productInfo) {
		List<ProductDetailPrice> productDetailPriceList = new ArrayList<ProductDetailPrice>();
		int pageNum = 1;
		for (int i = 1; i <= pageNum; i++) {
			String url = "https://www.miaoshou.com/search?keyword=" + productInfo.getProduct_name();
			if (i != 1) {
				url += "&page=" + i;
			}
			String htmlSC = ShareVar.opHttpClient.clientDownloadHtml(url, "utf-8");
			if (htmlSC == null)
				continue;
			Document document = Jsoup.parse(htmlSC);
			if (i == 1) {
				String pageNumText = document.select("div.select_content > div.navbar_page > div.fl.fs12.text6.mt5")
						.text().replaceAll(" ", "").replaceAll("1/", "");
				try {
					pageNum = Integer.parseInt(pageNumText);
				} catch (Exception e) {

				}
			}
			Elements lbElements = document.select("div.category_column_warp > div.list > ul > li > div > a.check");

			if (lbElements.size() > 0) {
				for (Element lbElement : lbElements) {
					String xqUrl = lbElement.attr("href");
					int lastindex = xqUrl.lastIndexOf("t/");
					String id = xqUrl.substring(lastindex + 2, xqUrl.length() - 5);
					xqUrl = "https://www.miaoshou.com" + xqUrl;
					ProductDetailPrice pdp = parseXQ(id, xqUrl, websiteRule, productInfo);
					if (pdp != null)
						productDetailPriceList.add(pdp);
				}
			}
		}

		return productDetailPriceList;
	}

	public static ProductDetailPrice parseXQ(String id, String url, WebsiteRule websiteRule, ProductInfo productInfo) {
		String htmlSC = ShareVar.opHttpClient.clientDownloadHtml(url, "utf-8");
		Document document = Jsoup.parse(htmlSC);
		Map<String, String> infoMap = new HashMap<String, String>();
		Elements infoElements = document.select("div.fr > div.new_price_box > dl");
		for (Element infoElement : infoElements) {
			String key = infoElement.select("dt").text();
			String value = infoElement.select("dd").text();
			if (key != null && !(key.replaceAll("", "").equals("")) && value != null
					&& !(value.replaceAll("", "").equals("")))
				infoMap.put(key.replaceAll(" ", "").replaceAll(" ", ""), value.replaceAll(" ", ""));
		}
		String name = null;
		String size = null;
		String scqy = null;
		if (infoMap.size() > 0) {
			name = infoMap.get("通用名称");
			size = infoMap.get("产品规格");
			scqy = infoMap.get("生产厂家");

		}
		String priceHtml = ShareVar.opHttpClient.clientDownloadHtml("https://www.miaoshou.com/product/info?id=" + id,
				"utf-8");
		BigDecimal price = null;
		try {
			if (priceHtml != null) {
				JSONObject jsonObject = JSONObject.parseObject(priceHtml);
				JSONObject dataObject = jsonObject.getJSONObject("data");
				price = dataObject.getBigDecimal("miaoshou_price");
			}
		} catch (Exception e) {
			System.out.println(url);
		}

		Map<String, String> jsMap = new HashMap<String, String>();
		Elements jsElements = document.select("div#describe > div > div.add > div.pro_decBox > div.ovH");
		for (Element jsElement : jsElements) {
			Elements jsElements2 = jsElement.select("div > div");
			if (jsElements2.size() == 2) {
				String key = jsElements2.get(0).text();
				String value = jsElements2.get(1).text();
				jsMap.put(key, value);
			}
		}
		if (jsMap.size() > 0) {

			if (name == null || name.replaceAll(" ", "").equals("")) {
				name = jsMap.get("通用名称");
			}

			if (size == null || size.replaceAll(" ", "").equals("")) {
				size = jsMap.get("规格");
			}
			if (scqy == null || scqy.replaceAll(" ", "").equals("")) {
				scqy = jsMap.get("生产企业");
			}
		}

		Elements smsElements = document.select("#describe > div:nth-child(2) > div.pro_decBox > div.ovH");
		Map<String, String> smsMap = new HashMap<String, String>();
		for (Element smsElement : smsElements) {
			Elements select = smsElement.select("div > div");
			if (select != null && select.size() == 2) {
				smsMap.put(select.get(0).text(), select.get(1).text());
			}
		}
		if (smsMap.size() > 0) {
			if (name == null || name.replaceAll(" ", "").equals("")) {
				name = smsMap.get("通用名称");
			}

			if (size == null || size.replaceAll(" ", "").equals("")) {
				size = smsMap.get("规格");
			}
			if (scqy == null || scqy.replaceAll(" ", "").equals("")) {
				scqy = smsMap.get("生产企业");
			}
		}
		String productId = ShareVar.getOnlyId(name, size, scqy, productInfo);
		if (productId == null)
			return null;
		ProductDetailPrice pdp = new ProductDetailPrice();
		pdp.setProduct_id(productId);
		pdp.setProduct_maxPrice(price);
		pdp.setProduct_minPrice(price);
		pdp.setWebsite_name(websiteRule.getWebsite());
		pdp.setWebsite_type(websiteRule.getWebsite_type());
		pdp.setSpname(name);
		pdp.setGg(size);
		pdp.setScqy(scqy);
		pdp.setUrl(url);
		pdp.setProduct_id(productId);
		return pdp;

	}
}
