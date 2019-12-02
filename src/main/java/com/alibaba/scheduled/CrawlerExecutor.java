package com.alibaba.scheduled;

import com.alibaba.bean.Result;
import com.alibaba.mapper.CrawlerMapper;
import com.alibaba.service.CrawlerService;
import com.alibaba.service.PrintUtil;
import com.alibaba.service.mail.IMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@EnableAsync
public class CrawlerExecutor {
    private static final Logger logger = LoggerFactory.getLogger(CrawlerExecutor.class);

    @Autowired
    private CrawlerService crawlerService;

    @Autowired
    public CrawlerMapper crawlerMapper;

    @Autowired
    private IMailService mailService;

    @Async
    @Scheduled(cron = "0 1 0 * * ?") // cron = "0 1 0 * * ?"
    public void crawlerAllSite(){
        Result result = crawlerService.spiderAll();
        PrintUtil.toString(result);
    }

    @Async
    @Scheduled(cron = "0 0 7,16 * * ?")  // cron = "0 0 7,16 * * ?"
    public void checkCrawlerResult(){
        String nowDay = PrintUtil.getDateShortStr();
        String beforeDay = PrintUtil.getDateStr(5);
        String content = "<h3>今日截止"+ LocalDateTime.now().getHour() +"时自动化执行信息：</h3>";
        List<String> allMissingProductMap = crawlerMapper.getAllMissingProduct(nowDay);
        content += "<br/> <h4>1.今日没有入库的维价表商品: </h4>" + PrintUtil.toString(allMissingProductMap);
        List<Map> everySiteResult =  crawlerMapper.getEverySiteResult(beforeDay,nowDay);
        content += "<br/> <h4>2.前5天有，今天没有入库的维价数据：</h4>" + PrintUtil.toString(everySiteResult);
        List<Map> contProduct = crawlerMapper.getCountProductNum(nowDay);
        content += "<br/> <h4>3.今天各个站点执行情况：</h4>" +contProduct.size() + "个站点"+ PrintUtil.toString(contProduct);
        List<Map> eachUser = crawlerMapper.getCountEachUser();
        content += "<br/><h4>4.维价产品维价情况列表:</h4>"+PrintUtil.toString(eachUser);
        mailService.sendCrawlerMail(content);
    }
}
