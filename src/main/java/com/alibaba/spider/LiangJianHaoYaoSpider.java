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

public class LiangJianHaoYaoSpider {

	public static List<ProductDetailPrice> parseLJHY(WebsiteRule websiteRule, ProductInfo productInfo) {
		List<ProductDetailPrice> productDetailPriceList = new ArrayList<ProductDetailPrice>();

		int pageNum = 1;
		for (int i = 1; i <= pageNum; i++) {
			String url = "https://www.360lj.com/search-0.html?strsearch=" + productInfo.getProduct_name();
			if (i != 1) {
				url = "https://www.360lj.com/search-0-0-" + i + "-0-1-0-0-0-0-0.html?strsearch="
						+ productInfo.getProduct_name();
			}
			String htmlSC = ShareVar.opHttpClient.clientDownloadHtml(url, "utf-8");
			if (htmlSC == null)
				continue;
			Document document = Jsoup.parse(htmlSC);

			if (i == 1) {
				String pageNumText = document.select("div.pages > span > strong").text();
				try {
					pageNum = Integer.parseInt(pageNumText);
				} catch (NumberFormatException nfe) {

				}
			}
			Elements productListEmements = document.select("div.pro-area > ul.pro-con > li");
			for (Element productListEmement : productListEmements) {
				String priceStr = productListEmement.select("div.pro-botxt > span:nth-child(1) > i:nth-child(1)").text()
						.trim();
				BigDecimal price = null;
				try {
					price = new BigDecimal(priceStr);
				} catch (Exception e) {

				}

				String scqy = productListEmement.select("div.pro-botxt > p.factory").text().replaceAll("\\【", "")
						.replaceAll("\\】", "");
				String productUrl = "https://www.360lj.com" + productListEmement.select("div.imgbig > a").attr("href");
				String productHtmlSC = ShareVar.opHttpClient.clientDownloadHtml(productUrl, "utf-8");
				if (productHtmlSC == null)
					continue;
				Document productDocument = Jsoup.parse(productHtmlSC);
				if (price == null) {
					priceStr = productDocument.select("dd.jk_price > span").text().trim();
					try {
						price = new BigDecimal(priceStr);
					} catch (Exception e) {

					}
				}
				String name = productDocument.select("span.KeyWorld").text();
				String size = productDocument.select("dl.goods_specs > dd.on").text();
				if (size.contains("(") && size.contains(")")) {
					String newSize = size.substring(size.indexOf("(") + 1, size.indexOf(")"));
					size = size.replaceAll(newSize, "");
					size = size.replaceAll("\\(", "").replaceAll("\\)", "");
					;
				}
				if (size.contains("（") && size.contains("）")) {
					String newSize = size.substring(size.indexOf("（") + 1, size.indexOf("）"));
					size = size.replaceAll(newSize, "");
					size = size.replaceAll("\\（", "").replaceAll("\\）", "");
					;
				}
				String id = null;
				if (name != null && scqy != null && !(size.equals(""))) {
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
