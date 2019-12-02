package com.alibaba.spider;

import java.io.IOException;
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
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.bean.ProductDetailPrice;
import com.alibaba.bean.ProductInfo;
import com.alibaba.bean.WebsiteRule;
import com.alibaba.mapper.CrawlerMapper;
import com.alibaba.service.ShareVar;

public class BaBaiFangSpider {

	private static Map<String, Map<String, List<String>>> keyMap = new HashMap<String, Map<String, List<String>>>();

	public static boolean keyIfExist(String scqy, String productName, String size) {
		if (keyMap.containsKey(scqy)) {
			Map<String, List<String>> productNameMap = keyMap.get(scqy);
			if (productNameMap.containsKey(productName)) {
				List<String> sizeList = productNameMap.get(productName);
				if (sizeList.contains(size)) {
					return false;
				} else {
					sizeList.add(size);
					return true;
				}
			} else {
				List<String> sizeList = new ArrayList<String>();
				sizeList.add(size);
				productNameMap.put(productName, sizeList);
				return true;
			}
		} else {
			List<String> productList = new ArrayList<String>();
			productList.add(size);
			Map<String, List<String>> productNameSizeMap = new HashMap<String, List<String>>();
			productNameSizeMap.put(productName, productList);
			keyMap.put(scqy, productNameSizeMap);
			return true;
		}
	}

	/**
	 * @param url
	 * @param websiteRule
	 * @param drugInfo
	 * @throws IOException
	 */
	public static ProductDetailPrice parseXq(String url, WebsiteRule websiteRule, ProductInfo productInfo) {
		String htmlSC = ShareVar.opHttpClient.clientDownloadHtml(url, "utf-8");
		if (htmlSC == null)
			return null;
		Document document = Jsoup.parse(htmlSC);
		Map<String, String> infoMap = new HashMap<String, String>();
		Elements infoElements = document.select("div.main_mid > ul.pro_msg > li");
		for (Element infoElement : infoElements) {
			String infoText = infoElement.text();
			String[] split = infoText.split("：");
			if (split.length == 2) {
				infoMap.put(split[0], split[1]);
			}
		}
		String tym = infoMap.get("通用名称");
		tym = tym != null ? tym.replaceAll(" ", "") : null;
		String size = null;
		Elements sizeElements = document.select("div.b_select > div");
		for (Element sizeElement : sizeElements) {
			String sizeId = sizeElement.attr("id").replaceAll("v_", "");
			if (url.contains(sizeId)) {
				size = sizeElement.text();
				break;
			}
		}
		size = size != null ? size.replaceAll(" ", "") : null;
		Map<String, String> smsMap = new HashMap<String, String>();
		Elements smsElements = document.select("div#introduction > table > tbody > tr");
		for (Element smsElement : smsElements) {
			String key = smsElement.select("td:nth-child(1)").text();
			String value = smsElement.select("td:nth-child(2)").text();
			if (key != null && !(key.replaceAll(" ", "").equals("")) && value != null
					&& !(value.replaceAll(" ", "").equals(""))) {
				smsMap.put(key.replaceAll("：", "").replaceAll(" ", ""), value.trim());
			}
		}
		String scqy = smsMap.get("生产厂商");
		String productId = ShareVar.getOnlyId(tym, size, scqy, productInfo);
		if (productId == null)
			return null;

		ProductDetailPrice pdp = new ProductDetailPrice();
		pdp.setProduct_id(productId);
		pdp.setUrl(url);
		pdp.setWebsite_name(websiteRule.getWebsite());
		pdp.setWebsite_type(websiteRule.getWebsite_type());
		pdp.setProduct_id(productId);
		pdp.setSpname(tym);
		pdp.setGg(size);
		pdp.setScqy(scqy);
		try {
			String minPrice = infoMap.get("八百方价").replaceAll(" ", "").replaceAll("￥", "");
			pdp.setProduct_minPrice(new BigDecimal(minPrice));
			String maxPrice = infoMap.get("市场价").replaceAll(" ", "").replaceAll("￥", "");
			pdp.setProduct_maxPrice(new BigDecimal(maxPrice));
		} catch (Exception e) {
			return null;
		}

		String shop = document.select("div.wrapper > div.busi_Heard > div.busi_top > div.busi_logo.fontGrey1.font22 > a.s_name").text();
		shop = shop != null ? shop.replaceAll(" ", "").replaceAll("·", "").replaceAll("经过八百方严格评选，给您提供更好的服务体验", "")
				: null;
		pdp.setShop_name(shop);
		return pdp;
	}

