package com.alibaba.spider;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.spider.util.ErrorInfoUtil;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.bean.ProductDetailPrice;
import com.alibaba.bean.ProductInfo;
import com.alibaba.bean.WebsiteRule;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.service.RedisUtil;
import com.alibaba.service.ShareVar;

public class JDSpider {

	public static BigDecimal getPrice(String skuId) {
		BigDecimal price = null;
		try {
			URIBuilder uriBuilder = new URIBuilder("https://p.3.cn/prices/mgets");
			uriBuilder.addParameter("skuId", skuId);
			CloseableHttpClient httpClient = null;
			try {
				httpClient = HttpClients.createDefault();
				HttpGet httpGet = new HttpGet(uriBuilder.build());

				httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
				httpGet.setConfig(ShareVar.opHttpClient.requestConfig);
				CloseableHttpResponse chResponse = httpClient.execute(httpGet);
				HttpEntity entity = chResponse.getEntity();
				String urlContent = EntityUtils.toString(entity, "gbk");
				JSONObject jsonObject = JSONObject.parseArray(urlContent).getJSONObject(0);
				price = jsonObject.getBigDecimal("p");
			} finally {
				if (httpClient != null)
					httpClient.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return price;
	}

	public static String getJDDownUrl(List<String> skuIdList, String key, int page, String url) {
		try {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			URIBuilder uriBuilder = new URIBuilder("https://search.jd.com/s_new.php");
			List<NameValuePair> paramList = new ArrayList<>();
			BasicNameValuePair param1 = new BasicNameValuePair("keyword", key);
			BasicNameValuePair param2 = new BasicNameValuePair("enc", "utf-8");
			BasicNameValuePair param3 = new BasicNameValuePair("qrst", "1");
			BasicNameValuePair param4 = new BasicNameValuePair("rt", "1");
			BasicNameValuePair param5 = new BasicNameValuePair("stop", "1");
			BasicNameValuePair param6 = new BasicNameValuePair("vt", "2");
			BasicNameValuePair param7 = new BasicNameValuePair("wq", key);
			BasicNameValuePair param8 = new BasicNameValuePair("page", "" + page);
			BasicNameValuePair param9 = new BasicNameValuePair("s", "31");
			BasicNameValuePair param10 = new BasicNameValuePair("scrolling", "y");
			BasicNameValuePair param11 = new BasicNameValuePair("tpl", "1_M");
			StringBuilder sbd = new StringBuilder();
			if (skuIdList.size() > 0) {
				int index = 0;
				for (String skuId : skuIdList) {
					sbd.append(skuId + (index != skuIdList.size() - 1 ? "," : ""));
					index++;
				}
			}
			BasicNameValuePair param12 = new BasicNameValuePair("show_items", sbd.toString());
			paramList.add(param1);
			paramList.add(param2);
			paramList.add(param3);
			paramList.add(param4);
			paramList.add(param5);
			paramList.add(param6);
			paramList.add(param7);
			paramList.add(param8);
			paramList.add(param9);
			paramList.add(param10);
			paramList.add(param11);
			paramList.add(param12);
			uriBuilder.addParameters(paramList);
			HttpGet httpGet = new HttpGet(uriBuilder.build());
			httpGet.setHeader(":authority", "search.jd.com");
			httpGet.setHeader(":method", "get");
			httpGet.setHeader(":scheme", "https");
			httpGet.setHeader("accept", "*/*");
			httpGet.setHeader("accept-encoding", "gzip, deflate, br");
			httpGet.setHeader("accept", "zh-CN,zh;q=0.8");
			httpGet.setHeader("accept-language", "zh-CN,zh;q=0.8");
			httpGet.setHeader("referer", url);
			httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
			httpGet.setHeader("x-requested-with", "XMLHttpRequest");
			httpGet.setConfig(ShareVar.opHttpClient.requestConfig);
			CloseableHttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			String urlContent = EntityUtils.toString(entity, "utf-8");
			return urlContent;
		} catch (Exception e) {
			return null;
		}
	}

	private static ProductDetailPrice parseJdXq(String url, BigDecimal price, String shop, WebsiteRule websiteRule,
			ProductInfo productInfo) {
		String htmlSC = ShareVar.opHttpClient.clientDownloadHtml(url, "utf-8");
		if (htmlSC == null || htmlSC.equals(""))
			return null;
		Document document = Jsoup.parse(htmlSC);
		String size = document.select("div#choose-attr-1 > div.dd > div.item.selected > a > i").text();
		Map<String, String> smsMap = new HashMap<String, String>();
		Map<String, String> infoMap = new HashMap<String, String>();
		Elements infoELements = document.select("ul.parameter2 > li");
		for (Element infoELement : infoELements) {
			String text = infoELement.text();
			String[] textArr = text.split("：");
			if (textArr.length == 2) {
				infoMap.put(textArr[0].replaceAll(" ", ""), textArr[1].replaceAll(" ", ""));
			}
		}

		Elements smsElements = document.select("div.Ptable-item > dl > dl");
		for (Element smsElement : smsElements) {
			String key = smsElement.select("dt").text();
			String value = smsElement.select("dd").text();
			if (key != null && !(key.equals("")) && value != null && !value.equals("")) {
				smsMap.put(key, value);
			}

		}
		if (size == null || size.equals("")) {
			size = smsMap.get("产品规格");
		}
		String scqy = smsMap.get("生产企业");
		String name = smsMap.get("药品通用名");
		String productId = ShareVar.getOnlyId(name, size, scqy, productInfo);
		if (productId == null)
			return null;

		ProductDetailPrice pdp = new ProductDetailPrice();
		pdp.setProduct_id(productId);
		pdp.setWebsite_name(websiteRule.getWebsite());
		pdp.setWebsite_type(websiteRule.getWebsite_type());
		pdp.setUrl(url);
		pdp.setProduct_minPrice(price);
		pdp.setProduct_maxPrice(price);
		pdp.setSpname(name);
		pdp.setGg(size);
		pdp.setScqy(scqy);

		if (shop == null || shop.equals("")) {
			shop = document.select("div.J-hove-wrap > div.item > div.name > a").text();
			if (shop == null || shop.equals("")) {
				shop = document.select("a.logo").text();
				pdp.setShop_name(shop);
			}
		}
		pdp.setShop_name(shop);
		return pdp;
	}

	public static List<ProductDetailPrice> parseJD(WebsiteRule websiteRule, ProductInfo productInfo) {
		List<ProductDetailPrice> productDetailPriceList = new ArrayList<ProductDetailPrice>();
//		RedisUtil.delDatabase("jdUrlSet");

		String keyEndoder = ShareVar.keyEncode(productInfo.getProduct_name(), "utf-8");
		int pageNum = 1;
		for (int i = 1; i <= pageNum; i++) {
			System.out.println(i);
			int page = 1;
			String htmlSC = null;
			List<String> skuIdList = new ArrayList<String>();
			do {
				String url = "https://search.jd.com/search?keyword=" + keyEndoder
						+ "&enc=utf-8&qrst=1&rt=1&stop=1&vt=2&wq=" + keyEndoder + "&page=" + i + "&s=61&click=0";
				if (page == 1) {
					htmlSC = ShareVar.opHttpClient.clientDownloadHtml(url, "utf-8");
				} else if (page != 1) {
					++i;
					htmlSC = getJDDownUrl(skuIdList, productInfo.getProduct_name(), i, url);
				}
				if (htmlSC != null) {
					Document document = Jsoup.parse(htmlSC);
					String numText = document.select("div#J_topPage > span.fp-text > i").text();
					try {
						pageNum = Integer.parseInt(numText);
					} catch (NumberFormatException nfe) {

					}
					Elements lbElements = null;
					if (page == 1) {
						lbElements = document.select("div#J_goodsList > ul.gl-warp > li");
					} else {
						lbElements = document.select("li");
					}
					if (lbElements != null && lbElements.size() > 0) {
						for (Element lbElement : lbElements) {
							skuIdList.add(lbElement.attr("data-sku"));
							String shop = lbElement.select("div.p-shop > span.J_im_icon > a").text();
							String xqUrl = lbElement.select("div.gl-i-wrap > div.p-name > a").attr("href");
							if (xqUrl != null && !(xqUrl.equals(""))) {
								if (!(xqUrl.substring(0, 2).equals("ht"))) {
									xqUrl = "https:" + xqUrl;
								}
								String priceStr = lbElement.select("div.p-price > strong > i").text();
								BigDecimal price = null;
								try {
									price = new BigDecimal(priceStr);
								} catch (Exception e) {

								}
//								if (!(RedisUtil.ifCz("jdUrlSet", xqUrl))) {
									ProductDetailPrice pdp = parseJdXq(xqUrl, price, shop, websiteRule, productInfo);
									if (pdp != null)
										productDetailPriceList.add(pdp);
//								}
							} else {
								Elements sonLbElements = lbElement
										.select("div.gl-i-tab-content > div.tab-content-item");
								for (Element sonLbElement : sonLbElements) {
									shop = sonLbElement.select("div.p-shop > span.J_im_icon > a").text();
									xqUrl = sonLbElement.select("div.p-name > a").attr("href");
									if (xqUrl != null && !(xqUrl.equals(""))) {
										if (!(xqUrl.substring(0, 2).equals("ht"))) {
											xqUrl = "https:" + xqUrl;
										}
										String priceStr = sonLbElement.select("div.p-price > strong > i").text();
										BigDecimal price = null;
										try {
											price = new BigDecimal(priceStr);
										} catch (Exception e) {

										}
										if (price == null) {
											String skuId = sonLbElement.select("div.p-price > strong").attr("class");
											price = getPrice(skuId);
										}
										if (price == null)
											continue;
//										if (!(RedisUtil.ifCz("jdUrlSet", xqUrl))) {
											ProductDetailPrice pdp = parseJdXq(xqUrl, price, shop, websiteRule,
													productInfo);
											if (pdp != null)
												productDetailPriceList.add(pdp);
//										}
									}
								}
							}
						}
					}
				}
				page++;
			} while (i != pageNum && page < 3);
		}

		return productDetailPriceList;
	}

}
