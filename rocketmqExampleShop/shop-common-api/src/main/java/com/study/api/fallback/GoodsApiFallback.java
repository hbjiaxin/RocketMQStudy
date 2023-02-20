package com.study.api.fallback;

import com.study.api.GoodsApi;
import com.study.common.constant.R;
import com.study.entity.TradeGoods;
import org.springframework.stereotype.Component;

@Component
public class GoodsApiFallback implements GoodsApi {
    @Override
    public R findOne(Long goodsId) {
        return R.fail();
    }

    @Override
    public R reduceNum(Long orderId, Long goodsId, Integer goodsNumber) {
        return R.fail();
    }
}

