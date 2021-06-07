package com.cyh.service.impl;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.cyh.util.SpringUtils;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Service
@Scope("prototype")
public class HandleService  implements ChannelAwareMessageListener
{
    @Autowired
    @Qualifier("defaultRedisTemplate")
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private CachingConnectionFactory connectionFactory;
    /**
     * @param
     *  、处理成功，这种时候用basicAck确认消息；
     * 2、可重试的处理失败，这时候用basicNack将消息重新入列；
     * 3、不可重试的处理失败，这时候使用basicNack将消息丢弃。
     *
     *  basicNack(long deliveryTag, boolean multiple, boolean requeue)
     *   deliveryTag:该消息的index
     *  multiple：是否批量.true:将一次性拒绝所有小于deliveryTag的消息。
     * requeue：被拒绝的是否重新入队列
     */
    @Override
    public synchronized void onMessage(Message message, Channel channel) throws Exception
    {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        byte[] body = message.getBody();
        String mapStr = new String(body);
        //获取监听队列容器
        SimpleMessageListenerContainer container = SpringUtils.getBean("mqMessageContainer",SimpleMessageListenerContainer.class);
        try
        {
            //保证消息不会被重复消费
            Boolean hasKey = redisTemplate.hasKey("messageId");
            if (hasKey)
            {
                HashMap<String,Object> map = JSONObject.parseObject(mapStr, (Type) Map.class);
                //获取生产者的信息
                String fromUserId = map.get("userId").toString();
                String fromQueueName = map.get("queueName").toString();
                //获取消费者的信息
                String toQueueName = message.getMessageProperties().getConsumerQueue();
                String toUserId = toQueueName.split("_")[1];
                //将生产者与消费者的信息加入缓存中
                redisTemplate.opsForValue().set(fromUserId,toUserId);
                redisTemplate.opsForValue().set(toUserId,fromUserId);
                //匹配完成,将队列删除
                channel.queueDelete(toQueueName);
                channel.queueDelete(fromQueueName);
                //移除监听
                container.removeQueueNames(fromQueueName,toQueueName);
                String[] queueNames = container.getQueueNames();

                for (int i = 0; i < queueNames.length; i++)
                {
                    System.out.println(queueNames[i]);
                }
                //消息已被消费
                redisTemplate.delete("messageId");
                //确认消息消费成功
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }
            else
            {
                //消费失败
                System.out.println("消费失败：消息已经被消费");
                //第一个参数：当前消息到的数据的唯一id;
                //第二个参数：是否拒绝此消息
                channel.basicReject(deliveryTag, false);

                //第一个参数依然是当前消息到的数据的唯一id;
                //第二个参数是指是否针对多条消息；如果是true，也就是说一次性针对当前通道的消息的tagID小于当前这条消息的，都拒绝确认。
                //第三个参数是指是否重新入列，也就是指不确认的消息是否重新丢回到队列里面去。
                //channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            }

//            if (hasKey)
//            {
//                redisTemplate.opsForValue().set(fromUserId,toUserId);
//                channel.queueDelete(toQueueName);
//                channel.queueDelete(fromQueueName);
//                container.removeQueueNames(fromQueueName,toQueueName);
//                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);//确认消息消费成功
//            }
//            else
//            {
//                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
//            }
//            else
//            {          //消费失败
//                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
//            }
        }
        catch (JSONException e)
        {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);//消息丢弃
        }
    }


    /**
     *
     * String转map
     * @param str
     * @return
     */
    public static Map<String,Object> getStringToMap(String str){
        //根据逗号截取字符串数组
        String[] str1 = str.split(",");
        //创建Map对象
        Map<String,Object> map = new HashMap<>();
        //循环加入map集合
        for (int i = 0; i < str1.length; i++) {
            //根据":"截取字符串数组
            String[] str2 = str1[i].split(":");
            //str2[0]为KEY,str2[1]为值
            map.put(str2[0],str2[1]);
        }
        return map;
    }

}
