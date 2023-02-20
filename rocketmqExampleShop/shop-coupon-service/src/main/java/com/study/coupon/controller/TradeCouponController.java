package com.study.coupon.controller;

import com.study.api.CouponApi;
import com.study.common.constant.R;
import com.study.common.constant.ShopCode;
import com.study.coupon.service.TradeCouponService;
import com.study.entity.TradeCoupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author studyStar
 * @since 2023-02-15
 */

@RestController
@RequestMapping("/coupon")
public class TradeCouponController implements CouponApi {

    @Autowired
    private TradeCouponService couponService;

    @Override
    @GetMapping("{couponId}")
    public R findOne(@PathVariable("couponId") Long couponId) {
        if (couponId == null) {
            return R.fail(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        TradeCoupon coupon = couponService.getById(couponId);
        if (coupon != null) {
            return R.success(coupon);
        }
        return R.fail(ShopCode.SHOP_COUPON_NO_EXIST);
    }

    @Override
    @PutMapping("updateStatus")
    public R updateCouponStatus(@RequestBody TradeCoupon coupon) {
        if (coupon == null || coupon.getCouponId() == null) {
            return R.fail(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        boolean success = couponService.updateById(coupon);
        if (!success) {
            return R.fail(ShopCode.SHOP_COUPON_USE_FAIL);
        }
        return R.success();
    }

}
