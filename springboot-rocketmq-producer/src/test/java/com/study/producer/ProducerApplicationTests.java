package com.study.producer;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProducerApplicationTests {

    @Autowired
    private RocketMQTemplate mqTemplate;

    @Test
    public void testSendMessage() {
        // 主题+内容
        mqTemplate.convertAndSend("springboot","helloWorld");
    }

}
