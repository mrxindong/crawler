package com.alibaba.bean;


import lombok.Data;

/**
 * 药品规则表
 * @author mayn
 *
 */
@Data
public class WebsiteRule {

	private int id;
	private String drug_url; // 药品网站主入口
	private String charset; // 网页编码格式
	private String website; // 药品网站来源
	private String website_eng_name; 
	private String website_type;
	private String drug_href; // 药品链接拼接：没有不需要拼接
	private String drug_href_rule; // 药品url规则
	private String drug_href_attribute; // url属性
//	private String if_son_href; // 是否含有三级url：0为没有、1为有
	private String drug_son_href_rule; // 药品子链接url规则
	private String drug_son_href_attribute; // 药品子链接url属性
	private String drug_size_rule; // 药品规格规则
	private String drug_price_rule; // 药品价格规则
	private String drug_approval_number_rule; // 药品批准文号规则
	private String drug_shop_rule; // 药品店铺规则
	private String drug_title_rule; // 药品标题规则
	private String drug_imageurl_rule; // 药品图片规则
	private String drug_imageurl_attribute; // 药品图片url属性
	private String drug_sales_volume_rule; // 药品销量规则
	private String drug_inventory_rule; // 药品库存规则
	private String drug_instruction_rule; // 药品说明书规则


}
