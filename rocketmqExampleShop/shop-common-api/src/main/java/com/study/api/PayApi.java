package com.study.api;

import com.study.api.fallback.PayApiFallback;
import com.study.common.constant.R;
import com.study.entity.TradePay;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "pay-service", fallback = PayApiFallback.class)
public interface PayApi {
    @PostMapping("/pay")
    R createPayment(@RequestBody TradePay pay);
}

