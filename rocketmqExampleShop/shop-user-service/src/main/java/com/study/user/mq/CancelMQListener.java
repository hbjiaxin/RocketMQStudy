package com.study.user.mq;

import com.alibaba.fastjson.JSON;
import com.study.common.constant.R;
import com.study.common.constant.ShopCode;
import com.study.entity.MQEntity;
import com.study.entity.TradeUserMoneyLog;
import com.study.user.controller.TradeUserController;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Slf4j
@Component
// 设置成广播模式，这条数据每个模块都需要（目前没考虑微服务的集群架构，如一个微服务有集群，可能广播模式不适合）
@RocketMQMessageListener(topic = "${mq.order.topic}", consumerGroup = "${mq.order.consumer.group.name}", messageModel = MessageModel.BROADCASTING)
public class CancelMQListener implements RocketMQListener<MessageExt> {

    @Resource
    private TradeUserController userController;

    // messageExt中有个MsgId
    @Override
    public void onMessage(MessageExt msg) {
        String body = null;
        try {
            // 1.解析消息
            body = new String(msg.getBody(), "UTF-8");
            MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
            if (mqEntity.getUserMoney() != null && mqEntity.getUserMoney().compareTo(BigDecimal.ZERO) > 0) {
                Long userId = mqEntity.getUserId();
                BigDecimal userMoney = mqEntity.getUserMoney();
                Long orderId = mqEntity.getOrderId();
                // 2.调用业务层进行余额修改（之前写在控制层了，直接用）
                TradeUserMoneyLog userMoneyLog = new TradeUserMoneyLog();
                userMoneyLog.setMoneyLogType(ShopCode.SHOP_USER_MONEY_REFUND.getCode());
                userMoneyLog.setUserId(userId);
                userMoneyLog.setUseMoney(userMoney);
                userMoneyLog.setOrderId(orderId);
                userController.updateMoneyPaid(userMoneyLog);
            }
            log.info("余额退款成功");
        } catch (Exception e) {
            log.info("余额退款失败");
        }
    }
}

