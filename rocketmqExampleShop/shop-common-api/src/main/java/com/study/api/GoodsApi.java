package com.study.api;

import com.study.api.fallback.GoodsApiFallback;
import com.study.common.constant.R;
import com.study.entity.TradeGoods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "goods-service", fallback = GoodsApiFallback.class)
public interface GoodsApi {

    // 根据ID查询商品对象是否存在
    @GetMapping("/goods/{goodsId}")
    R findOne(@PathVariable("goodsId") Long goodsId);

    // 扣减库存
    @PostMapping("/goods/reduceNum")
    R reduceNum(@RequestParam("orderId") Long orderId, @RequestParam("goodsId") Long goodsId, @RequestParam("goodsNum") Integer goodsNumber);

}

