package com.study.order.mq;

import com.alibaba.fastjson.JSON;
import com.study.common.constant.ShopCode;
import com.study.entity.TradeOrder;
import com.study.entity.TradePay;
import com.study.order.service.TradeOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Component
@RocketMQMessageListener(topic = "${mq.pay.topic}", consumerGroup = "${mq.pay.consumer.group.name}", messageModel = MessageModel.BROADCASTING)
public class PaymentListener implements RocketMQListener<MessageExt> {

    @Resource
    private TradeOrderService orderService;

    @Override
    public void onMessage(MessageExt msg) {
        log.info("接收到支付成功消息");
        String body = null;
        try {
            // 1.解析消息内容
            body = new String(msg.getBody(), "UTF-8");
            TradePay pay = JSON.parseObject(body, TradePay.class);
            // 2.根据订单id查询订单对象
            TradeOrder order = orderService.getById(pay.getOrderId());
            // 3.更改订单支付状态为已支付
            order.setPayStatus(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
            order.setPayTime(new Date());
            // 4.更新订单数据到数据库
            orderService.updateById(order);
            log.info("更改订单支付状态为已支付");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

