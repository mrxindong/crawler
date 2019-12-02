package com.yaodou.dispatcher.controller;

import com.alibaba.Application;
import com.alibaba.bean.ProductDetailPrice;
import com.alibaba.fastjson.JSON;
import com.alibaba.mapper.CrawlerMapper;
import com.alibaba.scheduled.CrawlerExecutor;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 针对Controller的测试
 * @author: 王新东
 * @create: 2019-09-29 09:25
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DemoInfoServiceTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    CrawlerMapper crawlerMapper;

    @Autowired
    CrawlerExecutor crawlerExecutor;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void test2() throws Exception {
        for (int i = 0; i < 25; i++) {
            if(i == 25){
                toString("25");
            }
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/crawler/getProduct")
                    .param("name", "yaofangwang")
            ).andReturn();
        }
    }

    @Test
    public void testInsert(){
        List<ProductDetailPrice> productDetailPrices=new ArrayList<>();
        ProductDetailPrice p1 = new ProductDetailPrice();
        p1.setProduct_id("1");
        p1.setProduct_maxPrice(new BigDecimal("2"));
        p1.setProduct_minPrice(new BigDecimal("2"));
        p1.setUrl("aaaaa");
        productDetailPrices.add(p1);

        crawlerMapper.insertBatchDrugData(productDetailPrices);
    }


    private void toString(Object object) {
        System.out.println(JSON.toJSONString(object));
    }
}
