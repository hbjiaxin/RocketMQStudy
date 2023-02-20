package com.study.consumer.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

// 注册的监听器来接收信息
@Slf4j
@Component
@RocketMQMessageListener(topic = "springboot", consumerGroup = "${rocketmq.consumer.group}")
// 其他参数：MessageModel：集群消费、广播消费，ConsumeMode：是否顺序消费，默认否
public class MyConsumerListener implements RocketMQListener<String> {

    @Override
    public void onMessage(String s) {
        System.out.println("123");
        System.out.println("接收到消息：" + s);
    }
}

