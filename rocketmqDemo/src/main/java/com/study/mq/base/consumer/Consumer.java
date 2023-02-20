package com.study.mq.base.consumer;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;

/**
 * 消费者
 */
public class Consumer {
    public static void main(String[] args) throws Exception {
        // 1.创建消者Consumer，制定消费者组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("group1");
        // 2.绑定NameServer
        consumer.setNamesrvAddr("192.168.200.100:9876;192.168.200.102:9876");
        // 3.订阅Topic，指定Tag增加消息过滤的条件，"*"为接收所有tag的消息，"xx || xx"
        consumer.subscribe("base", "tag1");
        // 3.1设置消费模式
//        consumer.setMessageModel(MessageModel.CLUSTERING); // 默认，负载均衡
//        consumer.setMessageModel(MessageModel.BROADCASTING); // 广播
        // 4.注册消息监听器，设置回调函数，处理消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            // 处理从Broker接收的消息内容
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                for (MessageExt msg : msgs) {
                    System.out.println(new String(msg.getBody()));
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        // 5.启动消费者Consumer
        consumer.start();
    }
}

