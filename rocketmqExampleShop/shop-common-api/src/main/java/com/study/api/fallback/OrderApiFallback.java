package com.study.api.fallback;

import com.study.api.OrderApi;
import com.study.common.constant.R;
import com.study.entity.TradeOrder;
import org.springframework.stereotype.Component;

@Component
public class OrderApiFallback implements OrderApi {
    @Override
    public R confirmOrder(TradeOrder order) {
        return R.fail();
    }
}

