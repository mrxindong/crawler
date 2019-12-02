package com.alibaba.spider;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.spider.util.ErrorInfoUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.bean.ProductDetailPrice;
import com.alibaba.bean.ProductInfo;
import com.alibaba.bean.WebsiteRule;
import com.alibaba.service.ShareVar;

public class YiYaoWangSpider {

	public static Map<String, String> getSmsMap(Document document) {
		Map<String, String> smsMap = new HashMap<String, String>();
		Elements smsElements = document.select("div#prodDetailCotentDiv");

		smsElements = Jsoup.parseBodyFragment(smsElements.text()).select("table.specificationBox > tbody > tr");

		for (Element smsElement : smsElements) {
			String key = smsElement.select("th").text();
			String value = smsElement.select("td").text();
			if (key != null && !(key.replaceAll(" ", "").equals("")) && value != null
					&& !(value.replaceAll(" ", "").equals(""))) {
				smsMap.put(key.replaceAll(" ", "").replaceAll("　", "").replaceAll("：", "").replaceAll(":", ""),
						value.trim());
			}
		}

		if (smsMap == null || smsMap.size() == 0) {
			String html = document.select("#prodDetailCotentDiv").html();
			html = html.replaceAll("&lt;", "<").replaceAll("&gt;", ">")
					.replaceAll("<textarea style=\"display:none;\" data-original>", "").replaceAll("</textarea>", "");
			Document smsDoc = Jsoup.parseBodyFragment(html);
			smsElements = smsDoc.select("table > tbody > tr");

			for (Element smsElement : smsElements) {
				Elements elements = smsElement.select("td");
				if (elements.size() == 2) {
					String key = elements.get(0).text().replaceAll(" ", "").replaceAll("　", "").replaceAll("：", "")
							.replaceAll(":", "");
					if (key.equals("药品名称")) {
						String value = elements.get(1).text().trim();
						String[] str = value.split(" ");
						for (String s : str) {
							try {
								if (!s.replaceAll(" ", "").equals("") && s.contains("：")) {
									String[] nameStr = s.split("：");
									key = nameStr[0];
									value = str[0].split("：")[1];
									smsMap.put(key.replaceAll(" ", "").replaceAll("　", "").replaceAll("：", "")
											.replaceAll(":", ""), value);
								}
							} catch (Exception e) {
							}
						}
						try {
							value = str[0].split("：")[1];
						} catch (Exception e) {
							smsMap.put(elements.get(0).text().replaceAll(" ", "").replaceAll("　", "")
									.replaceAll("：", "").replaceAll(":", ""), elements.get(1).text().trim());
						}
					} else {
						smsMap.put(elements.get(0).text().replaceAll(" ", "").replaceAll("　", "").replaceAll("：", "")
								.replaceAll(":", ""), elements.get(1).text().trim());
					}
				} else {
					for (Element element : elements) {
						Elements select = element.select("td > span");
						if (select == null || select.size() == 0) {
							select = element.select("span");
							for (Element e : select) {
								String[] s = e.text().split("：");
								if (s.length == 2) {
									String key = s[0];
									String value = s[1];
									smsMap.put(key.replaceAll(" ", "").replaceAll("　", "").replaceAll("：", "")
											.replaceAll(":", ""), value.trim());
								}
							}
						}
						if (select.size() == 2) {
							String key = select.get(0).text();
							String value = select.get(1).text();
							smsMap.put(
									key.replaceAll(" ", "").replaceAll("　", "").replaceAll("：", "").replaceAll(":", ""),
									value.trim());
						}
					}
				}
			}

		}
		return smsMap;
	}

