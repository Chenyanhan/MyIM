package com.cyh.MQ;

import com.alibaba.fastjson.JSON;
import com.cyh.util.SpringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import com.cyh.entity.UserInfo;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Component
public class MactchConsumer implements CommandLineRunner
{

    @Override
    public void run(String... args) throws Exception
    {
        // 实例化消费者
        DefaultMQPushConsumer remaleConsumer = new DefaultMQPushConsumer("remaleConsumer");
        this.startMatchConsumer(remaleConsumer);
    }


    @Autowired
    @Qualifier("defaultRedisTemplate")
    private RedisTemplate<String,Object> redisTemplate;

    public void startMatchConsumer(DefaultMQPushConsumer consumer) throws MQClientException
    {
        // 设置NameServer的地址
//        consumer.setNamesrvAddr("localhost:9876");
        consumer.setNamesrvAddr("47.95.217.141:9876");

        // 订阅一个或者多个Topic，以及Tag来过滤需要消费的消息
        consumer.subscribe("Topic_Test_4", "*");
        // 注册回调实现类来处理从broker拉取回来的消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

                for (int i = 0; i < msgs.size(); i++)
                {
                    MessageExt messageExt = msgs.get(i);
                    UserInfo userInfo = JSON.parseObject(messageExt.getBody(), UserInfo.class);
                    if (userInfo != null)
                    {

                        try
                        {
                            SpringUtils.getBean(messageExt.getTags(), Queue.class);
                        }
                        catch (Exception e){
//                            Queue maleQueue = new LinkedList();
//                            Queue remaleQueue = new LinkedList();
                            BlockingQueue maleQueue = new LinkedBlockingQueue();
                            BlockingQueue remaleQueue = new LinkedBlockingQueue();

                            SpringUtils.registerBean(messageExt.getTags(),maleQueue);
                            SpringUtils.registerBean("-" + messageExt.getTags(),remaleQueue);
                            MatchConsum matchConsum = new MatchConsum(redisTemplate);
                            matchConsum.setMaleQueueName(messageExt.getTags());
                            matchConsum.setRemaleQueueName("-" + messageExt.getTags());
                            Thread thread = new Thread(matchConsum);
                            thread.start();
                        }
                        Queue queue;
                        if (userInfo.getSex() == 1){
                            queue = SpringUtils.getBean(messageExt.getTags(),Queue.class);//1=男 0=女
                        }
                        else {
                            queue = SpringUtils.getBean("-" + messageExt.getTags(),Queue.class);
                        }
                        queue.offer(userInfo);
                        redisTemplate.opsForValue().set(userInfo.getId(),userInfo.getId(),15, TimeUnit.SECONDS);
                    }
                }

                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
                // 标记该消息已经被成功消费
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        // 启动消费者实例
        consumer.start();

        System.out.printf("Consumer Started.%n");
    }


}
