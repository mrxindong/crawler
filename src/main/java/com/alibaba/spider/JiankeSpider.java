package com.alibaba.spider;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.bean.ProductDetailPrice;
import com.alibaba.bean.ProductInfo;
import com.alibaba.bean.WebsiteRule;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.service.ShareVar;

public class JiankeSpider {
	public static List<ProductDetailPrice> parseJianKe(WebsiteRule drugRule, ProductInfo productInfo) {
		List<ProductDetailPrice> productDetailPriceList = new ArrayList<ProductDetailPrice>();

		int number = 1;
		for (int i = 1; i <= number; i++) {

		}

		return productDetailPriceList;
	}
}
