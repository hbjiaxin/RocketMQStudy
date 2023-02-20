package com.study.mq.base.order;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

public class Consumer {
    public static void main(String[] args) throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group1");
        consumer.setNamesrvAddr("192.168.200.100:9876;192.168.200.102:9876");
        consumer.subscribe("orderTopic", "*");
        // 注册消息监听器MessageListenerOrderly
        consumer.registerMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list, ConsumeOrderlyContext consumeOrderlyContext) {
                for (MessageExt msg : list) {
                    String body = new String(msg.getBody());
                    // 可以发现是用同一个线程来处理接收一个订单id的消息
                    // 每个queue有唯一的consume线程来消费, 订单对每个queue(分区)有序
                    System.out.println("【线程" + Thread.currentThread().getName() + "】" + body);
                }
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });
        consumer.start();
        System.out.println("消费启动");
    }
}

