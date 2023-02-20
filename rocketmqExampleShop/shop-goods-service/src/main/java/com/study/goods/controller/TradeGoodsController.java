package com.study.goods.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.study.api.GoodsApi;
import com.study.common.constant.R;
import com.study.common.constant.ShopCode;
import com.study.entity.TradeGoods;
import com.study.entity.TradeGoodsNumberLog;
import com.study.goods.service.TradeGoodsNumberLogService;
import com.study.goods.service.TradeGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author studyStar
 * @since 2023-02-15
 */

@RestController
@RequestMapping("/goods")
public class TradeGoodsController implements GoodsApi {

    @Autowired
    private TradeGoodsService goodsService;

    @Autowired
    private TradeGoodsNumberLogService goodsLogService;

    @Override
    @GetMapping("{goodsId}")
    public R findOne(@PathVariable("goodsId") Long goodsId) {
        if (goodsId == null) {
            return R.fail(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }

        TradeGoods goods = goodsService.getById(goodsId);
        if (goods == null) {
            return R.fail(ShopCode.SHOP_GOODS_NO_EXIST);
        }
        return R.success(goods);
    }

    @Override
    @PostMapping("reduceNum")
    public R reduceNum(@RequestParam("orderId") Long orderId, @RequestParam("goodsId") Long goodsId, @RequestParam("goodsNum") Integer goodsNumber) {
        // 校验数据
        if (orderId == null || goodsId == null || goodsNumber == null || goodsNumber <= 0) {
            return R.fail(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        TradeGoods goods = goodsService.getById(goodsId);
        if (goods.getGoodsNumber() < goodsNumber) {
            // 库存不足
            return R.fail(ShopCode.SHOP_GOODS_NUM_NOT_ENOUGH);
        }
        // 减库存
        goods.setGoodsNumber(goods.getGoodsNumber() - goodsNumber);
        boolean success = goodsService.updateById(goods); // 用SQL修改更好？？？
        if (!success) {
            return R.fail(ShopCode.SHOP_REDUCE_GOODS_NUM_FAIL);
        }
        // 记录库存操作日志（我的想法：和减库存放在一个事务中？？？）
        TradeGoodsNumberLog goodsNumberLog = new TradeGoodsNumberLog();
        goodsNumberLog.setGoodsId(goodsId);
        goodsNumberLog.setGoodsNumber(goodsNumber);
        goodsNumberLog.setOrderId(orderId);
        goodsNumberLog.setLogTime(new Date());
        goodsLogService.save(goodsNumberLog);

        return R.success();
    }
}
