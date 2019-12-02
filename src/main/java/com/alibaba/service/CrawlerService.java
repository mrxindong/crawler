package com.alibaba.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.spider.PingduoduoSpider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.bean.ProductDetailPrice;
import com.alibaba.bean.ProductInfo;
import com.alibaba.bean.WebsiteRule;
import com.alibaba.bean.Result;
import com.alibaba.mapper.CrawlerMapper;

@Service
@Transactional(rollbackFor = RuntimeException.class)
public class CrawlerService {

	@Autowired
	public CrawlerMapper crawlerMapper;

	@Autowired
	OperationMysql om;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Result spiderAll() {
		Result result = new Result();
		result.setSuccess(false);
		result.setDetail(null);
		try {
			List<WebsiteRule> drugRuleList = crawlerMapper.spiderAll();
			if (drugRuleList == null || drugRuleList.size() == 0) {
				result.setMsg("网站为空");
			} else {
				List<ProductInfo> productInfoList = getDrugInfos();
				System.out.println("拿到数据"+PrintUtil.toString(productInfoList));
				int size = 0;
				for (ProductInfo productInfo : productInfoList) {
					for (WebsiteRule drugRule : drugRuleList) {
						System.out.println(new Date()+"执行"+PrintUtil.toString(drugRule)+" 商品："+PrintUtil.toString(productInfo));
						List<ProductDetailPrice> drugDataList = ShareVar.hpt.getDrugUrlList(drugRule, productInfo);
						if (drugDataList == null || drugDataList.size() == 0) {

						} else {
//							List<ProductDetailPrice> errList = drugDataList.stream()
//									.filter(d -> d.getProduct_id() == null && !isExits(productInfoList, d))  //id为空并且不在后续规格中
//									.collect(Collectors.toList());
//							List<ProductDetailPrice> rightList =  drugDataList.stream().filter(d -> d.getProduct_id() != null).collect(Collectors.toList());

							size += drugDataList.size();

							om.insertProductDetailPrice(drugDataList);
							om.insertProductUrl(drugDataList);

//							if(errList != null && errList.size() > 0){
//								errList.forEach(e -> e.setProduct_id(productInfo.getProduct_id()));
//								om.insertProductDetailPriceErr(errList);
//							}

//							for (ProductDetailPrice pdp : drugDataList) {
//								om.insertProductDetailPrice(pdp);
//								// crawlerMapper.insertDrugData(pdp);
//								//insertDrugData(pdp);
//								//crawlerMapper.insertProductUrl(pdp.getProduct_id(), pdp.getWebsite_name(), pdp.getWebsite_type(), pdp.getUrl());
//								om.insertProductUrl(pdp.getProduct_id(), pdp.getWebsite_name(), pdp.getWebsite_type(), pdp.getUrl());
//							}
						}
					}
					om.updateSpiderStatus(productInfo.getProduct_id());
//					crawlerMapper.updateSpiderStatus(productInfo.getProduct_id());
				}
				PingduoduoSpider.close();
				System.out.println("爬取完毕");
				result.setSuccess(true);
				result.setMsg("爬取成功");
				result.setDetail("共爬取数据:" + size + "条");
			}
		} catch (Exception e) {
			result.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	private boolean isExits(List<ProductInfo> productInfoList, ProductDetailPrice productDetailPrice){
		for (ProductInfo productInfo : productInfoList){
			if(productInfo.getProduct_name().equals(productDetailPrice.getSpname())
					&& productInfo.getScqy_name().equals(productDetailPrice.getScqy())
					&& productInfo.getGg().equals(productDetailPrice.getGg())){
				return true;
			}
		}
		return false;
	}
	public void insertDrugData(ProductDetailPrice pdp) {
		crawlerMapper.insertDrugData(pdp);
	}
	@SuppressWarnings("unchecked")
	public Result productWebsiteSpider(String website, String product) {
		Result result = new Result();
		result.setSuccess(false);
		result.setDetail(null);
		try {
			if (!(websiteExist(website))) {
				result.setMsg("没有该网站！！");
			} else {
				if (!productExist(product)) {
					result.setMsg("没有该商品！！");
				} else {
					WebsiteRule drugRule = crawlerMapper.queryWebSiteRule(website);
					List<ProductInfo> productDrugInfos = getProductDrugInfos(product);
					int size = 0;
					for (ProductInfo productInfo : productDrugInfos) {
						List<ProductDetailPrice> drugDataList = ShareVar.hpt.getDrugUrlList(drugRule, productInfo);
						if (drugDataList == null || drugDataList.size() == 0) {

						} else {
							size+=drugDataList.size();
							for (ProductDetailPrice drugData : drugDataList) {
								crawlerMapper.insertDrugData(drugData);
							}
							result.setSuccess(true);
						}						
					}
					result.setSuccess(true);
					result.setMsg("爬取成功");
					result.setDetail("共爬取数据：" + size + "条");
				}
			}
		} catch (Exception e) {
			result.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Result assignWebsite(String website) {
		Result result = new Result();
		result.setSuccess(false);
		result.setDetail(null);
		try {
			if (!(websiteExist(website))) {
				result.setMsg("没有该网站！！");
			} else {
				WebsiteRule drugRule = crawlerMapper.queryWebSiteRule(website);
				List<ProductInfo> productInfoList = getDrugInfos();
				// HtmlParseTool hpt = new HtmlParseTool();
				int size = 0;
				for (ProductInfo productInfo : productInfoList) {
					List<ProductDetailPrice> drugDataList = ShareVar.hpt.getDrugUrlList(drugRule,productInfo);
					if (drugDataList == null || drugDataList.size() == 0) {

					} else {
						size+=drugDataList.size();
						for (ProductDetailPrice productDetailPrice : drugDataList) {
							crawlerMapper.insertDrugData(productDetailPrice);
						}
						result.setSuccess(true);
					}					
				}
				result.setSuccess(true);
				result.setMsg("爬取成功");
				result.setDetail("共爬取数据：" + size + "条");
			}
		} catch (Exception e) {
			result.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}


	public List<ProductInfo> getDrugInfos() {
		List<ProductInfo> drugInfoList = crawlerMapper.getDrugInfos();
//		for (DrugInfo drugInfo : drugInfoList) {
//			List<String> drugSizeList = userMapper.getDrugSize(drugInfo.getDrug_name(), drugInfo.getDrug_brand());
//			drugInfo.setDrugSizeList(drugSizeList);
//		}
		return drugInfoList;
	}

	public List<ProductInfo> getProductDrugInfos(String product) {
		List<ProductInfo> drugInfoList = crawlerMapper.getProductDrugInfos(product);
		return drugInfoList;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Result queryWebSiteRule(String website) {
		Result r = new Result();
		r.setSuccess(false);
		r.setDetail(null);
		if (websiteExist(website)) {
			WebsiteRule websiteRule = crawlerMapper.queryWebSiteRule(website);
			r.setDetail(websiteRule);
			r.setSuccess(true);
			r.setMsg("查询成功");
		} else {
			r.setMsg("没有该网站！！");
		}
		return r;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Result queryAllWebsitRule() {
		Result r = new Result();
		r.setSuccess(false);
		r.setDetail(null);
		List<WebsiteRule> drugRule = crawlerMapper.queryAllWebsiteRule();
		r.setDetail(drugRule);
		return r;
	}

	public boolean websiteExist(String website) {
		List<String> websiteList = crawlerMapper.getAllWebsite();
		return websiteList.contains(website);
	}

	public boolean productExist(String product) {
		List<String> productList = crawlerMapper.getAllProduct();
		return productList.contains(product);
	}
}
