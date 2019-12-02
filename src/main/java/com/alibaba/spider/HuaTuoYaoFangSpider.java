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

public class HuaTuoYaoFangSpider {

	public static List<ProductDetailPrice> parseHTYF(WebsiteRule websiteRule, ProductInfo productInfo) {
		List<ProductDetailPrice> productDetailPriceList = new ArrayList<ProductDetailPrice>();

		int pageNum = 1;
		for (int i = 1; i <= pageNum; i++) {
			String url = "http://www.huatuoyf.com/search?keyword=" + productInfo.getProduct_name();
			if (i != 1) {
				url = "http://www.huatuoyf.com/search?keyword=" + productInfo.getProduct_name() + "&page=" + i;
			}
			String htmlSC = ShareVar.opHttpClient.clientDownloadHtml(url, "utf-8");
			if (htmlSC == null)
				continue;
			Document document = Jsoup.parse(htmlSC);

			if (i == 1) {
				String pageNumText = document.select("div.rig-sear > b.old").text().replaceAll("/", "").trim();
				try {
					pageNum = Integer.parseInt(pageNumText);
				} catch (NumberFormatException nfe) {

				}
			}
			Elements productListEmements = document.select("ul.list-index > li");
			for (Element productListEmement : productListEmements) {
				String priceStr = productListEmement.select("a.dollor").text().trim().replaceAll("￥", "");
				BigDecimal price = null;
				try {
					price = new BigDecimal(priceStr);
				} catch (Exception e) {

				}
				String productUrl = "http://www.huatuoyf.com" + productListEmement.select("a.name").attr("href");
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
				Elements infoElements = productDocument.select("ul.list-detail > li");
				String name = null;
				String scqy = null;
				String size = productDocument.select("li#diffBox > a").text();
				for (Element infoElement : infoElements) {
					String[] infoStrArr = infoElement.text().split("：");
					if (infoStrArr.length == 2) {
						if (infoStrArr[0].equals("通用名称")) {
							name = infoElement.select("span.con").text();
							try {
								if (name.contains("(") && name.contains(")")) {
									String newName = name.substring(name.indexOf("(") + 1, name.indexOf(")"));
									name = name.replaceAll(newName, "");
									name = name.replaceAll("\\(", "").replaceAll("\\)", "");
								}
							} catch (Exception e) {
								continue;
							}
						} else if (infoStrArr[0].equals("生产企业")) {
							scqy = infoElement.select("span.con").text();
						}
					}
				}
				String id = null;
				if (name != null && scqy != null && !(size.equals("")) && price != null) {
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
