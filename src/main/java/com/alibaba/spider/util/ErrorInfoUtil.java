package com.alibaba.spider.util;

import org.apache.commons.lang3.StringUtils;

public class ErrorInfoUtil {
    public static boolean errInfo(String name, String size, String scqy) {
        if(StringUtils.isNotBlank(name) && StringUtils.isNotBlank(size) && StringUtils.isNotBlank(scqy) ){
            return true;
        }
        return false;
    }
}
