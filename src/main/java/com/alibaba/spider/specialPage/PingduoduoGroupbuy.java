package com.alibaba.spider.specialPage;

import com.alibaba.bean.ProductDetailPrice;
import com.alibaba.bean.ProductInfo;
import com.alibaba.bean.WebsiteRule;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.service.ShareVar;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PingduoduoGroupbuy {

    public static List<ProductDetailPrice> parseSonHref(String href, WebsiteRule websiteRule, ProductInfo productInfo) {
        List<ProductDetailPrice> productDetailPrices = new ArrayList<>();

        String htmlSC = ShareVar.opHttpClient.clientDownloadHtml(href, "utf-8");
        Document document = Jsoup.parse(htmlSC);
            String rawData = document.getElementsByTag("script").get(8).data();
            rawData = rawData.substring(rawData.indexOf("{"), rawData.lastIndexOf("}") + 1);
            JSONObject jsonObject = JSONObject.parseObject(rawData);
            JSONObject initDataObj = jsonObject.getJSONObject("store").getJSONObject("initDataObj");
            String price = initDataObj.getJSONObject("goods").getString("minGroupPrice");
            String store = initDataObj.getJSONObject("mall").getString("mallName");
            String name = "";
            String size = "";
            String scqy = "";
            JSONArray array = initDataObj.getJSONObject("goods").getJSONArray("goodsProperty");

            for (int i = 0; i < array.size(); i++) {
                JSONObject object = (JSONObject) array.get(i);
                if ("药品通用名".equals(object.getString("key"))) {
                    name = object.getString("values");
                    name = filter(name);
                }
                if ("使用剂量".equals(object.getString("key"))) {
                    size = object.getString("values");
                    size = filter(size);
                }
                if ("生产企业".equals(object.getString("key"))) {
                    scqy = object.getString("values");
                    scqy = filter(scqy);
                }
            }

            String productId = ShareVar.getOnlyId(name, size, scqy, productInfo);
            ProductDetailPrice pdp = new ProductDetailPrice();
            pdp.setProduct_id(productId);
//        pdp.setWebsite_name(websiteRule.getWebsite());
//        pdp.setWebsite_type(websiteRule.getWebsite_type());
            pdp.setUrl(href);
            pdp.setProduct_maxPrice(new BigDecimal(price));
            pdp.setProduct_minPrice(new BigDecimal(price));
            pdp.setShop_name(store);
            productDetailPrices.add(pdp);
        return productDetailPrices;
    }

    private static String filter(String value) {
        return value.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "");
    }

//    public static void main(String[] args) {
//        List<ProductDetailPrice> productDetailPrices = new ArrayList<>();
//        String[] urls = {"https://mobile.yangkeduo.com/goods1.html?_wvx=10&refer_share_uid=4400263409&share_uin=HIHP5EP3332ZNMSQWTI3ALZXHU_GEXDA&page_from=23&_wv=41729&refer_share_channel=copy_link&refer_share_id=nWZMwNUIK9TaKzZNndEgGfZMYZFJPIth&share_uid=4400263409&goods_id=41352613192",
//                "https://mobile.yangkeduo.com/goods.html?_wvx=10&refer_share_uid=4400263409&share_uin=HIHP5EP3332ZNMSQWTI3ALZXHU_GEXDA&page_from=23&_wv=41729&refer_share_channel=copy_link&refer_share_id=0T6yA6mO9CJKZH11lM3Z70oR3VsL3MLq&share_uid=4400263409&goods_id=22912598730",
//                "https://mobile.yangkeduo.com/goods.html?_wvx=10&refer_share_uid=4400263409&share_uin=HIHP5EP3332ZNMSQWTI3ALZXHU_GEXDA&page_from=23&_wv=41729&refer_share_channel=copy_link&refer_share_id=ZJ2M8bADSAe8TgLmw5kGwFuvgyoyCc8K&share_uid=4400263409&goods_id=21119744710#pushState",
//                "https://mobile.yangkeduo.com/goods1.html?_wvx=10&refer_share_uid=4400263409&share_uin=HIHP5EP3332ZNMSQWTI3ALZXHU_GEXDA&page_from=23&_wv=41729&refer_share_channel=copy_link&refer_share_id=gfaFyfFPO2IJtj3h2JDbHRhu9R8QX02A&share_uid=4400263409&goods_id=22912598730",
//                "https://mobile.yangkeduo.com/goods1.html?_wvx=10&refer_share_uid=4400263409&share_uin=HIHP5EP3332ZNMSQWTI3ALZXHU_GEXDA&page_from=23&_wv=41729&refer_share_channel=copy_link&refer_share_id=zoWN9rQAnM8u5GhkxD7rLAnRBtW2bgDs&share_uid=4400263409&goods_id=9183008446",
//                "https://mobile.yangkeduo.com/goods.html?_wvx=10&refer_share_uid=4400263409&share_uin=HIHP5EP3332ZNMSQWTI3ALZXHU_GEXDA&page_from=23&_wv=41729&refer_share_channel=copy_link&refer_share_id=dwPi9bds1pBrs17xTVshqouTYU3LnHAT&share_uid=4400263409&goods_id=20150955299"};
//        for (int i = 0; i < urls.length; i++) {
//            productDetailPrices.addAll(parseSonHref(urls[i], null, null));
//        }
//        System.out.println(productDetailPrices.size());
//    }
}
