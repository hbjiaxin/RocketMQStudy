package com.study.mq.base.batch;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.util.ArrayList;

public class Producer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        producer.setNamesrvAddr("192.168.200.100:9876;192.168.200.102:9876");
        producer.start();
        ArrayList<Message> msgs = new ArrayList<>();
        msgs.add(new Message("batch", "tag1", ("批量消息1").getBytes()));
        msgs.add(new Message("batch", "tag1", ("批量消息2").getBytes()));
        msgs.add(new Message("batch", "tag1", ("批量消息3").getBytes()));
        SendResult result = producer.send(msgs);
        System.out.println(result);
        producer.shutdown();
    }


}

