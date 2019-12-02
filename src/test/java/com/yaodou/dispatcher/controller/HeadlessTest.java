package com.yaodou.dispatcher.controller;

import com.alibaba.Application;
import com.alibaba.fastjson.JSON;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.xml.sax.Locator;

/**
 * @description: headless
 * @author: 王新东
 * @create: 2019-09-29 09:25
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HeadlessTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private Environment env;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void test2() throws Exception {
        System.setProperty("webdriver.chrome.driver", "/Users/mrxindong/iCloud/Documents/java/chrome/chromedriver");
        ChromeOptions chromeOptions = new ChromeOptions();
//        chromeOptions.addArguments("--headless");
        //等待所有资源加载完在往下执行
        chromeOptions.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        chromeOptions.setHeadless(true);
        WebDriver driver = new ChromeDriver(chromeOptions);
        driver.get("https://mobile.yangkeduo.com/goods1.html?_wvx=10&refer_share_uid=4400263409&share_uin=HIHP5EP3332ZNMSQWTI3ALZXHU_GEXDA&page_from=23&_wv=41729&refer_share_channel=copy_link&refer_share_id=nWZMwNUIK9TaKzZNndEgGfZMYZFJPIth&share_uid=4400263409&goods_id=41352613192#pushState");
        //最大化浏览器窗口
        driver.manage().window().maximize();

//        env.getProperty("");
        checkPageIsReady(driver);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Object o =  js.executeScript("return window.rawData");
        toString(o);

        //打印网页标题
        System.out.println(driver.getTitle());
        driver.close();
        //退出浏览器
        driver.quit();
    }

    /**
     * 每隔一秒check一下页面加载是否完成，check次数是25
     */
    public void checkPageIsReady(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        for (int i = 0; i < 25; i++) {
            if ("complete".equals(js
                    .executeScript("return document.readyState").toString())) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void setRichTextBox(String text, WebDriver driver) {
        WebElement e = driver.findElement(By.id("kw"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value = \"" + text + "\"", e);
    }

    protected String getRichTextBox( WebDriver driver) {
        WebElement e =  driver.findElement(By.id("kw"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        String result=(String) js.executeScript("var result=arguments[0].innerHTML;return result", e);
        return result;
    }

    protected void waitForEle(WebDriver driver,String id){
        WebDriverWait wait=new WebDriverWait(driver,10);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(id)));
    }

    private void toString(Object object) {
        System.out.println(JSON.toJSONString(object));
    }
}
