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

import com.alibaba.bean.ProductDetailPrice;
import com.alibaba.bean.ProductInfo;
import com.alibaba.bean.WebsiteRule;
import com.alibaba.service.ShareVar;

public class YaoFangWangSpider {

	public static List<ProductDetailPrice> parseSonHref(String href, WebsiteRule websiteRule, ProductInfo productInfo) {
		List<ProductDetailPrice> pdPriceList = new ArrayList<>();
		String productId = null;
		String name = null;
		String scqy = null;
		String size = null;
		int number = 1;
		for (int i = 1; i <= number; i++) {
			String sunUrl = href.replaceAll(".html", "-p" + i + ".html");
			String htmlSC = ShareVar.opHttpClient.clientDownloadHtml(sunUrl, "utf-8");
			if (htmlSC == null)
				continue;
			Document document = Jsoup.parse(htmlSC);
			if (i == 1) {
				String pageNumStr = document.select("div.page > span.num").text().replaceAll(" ", "").replaceAll("1/",
						"");
				try {
					number = Integer.parseInt(pageNumStr);
				} catch (Exception e) {

				}
				String nameText = document.select("dl.clearfix > dd.w2.l > strong").text();
				name = nameText != null ? nameText.trim() : null;

				// 规格
				try {
					size = document.select("div#standardOther > div.now").text().replaceAll(" ", "");
				} catch (Exception e) {
					size = document.select("div#standardOther > div.now").text().replaceAll(" ", "");
				}
				if (size == null || size.replaceAll(" ", "").equals("")) {
					size = document.select(
							"#wrap > div.maininfo.clearfix > div.right > div > dl:nth-child(1) > dd:nth-child(6)")
							.text().replaceAll(" ", "");
				}

				String scqyText = document.select("dl.clearfix > dd.w1.l").text().replaceAll(" ", "");
				scqy = scqyText != null ? scqyText.trim() : null;
				productId = ShareVar.getOnlyId(name, size, scqy, productInfo);
				if (productId == null || ErrorInfoUtil.errInfo(name,size,scqy))
					return pdPriceList;
			}

			Elements elements = document.select("div#slist > ul.slist li");
			if (elements == null || elements.size() == 0) {
				break;
			} else {
				for (Element element : elements) {
					String productUrl = "https://www.yaofangwang.com"
							+ (element.select("div.info  > h3 > a").attr("href"));
					ProductDetailPrice pdp = new ProductDetailPrice();
					pdp.setProduct_id(productId);
					pdp.setWebsite_name(websiteRule.getWebsite());
					pdp.setWebsite_type(websiteRule.getWebsite_type());
					pdp.setUrl(productUrl);
					pdp.setSpname(name);
					pdp.setGg(size);
					pdp.setScqy(scqy);
					BigDecimal price = null;
					try {
						String priceStr = element.select("div.sale > p.money").text().replaceAll("¥", "")
								.replaceAll(" ", "");
						price = new BigDecimal(priceStr);
					} catch (Exception e) {
						String[] textArr = element.select("div.sale > p.money").text().split("返现");
						String priceStr = textArr[0].replaceAll("¥", "").replaceAll(" ", "");
						price = new BigDecimal(priceStr);
					}
					if (price == null )
						continue;
					pdp.setProduct_maxPrice(price);
					pdp.setProduct_minPrice(price);
					String shopText = element.select("div.shop > p.clearfix").text();
					shopText = shopText != null ? shopText.trim() : null;
					pdp.setShop_name(shopText);
					pdPriceList.add(pdp);
				}
			}
		}
		return pdPriceList;
	}

	public static List<ProductDetailPrice> parseYaoFangWang(WebsiteRule websiteRule, ProductInfo productInfo)
			throws IOException {

		List<ProductDetailPrice> pDataList = new ArrayList<>();
		int number = 1;
		for (int i = 1; i <= number; i++) {
			String url = "https://www.yaofangwang.com/search.html?keyword=" + productInfo.getProduct_name() + "%20"
					+ productInfo.getScqy_name() + "&price=0";
			url = (i == 1) ? url : url + "&page=" + i;
			String htmlSC = ShareVar.opHttpClient.clientDownloadHtml(url, "utf-8");
			// if(htmlSC == null || htmlSC.equals(""))
			Document document = Jsoup.parse(htmlSC);
			if (i == 1) {
				try {
					String num = document.select("div.page > span.num").text().replaceAll("1 / ", "");
					number = Integer.parseInt(num);
				} catch (NumberFormatException nfe) {

				}
			}

			Elements sonElements = document.select("ul.goodlist_search > li > div > div > a");
			for (Element sonElement : sonElements) {
				String href = "https:" + sonElement.attr("href");
				List<ProductDetailPrice> pdPriceList = parseSonHref(href, websiteRule, productInfo);
				if (pdPriceList.size() > 0)
					pDataList.addAll(pdPriceList);
			}
		}

		return pDataList;
	}

}
