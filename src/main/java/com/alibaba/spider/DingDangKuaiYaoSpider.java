package com.alibaba.spider;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import com.alibaba.bean.ProductDetailPrice;
import com.alibaba.bean.ProductInfo;
import com.alibaba.bean.WebsiteRule;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.service.Encryption;
import com.alibaba.service.ShareVar;
import com.alibaba.spider.util.ErrorInfoUtil;

public class DingDangKuaiYaoSpider {

	public static String parseDDKY(String productName, int pageNum) {
		return Encryption.getSearchProductListJson(productName, pageNum);

	}

	public static List<ProductDetailPrice> parseDDKY(WebsiteRule websiteRule, ProductInfo productInfo) {
		List<ProductDetailPrice> productDetailPriceList = new ArrayList<ProductDetailPrice>();
		for (int i = 1; i > 0; i++) {
			String searchProductJson = parseDDKY(productInfo.getProduct_name(), i);
			if (searchProductJson == null)
				break;
			String title = searchProductJson.substring(1, 5);
			if (title.equals("html"))
				break;
			JSONObject searchProductJsonObj = null;
			try {
				searchProductJsonObj = JSONObject.parseObject(searchProductJson);
			} catch (Exception e) {
				continue;
			}
			JSONObject resultJsonObj = searchProductJsonObj.getJSONObject("result");
			JSONObject productMapJsonObj = resultJsonObj.getJSONObject("productMap").getJSONObject("o2oTab");
			JSONArray productJsonArr = productMapJsonObj.getJSONArray("productList");
			if (productJsonArr.size() == 0) {
				break;
			}
			for (Object productObj : productJsonArr) {
				JSONObject productJsonObj = JSONObject.parseObject(JSON.toJSONString(productObj));
				String skuId = productJsonObj.getString("skuId");

				String name = productJsonObj.getString("name");
				try {
					if (name.contains("[") && name.contains("]")) {
						String newName = name.substring(name.indexOf("[") + 1, name.indexOf("]"));
						name = name.replaceAll(newName, "").replaceAll("\\[", "").replaceAll("\\]", "");
					}

					if (name.contains("【") && name.contains("】")) {
						String newName = name.substring(name.indexOf("【") + 1, name.indexOf("】"));
						name = name.replaceAll(newName, "").replaceAll("\\【", "").replaceAll("\\】", "");
					}

					if (name.contains("(") && name.contains(")")) {
						String newName = name.substring(name.indexOf("(") + 1, name.indexOf(")"));
						name = name.replaceAll(newName, "").replaceAll("\\(", "").replaceAll("\\)", "");
					}
				} catch (Exception pse) {
					continue;
				}
				if (name.contains("+"))
					continue;
				BigDecimal price = productJsonObj.getBigDecimal("productPrice");
				String shopId = productJsonObj.getString("shopId");
				String suite = productJsonObj.getString("suite");
				String size = productJsonObj.getString("productSpecifications");
				String productJson = Encryption.getProductJson(skuId, shopId, suite);
				JSONObject productXqJsonObj = JSONObject.parseObject(productJson);
				JSONObject productDataJsonObj = productXqJsonObj.getJSONObject("data");
				JSONObject detailJsonObj = productDataJsonObj.getJSONObject("detail");
				String shopName = detailJsonObj.getString("shopName");
				String manufacturers = detailJsonObj.getString("manufacturers");
				if (name != null)
					name = name.trim();
				if (size != null)
					size = size.trim();
				if (manufacturers != null)
					manufacturers = manufacturers.trim();
				String productId = ShareVar.getOnlyId(name, size, manufacturers, productInfo);
				System.out.println(name + "\t" + size + "\t" + manufacturers + "\t" + productId);
				if (productId != null ) {
					ProductDetailPrice pdp = new ProductDetailPrice();
					pdp.setProduct_id(productId);
					pdp.setProduct_maxPrice(price);
					pdp.setProduct_minPrice(price);
					pdp.setShop_name(shopName);
					pdp.setUrl("https://m.ddky.com/groupDetail.html?skuId=" + skuId);
					pdp.setWebsite_name(websiteRule.getWebsite());
					pdp.setWebsite_type(websiteRule.getWebsite_type());
					pdp.setSpname(name);
					pdp.setGg(size);
					pdp.setScqy(manufacturers);
					productDetailPriceList.add(pdp);
				}
			}
		}

		return productDetailPriceList;
	}
}
