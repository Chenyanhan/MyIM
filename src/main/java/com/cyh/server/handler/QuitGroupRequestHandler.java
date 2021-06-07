package com.cyh.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import protocol.request.QuitGroupRequestPacket;

public class QuitGroupRequestHandler extends SimpleChannelInboundHandler<QuitGroupRequestPacket>
{
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, QuitGroupRequestPacket quitGroupRequestPacket) throws Exception
    {
//        // 1. 获取群对应的 channelGroup，然后将当前用户的 channel 移除
//        ChannelGroup channelGroup = MyRedisTemplate.groupChannelTemplate.opsForValue().get(quitGroupRequestPacket.getGroupId());
//
//        if (channelGroup != null)
//        {
//            channelGroup.remove(ctx.channel());
//            List<String> users = MyRedisTemplate.groupUserTemplate.opsForValue().get(quitGroupRequestPacket.getGroupId());
//            users.remove(quitGroupRequestPacket.getUserId());
//        }
//
//        // 2. 构造退群响应发送给客户端
//        QuitGroupResponsePacket responsePacket = new QuitGroupResponsePacket();
//
//        responsePacket.setGroupId(quitGroupRequestPacket.getGroupId());
//        responsePacket.setSuccess(true);
//        ctx.channel().writeAndFlush(responsePacket);
    }
}
