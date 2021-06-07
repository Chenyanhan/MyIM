package com.cyh.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import protocol.request.JoinGroupRequestPacket;

/**
 * @author C
 */
public class JoinGroupRequestHandler extends SimpleChannelInboundHandler<JoinGroupRequestPacket>
{
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, JoinGroupRequestPacket joinGroupRequestPacket) throws Exception
    {
//        ChannelGroup channelGroup = MyRedisTemplate.groupChannelTemplate.opsForValue().get(joinGroupRequestPacket.getGroupId());
//        Channel channel = MyRedisTemplate.channelTemplate.opsForValue().get(joinGroupRequestPacket.getUserId());
//        if (channelGroup != null)
//        {
//            channelGroup.add(channel);
//            List<String> userIds = MyRedisTemplate.groupUserTemplate.opsForValue().get(joinGroupRequestPacket.getGroupId());
//            userIds.add(joinGroupRequestPacket.getUserId());
//        }
//        // 2. 构造加群响应发送给客户端
//        JoinGroupResponsePacket responsePacket = new JoinGroupResponsePacket();
//
//        responsePacket.setSuccess(true);
//        responsePacket.setGroupId(joinGroupRequestPacket.getGroupId());
//        channelHandlerContext.channel().writeAndFlush(responsePacket);
    }
}
