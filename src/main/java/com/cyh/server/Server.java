package com.cyh.server;

import com.cyh.NettyServer;
import com.cyh.codec.PacketDecoder;
import com.cyh.codec.PacketEncoder;
import com.cyh.codec.Spliter;
import com.cyh.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.boot.SpringApplication;

import java.util.Date;

public class Server
{
    public static void startNettyServer(){

        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        final ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap
                .group(boosGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        ch.pipeline().addLast(ChannelHandlerHolder.HANDLERS);
//                        ch.pipeline().addLast(new IMIdleStateHandler());
//                        ch.pipeline().addLast(new Spliter());
//                        ch.pipeline().addLast(new PacketDecoder());
//                        ch.pipeline().addLast(new LoginRequestHandler());
//                        ch.pipeline().addLast(new BindChannelReqHandler());
//                        ch.pipeline().addLast(new HeartBeatRequestHandler());
//                        ch.pipeline().addLast(new AuthHandler());
//                        ch.pipeline().addLast(new MessageRequestHandler());
//                        ch.pipeline().addLast(new PacketEncoder());
                    }
                });

        bind(serverBootstrap, ChannelHandlerHolder.PORT);
    }

    private static void bind(final ServerBootstrap serverBootstrap, final int port) {
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println(new Date() + ": 端口[" + port + "]绑定成功!");
            } else {
                System.err.println("端口[" + port + "]绑定失败!");
            }
        });
    }
}
