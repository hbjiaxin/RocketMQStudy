package com.study.mq.base.batch;

import org.apache.rocketmq.common.message.Message;

import java.util.*;

// 批量消息的大小不能超过 1MiB（否则需要自行分割），其次同一批 batch 中 topic 必须相同
public class MessageSplitter implements Iterator<List<Message>> {
    private final int SIZE_LIMIT = 1024 * 1024 * 1;
    private final List<Message> msgs;
    private int currIndex;

    public MessageSplitter(List<Message> msgs) {
        this.msgs = msgs;
    }

    @Override
    public boolean hasNext() {
        return currIndex < msgs.size();
    }

    @Override
    public List<Message> next() {
        int nextIndex = currIndex;
        int total = 0;
        for (; nextIndex < msgs.size(); nextIndex++) {
            // 计算msg的长度
            Message msg = msgs.get(nextIndex);
            int msgSize = msg.getBody().length + msg.getTopic().length() + 20; // 包含日志的开销20字节
            Map<String, String> properties = msg.getProperties();
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                msgSize += entry.getKey().length() + entry.getValue().length();
            }
            // 判断单条消息是否大于限制
            if (msgSize > SIZE_LIMIT) {
                // 若该单条消息为开始第一条，则忽略该条消息，currIndex + 1
                if (nextIndex - currIndex == 0) {
                    nextIndex++;
                }
                break;
            }
            if (total + msgSize > SIZE_LIMIT) {
                break;
            } else {
                total += msgSize;
            }
        }
        List<Message> messages = msgs.subList(currIndex, nextIndex);
        nextIndex = currIndex;
        return messages;
    }

    public void test() {
        ArrayList<Message> messages = new ArrayList<>();
        MessageSplitter splitter = new MessageSplitter(messages);
        while (splitter.hasNext()) {
            List<Message> list = splitter.next();
            // 业务逻辑
            // eg: producer.send(list);
        }
    }
}
