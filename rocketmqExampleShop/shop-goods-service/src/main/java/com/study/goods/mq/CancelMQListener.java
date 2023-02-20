package com.study.goods.mq;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.study.common.constant.ShopCode;
import com.study.entity.MQEntity;
import com.study.entity.TradeGoods;
import com.study.entity.TradeMqConsumerLog;
import com.study.goods.service.TradeGoodsNumberLogService;
import com.study.goods.service.TradeGoodsService;
import com.study.goods.service.TradeMqConsumerLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.MQConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Queue;

@Slf4j
@Component
// 设置成广播模式，这条数据每个模块都需要（目前没考虑微服务的集群架构，如一个微服务有集群，可能广播模式不适合）
@RocketMQMessageListener(topic = "${mq.order.topic}", consumerGroup = "${mq.order.consumer.group.name}", messageModel = MessageModel.BROADCASTING)
public class CancelMQListener implements RocketMQListener<MessageExt> {

    @Value("${mq.order.consumer.group.name}")
    private String groupName;

    @Resource
    private TradeGoodsService goodsService;

    @Resource
    private TradeMqConsumerLogService consumerLogService;

    @Resource
    private TradeGoodsNumberLogService goodsNumberLogService;

    // messageExt中有个MsgId
    @Override
    public void onMessage(MessageExt msg) {
        String tag = null;
        String msgId = null;
        String key = null;
        String body = null;
        LambdaQueryWrapper<TradeMqConsumerLog> logWrapper = null;
        try {
            // 1.解析消息内容
            tag = msg.getTags();
            msgId = msg.getMsgId();
            key = msg.getKeys();
            body = new String(msg.getBody(), "UTF-8");
            log.info("接收消息成功");
            // 2.查询消息消费情况，防止重复消费（消息的幂等性）
            logWrapper = new LambdaQueryWrapper<>();
            // 主键：tag,key(orderId),groupName
            logWrapper.eq(TradeMqConsumerLog::getMsgTag, tag)
                    .eq(TradeMqConsumerLog::getMsgKey, key)
                    .eq(TradeMqConsumerLog::getGroupName, groupName);
            TradeMqConsumerLog mqConsumerLog = consumerLogService.getOne(logWrapper);
            // 3.查询消息消费记录
            if (mqConsumerLog != null) {
                // 3.1如果消费过
                //  获取消息的处理状态
                Integer status = mqConsumerLog.getConsumerStatus();
                // 消息正在处理(0)、处理成功（1），直接返回
                if (ShopCode.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode().equals(status)) {
                    log.info("消息" + msgId + "正在处理");
                    return;
                }
                if (ShopCode.SHOP_MQ_MESSAGE_STATUS_SUCCESS.getCode().equals(status)) {
                    log.info("消息" + msgId + "已经处理完成");
                    return;
                }
                if (ShopCode.SHOP_MQ_MESSAGE_STATUS_FAIL.getCode().equals(status)) {
                    // 获取消息处理次数
                    Integer times = mqConsumerLog.getConsumerTimes();
                    if (times >= 3) {
                        log.info("消息" + msgId + "，消息已处理超过3次，不能继续处理");
                        return;
                    } else {
                        // 用乐观锁的形式来修改状态为正在处理状态（用修改次数当version），处理消息用while更好？？？
                        logWrapper.eq(TradeMqConsumerLog::getConsumerTimes, times);
                        mqConsumerLog.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode());
                        boolean result = consumerLogService.update(mqConsumerLog, logWrapper);
                        if (!result) {
                            // 未修改成功，其他线程并发修改
                            log.info("并发修改，稍后处理");
                            return;
                        }
                    }
                }
            } else {
                // 3.2如果没有消费过
                TradeMqConsumerLog consumerLog = new TradeMqConsumerLog();
                consumerLog.setMsgTag(tag);
                consumerLog.setGroupName(groupName);
                consumerLog.setMsgId(msgId);
                consumerLog.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode());
                consumerLog.setMsgBody(body);
                consumerLog.setMsgKey(key);
                consumerLog.setConsumerTimes(0);
                mqConsumerLog = consumerLog;
                // 将消息处理添加到数据库
                boolean result = consumerLogService.save(consumerLog);
                if (!result) {
                    // 未修改成功，其他线程并发修改
                    log.info("并发修改，稍后处理");
                    return;
                }
            }
            // 4.回退库存
            MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
            Long goodsId = mqEntity.getGoodsId();
            Integer goodsNum = mqEntity.getGoodsNum();
            TradeGoods goods = goodsService.getById(goodsId);
            goods.setGoodsNumber(goods.getGoodsNumber() + goodsNum);
            goodsService.updateById(goods);
            // 记录库存操作日志

            // 5.将消息的处理状态改为成功
            logWrapper.eq(TradeMqConsumerLog::getConsumerTimes, mqConsumerLog.getConsumerTimes());
            mqConsumerLog.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_SUCCESS.getCode());
            mqConsumerLog.setConsumerTimestamp(new Date());
            consumerLogService.update(mqConsumerLog, logWrapper);
            log.info("回退库存成功");
        } catch (Exception e) {
            e.printStackTrace();
            logWrapper.eq(TradeMqConsumerLog::getMsgTag, tag)
                    .eq(TradeMqConsumerLog::getMsgKey, key)
                    .eq(TradeMqConsumerLog::getGroupName, groupName);
            TradeMqConsumerLog consumerLog = consumerLogService.getOne(logWrapper);
            if (consumerLog == null) { // 数据库没有记录
                consumerLog = new TradeMqConsumerLog();
                consumerLog.setMsgTag(tag);
                consumerLog.setGroupName(groupName);
                consumerLog.setMsgId(msgId);
                consumerLog.setMsgKey(key);
                consumerLog.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_FAIL.getCode());
                consumerLog.setMsgBody(body);
                consumerLog.setConsumerTimes(1);
                consumerLogService.save(consumerLog);
            } else {
                consumerLog.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_FAIL.getCode());
                consumerLog.setConsumerTimes(consumerLog.getConsumerTimes() + 1);
                logWrapper.eq(TradeMqConsumerLog::getConsumerTimes, consumerLog.getConsumerTimes());
                consumerLogService.update(consumerLog, logWrapper);
            }
        }
    }
}

