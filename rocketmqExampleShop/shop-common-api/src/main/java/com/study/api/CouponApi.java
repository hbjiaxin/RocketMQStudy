package com.study.api;

import com.study.api.fallback.CouponApiFallback;
import com.study.common.constant.R;
import com.study.entity.TradeCoupon;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "coupon-service", fallback = CouponApiFallback.class)
public interface CouponApi {

    @GetMapping("/coupon/{couponId}")
    R findOne(@PathVariable("couponId") Long couponId);

    // 更新优惠券状态
    @PutMapping("/coupon/updateStatus")
    R updateCouponStatus(@RequestBody TradeCoupon coupon);
}

