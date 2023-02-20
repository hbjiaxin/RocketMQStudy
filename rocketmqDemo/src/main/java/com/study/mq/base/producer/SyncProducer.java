package com.study.mq.base.producer;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.concurrent.TimeUnit;

/**
 * 发送同步消息
 * 用于可靠性同步发送，如：重要消息通知、短信通知
 */
public class SyncProducer {

    public static void main(String[] args) throws MQClientException, MQBrokerException, RemotingException, InterruptedException {
        // 1.创建消费生产者producer，并指定生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        // 2.指定NameServer地址
        producer.setNamesrvAddr("192.168.200.100:9876;192.168.200.102:9876");
        // 3.启动producer
        producer.start();
        for (int i = 0; i < 100; i++) {
            // 4.创建消息对象，指定主题Topic、Tag和消息体
            /**
             参数一：消息主题Topic
             参数二：消息Tag
             参数三：消息内容
             */
            Message msg = new Message("base", "tag1", ("hello world" + i).getBytes());
            // 5.发送信息（同步的体现：得到发送成功的返回结果才继续执行）
            SendResult result = producer.send(msg);
            // 发送状态
            SendStatus status = result.getSendStatus();
            // 消息ID
            String msgId = result.getMsgId();
            // 消息接收队列ID
            int queueId = result.getMessageQueue().getQueueId();
            System.out.println(result);
            TimeUnit.SECONDS.sleep(1);
        }
        // 6.关闭生产者producer
        producer.shutdown();
    }
}

