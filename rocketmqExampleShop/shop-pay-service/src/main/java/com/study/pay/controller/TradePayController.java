package com.study.pay.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.api.PayApi;
import com.study.common.constant.R;
import com.study.common.constant.ShopCode;
import com.study.common.exceptionHandler.StudyException;
import com.study.entity.TradeMqProducerTemp;
import com.study.entity.TradePay;
import com.study.pay.service.TradeMqProducerTempService;
import com.study.pay.service.TradePayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/pay")
public class TradePayController implements PayApi {

    @Value("${rocketmq.producer.group}")
    private String groupName;

    @Value("${mq.topic}")
    private String topic;

    @Value("${mq.tag}")
    private String tag;

    @Resource
    TradeMqProducerTempService mqProducerTempService;

    @Resource
    RocketMQTemplate mqTemplate;

    @Resource
    private TradePayService payService;

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @PostMapping
    @Override
    public R createPayment(@RequestBody TradePay pay) {
        ObjectMapper objectMapper = new ObjectMapper();
        if (pay == null
                || pay.getOrderId() == null
                || pay.getPayAmount() == null
                || pay.getPayAmount().compareTo(BigDecimal.ZERO) < 0) {
            return R.fail(ShopCode.SHOP_REQUEST_PARAMETER_VALID);
        }
        // 1.判断订单支付状态
        LambdaQueryWrapper<TradePay> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TradePay::getOrderId, pay.getOrderId())
                .eq(TradePay::getIsPaid, ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
        long count = payService.count(wrapper);
        if (count > 0) {
            return R.fail(ShopCode.SHOP_PAYMENT_IS_PAID);
        }
        // 2.设置订单的状态为未支付
        pay.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_NO_PAY.getCode());
        // 3.保存支付订单
        boolean success = payService.save(pay);
        if (success) {
            log.info("支付订单创建成功");
            return R.success(pay.getPayId());
        } else {
            log.info("支付订单创建失败");
            return R.fail();
        }
    }

    // 第三方支付平台在用户支付后回调的函数
    @PostMapping("/callback")
    R callbackPayment(@RequestBody TradePay pay) {
        log.info("支付回调");
        // 1.判断用户支付状态
        if (!pay.getIsPaid().equals(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode())) {
            return R.fail(ShopCode.SHOP_PAYMENT_PAY_ERROR);
        }
        // 2.更新支付订单状态为已支付
        Long payId = pay.getPayId();
        TradePay payData = payService.getById(payId);
        if (payData == null) {
            return R.fail(ShopCode.SHOP_PAYMENT_NOT_FOUND);
        }
        payData.setIsPaid(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY.getCode());
        boolean success = payService.updateById(payData);
        log.info("支付订单状态改为已支付");
        if (success) {
            // 3.创建支付成功消息（通知其他微服务）
            TradeMqProducerTemp producerTemp = new TradeMqProducerTemp();
            producerTemp.setGroupName(groupName);
            producerTemp.setMsgTag(tag);
            // producerTemp.setId(xxx); // id会通过mybatisPlus自动生成
            producerTemp.setMsgTopic(topic);
            producerTemp.setMsgKey(String.valueOf(pay.getOrderId()));
            producerTemp.setMsgBody(JSON.toJSONString(payData));
            producerTemp.setCreateTime(new Date());
            producerTemp.setMsgStatus(ShopCode.SHOP_DATA_MESSAGE_NOT_SEND.getCode());
            // 4.将消息持久化到数据库，保证消息发送成功（也可异步写个定时任务扫描未发送成功的消息重新发送消息）
            mqProducerTempService.save(producerTemp);
            log.info("将支付成功消息持久化到数据库，还未发送消息给MQ");
            // 在线程池中进行处理，异步操作
            threadPoolTaskExecutor.submit(() -> {
                // 5.发送消息到MQ
                SendResult sendResult = null;
                try {
                    sendResult = sendMessage(topic, tag, producerTemp.getMsgKey(), producerTemp.getMsgBody());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
                    log.info("消息发送成功");
                    // 6.等待发送结果，如果MQ接收到消息，就删除数据库消息
                    producerTemp.setMsgStatus(ShopCode.SHOP_DATA_MESSAGE_SEND.getCode());
                    mqProducerTempService.updateById(producerTemp);
                    log.info("持久化到数据库的消息修改状态为发送成功");
                }
            });

        } else {
            return R.fail();
        }
        return R.success(ShopCode.SHOP_ORDER_PAY_STATUS_IS_PAY);
    }

    // 发送支付成功消息
    private SendResult sendMessage(String topic, String tag, String key, String body) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        if (!StringUtils.hasLength(topic)) {
            throw new StudyException(ShopCode.SHOP_MQ_TOPIC_IS_EMPTY);
        }
        if (!StringUtils.hasLength(body)) {
            throw new StudyException(ShopCode.SHOP_MQ_MESSAGE_BODY_IS_EMPTY);
        }
        Message msg = new Message(topic, tag, key, body.getBytes());
        SendResult sendResult = mqTemplate.getProducer().send(msg);
        return sendResult;
    }
}
