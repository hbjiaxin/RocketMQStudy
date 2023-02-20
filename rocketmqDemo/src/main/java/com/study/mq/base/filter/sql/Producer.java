package com.study.mq.base.filter.sql;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.awt.event.MouseWheelEvent;
import java.util.concurrent.TimeUnit;

public class Producer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        producer.setNamesrvAddr("192.168.200.100:9876;192.168.200.102:9876");
        producer.start();
        for (int i = 0; i < 10; i++) {
            Message msg = new Message("filterSQLTopic", "tag1", ("sql过滤消息" + i).getBytes());
            // 消费者会根据property的value值过滤
            msg.putUserProperty("i", String.valueOf(i));
            SendResult result = producer.send(msg);
            System.out.println(result);
            TimeUnit.SECONDS.sleep(1);
        }
        producer.shutdown();
    }
}

