package com.alibaba.bean;

import java.math.BigDecimal;
import lombok.Data;

public class ProductDetailPrice {

	private String product_id; // 唯一id
	private BigDecimal product_minPrice;
	private BigDecimal product_maxPrice; 
	private String shop_name; 
	private String website_name; 
	private String website_type;
	private String url;
	private String spname;
	private String gg;
	private String scqy;


	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public BigDecimal getProduct_minPrice() {
		return product_minPrice;
	}

	public void setProduct_minPrice(BigDecimal product_minPrice) {
		this.product_minPrice = product_minPrice;
	}

	public BigDecimal getProduct_maxPrice() {
		return product_maxPrice;
	}

	public void setProduct_maxPrice(BigDecimal product_maxPrice) {
		this.product_maxPrice = product_maxPrice;
	}

	public String getShop_name() {
		return shop_name;
	}

	public void setShop_name(String shop_name) {
		this.shop_name = shop_name;
	}

	public String getWebsite_name() {
		return website_name;
	}

	public void setWebsite_name(String website_name) {
		this.website_name = website_name;
	}

	public String getWebsite_type() {
		return website_type;
	}

	public void setWebsite_type(String website_type) {
		this.website_type = website_type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSpname() {
		return spname;
	}

	public void setSpname(String spname) {
		this.spname = spname;
	}

	public String getGg() {
		return gg;
	}

	public void setGg(String gg) {
		this.gg = gg;
	}

	public String getScqy() {
		return scqy;
	}

	public void setScqy(String scqy) {
		this.scqy = scqy;
	}
}
