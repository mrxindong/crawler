package com.alibaba.spider;

import com.alibaba.bean.ProductDetailPrice;
import com.alibaba.bean.ProductInfo;
import com.alibaba.bean.WebsiteRule;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.service.ShareVar;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PingduoduoSpider {

    private static int count;

    private static ChromeDriver driver = null;
    private static BrowserMobProxy proxy = null;

    public static WebDriver getWebDriver() {
        if (driver == null) {
//            proxy = new BrowserMobProxyServer();
//            proxy.start(0);
//            // get the Selenium proxy object
//            Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
//
//            // configure it as a desired capability
//            DesiredCapabilities capabilities = new DesiredCapabilities();
//            capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);

            //创建新的Chrome选项

            System.setProperty("webdriver.chrome.driver", "/Users/mrxindong/iCloud/Documents/java/chrome/chromedriver");
            ChromeOptions chromeOptions = new ChromeOptions()
                    //设置启动为无头模式
                    .addArguments("--headless")
                    .addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36")
                    .addArguments("--disable-gpu")
                    .addArguments("--disable-dev-shm-usage")
                    .addArguments("--no-sandbox")
//                    .addArguments("--incognito")  // 启动进入隐身模式
                    .addArguments("lang=zh_CN.UTF-8")
                    //设置Chrome启动时的参数,忽略证书（SSL）错误
                    .addArguments("--ignore-certificate-errors")
                    //设置Chrome启动时的参数,设置窗口大小
                    .addArguments("----window-size=1920,1080")
                    .addArguments("--proxy-server=127.0.0.1:8080")
//                    .setProxy(seleniumProxy)
                    //开启一个实验性参数excludeSwitches，用来隐藏window.navigator.webdriver返回true,这个参数必须是List   Arrays.asList("enable-automation")
                    .setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation") );
            //等待所有资源加载完在往下执行
            chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
            chromeOptions.addArguments("disable-infobars");
            driver = new ChromeDriver(chromeOptions);
            //最大化浏览器窗口
            driver.manage().window().maximize();

        }
        return driver;
    }

    public static WebDriver getWebDriverRemote() {
        if (driver == null) {
            proxy = new BrowserMobProxyServer();
            proxy.start(0);
            // get the Selenium proxy object
            Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

            // configure it as a desired capability
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);

            System.setProperty("webdriver.chrome.driver", "/Users/mrxindong/iCloud/Documents/java/chrome/chromedriver");
            ChromeOptions chromeOptions = new ChromeOptions()
//                    .addArguments("--remote-debugging-port=9222")
                    .addArguments("--disable-gpu")
                    .addArguments("--incognito")
                    .setExperimentalOption("debuggerAddress","127.0.0.1:9222")
                    .addArguments("--disable-extensions")
                    .setProxy(seleniumProxy)
                    .addArguments("--user-data-dir=\"/Users/mrxindong/iCloud/Documents/work/yaodou-cloud/temp/chrome_file\"");

            //等待所有资源加载完在往下执行
            chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
//            chromeOptions.setHeadless(true);
//            chromeOptions.setBinary("/Applications/Google Chrome.app/Contents/MacOS/Google Chrome");
            driver = new ChromeDriver(chromeOptions);
            //最大化浏览器窗口
            driver.manage().window().maximize();
        }
        return driver;
    }

    /**
     * 每隔一秒check一下页面加载是否完成，check次数是25
     */
    public static void checkPageIsReady(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        for (int i = 0; i < 25; i++) {
            if ("complete".equals(js
                    .executeScript("return document.readyState").toString())) {
                break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected static void waitForEle(WebDriver driver, String id) {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
    }

    public static List<String> getList(String productName) {
        proxy.newHar("https://mobile.yangkeduo.com/search_result.html");
        String searchUrl = "https://mobile.yangkeduo.com/search_result.html?search_key="+ productName ;
//                "&search_src=history&search_met=history_sort&search_met_track=history&refer_search_met_pos=0&refer_page_name=search_result&refer_page_id=10015_1574497851170_T0izBend6A&refer_page_sn=10015&page_id=10015_1574645645584_lmKx3DFS4G&list_id=8PfrDdU903&flip=40%3B0%3B0%3B20%3Ba9819e29-b667-44c4-bee6-c435f1e83f9e&item_index=0&sp=0&is_back=1";
        driver.get(searchUrl);
//        PinduoduoCookie.changeCookie(driver);
        //更多搜索方式  显示等待页面
        checkPageIsReady(driver);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        //更多搜索方式  显示等待页面
        checkPageIsReady(driver);
        String nextData = js.executeScript("return document.getElementById(\"__NEXT_DATA__\").text").toString();
        List<String> goodsIdList = new ArrayList<>();
        JSONArray goodsList = JSONObject.parseObject(nextData).getJSONObject("props").getJSONObject("pageProps").getJSONObject("data").getJSONObject("ssrListData").getJSONArray("list");

        for (int i = 0; i < goodsList.size(); i++) {
            JSONObject goods = goodsList.getJSONObject(i);
            String goodsId = goods.getString("key").split("_")[1];
            goodsIdList.add(goodsId);
        }
//        下一页
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        checkPageIsReady(driver);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Har har = proxy.getHar();
        List<HarEntry> entries = har.getLog().getEntries();
        for (int i = 0; i < entries.size(); i++) {
            HarEntry harEntry =entries.get(i);
            if(harEntry.getRequest().getUrl().contains("/proxy/api/search?")){
                String searchJson = harEntry.getResponse().getContent().getComment();
                if(StringUtils.isBlank(searchJson)){
                    continue;
                }
                JSONArray items = JSONObject.parseObject(searchJson).getJSONArray("items");
                for (int j = 0; j < items.size(); j++) {
                    JSONObject item = items.getJSONObject(j);
                    goodsIdList.add(item.getString("goods_id"));
                }
            }
        }

        return goodsIdList;
    }

    public static List<String> getListFromYOUHUI(String productName) {
        List<String> goodsIdList = new ArrayList<>();
        String searchUrl = "https://youhui.pinduoduo.com/search/landing?keyword="+ productName ;
        driver.get(searchUrl);
        //更多搜索方式  显示等待页面
        checkPageIsReady(driver);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        List<WebElement> elements = driver.findElements(By.cssSelector(".goods-detail-card-wrapper a"));
        for(WebElement webElement : elements){
            String href = webElement.getAttribute("href");
            if(StringUtils.isNotBlank(href)){
                goodsIdList.add(href.split("=")[1]);
            }
        }
        return goodsIdList;
    }

    public static List<ProductDetailPrice> parseDtail(List<String> goodsIdList, WebsiteRule websiteRule, ProductInfo productInfo){
        List<ProductDetailPrice> productDetailPrices = new ArrayList<>();
//        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        Random random = new Random(1);
        for (int g = 0; g < goodsIdList.size(); g++) {
//            count ++;
//            if(count == 28){
//                count = 0;
//                PinduoduoCookie.changeCookie(driver);
//            }
            String goodId =goodsIdList.get(g);

            String url = "https://mobile.yangkeduo.com/goods.html?goods_id="+ goodId;
            driver.get(url);
            checkPageIsReady(driver);
            try {
                    Thread.sleep(1000 + random.nextInt(1000) );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String getRawDataJs = "return JSON.stringify(window.rawData)";
            Object rawDataObject = js.executeScript(getRawDataJs);

            String rawData = String.valueOf(rawDataObject);
            JSONObject jsonObject = JSONObject.parseObject(rawData);
            if(jsonObject == null || jsonObject.getJSONObject("store") == null){
                continue;
            }
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
                if ("使用剂量".equals(object.getString("key")) && !object.getString("values").contains("一天")) {
                    size = object.getString("values");
                    size = filter(size);
                }
                if ("药品规格".equals(object.getString("key")) && !"如图".equals(object.getString("values"))) {
                    size = object.getString("values");
                    size = filter(size);
                }
                if ("生产企业".equals(object.getString("key"))) {
                    scqy = object.getString("values");
                    scqy = filter(scqy);
                }
            }
            if(StringUtils.isBlank(size)){
               String[] sizes = initDataObj.getJSONObject("goods").getString("goodsName").split(" ");
                for (int i = 0; i <sizes.length; i++) {
                    if((sizes[i].contains("g") && sizes[i].contains("*"))
                            || (sizes[i].contains("盒") && sizes[i].contains("*"))){
                        size = sizes[i];
                    }
                }
            }
            String productId = ShareVar.getOnlyId(name, size, scqy, productInfo);
            ProductDetailPrice pdp = new ProductDetailPrice();
            if(productId != null){

                pdp.setProduct_id(productId);
                pdp.setWebsite_name(websiteRule.getWebsite());
                pdp.setWebsite_type(websiteRule.getWebsite_type());
                pdp.setUrl(url);
                pdp.setProduct_maxPrice(new BigDecimal(price));
                pdp.setProduct_minPrice(new BigDecimal(price));
                pdp.setShop_name(store);

            }
            productDetailPrices.add(pdp);
        };
        return productDetailPrices;
    }

    private static String filter(String value) {
        return value.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "");
    }

    public static List<ProductDetailPrice> run(WebsiteRule websiteRule, ProductInfo productInfo){
        getWebDriverRemote();

        List<String> goodsIds = getList(productInfo.getProduct_name());
        List<ProductDetailPrice> productDetailPrices = parseDtail(goodsIds,websiteRule,productInfo);
//        crawlerMapper.insertBatchDrugData(productDetailPrices);
//        crawlerMapper.insertBatchProductUrl(productDetailPrices);
//        driver.close();
        return productDetailPrices;

    }

    public static void close(){
        if(driver != null){
            //关闭窗口
            driver.close();
            //退出浏览器
//            driver.quit();
        }
    }

    public static void main(String[] args) {
        PingduoduoSpider pingduoduoSpider = new PingduoduoSpider();
        List<String> goodsIdList = new ArrayList<>();
        goodsIdList.add("58895720300");
        WebsiteRule websiteRule = new WebsiteRule();
        websiteRule.setWebsite("website");
        websiteRule.setWebsite_type("type");
        ProductInfo productInfo = new ProductInfo();
        productInfo.setScqy_name("企业");
        productInfo.setGg("gg");
        productInfo.setProduct_name("name");
        pingduoduoSpider.run( websiteRule, productInfo);
        pingduoduoSpider.close();
    }

    //            if(rawDataObject == null){
//                int timeOut = 0;
//                while (true){
//                    rawDataObject = js.executeScript(getRawDataJs);
//                    if(rawDataObject != null || timeOut >5){
//                        break;
//                    }
//                    try {
//                        timeOut++;
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
}
