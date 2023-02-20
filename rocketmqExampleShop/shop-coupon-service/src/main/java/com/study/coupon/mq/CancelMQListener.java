package com.study.coupon.mq;

import com.alibaba.fastjson.JSON;
import com.study.common.constant.ShopCode;
import com.study.coupon.service.TradeCouponService;
import com.study.entity.MQEntity;
import com.study.entity.TradeCoupon;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Slf4j
@Component
// 设置成广播模式，这条数据每个模块都需要（目前没考虑微服务的集群架构，如一个微服务有集群，可能广播模式不适合）
@RocketMQMessageListener(topic = "${mq.order.topic}", consumerGroup = "${mq.order.consumer.group.name}", messageModel = MessageModel.BROADCASTING)
public class CancelMQListener implements RocketMQListener<MessageExt> {

    @Autowired
    private TradeCouponService couponService;

    // messageExt中有个MsgId
    @Override
    public void onMessage(MessageExt msg) {
        // 1. 解析消息内容
        String body = null;
        try {
            body = new String(msg.getBody(), "UTF-8");
            MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
            log.info("接收到消息");
            Long couponId = mqEntity.getCouponId();
            if (couponId != null) {
                // 2.查询消费券信息（消费券撤回消息重复消费，不影响结果，和库存回退情况不同）
                TradeCoupon coupon = couponService.getById(couponId);
                // 3.更改优惠券状态
                coupon.setIsUsed(ShopCode.SHOP_COUPON_UNUSED.getCode());
                coupon.setOrderId(null);
                coupon.setUsedTime(null);
                couponService.updateById(coupon);
            }
            log.info("回退优惠券成功");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.info("回退优惠券失败");
        }
    }
}
