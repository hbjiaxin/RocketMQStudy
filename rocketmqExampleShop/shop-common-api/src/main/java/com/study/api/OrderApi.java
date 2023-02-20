package com.study.api;

import com.study.api.fallback.OrderApiFallback;
import com.study.common.constant.R;
import com.study.entity.TradeOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order-service", fallback = OrderApiFallback.class)
public interface OrderApi {

    @PostMapping("/order/confirmOrder")
    R confirmOrder(@RequestBody TradeOrder order);
}

