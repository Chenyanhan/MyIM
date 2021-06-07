package com.cyh.listener;

import com.cyh.util.SessionUtil;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import protocol.response.MessageResponsePacket;

import java.nio.charset.StandardCharsets;

@Component
public class RedisKeyExpirationListener  extends KeyExpirationEventMessageListener
{


    @Qualifier("defaultRedisTemplate")
    private RedisTemplate redisTemplate;

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer)
    {
        super(listenerContainer);
    }

    /**
     * 针对redis数据失效事件，进行数据处理
     * @param message
     * @param pattern
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String key = new String(message.getBody(), StandardCharsets.UTF_8);
        Channel channel = SessionUtil.getChannel(key);
        channel.writeAndFlush(new MessageResponsePacket(key,key, key+"匹配超时"));
    }
}
