package com.study.mq.base.filter.tag;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import java.util.concurrent.TimeUnit;

public class Producer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        producer.setNamesrvAddr("192.168.200.100:9876;192.168.200.102:9876");
        producer.start();
        for (int i = 0; i < 10; i++) {
            Message msg1 = new Message("filterTagTopic", "tag1", ("tag1过滤消息" + i).getBytes());
            Message msg2 = new Message("filterTagTopic", "tag2", ("tag2过滤消息" + i).getBytes());
            producer.send(msg1);
            producer.send(msg2);
            TimeUnit.SECONDS.sleep(1);
        }
        producer.shutdown();
    }
}

