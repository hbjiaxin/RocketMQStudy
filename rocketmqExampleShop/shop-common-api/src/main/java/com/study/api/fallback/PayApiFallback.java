package com.study.api.fallback;

import com.study.api.PayApi;
import com.study.common.constant.R;
import com.study.entity.TradePay;
import org.springframework.stereotype.Component;

@Component
public class PayApiFallback implements PayApi {
    @Override
    public R createPayment(TradePay pay) {
        return R.fail();
    }
}

