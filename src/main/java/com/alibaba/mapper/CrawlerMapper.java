package com.alibaba.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.alibaba.bean.ProductDetailPrice;
import com.alibaba.bean.ProductInfo;
import com.alibaba.bean.WebsiteRule;

/**
 * mapper的具体表达式
 */
@Mapper 
@Repository
public interface CrawlerMapper {
	
	@Select(value = "SELECT * FROM website_rule where state = 1")
	public List<WebsiteRule> spiderAll();
	
	@Insert("INSERT INTO angel_user_url_copy(product_id,website_name,website_type,product_url) VALUES (#{product_id},#{website_name},#{website_type},#{product_url})")
	public void insertProductUrl(String product_id, String website_name, String website_type, String product_url);

	
	/**
	 *  插入数据
	 * @param drugData
	 */
	@Insert("INSERT INTO product_detail_price(" + 
						"product_id," + 
						"min_price," + 
						"max_price," + 
						"shop_name," + 
						"website_name," + 
						"website_type," + 
						"url)" + 
					"VALUES (" + 
					 	"#{product_id}," + 
					 	"#{product_minPrice}," + 
					 	"#{product_maxPrice}," + 
					 	"#{shop_name}," + 
					 	"#{website_name}," + 
					 	"#{website_type}," + 
					 	"#{url}" +
					")")
	public void insertDrugData(ProductDetailPrice drugData);


	public int insertBatchDrugData(@Param("drugDatas") List<ProductDetailPrice> drugDatas);

	public int insertBatchProductUrl(@Param("drugDatas") List<ProductDetailPrice> drugDatas);
	
	/**
	 * 获取药品信息
	 * @return
	 */
	@Select(value = "SELECT " +
			"  wj.product_id, pn.product_name,scqy.scqy_name,obj.gg " +
			"  FROM " +
			"  (select product_id,product_name,scqy as scqy_name,gg from product_wj GROUP BY product_id,product_name,scqy,gg) wj " +
			"  INNER JOIN pcc_product_obj obj on wj.product_id = obj.po_id " +
			"  INNER JOIN pcc_product p on obj.p_id = p.p_id " +
			"  INNER JOIN pcc_product_scqy scqy on p.pps_id = scqy.pps_id " +
			"  INNER JOIN pcc_product_name pn on p.ppn_id = pn.ppn_id ")
	public List<ProductInfo> getDrugInfos();


//	/**
//	 * 获取药品信息
//	 * @return
//	 */
//	@Select(value = "SELECT\n" +
//			"\tt1.* \n" +
//			"FROM\n" +
//			"\t( SELECT * FROM product_wj GROUP BY product_id ) t1\n" +
//			"\tLEFT JOIN ( SELECT product_id, count( * ) FROM `esstat`.`product_detail_price` pdp WHERE `create_time` LIKE '2019-11-20 %' GROUP BY product_id ) t2 ON t1.product_id = t2.product_id \n" +
//			"WHERE\n" +
//			"\tt2.product_id IS NULL")
//	public List<ProductInfo> getDrugInfos();

	/**
	 * 获取药品信息
	 * @return
	 */
	@Select(value = "SELECT * FROM product_wj WHERE wj_Status = 'Y' AND product_name = #{product_name}")
	public List<ProductInfo> getProductDrugInfos(@Param("product_name") String product_name);
	/**
	 * 获取药品规格
	 * @param drug_name
	 * @param drug_brand
	 * @return
	 */
	@Select(value = "SELECT drug_size FROM drug_size WHERE drug_name = #{drug_name} AND drug_brand = #{drug_brand}")
	@Results( {@Result(property = "drug_name", column = "drug_name")
	,@Result(property = "drug_brand", column = "drug_brand")})
	public List<String> getDrugSize(@Param("drug_name") String drug_name, @Param("drug_brand") String drug_brand);
	
	/**
	 * 获取所有规则
	 * @return
	 */
	@Select(value = "SELECT * FROM website_rule;")
	public List<WebsiteRule> queryAllWebsiteRule();

	/**
	 * 获取指定网站规则
	 * @param website
	 * @return
	 */
	@Select(value = "SELECT * FROM website_rule WHERE website = #{website}")
	@Results( @Result(property = "website", column = "website"))
	public WebsiteRule queryWebSiteRule(String website);
	
	@Update(value = "UPDATE `product_wj_status` SET `spider_status`='0' WHERE product_id = #{product_id}")
	public void updateSpiderStatus(@Param("product_id") String product_id);

	/**
	 * 获取所有网站
	 * @return
	 */
	@Select(value = "SELECT website FROM website_rule")
	public List<String> getAllWebsite();
	
	@Select(value = "SELECT DISTINCT product_name FROM `product_wj`")
	public List<String> getAllProduct();

	/**
	 * 查询今日没有爬取的维价商品
	 * @param currentDate
	 * @return
	 */
	@Select(value = " SELECT\n" +
			"\tt1.* \n" +
			"FROM\n" +
			"\t( SELECT product_id FROM product_wj GROUP BY product_id ) t1\n" +
			"\tLEFT JOIN ( SELECT product_id, count( * ) FROM `esstat`.`product_detail_price` pdp WHERE `create_time` LIKE CONCAT(#{currentDate},' %') GROUP BY product_id ) t2 ON t1.product_id = t2.product_id \n" +
			"WHERE\n" +
			"\tt2.product_id IS NULL")
	public List<String> getAllMissingProduct(String currentDate);

	/**
	 * 查询以前有，今天没有爬取到的维价数据
	 * @param recentlyDate
	 * @param currentDate
	 * @return
	 */
	@Select(value = "\tSELECT\n" +
			"\tt1.* \n" +
			"FROM\n" +
			"\t(SELECT product_id,website_name FROM `esstat`.`product_detail_price` pdp WHERE `create_time` > #{recentlyDate} GROUP BY product_id,website_name ) t1\n" +
			"\tLEFT JOIN ( SELECT product_id,website_name, count( * ) FROM `esstat`.`product_detail_price` pdp WHERE `create_time` > #{currentDate} GROUP BY product_id,website_name ) t2 ON t1.product_id = t2.product_id and t1.website_name= t2.website_name\n" +
			"WHERE\n" +
			"\tt2.product_id IS NULL")
	public List<Map> getEverySiteResult(@Param("recentlyDate") String recentlyDate, @Param("currentDate") String currentDate);

	/**
	 * 查询今天爬虫的统计数据
	 * @param currentDate
	 * @return
	 */
	@Select(value = "\tSELECT website_name as '网站', count( * )  as '数量' \n" +
			"\t FROM `esstat`.`product_detail_price` pdp \n" +
			"\t WHERE `create_time` LIKE CONCAT(#{currentDate},' %') GROUP BY website_name")
	public List<Map> getCountProductNum(@Param("currentDate")String currentDate);

	@Select(value = "select pw.scqy 维价产品生产企业,ul.USER_LOGIN_ID 用户登录名,ul.id 用户编号,count(distinct pw.product_id) 维价产品数据量,count(distinct pdp.product_id) 返回产品数据量,count(pdp.id) 返回记录数 from user_login ul  join product_wj pw on ul.id=pw.user_id left join (select id,product_id from product_detail_price  where `create_time` like concat(date_format( now(), '%Y-%m-%d' ),'%') ) pdp on pw.product_id=pdp.product_id   group by pw.scqy,ul.USER_LOGIN_ID")
	public List<Map> getCountEachUser();
}
