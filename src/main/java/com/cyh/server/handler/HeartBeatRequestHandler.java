package com.cyh.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import protocol.request.HeartBeatRequestPacket;
import protocol.response.HeartBeatResponsePacket;


public class HeartBeatRequestHandler  extends SimpleChannelInboundHandler<HeartBeatRequestPacket>
{
    public static final HeartBeatRequestHandler INSTANCE = new HeartBeatRequestHandler();

    public HeartBeatRequestHandler() {

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HeartBeatRequestPacket requestPacket) {
        System.out.println("收到心跳包");
        ctx.writeAndFlush(new HeartBeatResponsePacket());
//                .addListener((ChannelFutureListener) future ->{
//            if (!future.isSuccess()) {
//                RedisTemplate<String, Object>  myRedisTemplate = SpringUtils.getBean("myRedisTemplate", RedisTemplate.class);
//                if (myRedisTemplate != null)
//                {
//                    HashMap<String, String> session = (HashMap<String, String>) myRedisTemplate.opsForValue().get("session");
//                    Session userInfo = SessionUtil.getSession(ctx.channel());
//                    boolean hasKey = session.containsKey(userInfo.getUserId());
//                    if (hasKey)
//                    {
//                        session.remove(userInfo.getUserId());
//                    }
//                }
////                LOGGER.error("IO error,close Channel");
//                System.out.println("IO error,close Channel");
//                future.channel().close();
//            }
//        });

    }
}
