package com.alibaba.service;

import java.io.IOException;
import java.util.List;

import com.alibaba.bean.ProductDetailPrice;
import com.alibaba.bean.ProductInfo;
import com.alibaba.bean.WebsiteRule;
import com.alibaba.spider.*;


public class HtmlParseTool {

	public List<ProductDetailPrice> getDrugUrlList(WebsiteRule websiteRule,ProductInfo productInfo) throws IOException {
		List<ProductDetailPrice> productDetailPriceList = null;
		try{

		switch (websiteRule.getId()) {
		case 1:
			productDetailPriceList = BaBaiFangSpider.parseBaBaiFang(websiteRule,productInfo);
			break;
		case 2:
			productDetailPriceList = BaiOuSpider.parseBaiOu(websiteRule,productInfo);
			break;
		case 3:
			productDetailPriceList = JDSpider.parseJD(websiteRule,productInfo);
			break;
		case 4:
//			productDetailPriceList = JiankeSpider.parseJianKe(websiteRule,productInfo);
			break;
		case 5:
			productDetailPriceList = KangAiDuoSpider.parseKangAiDuo(websiteRule,productInfo);
			break;
		case 6:
			productDetailPriceList = YaoFangWangSpider.parseYaoFangWang(websiteRule,productInfo);
			break;
		case 7:
			// drugDataList = TianMaoSpider.parseTianMao();
			break;
		case 8:
			productDetailPriceList = YiYaoWangSpider.parseYiYaoWang(websiteRule,productInfo);
			break;
		case 9:
			productDetailPriceList = MiaoShouYishengSpider.parseMSYS(websiteRule,productInfo);
			break;
		case 10:
			productDetailPriceList = DingDangKuaiYaoSpider.parseDDKY(websiteRule,productInfo);
			break;
		case 11:
			productDetailPriceList = HaoYaoShiSpider.parseHYS(websiteRule,productInfo);
			break;
		case 12:
			productDetailPriceList = LiangJianHaoYaoSpider.parseLJHY(websiteRule,productInfo);
			break;
		case 13:
			productDetailPriceList = QuanYuanTangSpider.parseQYT(websiteRule,productInfo);
			break;
		case 14:
			productDetailPriceList = HuaTuoYaoFangSpider.parseHTYF(websiteRule,productInfo);
			break;
		case 15:
			productDetailPriceList = AKangDaYaoFangSpider.parseAKDYF(websiteRule,productInfo);
			break;
//		case 16:
//			productDetailPriceList = PingduoduoSpider.run(websiteRule, productInfo);
//			break;
		}
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return productDetailPriceList;
	}

}