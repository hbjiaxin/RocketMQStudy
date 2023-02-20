package com.study.mq.base.delay;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

public class Producer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        producer.setNamesrvAddr("192.168.200.100:9876;192.168.200.102:9876");
        producer.start();
        for (int i = 0; i < 10; i++) {
            Message msg = new Message("delayTopic", "tag1", ("延迟消息" + i).getBytes());
            // 设置延迟等级（等级与时间对应关系：https://rocketmq.apache.org/zh/docs/4.x/producer/04message3）
            msg.setDelayTimeLevel(2);
            SendResult result = producer.send(msg);
            System.out.println(result);
        }
        producer.shutdown();
    }
}

