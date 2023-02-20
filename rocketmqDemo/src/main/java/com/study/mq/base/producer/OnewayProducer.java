package com.study.mq.base.producer;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.util.concurrent.TimeUnit;

/**
 * 单向发送消息
 * 不用特别关心发送结果的场景，如日志发送。
 */
public class OnewayProducer {
    public static void main(String[] args) throws Exception {
        // 1.创建producer
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        // 2.绑定NameServer
        producer.setNamesrvAddr("192.168.200.100:9876;192.192.168.200.102:9876");
        // 3.启动producer
        producer.start();
        for (int i = 0; i < 10; i++) {
            // 4.创建消息对象
            Message msg = new Message("base", "tag3", ("单向发送消息" + i).getBytes());
            // 5.发送单向消息，没有返回值
            producer.sendOneway(msg);
            TimeUnit.SECONDS.sleep(1);
        }
        // 6.关闭producer
        producer.shutdown();
    }
}

