package com.study.mq.base.transaction;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.security.ProtectionDomain;
import java.util.concurrent.TimeUnit;

public class Producer {
    public static void main(String[] args) throws Exception {
        TransactionMQProducer producer = new TransactionMQProducer("group5");
        producer.setNamesrvAddr("192.168.200.100:9876;192.168.200.102:9876");
        // 重点：添加事务监听器（也可写一个实现TransactionListener的监听器类）
        producer.setTransactionListener(new TransactionListener() {
            // 在该方法中执行本地事务
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object o) {
                if (message != null) {
                    switch (message.getTags()) {
                        case "TagA":
                            return LocalTransactionState.COMMIT_MESSAGE;
                        case "TagB":
                            return LocalTransactionState.ROLLBACK_MESSAGE;
                        default:
                            return LocalTransactionState.UNKNOW;
                    }
                }
                return LocalTransactionState.UNKNOW;
            }

            // 该方法用于MQ进行消息事务状态回查
            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                System.out.println("消息的Tag：" + messageExt.getTags());
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });
        producer.start();
        String[] tags = {"TagA", "TagB", "TagC"};
        for (int i = 0; i < 3; i++) {
            Message msg = new Message("TransactionTopic", tags[i], ("事务消息" + i).getBytes());
            TransactionSendResult result = producer.sendMessageInTransaction(msg, null);// 第二个参数是执行本地事务的回调参数
            System.out.println(result);
            TimeUnit.SECONDS.sleep(2);
        }
//        producer.shutdown();
    }
}

