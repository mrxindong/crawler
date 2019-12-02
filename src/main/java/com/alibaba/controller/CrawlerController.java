package com.alibaba.controller;

import com.alibaba.bean.WebsiteRule;
import com.alibaba.bean.Result;
import com.alibaba.mapper.CrawlerMapper;
import com.alibaba.service.CrawlerService;

import com.alibaba.service.PrintUtil;
import org.apache.http.client.UserTokenHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/crawler")
public class CrawlerController {

	@Autowired
	private CrawlerService userService;

	@Autowired
	public CrawlerMapper crawlerMapper;

	/**
	 * 爬取指定网站
	 * 
	 * @param user 参数封装
	 * @return Result
	 */
	@PostMapping(value = "/assign_website")
	public Result assignWebsite(String website) {
		return userService.assignWebsite(website);
	}
	
	@PostMapping(value = "/product_website_spider")
	public Result productWebsiteSpider(String website,String product) {
		return userService.productWebsiteSpider(website,product);
	}
	
	/**
	 * 爬取全部网站
	 * 
	 * @return
	 */
	@PostMapping(value = "/all_website")
	public Result spiderAll() {
		return userService.spiderAll();
	}

	/**
	 * 查询指定网站规则
	 * 
	 * @param website
	 * @return
	 */
	@PostMapping(value = "/query_website_rule")
	public Result queryWebSiteRule(String website) {
		return userService.queryWebSiteRule(website);
	}
	
	

	/**
	 * 获取所有网站规则
	 * 
	 * @return
	 */
	@PostMapping(value = "/query_all_website_rule")
	public Result queryAllWebsitRule() {
		return userService.queryAllWebsitRule();
	}

	@GetMapping("/getToadyInfo")
	public List<String> getToadyInfo(){
		String nowDay = PrintUtil.getDateShortStr();
		return  crawlerMapper.getAllMissingProduct(nowDay);
	}
}
