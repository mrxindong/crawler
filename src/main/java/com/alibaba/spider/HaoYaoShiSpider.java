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
import com.alibaba.service.ShareVar;

public class HaoYaoShiSpider {

	public static List<ProductDetailPrice> parseHYS(WebsiteRule websiteRule, ProductInfo productInfo) {
		List<ProductDetailPrice> productDetailPriceList = new ArrayList<ProductDetailPrice>();
		int pageNum = 1;
		for (int i = 1; i <= pageNum; i++) {
			String url = "http://www.ehaoyao.com/search/" + productInfo.getProduct_name();
			if (i != 1) {
				url = "http://www.ehaoyao.com/search/" + productInfo.getProduct_name() + "?type=1&page=" + i;
			}
			String htmlSC = ShareVar.opHttpClient.clientDownloadHtml(url, "utf-8");
			if (htmlSC == null)
				continue;
			Document document = Jsoup.parse(htmlSC);

			if (i == 1) {
				String pageNumText = document.select("div.Numberpage > div.digg > span > i.countpage").text();
				try {
					pageNum = Integer.parseInt(pageNumText);
				} catch (NumberFormatException nfe) {

				}
			}
			Elements productListEmements = document.select("div.products-list > div.list > ul > li");
			for (Element productListEmement : productListEmements) {
				String priceStr = productListEmement.select("div.price > p.now_price").text().replaceAll("￥", "");
				BigDecimal price = null;
				try {
					price = new BigDecimal(priceStr);
				} catch (Exception e) {

				}
				String size = productListEmement.select("div.des").text().replaceAll("规格：", "");
				String shopName = productListEmement.select("div.pharmName").text();
				String productUrl = "http://www.ehaoyao.com" + productListEmement.select("a").attr("href");
				String productHtmlSC = ShareVar.opHttpClient.clientDownloadHtml(productUrl, "utf-8");
				if (productHtmlSC == null)
					continue;
				Document productDocument = Jsoup.parse(productHtmlSC);
				if (price == null) {
					priceStr = productDocument.select("span.price.lFloat > em").text();
					try {
						price = new BigDecimal(priceStr);
					} catch (Exception e) {

					}
				}
				Elements infoElements = productDocument.select("div.proDetailInfo > span");
				String name = null;
				String scqy = null;
				for (Element infoElement : infoElements) {
					String[] infoStrArr = infoElement.text().split("：");
					if (infoStrArr.length == 2) {
						if (infoStrArr[0].equals("通用名称")) {
							name = infoStrArr[1];
							try {
								if (name.contains("(") && name.contains(")")) {
									String newName = name.substring(name.indexOf("(") + 1, name.indexOf(")"));
									name = name.replaceAll(newName, "");
									name = name.replaceAll("\\(", "").replaceAll("\\)", "");
									;
								}
							} catch (Exception e) {
								continue;
							}
						} else if (infoStrArr[0].equals("生产厂家")) {
							scqy = infoStrArr[1].trim();
						}
					}
				}
				String id = null;
				if (name != null && scqy != null && !(size.equals(""))) {
					id = ShareVar.getOnlyId(name, size, scqy, productInfo);
				}

				if (id != null) {
					ProductDetailPrice pdp = new ProductDetailPrice();
					pdp.setProduct_id(id);
					pdp.setProduct_maxPrice(price);
					pdp.setProduct_minPrice(price);
					pdp.setShop_name(shopName);
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
