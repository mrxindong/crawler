package com.alibaba.bean;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

/**
 * 药品信息类
 * 
 * @author mayn
 *
 */
@Data
public class ProductInfo {

	private String product_id;
	private String product_name; 
	private String scqy_name;
	private String gg; 

}
