package com.cyh.server.handler;

import com.cyh.codec.PacketDecoder;
import com.cyh.codec.PacketEncoder;
import com.cyh.codec.Spliter;
import io.netty.channel.ChannelHandler;

public interface ChannelHandlerHolder
{
    int PORT = 8310;
    ChannelHandler[] HANDLERS = new ChannelHandler[]{
            new IMIdleStateHandler(),
            new Spliter(),
            new PacketDecoder(),
            //new LoginRequestHandler(),
            // 单聊消息请求处理器
            //new MessageRequestHandler(),
            // 创建群请求处理器
            new CreateGroupRequestHandler(),
            new BindChannelReqHandler(),
            new HeartBeatRequestHandler(),
            new AuthHandler(),
            new HeartBeatRequestHandler(),
            new PacketEncoder(),
    };
}
