package com.alibaba.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringDenoising {

	/**
	 * 
	 * @param ob
	 * @return
	 */
	public boolean objIfNull(Object ob) {
		return ob == null ? false : true;
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public boolean strIfNull(String str) {
		return (str != null && !(str.equals(""))) ? true : false;
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public String strIllegalCharacter(String str) {
		str = strMatching(str);
		try {
			str = str.replaceAll("/", "");
		} catch (Exception e) {
		}
		try {
			str = str.replaceAll("#", "");
		} catch (Exception e) {

		}
		try {
			str = str.replaceAll("\\", "");
		} catch (Exception e) {
		}
		try {
			str = str.replaceAll(":", "");
		} catch (Exception e) {
		}
		try {
			str = str.replaceAll("\"", "");
		} catch (Exception e) {
		}
		try {
			str = str.replaceAll("|", "");
		} catch (Exception e) {
		}
		try {
			str = str.replaceAll("*", "");
		} catch (Exception e) {
		}
		try {
			str = str.replaceAll("<", "");
		} catch (Exception e) {
		}
		try {
			str = str.replaceAll("<", "");
		} catch (Exception e) {
		}
		try {
			str = str.replaceAll(">", "");
		} catch (Exception e) {
		}
		try {
			str = str.replaceAll("'", "");
		} catch (Exception e) {
		}
		try {
			str = str.replaceAll("\r\n", "");
		} catch (Exception e) {
		}
		try {
			str = str.replace("\n", "");
		} catch (Exception e) {

		}
		return str;
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	private String strMatching(String str) {
		Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\_\u4e00-\u9fa5\\__\\pP]");
		// Pattern pattern = Pattern.compile("[@#]");
		Matcher matcher = pattern.matcher(str);
		StringBuffer buffer = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(buffer, "");
		}
		matcher.appendTail(buffer);

		return buffer.toString();
	}

	/**
	 * url做替换
	 * @param url
	 * @param drug_name
	 * @param drug_brand
	 * @return
	 */
	public String urlReplace(String url,String drug_name,String drug_brand) {
		if(strIfNull(url)) {
			if(strIfNull(drug_name)) {
				if(strIfNull(drug_brand)) {
					return url.replaceAll("##", drug_name).replaceAll("@@", drug_brand);
				} else {
					return url.replaceAll("##", drug_name).replaceAll("@@", "");
				}
			} else
				return null;
		}else
			return null;
	}

}
