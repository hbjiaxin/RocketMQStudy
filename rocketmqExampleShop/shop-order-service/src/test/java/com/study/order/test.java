package com.study.order;

import com.baomidou.mybatisplus.core.conditions.segments.OrderBySegmentList;
import com.study.common.constant.R;
import com.study.entity.TradeOrder;
import com.study.order.OrderApplication;
import com.study.order.controller.TradeOrderController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@SpringBootTest(classes = OrderApplication.class,properties = "application.yml")
public class test {

    @Test
    public void testConfirmOrder() {
        RestTemplate template = new RestTemplate();
        String url = "http://localhost:9005/order/confirmOrder";

        Long couponId = 1231243423L;
        Long goodsId = 345959443973935104L;
        Long userId = 345963634385633280L;
        TradeOrder order = new TradeOrder();
        order.setUserId(userId);
        order.setGoodsId(goodsId);
        order.setCouponId(couponId);
        order.setAddress("湖北");
        order.setGoodsNumber(1);
        order.setGoodsPrice(new BigDecimal(1000)); // 前台价格
        order.setPayAmount(new BigDecimal(100));
        order.setShippingFee(BigDecimal.ZERO);
        order.setOrderAmount(new BigDecimal(880));

        R r = template.postForObject(url, order, R.class);
        System.out.println(r.toString());
    }
}

