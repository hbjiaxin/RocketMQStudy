package com.study.order.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.api.CouponApi;
import com.study.api.GoodsApi;
import com.study.api.OrderApi;
import com.study.api.UserApi;
import com.study.common.constant.R;
import com.study.common.constant.ShopCode;
import com.study.common.exceptionHandler.StudyException;
import com.study.entity.*;
import com.study.order.service.TradeOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author studyStar
 * @since 2023-02-15
 */

@Slf4j
@RestController
@RequestMapping("/order")
public class TradeOrderController implements OrderApi {

    @Autowired
    private TradeOrderService orderService;

    @Resource
    private GoodsApi goodsApi;

    @Resource
    private UserApi userApi;

    @Resource
    private CouponApi couponApi;

    @Value("${mq.order.topic}")
    private String topic;

    @Value("${mq.order.tag.cancel}")
    private String tag;


    @Resource
    private RocketMQTemplate mqTemplate;

    @Override
    @GetMapping("{orderId}")
    public R findOne(@PathVariable("orderId") Long orderId) {
        if (orderId == null) {
            return R.fail(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        TradeOrder order = orderService.getById(orderId);
        if (order == null) {
            return R.fail();
        }
        return R.success(order);
    }

    // 下单流程
    @Override
    @PostMapping("confirmOrder")
    public R confirmOrder(@RequestBody TradeOrder order) {
        // 1.校验订单
        checkOrder(order);
        // 2.生成预订单
        savePreOrder(order);
        try {
            // 3.扣减库存
            reduceGoodsNum(order);
            // 4. 扣减优惠券
            updateCouponStatus(order);
            // 5.使用余额
            reduceMoneyPaid(order);
            // 模拟异常（以上操作都对数据库进行修改数据，就得需要回退）
            int i = 10 / 0;
            // 6.确认订单
            updateOrderStatus(order);
            // 7.返回成功状态
            return R.success();
        } catch (Exception e) {
            // 1.确认订单失败，发送消息
            // 订单Id 优惠券ID 用户ID 余额 商品ID 商品数量
            System.out.println("出现异常");
            MQEntity mqEntity = new MQEntity();
            mqEntity.setOrderId(order.getOrderId());
            mqEntity.setCouponId(order.getCouponId());
            mqEntity.setUserId(order.getUserId());
            mqEntity.setUserMoney(order.getMoneyPaid());
            mqEntity.setGoodsId(order.getGoodsId());
            mqEntity.setGoodsNum(order.getGoodsNumber());
            // 2.返回订单确认失败状态
            // 发送mq消息（参数：topic,tag,keys,body），keys是业务标识
            try {
                sendCancelOrder(topic, tag, mqEntity.getOrderId().toString(), JSON.toJSONString(mqEntity));
            } catch (Exception ex) {
                e.printStackTrace();
                return R.fail();
            }
        }
        return R.fail();
    }

    // 校验订单
    private void checkOrder(TradeOrder order) {
        R result;
        // 注意：由于openfeign返回的data数据是LinkedHashMap，则需要转换成指定类，需要以下对象
        ObjectMapper objectMapper = new ObjectMapper();
        // 1.校验订单是否存在
        if (order == null) {
            throw new StudyException(ShopCode.SHOP_ORDER_INVALID);
        }
        // 2.校验订单中商品是否存在
        result = goodsApi.findOne(order.getGoodsId());
        if (!result.getBaseCode().getSuccess()) {
            throw new StudyException(result.getBaseCode());
        }
        TradeGoods goods = objectMapper.convertValue(result.getData(), TradeGoods.class); // 转换对象
        // 3.校验下单用户是否存在
        result = userApi.findOne(order.getUserId());
        if (!result.getBaseCode().getSuccess()) {
            throw new StudyException(result.getBaseCode());
        }
        TradeUser user = objectMapper.convertValue(result.getData(), TradeUser.class);
        // 4.校验下单单价是否合法
        if (order.getGoodsPrice().compareTo(goods.getGoodsPrice()) != 0) {
            throw new StudyException(ShopCode.SHOP_GOODS_PRICE_INVALID);
        }
        // 5.校验订单商品数量是否合法
        if (order.getGoodsNumber() >= goods.getGoodsNumber()) {
            throw new StudyException(ShopCode.SHOP_GOODS_NUM_NOT_ENOUGH);
        }
        log.info("校验订单通过");
    }

    // 生成预订单
    private Long savePreOrder(TradeOrder order) {
        // 1.设置订单状态为不可见
        order.setOrderStatus(ShopCode.SHOP_ORDER_NO_CONFIRM.getCode());
        // 2.设置订单ID（直接用MybatisPlus的雪花算法）
        // 3.核算订单运费
        BigDecimal shippingFee = calculateShippingFee(order.getOrderAmount());
        if (order.getShippingFee().compareTo(shippingFee) != NGFEE_INVALI0) {
            throw new StudyException(ShopCode.SHOP_ORDER_SHIPPID);
        }
        // 4.核算订单总金额是否合法
        BigDecimal goodsAmount = order.getGoodsPrice().multiply(new BigDecimal(order.getGoodsNumber()));
        order.setGoodsAmount(goodsAmount.longValue());
        BigDecimal orderAmount = goodsAmount.add(shippingFee);
        if (orderAmount.compareTo(order.getOrderAmount()) != 0) {
            throw new StudyException(ShopCode.SHOP_ORDERAMOUNT_INVALID);
        }
        // 5.判断用户是否使用账户余额
        BigDecimal moneyPaid = order.getMoneyPaid();
        if (moneyPaid != null) {
            int r = moneyPaid.compareTo(BigDecimal.ZERO);
            // 余额小于0
            if (r == -1) {
                throw new StudyException(ShopCode.SHOP_MONEY_PAID_LESS_ZERO);
            }
        } else { // 不使用余额
            order.setMoneyPaid(BigDecimal.ZERO);
        }
        // 6.判断用户是否使用优惠券
        Long couponId = order.getCouponId();
        if (couponId != null) {
            // 6.1 判断优惠券是否存在
            R result = couponApi.findOne(order.getCouponId());
            if (!result.getBaseCode().getSuccess()) {
                throw new StudyException(result.getBaseCode());
            }
            // 6.2 判断优惠券是否被使用
            ObjectMapper objectMapper = new ObjectMapper();
            TradeCoupon coupon = objectMapper.convertValue(result.getData(), TradeCoupon.class);
            if (coupon.getIsUsed().equals(ShopCode.SHOP_COUPON_ISUSED.getCode())) {
                throw new StudyException(ShopCode.SHOP_COUPON_ISUSED);
            }
            order.setCouponPaid(coupon.getCouponPrice());
        } else {
            order.setCouponPaid(BigDecimal.ZERO);
        }
        // 7.核算订单支付金额（订单总金额-账户余额-优惠券）
        BigDecimal payAmount = order.getOrderAmount().subtract(order.getMoneyPaid()).subtract(order.getCouponPaid());
        order.setPayAmount(payAmount);
        // 8.设置下单时间
        order.setAddTime(new Date());
        // 9.保存订单时间
        orderService.save(order);
        // 10.返回订单ID
        return order.getOrderId();
    }

    // 核算运费
    private BigDecimal calculateShippingFee(BigDecimal orderAmount) {
        if (orderAmount.compareTo(new BigDecimal(100)) == 1) {
            return BigDecimal.ZERO;
        } else {
            return new BigDecimal(10);
        }
    }

    // 扣减库存
    private void reduceGoodsNum(TradeOrder order) {
        // 扣减库存需要的参数：订单Id，商品Id，商品数量
        Long orderId = order.getOrderId();
        Long goodsId = order.getGoodsId();
        Integer goodsNumber = order.getGoodsNumber();
        R result = goodsApi.reduceNum(orderId, goodsId, goodsNumber);
        if (!result.getBaseCode().getSuccess()) {
            throw new StudyException(result.getBaseCode());
        }
        log.info("订单：" + order.getOrderId() + "扣减库存成功");
    }

    // 使用优惠券
    private void updateCouponStatus(TradeOrder order) {
        Long couponId = order.getCouponId();
        ObjectMapper objectMapper = new ObjectMapper();
        if (couponId != null) {
            R result = null;
            result = couponApi.findOne(couponId);
            if (!result.getBaseCode().getSuccess()) {
                throw new StudyException(result.getBaseCode());
            }
            TradeCoupon coupon = objectMapper.convertValue(result.getData(), TradeCoupon.class);
            if (coupon.getIsUsed().equals(ShopCode.SHOP_COUPON_ISUSED.getCode())) {
                throw new StudyException(result.getBaseCode());
            }
            coupon.setOrderId(order.getOrderId());
            coupon.setUserId(order.getUserId());
            coupon.setUsedTime(new Date());
            coupon.setIsUsed(ShopCode.SHOP_COUPON_ISUSED.getCode());
            result = couponApi.updateCouponStatus(coupon);
            if (!result.getBaseCode().getSuccess()) {
                throw new StudyException(result.getBaseCode());
            }
            log.info("订单：" + order.getOrderId() + "成功使用优惠券");
        }
    }

    // 扣减余额
    private void reduceMoneyPaid(TradeOrder order) {
        if (order.getMoneyPaid() != null && order.getMoneyPaid().compareTo(BigDecimal.ZERO) == 1) {
            TradeUserMoneyLog userMoneyLog = new TradeUserMoneyLog();
            userMoneyLog.setMoneyLogType(ShopCode.SHOP_USER_MONEY_PAID.getCode());
            userMoneyLog.setUserId(order.getUserId());
            userMoneyLog.setOrderId(order.getOrderId());
            userMoneyLog.setUseMoney(order.getMoneyPaid());
            R result = userApi.updateMoneyPaid(userMoneyLog);
            if (!result.getBaseCode().getSuccess()) {
                throw new StudyException(result.getBaseCode());
            }
            log.info("订单" + order.getOrderId() + "扣减账户平台余额成功");
        }
    }

    // 确认订单
    private void updateOrderStatus(TradeOrder order) {
        order.setShippingStatus(ShopCode.SHOP_ORDER_CONFIRM.getCode());
        order.setPayStatus(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode());
        order.setConfirmTime(new Date());
        boolean success = orderService.updateById(order);
        if (!success) {
            throw new StudyException(ShopCode.SHOP_ORDER_CONFIRM_FAIL);
        }
        log.info("订单" + order.getOrderId() + "确认成功");
    }

    // 发送订单确认失败消息
    private void sendCancelOrder(String topic, String tag, String keys, String body) throws Exception {
        Message message = new Message(topic, tag, keys, body.getBytes());
        mqTemplate.getProducer().send(message);
    }
}