	public static ProductDetailPrice parseSon(String shop, String url, String price, WebsiteRule websiteRule,
			ProductInfo productInfo) {
		String htmlSC = ShareVar.opHttpClient.clientDownloadHtml(url, "gbk");
		Document document = Jsoup.parse(htmlSC);
		Map<String, String> infoMap = getSmsMap(document);
		Elements infoElements = document.select("div.goods_intro > table > tbody > tr");
		for (Element infoElement : infoElements) {
			Elements keys = infoElement.select("th");
			Elements values = infoElement.select("td");
			if (keys.size() == values.size()) {
				for (int i = 0; i < keys.size(); i++) {
					infoMap.put(keys.get(i).text().replaceAll(" ", "").replaceAll("　", "").replaceAll("：", "")
							.replaceAll(":", ""), values.get(i).text().trim());
				}
			}
		}

		String scqy = infoMap.get("生产厂商");
		String size = infoMap.get("规格");

		Map<String, String> smsMap = getSmsMap(document);
		String name = smsMap.get("通用名称");
		if (scqy == null || scqy.replaceAll(" ", "").equals("")) {
			scqy = smsMap.get("企业名称");
			if (scqy == null)
				scqy = smsMap.get("生产企业");
		}

		if (size == null || size.replaceAll(" ", "").equals("")) {
			size = smsMap.get("规格");
		}

		name = (name != null) ? name.trim() : null;
		scqy = (scqy != null) ? scqy.trim() : null;
		size = (size != null) ? size.trim() : null;

		String productId = ShareVar.getOnlyId(name, size, scqy, productInfo);
		if ((productId == null || price == null))
			return null;
		ProductDetailPrice pdp = new ProductDetailPrice();
		pdp.setProduct_id(productId);
		pdp.setUrl(url);
		pdp.setShop_name(shop);
		pdp.setWebsite_name(websiteRule.getWebsite());
		pdp.setWebsite_type(websiteRule.getWebsite_type());
		pdp.setProduct_minPrice(new BigDecimal(price));
		pdp.setProduct_maxPrice(new BigDecimal(price));
		pdp.setSpname(name);
		pdp.setGg(size);
		pdp.setScqy(scqy);
		return pdp;
	}

	public static List<ProductDetailPrice> parseYiYaoWang(WebsiteRule websiteRule, ProductInfo productInfo) {
		List<ProductDetailPrice> productDetailPriceList = new ArrayList<ProductDetailPrice>();

		int pageNum = 1;
		String tym = ShareVar.keyEncode(productInfo.getProduct_name(), "gbk");
		for (int nowPage = 1; nowPage <= pageNum; nowPage++) {
			String htmlSC = null;
			if (nowPage == 1) {
				htmlSC = ShareVar.opHttpClient
						.clientDownloadHtml("https://www.111.com.cn/search/search.action?keyWord=" + tym, "utf-8");
			} else {
				htmlSC = ShareVar.opHttpClient.clientDownloadHtml(
						"https://www.111.com.cn/search/search.action?keyWord=" + tym + "&gotoPage=" + nowPage, "utf-8");
			}
			if (htmlSC == null)
				continue;
			Document document = Jsoup.parse(htmlSC);
			if (nowPage == 1) {
				Elements numElements = document.select("div#rankOpDiv > ul.page > li.pageNum");
				if (numElements.size() != 0) {
					String numText = numElements.text().replaceAll(" ", "");
					numText = numText.replace("1/", "");
				}
			}
			Elements lbElements = document.select("ul#itemSearchList > li");
			for (Element lbElement : lbElements) {
				String shop = lbElement.select("span.goldseller_name").text();
				Elements tcElements = lbElement.select("li.taocan > div");
				if (tcElements == null || tcElements.size() == 0) {
					String xqUrl = "https:" + lbElement.select("p.titleBox > a.productName").attr("href");
					String price = lbElement.select("p.price > span").text();

					if (price == null || price.replaceAll(" ", "").equals("")) {
						Elements select = lbElement.select("p.price");
						Document pDoc = Jsoup.parseBodyFragment(select.text());
						price = pDoc.select("span").text();
					}
					if (price == null || price.replaceAll(" ", "").equals("")) {
						System.out.println();
					}
					ProductDetailPrice pdp = parseSon(shop, xqUrl, price, websiteRule, productInfo);
					if (pdp != null)
						productDetailPriceList.add(pdp);
				} else {
					for (Element tcElement : tcElements) {
						String xqUrl = "https:" + tcElement.select("a").attr("href");
						String price = tcElement.select("p.price > span").text().split(" ")[0];
						shop = shop.split(" ")[0];
						if (price == null || price.replaceAll(" ", "").equals("")) {
							Elements select = tcElement.select("p.price");
							Document pDoc = Jsoup.parseBodyFragment(select.text());
							price = pDoc.select("span").text();
						}
						ProductDetailPrice pdp = parseSon(shop, xqUrl, price, websiteRule, productInfo);
						if (pdp != null)
							productDetailPriceList.add(pdp);
					}
				}
			}
		}

		return productDetailPriceList;
	}

}
