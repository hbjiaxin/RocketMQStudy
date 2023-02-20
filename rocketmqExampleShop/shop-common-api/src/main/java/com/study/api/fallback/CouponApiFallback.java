package com.study.api.fallback;

import com.study.api.CouponApi;
import com.study.common.constant.R;
import com.study.entity.TradeCoupon;
import org.springframework.stereotype.Component;

@Component
public class CouponApiFallback implements CouponApi {
    @Override
    public R findOne(Long couponId) {
        return R.fail();
    }

    @Override
    public R updateCouponStatus(TradeCoupon coupon) {
        return R.fail();
    }
}

