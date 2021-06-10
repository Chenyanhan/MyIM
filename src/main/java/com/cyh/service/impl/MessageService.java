package com.cyh.service.impl;

import com.cyh.common.enums.StatusEnum;
import com.cyh.common.res.Result;
import com.cyh.entity.CacheMsg;
import com.cyh.session.Session;
import com.cyh.util.CopyUtils;
import com.cyh.util.SessionUtil;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import protocol.request.GroupMessageRequestPacket;
import protocol.request.MessageRequestPacket;
import protocol.response.GroupMessageResponsePacket;
import protocol.response.MessageResponsePacket;

import java.util.ArrayList;
import java.util.List;

/**
 * @author C
 */
@Service
public class MessageService
{
    @Autowired
    @Qualifier("defaultRedisTemplate")
    private RedisTemplate<String,Object> redisTemplate;

    public Result MessagePush(MessageRequestPacket message)
    {
        Channel toUserChannel = GetChannelByUserId(message.getReceiverId());
        Channel fromUserChannel = GetChannelByUserId(message.getSenderId());

        // 1.拿到消息发送方的会话信息
        Session session = SessionUtil.getSession(fromUserChannel);
        // 2.通过消息发送方的会话信息构造要发送的消息
        MessageResponsePacket messageResponsePacket = new MessageResponsePacket();
        messageResponsePacket.setFromUserId(session.getUserId());
        messageResponsePacket.setMessage(message.getContent());
        // 4.将消息发送给消息接收方
        if (toUserChannel != null && SessionUtil.hasLogin(toUserChannel))
        {
            //如果对方在线则直接将消息推给对方
            toUserChannel.writeAndFlush(messageResponsePacket);
        }
        else//如果不在线则先缓存起来，等对方上线后在全部推给对方
        {
            Object pop = redisTemplate.opsForValue().get(message.getReceiverId());
            if (pop != null)//如果已存在消息列表则往里添加
            {
                List<CacheMsg> cacheMsgList = (List<CacheMsg>) pop;
                CacheMsg cacheMsg = new CacheMsg();
                //复制属性
                CopyUtils.copyProperties(message,cacheMsg);
                cacheMsgList.add(cacheMsg);
                redisTemplate.opsForValue().set(message.getReceiverId(),cacheMsgList);
            }
            else//如果不存在消息列表则创建消息列表
            {
                //创建缓存List
                List<CacheMsg> cacheMsgList = new ArrayList<>();
                CacheMsg cacheMsg = new CacheMsg();
                //复制属性
                CopyUtils.copyProperties(message,cacheMsg);
                //加入集合
                cacheMsgList.add(cacheMsg);
                //放入缓存中
                redisTemplate.opsForValue().set(message.getReceiverId(),cacheMsgList);
            }
        }
        return new Result(1,"");

    }

    private Channel GetChannelByUserId(String userId)
    {
        return SessionUtil.getChannel(userId);
    }

    public void sendMsgByGroup(GroupMessageRequestPacket groupMessage)
    {
        String toGroupId = groupMessage.getToGroupId();
        if (toGroupId != null)
        {
            ChannelGroup groupChannel = SessionUtil.getGroupChannel(groupMessage.getToGroupId());
            if (groupChannel != null)
            {
                // 1.拿到 groupId 构造群聊消息的响应
                GroupMessageResponsePacket responsePacket = new GroupMessageResponsePacket();
                responsePacket.setFromGroupId(toGroupId);
                responsePacket.setMessage(groupMessage.getMessage());
                responsePacket.setFromUser(groupMessage.getUserId());
            }
        }
    }

}
