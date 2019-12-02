package com.alibaba.spider.util;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.chrome.ChromeDriver;

public class PinduoduoCookie {
    private static String account ="1";

    public static void changeCookie(ChromeDriver driver){
        cookieBase(driver);
        if(account.equals("1")){
            setCookie2(driver);
        }else {
            setCookie1(driver);
        }
    }

    public static void cookieBase(ChromeDriver driver){
        Cookie _nano_fp = new Cookie("_nano_fp","Xpd8n0Xan5UjX5Tyno_FKrD0Shp1lfFjnBz6Mgbc","mobile.yangkeduo.com","/",null);
        Cookie api_uid = new Cookie("api_uid","CiSvsV3WK6LAxQBE6zJtAg==",".yangkeduo.com","/",null);
        Cookie chat_list_rec_list = new Cookie("chat_list_rec_list","chat_list_rec_list_l1xJD2","mobile.yangkeduo.com","/",null);
        Cookie msec = new Cookie("msec","1800000","mobile.yangkeduo.com","/",null);
        Cookie pdd_user_id = new Cookie("pdd_user_id","7704763926128","mobile.yangkeduo.com","/",null);
        Cookie pdd_user_uin = new Cookie("pdd_user_uin","6Z2CU2PUYO77S5JOISMNEAOSXU_GEXDA","mobile.yangkeduo.com","/",null);
        Cookie webp = new Cookie("webp","1","mobile.yangkeduo.com","/",null);
        driver.manage().addCookie(_nano_fp);
        driver.manage().addCookie(api_uid);
        driver.manage().addCookie(pdd_user_uin);
        driver.manage().addCookie(chat_list_rec_list);
        driver.manage().addCookie(msec);
        driver.manage().addCookie(pdd_user_id);
        driver.manage().addCookie(webp);
    }
    //        Cookie webp = new Cookie("webp","1","mobile.yangkeduo.com","/",null);


    public static void setCookie1(ChromeDriver driver ){
        Cookie JSESSIONID = new Cookie("JSESSIONID","0E72FAEC503C158EADB278671900E840","mobile.yangkeduo.com","/",null);
        Cookie PDDAccessToken = new Cookie("PDDAccessToken","J3KCQGEYOTFEFPDTTZDGMUC5ME4VMHISW6PLG5JVIDTGLDOVD72Q113962a","mobile.yangkeduo.com","/",null);
        Cookie rec_list_index = new Cookie("rec_list_index","rec_list_index_HCObkR","mobile.yangkeduo.com","/",null);
        Cookie rec_list_personal = new Cookie("rec_list_personal","rec_list_personal_oi8z89","mobile.yangkeduo.com","/",null);
        Cookie ua = new Cookie("ua","Mozilla%2F5.0%20(Macintosh%3B%20Intel%20Mac%20OS%20X%2010_13_4)%20AppleWebKit%2F537.36%20(KHTML%2C%20like%20Gecko)%20Chrome%2F78.0.3904.108%20Safari%2F537.36","mobile.yangkeduo.com","/",null);

        driver.manage().addCookie(JSESSIONID);
        driver.manage().addCookie(PDDAccessToken);
        driver.manage().addCookie(rec_list_index);
        driver.manage().addCookie(rec_list_personal);
        driver.manage().addCookie(ua);

    }

    public static void setCookie2(ChromeDriver driver ){ //我
        Cookie JSESSIONID = new Cookie("JSESSIONID","768947B2C138E336BD452697BCB4B8C7","mobile.yangkeduo.com","/",null);
        Cookie PDDAccessToken = new Cookie("PDDAccessToken","NKHFFCZPN5J2BOEY36Y2SCV3SGS67JQNYMT47NC7LSDTF6SOM2FQ11270bf","mobile.yangkeduo.com","/",null);
        Cookie rec_list_index = new Cookie("rec_list_index","rec_list_index_MAbj4l","mobile.yangkeduo.com","/",null);
        Cookie rec_list_personal = new Cookie("rec_list_personal","rec_list_personal_5vqld3","mobile.yangkeduo.com","/",null);
        Cookie ua = new Cookie("ua","Mozilla%2F5.0%20(Windows%20NT%2010.0%3B%20Win64%3B%20x64)%20AppleWebKit%2F537.36%20(KHTML%2C%20like%20Gecko)%20Chrome%2F73.0.3683.75%20Safari%2F537.36","mobile.yangkeduo.com","/",null);
        driver.manage().addCookie(JSESSIONID);
        driver.manage().addCookie(PDDAccessToken);
        driver.manage().addCookie(rec_list_index);
        driver.manage().addCookie(rec_list_personal);
        driver.manage().addCookie(ua);

    }

}
