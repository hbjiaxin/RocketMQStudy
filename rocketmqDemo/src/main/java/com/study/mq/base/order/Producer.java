package com.study.mq.base.order;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;

public class Producer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("group1");
        producer.setNamesrvAddr("192.168.200.100:9876;192.168.200.102:9876");
        List<OrderStep> orders = OrderStep.buildOrders(); // 构建消息集合
        producer.start();
        for (int i = 0; i < orders.size(); i++) {
            // 同一个订单ID发送到同一个queue中
            String body = orders.get(i) + "";
            // topic，tags，keys，body
            Message msg = new Message("orderTopic", "Order", "Order" + i, body.getBytes());
            /**
             * 参数一：消息对象
             * 参数二：消息队列的选择器
             * 参数三：选择队列的业务标识
             */
            SendResult result = producer.send(msg, new MessageQueueSelector() {
                /**
                 * 参数一：队列集合
                 * 参数二：消息对象(msg)
                 * 参数三：业务标识的参数(orders.get(i).getId())，分区关键字
                 *  生产环境中建议选择最细粒度的分区键进行拆分，例如，将订单ID、用户ID作为分区键关键字，
                 *  可实现同一终端用户的消息按照顺序处理，不同用户的消息无需保证顺序
                 */
                @Override
                public MessageQueue select(List<MessageQueue> list, Message message, Object o) {

                    long orderId = (long) o;
                    long index = orderId % list.size();
                    return list.get((int) index);
                }
            }, orders.get(i).getOrderId());
            System.out.println(result);
        }
        producer.shutdown();
    }
}

