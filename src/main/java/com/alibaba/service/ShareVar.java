package com.alibaba.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.alibaba.bean.ProductInfo;
import com.alibaba.bean.WebsiteRule;
import com.alibaba.fastjson.JSONObject;

/**
 * 公共类
 * 
 * @author mayn
 *
 */
public class ShareVar {
	/*
	 * 项目路径
	 */
	private static final String PROJECT_PATH = System.getProperty("user.dir");

	public static OperationHttpClient opHttpClient = new OperationHttpClient();
	@Autowired
	public static HtmlParseTool hpt = new HtmlParseTool();

	/**
	 * 字符串操作类
	 */
	public static StringDenoising strDenoising = new StringDenoising();

	/**
	 * 网站规则集合
	 */
	public static List<WebsiteRule> websiteRuleList;


	/**
	 * 获取唯一id
	 * 
	 * @return
	 */
	public static String getOnlyId(String name, String size, String scqy, ProductInfo productInfo) {
		try {
			boolean isFullAndHalfScpy = scqy.replaceAll("（","(").replaceAll("）",")").equals(productInfo.getScqy_name());
			if (name.equals(productInfo.getProduct_name()) && (scqy.equals(productInfo.getScqy_name()) || isFullAndHalfScpy) && ggjs(size) == ggjs(productInfo.getGg())) {
				return productInfo.getProduct_id();
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 计算规格中的数字相乘值，注意清理无用信息如括号里的值 去掉数字中的小数点，然后计算
	 * 
	 * @param gg
	 * @return
	 */
	public static int ggjs(String gg) {
		if (gg == null)
			return 0;
		String num = "1234567890.";
		String bu = "";
		List<String> ll = new ArrayList<String>();
		// 提取规则中的数字
		for (int n = 0; n < gg.length(); n++) {
			if (num.indexOf(gg.charAt(n)) >= 0) {
				bu += gg.charAt(n);
			} else {
				if (bu.length() > 0) {
					ll.add(bu);
					bu = "";
				}
			}
		}
		if (bu.length() > 0) {
			ll.add(bu);
		}
		int res = 1;
		if (ll.size() > 0) {
			// 数字中去掉小数点，（解决数字精度问题）数字间做乘法运算
			for (String ns : ll) {
				ns = ns.replace(".", "");
				if (ns.length() > 0) {
					res = res * Integer.parseInt(ns);
				}
			}
			return res;
		}
		return 0;
	}

	public static String keyEncode(String key, String charset) {
		try {
			String urlString = URLEncoder.encode(key, charset); // 输出%C4%E3%BA%C3
			return urlString;
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	/**
	 * 将json数据存入本地磁盘并返回路径
	 * 
	 * @param map
	 * @param path
	 * @param source_website
	 * @param drug_name
	 * @param drug_only_id
	 */
	public static String getInstructionPath(Map<String, Object> map, String source_website, String drug_name,
			String drug_only_id) {
		JSONObject jsonObject = new JSONObject(map);
		System.out.println(jsonObject.toJSONString());
		FileWriter fw = null;
		try {
			try {
				mkdirs(PROJECT_PATH + "\\instruction\\" + source_website + "\\" + drug_name + "\\");
				String path = PROJECT_PATH + "\\instruction\\" + source_website + "\\" + drug_name + "\\" + drug_only_id
						+ ".json";
				fw = new FileWriter(path);
				fw.write(jsonObject.toJSONString());
				fw.flush();
				return path;
			} finally {
				if (fw != null)
					fw.close();
			}
		} catch (IOException e) {
			return null;
		}
	}

	public static void mkdirs(String path) {
		File file = new File(path);
		if (!file.exists())
			file.mkdirs();
	}
}
