package com.alibaba.controller;

import com.alibaba.service.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class CrawlerTaskExecutor {

    @Value("${yaodou.dispatcher}")
    private String dispatcherUrl;

    @Autowired
    private CrawlerService crawlerService;

//    @Scheduled(fixedDelay = 1000 * 6)
    public void scheduledCraw(){
        for(crawlerEnum crawlerEnum : crawlerEnum.values()){
            while (execCrawler(crawlerEnum.name())){

            }
        }
    }

    /**
     * 执行爬虫，是否成功
     * @param crawlerName
     */
    public boolean execCrawler(String crawlerName){
        RestTemplate restTemplate = new RestTemplate();
        String params="?name="+crawlerName;
        //获取商品
        String product =  restTemplate.getForObject(dispatcherUrl + "/crawler/getProduct"+params, String.class);
        if(product == null || "".equals(product) || product.contains("未知异常")){
            return false;
        }
        params += "&product="+product;
        boolean isSuccess = false;
        try{
            crawlerService.productWebsiteSpider(crawlerName,product);
            isSuccess = true;
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        if(isSuccess){
            restTemplate.getForObject(dispatcherUrl + "/crawler/done"+params, String.class);
        }else {
            restTemplate.getForObject(dispatcherUrl + "/crawler/failTask"+params, String.class);
        }
        return true;
    }
}

/**
 * 网站
 */
enum crawlerEnum {
    babaipharm,
    bioey,
    jd,
    jianke,
    kangaiduo,
    yaofangwang,
    yiyaowang,
    miaoshou,
    ddky,
    ehaoyao,
    liangjianhaoyao,
    qyt1902,
    huatuoyf,
    ak1ak1
}
