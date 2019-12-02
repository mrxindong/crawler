package com.alibaba.service;

import com.alibaba.fastjson.JSON;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PrintUtil {

    public static void println(Object object){
        System.out.println(toString(object));
    }


    public static String getDateTodayStr() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }

    public static String getDateShortStr(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now());
    }

    public static String getDateStr(int day) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDateTime.now().plusDays(day));
    }

    public static String toString(Object object) {
        return JSON.toJSONString(object);
    }
}
