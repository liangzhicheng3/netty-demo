package com.liangzhicheng.netty.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 实现心跳机制检测客户端处理器
 */
public class HeartBeatClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext context, String message) throws Exception {
        System.out.println("接收到heartbeat client消息：" + message);
        if(message != null && "idle close".equals(message)){
            System.out.println("服务端已经关闭，客户端也关闭");
            context.channel().closeFuture();
        }
    }

}
