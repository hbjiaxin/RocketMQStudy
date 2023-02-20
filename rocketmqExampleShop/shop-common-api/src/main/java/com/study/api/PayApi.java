package com.study.api;

import com.study.api.fallback.PayApiFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@FeignClient(name = "pay-service", fallback = PayApiFallback.class)
public interface PayApi {
}

