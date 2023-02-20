package com.study.mq.base.producer;

import ch.qos.logback.core.util.TimeUtil;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.util.concurrent.TimeUnit;

/**
 * 发送异步消息
 * 可靠性没有同步消息高，用在对相应时间敏感的业务场景，即发送端不能容忍长时间等待Broker响应
 */
public class AsyncProducer {

    public static void main(String[] args) throws Exception {
        // 1. 创建消息生产者producer，并制定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        // 2. 指定NameServer地址
        producer.setNamesrvAddr("192.168.200.100:9876;192.168.200.102:9876");
        // 3. 启动producer
        producer.start();
        for (int i = 0; i < 10; i++) {
            // 4. 创建消息
            Message msg = new Message("base", "tag2", ("异步消息" + i).getBytes());
            // 5. 发送异步消息
            producer.send(msg, new SendCallback() {
                // 发送成功回调函数
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.println("发送成功" + sendResult);
                }

                // 发送失败回调函数
                @Override
                public void onException(Throwable throwable) {
                    System.out.println("发送异常" + throwable);
                }
            });
            System.out.println("已经发送异步消息" + i);
            TimeUnit.SECONDS.sleep(1);
        }

        // 6. 关闭producer
        producer.shutdown();
    }
}

