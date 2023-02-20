package com.study.pay.controller;

import com.study.api.OrderApi;
import com.study.api.PayApi;
import com.study.common.constant.R;
import com.study.common.constant.ShopCode;
import com.study.entity.TradePay;
import com.study.pay.service.TradePayService;
import org.apache.ibatis.annotations.Result;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author studyStar
 * @since 2023-02-15
 */
@RestController
@RequestMapping("/pay")
public class TradePayController implements PayApi {

    @Resource
    private OrderApi orderApi;

    @Resource
    private TradePayService payService;

    @PostMapping
    @Override
    public R createPayment(@RequestBody TradePay pay) {
        if (pay == null
                || pay.getOrderId() == null
                || pay.getPayAmount() == null
                || pay.getPayAmount().compareTo(BigDecimal.ZERO) < 0) {
            return R.fail(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        // 1.判断订单支付状态

        // 2.设置订单的状态未支付

        // 3.保存支付订单
        return R.fail();
    }
}
