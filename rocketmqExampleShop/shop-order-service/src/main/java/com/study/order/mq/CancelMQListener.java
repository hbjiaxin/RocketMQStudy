package com.study.order.mq;

import com.alibaba.fastjson.JSON;
import com.study.common.constant.ShopCode;
import com.study.entity.MQEntity;
import com.study.entity.TradeOrder;
import com.study.order.service.TradeOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
// 设置成广播模式，这条数据每个模块都需要（目前没考虑微服务的集群架构，如一个微服务有集群，可能广播模式不适合）
@RocketMQMessageListener(topic = "${mq.order.topic}", consumerGroup = "${mq.order.consumer.group.name}", messageModel = MessageModel.BROADCASTING)
public class CancelMQListener implements RocketMQListener<MessageExt> {

    @Resource
    private TradeOrderService orderService;

    // messageExt中有个MsgId
    @Override
    public void onMessage(MessageExt msg) {
        String body = null;
        try {
            // 1.解析信息
            body = new String(msg.getBody(), "UTF-8");
            log.info("接收消息成功");
            MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
            Long orderId = mqEntity.getOrderId();
            if (orderId != null) {
                // 2.修改订单状态（我的想法：修改订单状态时，需要查询对应的订单创建状态（一对一的关系），防止取消撤销订单后后台撤销订单的操作？？？？
                TradeOrder order = orderService.getById(orderId);
                order.setOrderStatus(ShopCode.SHOP_ORDER_CANCEL.getCode());
                orderService.updateById(order);
            }
            log.info("修改订单状态成功");
        } catch (Exception e) {
            log.info("修改订单状态失败");
        }
    }
}

