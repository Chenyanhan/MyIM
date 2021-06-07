package com.cyh.MQ;

import com.cyh.util.SessionUtil;
import com.cyh.util.SpringUtils;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import protocol.response.MessageResponsePacket;
import com.cyh.entity.UserInfo;

import java.util.Queue;

@Component
public class MatchConsum implements Runnable
{

    private final RedisTemplate<String,Object> redisTemplate;

    Queue maleQueue;
    Queue remaleQueue;
    String maleQueueName;
    String remaleQueueName;

    public MatchConsum(@Qualifier("defaultRedisTemplate") RedisTemplate<String, Object> redisTemplate)
    {
        this.redisTemplate = redisTemplate;
    }

    public void setMaleQueueName(String maleQueueName)
    {
        this.maleQueueName = maleQueueName;
    }

    public void setRemaleQueueName(String remaleQueueName)
    {
        this.remaleQueueName = remaleQueueName;
    }

    @Override
    public void run()
    {
        this.match(maleQueueName,remaleQueueName);
    }

    public void match(String maleQueueName, String remaleQueueName){
        while (true){
            boolean done = true;
            if (maleQueue == null){
                maleQueue = SpringUtils.getBean(maleQueueName, Queue.class);
            }
            if (remaleQueue == null){
                remaleQueue = SpringUtils.getBean(remaleQueueName, Queue.class);
            }
            UserInfo maleInfo = (UserInfo) maleQueue.poll();
//            UserInfo remaleInfo = (UserInfo) remaleQueue.poll();

            if (maleInfo == null || maleInfo.getExpireTime() < System.currentTimeMillis()){
                while (done){
                    maleInfo =  (UserInfo) maleQueue.poll();
                    if (maleInfo != null && maleInfo.getExpireTime() > System.currentTimeMillis()) {
                        UserInfo remaleInfo = (UserInfo) remaleQueue.poll();
                        if (remaleInfo == null || remaleInfo.getExpireTime() < System.currentTimeMillis()){
                            while (done){
                                if (maleInfo.getExpireTime() > System.currentTimeMillis()) {
                                    remaleInfo = (UserInfo) remaleQueue.poll();
                                    if (remaleInfo != null && maleInfo.getExpireTime() > System.currentTimeMillis())
                                    {
                                        done = false;
                                        writeMatchInfo(maleInfo,remaleInfo);
                                    }
                                }
                                else {
                                    maleInfo = null;
                                    done = false;
                                }

                            }
                        }
                        else {
                            done = false;
                            writeMatchInfo(maleInfo,remaleInfo);
                        }
                    }
                }

            }
//            done = true;
//            remaleInfo = (UserInfo) remaleQueue.poll();
//            if (remaleInfo == null || remaleInfo.getExpireTime() < System.currentTimeMillis()){
//                while (done){
//                    remaleInfo = (UserInfo) remaleQueue.poll();
//                    if (remaleInfo != null && remaleInfo.getExpireTime() > System.currentTimeMillis())
//                    {
//                        if (maleInfo == null || maleInfo.getExpireTime() < System.currentTimeMillis()) {
//                            while (done)
//                            {
//                                maleInfo =  (UserInfo) maleQueue.poll();
//                                if (remaleInfo.getExpireTime() > System.currentTimeMillis()) {
//                                    if (maleInfo != null && maleInfo.getExpireTime() > System.currentTimeMillis())
//                                    {
//                                        done = false;
//                                    }
//                                }
//                                else {
//                                    remaleInfo = null;
//                                    done = false;
//                                }
//                            }
//                        }
//                    }
//                }
//                writeMatchInfo(maleInfo,remaleInfo);
//            }

//            if (maleInfo == null)
//            {
//                while (done){
//                    maleInfo =  (UserInfo) maleQueue.poll();
//                    if (maleInfo != null)
//                    {
//                        done = false;
//                    }
//                }
//            }
//            UserInfo remaleInfo = (UserInfo) remaleQueue.poll();
//            done = true;
//            if (remaleInfo == null)
//            {
//                while (done){
//                    remaleInfo = (UserInfo) remaleQueue.poll();
//                    if (remaleInfo != null)
//                    {
//                        done = false;
//                    }
//                }
//            }
//            Channel maleChannel = SessionUtil.getChannel(maleInfo.getId());
//            Channel remaleChannel = SessionUtil.getChannel(remaleInfo.getId());
//            try
//            {
//                maleChannel.writeAndFlush(new MessageResponsePacket(remaleInfo.getId(), remaleInfo.getName(), "匹配到了" + remaleInfo.getName()));
//                remaleChannel.writeAndFlush(new MessageResponsePacket(maleInfo.getId(), maleInfo.getName(), "匹配到了" + maleInfo.getName()));
//                redisTemplate.delete(remaleInfo.getId());
//                redisTemplate.delete(maleInfo.getId());
//
//            }
//            catch (Exception e){
//                System.out.println(e.getMessage());
//            }

        }
    }

    private void writeMatchInfo(UserInfo maleInfo, UserInfo remaleInfo)
    {
        if (maleInfo != null && remaleInfo != null){
            Channel maleChannel = SessionUtil.getChannel(maleInfo.getId());
            Channel remaleChannel = SessionUtil.getChannel(remaleInfo.getId());
            try
            {
                maleChannel.writeAndFlush(new MessageResponsePacket(remaleInfo.getId(), remaleInfo.getName(), maleInfo.getName() + "匹配到了" + remaleInfo.getName()));
                remaleChannel.writeAndFlush(new MessageResponsePacket(maleInfo.getId(), maleInfo.getName(), remaleInfo.getName() + "匹配到了" + maleInfo.getName()));
                redisTemplate.delete(remaleInfo.getId());
                redisTemplate.delete(maleInfo.getId());
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }
}