	public static List<ProductDetailPrice> parseYffb(String url, WebsiteRule websiteRule, ProductInfo productInfo) {
		List<ProductDetailPrice> pdpList = new ArrayList<>();
		int num = 1;
		for (int i = 1; i <= num; i++) {
			String newUrl = null;
			if (i == 1) {
				newUrl = url;
			} else {
				newUrl = url + "&p=" + i;
			}
			String htmlSC = null;
			int cycleNum = 1;
			do {
				htmlSC = ShareVar.opHttpClient.clientDownloadHtml(newUrl, "utf-8");
				cycleNum++;
			} while (cycleNum < 4 && htmlSC == null);
			if (htmlSC == null)
				continue;
			Document document = Jsoup.parse(htmlSC);
			if (i == 1) {
				Elements pageElements = document.select("div#pageDiv > div.pages > a");
				if (pageElements != null && pageElements.size() > 0) {
					Element pageElement = pageElements.get(pageElements.size() - 1);
					String href = pageElement.attr("href");
					int lastIndex = href.lastIndexOf("&p=");
					href = href.substring(lastIndex + 3, href.length());
					try {
						num = Integer.parseInt(href);
					} catch (Exception e) {

					}
				}
			}
			Elements lbElements = document.select("ul.prod_list > li");
			for (Element lbElement : lbElements) {
				String price = lbElement.select("div.prod_price > p.prodPrice").text();
				if (price.replaceAll(" ", "").equals("")) {
					return pdpList;
				}
				String lbUrl = lbElement.select("div.prod_info > h3 > a").attr("href");
				ProductDetailPrice parseXq = parseXq(lbUrl, websiteRule, productInfo);
				if (parseXq != null)
					pdpList.add(parseXq);
			}
		}
		return pdpList;
	}

	/**
	 * 解析八百方网站
	 * 
	 * @param websiteRule
	 * @throws IOException
	 */
	public static List<ProductDetailPrice> parseBaBaiFang(WebsiteRule websiteRule, ProductInfo productInfo) throws IOException {

		List<ProductDetailPrice> pdpList = new ArrayList<ProductDetailPrice>();

		int num = 1;
		for (int i = 1; i <= num; i++) {
			System.out.println(productInfo.getProduct_name());
			String url = "http://www.800pharm.com/shop/search.html?keyword=" + productInfo.getProduct_name();
			if (i != 1) {
				url += "&p=" + i;
			}
			String htmlSC = null;
			int number = 0;
			do {
				htmlSC = ShareVar.opHttpClient.clientDownloadHtml(url, websiteRule.getCharset());
				number++;
			} while (htmlSC == null && number < 3);

			if (htmlSC == null)
				continue;

			Document document = Jsoup.parse(htmlSC);
			if (i == 1) {
				String str = document.select("div.res_tips > div.b_text > p.res_cont").text();
				if (str.contains("没有找到关于"))
					continue;
				String numStr = document.select("div#content > div.ly_lt > div.b_pade > span.page_text").text();
				try {
					numStr = numStr.replaceAll("1/", "");
					num = Integer.parseInt(numStr);
				} catch (Exception e) {

				}
			}
			Elements elements = document.select("div#search_result > div.mod > a");
			for (Element element : elements) {
				String eleStr = element.text();
				String sonUrl = element.attr("href");
				if (eleStr.contains("药房发布")) {
					List<ProductDetailPrice> sonPdpList = parseYffb(sonUrl, websiteRule, productInfo);
					if (pdpList.size() > 0)
						pdpList.addAll(sonPdpList);
				} else if (eleStr.contains("商品详情")) {
					ProductDetailPrice pdp = parseXq(sonUrl, websiteRule, productInfo);
					if (pdp != null)
						pdpList.add(pdp);
				}
			}
		}

		return pdpList;
	}
}
