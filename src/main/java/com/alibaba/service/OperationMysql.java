package com.alibaba.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.bean.ProductDetailPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class OperationMysql {

	private Connection conn = null;

	@Autowired
	DataSource dataSource;

	public void insertProductUrl(String product_id,String website_name,String website_type,String product_url) {
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = null;
			try {
				ps = conn.prepareStatement("INSERT INTO angel_user_url_copy(product_id,website_name,website_type,product_url) VALUES (?,?,?,?)");
				ps.setString(1, product_id);
				ps.setString(2, website_name);
				ps.setString(3, website_type);
				ps.setString(4, product_url);	
				ps.execute();
			}finally {
				if(ps!= null)
					ps.close();
					conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertProductUrl(List<ProductDetailPrice> pdps) {
		if(pdps == null || pdps.size()==0){
			return;
		}
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = null;
			try {
				ps = conn.prepareStatement("INSERT INTO angel_user_url_copy(product_id,website_name,website_type,product_url) VALUES (?,?,?,?)");
				for(ProductDetailPrice pdp : pdps) {
					ps.setString(1, pdp.getProduct_id());
					ps.setString(2, pdp.getWebsite_name());
					ps.setString(3, pdp.getWebsite_type());
					ps.setString(4, pdp.getUrl());
					ps.addBatch();
				}
				ps.executeBatch();
				ps.clearBatch();
			}finally {
				if(ps!= null)
					ps.close();
					conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertProductDetailPrice(ProductDetailPrice pdp) {
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = null;
			try {
				ps = conn.prepareStatement("INSERT INTO product_detail_price(product_id,min_price,max_price,"
						+ "shop_name,website_name,website_type,url) VALUES (?,?,?,?,?,?,?)");
				ps.setString(1, pdp.getProduct_id());
				ps.setBigDecimal(2, pdp.getProduct_minPrice());
				ps.setBigDecimal(3, pdp.getProduct_maxPrice());
				ps.setString(4, pdp.getShop_name());
				ps.setString(5, pdp.getWebsite_name());
				ps.setString(6, pdp.getWebsite_type());	
				ps.setString(7, pdp.getUrl());
				ps.execute();
			}finally {
				if(ps!= null)
					ps.close();
					conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertProductDetailPrice(List<ProductDetailPrice> pdps) {
		if(pdps == null || pdps.size()==0){
			return;
		}
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = null;
			try {
				ps = conn.prepareStatement("INSERT INTO product_detail_price(product_id,min_price,max_price,"
						+ "shop_name,website_name,website_type,url) VALUES (?,?,?,?,?,?,?)");

				for(ProductDetailPrice pdp : pdps){
					ps.setString(1, pdp.getProduct_id());
					ps.setBigDecimal(2, pdp.getProduct_minPrice());
					ps.setBigDecimal(3, pdp.getProduct_maxPrice());
					ps.setString(4, pdp.getShop_name());
					ps.setString(5, pdp.getWebsite_name());
					ps.setString(6, pdp.getWebsite_type());
					ps.setString(7, pdp.getUrl());
					ps.addBatch();
				}
				ps.executeBatch();
				ps.clearBatch();
			}finally {
				if(ps!= null)
					ps.close();
					conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertProductDetailPriceErr(List<ProductDetailPrice> pdps) {
		if(pdps == null || pdps.size()==0){
			return;
		}
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = null;
			try {
				ps = conn.prepareStatement("INSERT INTO product_detail_price_err(product_id,min_price,max_price,"
						+ "shop_name,website_name,website_type,url,spname,gg,scqy) VALUES (?,?,?,?,?,?,?,?,?,?)");

				for(ProductDetailPrice pdp : pdps){
					ps.setString(1, pdp.getProduct_id());
					ps.setBigDecimal(2, pdp.getProduct_minPrice());
					ps.setBigDecimal(3, pdp.getProduct_maxPrice());
					ps.setString(4, pdp.getShop_name());
					ps.setString(5, pdp.getWebsite_name());
					ps.setString(6, pdp.getWebsite_type());
					ps.setString(7, pdp.getUrl());
					ps.setString(8,pdp.getSpname());
					ps.setString(9,pdp.getGg());
					ps.setString(10,pdp.getScqy());
					ps.addBatch();
				}
				ps.executeBatch();
				ps.clearBatch();
			}finally {
				if(ps!= null)
					ps.close();
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateSpiderStatus(String productId) {
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = null;
			try {
				ps = conn.prepareStatement("UPDATE `product_wj_status` SET `spider_status`='0' WHERE product_id = ?");
				ps.setString(1, productId);
				ps.executeUpdate();
			}finally {
				if(ps!= null)
					ps.close();
					conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
	}
}
