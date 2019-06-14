package com.tiza.sih.rp.support.util;

import com.tiza.sih.rp.support.model.KafkaMsg;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Description: KafkaUtil
 * Author: DIYILIU
 * Update: 2019-04-24 14:26
 */

public class KafkaUtil {
    private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    private final static Queue<KafkaMsg> pool = new ConcurrentLinkedQueue();

    private String topic;
    private String brokers;
    private String serializer;

    private Producer producer;

    public KafkaUtil() {

    }

    public void init() {
        Properties props = new Properties();
        props.put("metadata.broker.list", brokers);
        // 消息传递到broker时的序列化方式
        props.put("key.serializer.class", "kafka.serializer.StringEncoder");
        props.put("serializer.class", serializer);
        // 是否获取反馈
        props.put("request.required.acks", "1");
        // 内部发送数据是异步还是同步 sync：同步(来一条数据提交一条不缓存), 默认 async：异步
        props.put("producer.type", "async");
        // 重试次数
        props.put("message.send.max.retries", "3");
        producer = new Producer(new ProducerConfig(props));

        // 延时 1s 执行一次
        scheduledExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                while (!pool.isEmpty()) {
                    KafkaMsg data = pool.poll();
                    producer.send(new KeyedMessage(topic, data.getKey(), data.getValue()));
                }
            }
        }, 10, 1, TimeUnit.SECONDS);
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setBrokers(String brokers) {
        this.brokers = brokers;
    }

    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }

    public static void send(KafkaMsg msg) {
        pool.add(msg);
    }
}
