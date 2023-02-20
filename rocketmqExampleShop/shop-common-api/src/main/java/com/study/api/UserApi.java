package com.study.api;

import com.study.api.fallback.UserApiFallback;
import com.study.common.constant.R;
import com.study.entity.TradeUserMoneyLog;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", fallback = UserApiFallback.class)
public interface UserApi {

    @GetMapping("/user/{userId}")
    R findOne(@PathVariable("userId") Long userId);

    @PutMapping("/user/pay")
    R updateMoneyPaid(@RequestBody TradeUserMoneyLog userMoneyLog);
}

