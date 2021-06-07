package com.cyh;

import com.cyh.codec.PacketDecoder;
import com.cyh.codec.PacketEncoder;
import com.cyh.codec.Spliter;
import com.cyh.server.Server;
import com.cyh.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.rocketmq.client.exception.MQClientException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;



import java.util.*;


@SpringBootApplication
public class NettyServer extends SpringBootServletInitializer
{

    private static final int PORT = 8310;

    public static void main(String[] args) throws MQClientException
    {
        SpringApplication.run(NettyServer.class, args);
        Server.startNettyServer();
    }




}

