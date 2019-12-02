package com.alibaba.spider;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.spider.util.ErrorInfoUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
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
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.service.ShareVar;

public class BaiOuSpider {

	public static String parseBaiOu(String tym, int page) {

		try {
			CloseableHttpClient httpClient = null;
			try {
				httpClient = HttpClients.createDefault();
				HttpPost httpPost = new HttpPost("http://www.bioey.com/site/json/getProductInfor.aspx");
				httpPost.setHeader("Accept", "application/json, text/javascript, */*; q=0.01");
				httpPost.setHeader("Accept-Encoding", "gzip, deflate");
				httpPost.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
				httpPost.setHeader("Connection", "keep-alive");
				httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
				httpPost.setHeader("Cache-Control", "no-cache");
				httpPost.setHeader("Cookie",
						"UM_distinctid=16ccc5913ff568-0346f6fc23d122-5d4e211f-1fa400-16ccc591401926; the_address=%E5%8C%97%E4%BA%AC; the_address_id=28; ASP.NET_SessionId=ixajhh45hviiqfetns4kvw55; medicine_Looked=201560; CNZZDATA1259199248=451462536-1566792570-http%253A%252F%252Fwww.bioey.com%252F%7C1569820089; search_view=view1");
				httpPost.setHeader("Host", "www.bioey.com");
				httpPost.setHeader("Origin", "http://www.bioey.com");
				String jmKey = ShareVar.keyEncode(tym, "utf-8");
				httpPost.setHeader("Referer", "http://www.bioey.com/site/search.aspx?keyword=" + jmKey);
				httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
				httpPost.setHeader("X-Requested-With", "XMLHttpRequest");
				StringEntity se = new StringEntity("address=28&isNormal=&dishes_goodsMark=&jx=&pageIndex=" + page
						+ "&pageSize=100&key=" + ShareVar.keyEncode(jmKey, "utf-8") + "&strWh=&type=&isValue=");
				httpPost.setEntity(se);
				CloseableHttpResponse chResponse = httpClient.execute(httpPost);
				HttpEntity entity = chResponse.getEntity();
				String urlContent = EntityUtils.toString(entity, "utf-8");
				return urlContent;
			} finally {
				if (httpClient != null)
					httpClient.close();
			}
		} catch (Exception e) {
			e.getStackTrace();
			return null;
		}

	}

	public static List<ProductDetailPrice> parseBaiOu(WebsiteRule websiteRule, ProductInfo productInfo) {

		List<ProductDetailPrice> productDetailPriceList = new ArrayList<ProductDetailPrice>();
		int pageNum = 1;
		for (int i = 1; i <= pageNum; i++) {
			String json = parseBaiOu(productInfo.getProduct_name(), i);
			if (json == null) {
				continue;
			}
			JSONObject jsonObject = JSONObject.parseObject(json);
			if (i == 1) {
				int count = jsonObject.getIntValue("count");
				if (count == 0)
					break;
				pageNum = jsonObject.getIntValue("pnCount");
			}
			JSONArray resJsonArr = jsonObject.getJSONArray("res");
			if (resJsonArr == null || resJsonArr.size() == 0) {
				break;
			}

			for (Object resObj : resJsonArr) {
				JSONObject resJsonObj = JSONObject.parseObject(JSON.toJSONString(resObj));

				String dishes_id = resJsonObj.getString("dishes_id");
				String xqUrl = "http://www.bioey.com/site/showProduct.aspx?pid=" + dishes_id;

				String size = resJsonObj.getString("dishes_ingredients");
				BigDecimal price = resJsonObj.getBigDecimal("dishes_price");
				if (price == null)
					return null;
				String scqy = resJsonObj.getString("dishes_made_in");

				String htmlSC = ShareVar.opHttpClient.clientDownloadHtml(xqUrl, "utf-8");
				Document document = Jsoup.parse(htmlSC);
				Elements smsElements = document.select("ul.jbxxul > li");
				if (smsElements.size() == 0) {
					smsElements = document.select("ul.tab_cpsx > li");
				}
				Map<String, String> smsMap = new HashMap<>();
				for (Element smsElement : smsElements) {
					for (int j = 0; j < 2; j++) {
						String key = smsElement.select("b").get(j).text();
						String value = smsElement.select("span").get(j).text();
						smsMap.put(key, value);
					}
				} // 通用名称
				String name = smsMap.get("通用名称");
				if (name != null && !(name.replaceAll(" ", "").equals(""))) {
					if (name.contains("(") || name.contains("（") || name.contains(")") || name.contains("）")) {
						try {
							name = name.replaceAll("\\(", "(").replaceAll("（", "(").replaceAll("\\)", ")")
									.replaceAll("）", ")");
							int index = name.indexOf("(");
							int lastIndex = name.indexOf(")");
							String key1 = name.substring(index, lastIndex);
							name = name.replaceAll("\\" + key1 + "\\)", "");
						} catch (Exception e) {

						}
					}
				}
				String productId = ShareVar.getOnlyId(name, size, scqy, productInfo);
				if (productId != null) {
					ProductDetailPrice pdp = new ProductDetailPrice();
					pdp.setProduct_id(productId);
					pdp.setProduct_maxPrice(price);
					pdp.setProduct_minPrice(price);
					pdp.setUrl(xqUrl);
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
