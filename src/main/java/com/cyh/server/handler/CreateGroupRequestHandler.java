package com.cyh.server.handler;

import com.cyh.util.SessionUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import protocol.request.CreateGroupRequestPacket;


import java.util.ArrayList;
import java.util.List;

/**
 * @author C
 */
public class CreateGroupRequestHandler extends SimpleChannelInboundHandler<CreateGroupRequestPacket>
{
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, CreateGroupRequestPacket createGroupRequestPacket) throws Exception
    {
        List<String> userIdList = createGroupRequestPacket.getUserIdList();

        // 1. 创建一个 channel 分组
        ChannelGroup channelGroup = new DefaultChannelGroup(channelHandlerContext.executor());

        List<String> userNameList = new ArrayList<>();

        for (String userId:userIdList)
        {
            Channel channel = SessionUtil.getChannel(userId);
            if (channel != null) {
                channelGroup.add(channel);
            }
//            RegisterInfoReqVO userInfo = GetRedisTemplate.userInfoTemplate.opsForValue().get(userId);
//            if (userInfo != null)
//            {
//                userNameList.add(userInfo.getUserName());
//            }
        }
//        String groupId = UUID.randomUUID().toString();
//        MyRedisTemplate.groupChannelTemplate.opsForValue().set(groupId,channelGroup);
//        // 3. 创建群聊创建结果的响应
//        CreateGroupResponsePacket createGroupResponsePacket = new CreateGroupResponsePacket();
//        createGroupResponsePacket.setSuccess(true);
//        createGroupResponsePacket.setGroupId(groupId);
//        createGroupResponsePacket.setUserNameList(userNameList);
//
//        // 4. 给每个客户端发送拉群通知
//        channelGroup.writeAndFlush(createGroupResponsePacket);
//
//        MyRedisTemplate.groupUserTemplate.opsForValue().set(groupId,userIdList);
    }
}
