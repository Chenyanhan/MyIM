package com.cyh.server.handler;

import com.cyh.session.Session;
import com.cyh.util.SessionUtil;
import com.cyh.util.SpringUtils;
import io.netty.channel.ChannelHandlerContext;

import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.data.redis.core.RedisTemplate;
import protocol.request.BindChannelReqPacket;
import protocol.response.BindChannelResPacket;
import protocol.response.MessageResponsePacket;
import com.cyh.entity.CacheMsg;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class BindChannelReqHandler extends SimpleChannelInboundHandler<BindChannelReqPacket>
{
    private RedisTemplate redisTemplate;
    public BindChannelReqHandler()
    {
        redisTemplate = SpringUtils.getBean("defaultRedisTemplate", RedisTemplate.class);
    }

    static Lock lock = new ReentrantLock();
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BindChannelReqPacket bindChannelReqPacket) {
        lock.lock();
//        LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
        BindChannelResPacket bindChannelResPacket = new BindChannelResPacket();
        bindChannelResPacket.setVersion(bindChannelReqPacket.getVersion());
        bindChannelResPacket.setUserName(bindChannelReqPacket.getUserName());
        System.out.println("用户名" + bindChannelReqPacket.getUserName());
        if (valid(bindChannelReqPacket)) {
            bindChannelResPacket.setSuccess(true);
            bindChannelResPacket.setUserId(bindChannelReqPacket.getUserId());
            SessionUtil.bindSession(new Session(bindChannelReqPacket.getUserId(), bindChannelReqPacket.getUserName()), ctx.channel());

            //查询是否有离线消息
            redisTemplate = SpringUtils.getBean("defaultRedisTemplate", RedisTemplate.class);
            Object msg = redisTemplate.opsForValue().get(bindChannelReqPacket.getUserId());
//            Object msg = null;
            if (msg != null)
            {
                List<CacheMsg> cacheMsgList = (List<CacheMsg>) msg;
                for (CacheMsg item: cacheMsgList)
                {
                    ctx.channel().writeAndFlush(new MessageResponsePacket(item.getSenderId(),"",item.getContent()));
                }
                //移除缓存中的数据
//                redisTemplate.delete(bindChannelReqPacket.getUserId());
            }
            System.out.println("[" + bindChannelReqPacket.getUserId() + "]绑定成功");
        }
        else
        {
            bindChannelResPacket.setReason("还未登陆，请先登录");
            bindChannelResPacket.setSuccess(false);
            System.out.println(new Date() + ": 未登录!");
        }

        // 登录响应
        ctx.channel().writeAndFlush(bindChannelResPacket);
        lock.unlock();
    }

    private boolean valid(BindChannelReqPacket bindChannelReqPacket) {
//        RedisTemplate myRedisTemplate = SpringUtils.getBean("myRedisTemplate", RedisTemplate.class);
        redisTemplate = SpringUtils.getBean("defaultRedisTemplate", RedisTemplate.class);
        Object value = redisTemplate.opsForValue().get("session");
        if (value != null)
        {
            Map<String, String> stringMap = (Map<String, String>) value;
            if (stringMap.containsKey(bindChannelReqPacket.getUserId()))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        SessionUtil.unBindSession(ctx.channel());
    }

}
