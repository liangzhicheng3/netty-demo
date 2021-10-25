package com.liangzhicheng.netty.reconnect;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * 实现断线重连客户端处理器
 */
public class ReconnectClientHandler extends ChannelInboundHandlerAdapter {

    private ReconnectClient reconnectClient;

    public ReconnectClientHandler(ReconnectClient reconnectClient){
        this.reconnectClient = reconnectClient;
    }

    /**
     * 当客户端连接服务端完成后就会触发该方法
     * @param context
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        Scanner scanner = new Scanner(System.in);
        if(scanner.hasNext()){
            context.writeAndFlush(Unpooled.copiedBuffer(scanner.next().getBytes(CharsetUtil.UTF_8)));
        }
    }

    /**
     * 当通道有读取事件时会触发，即服务端发送消息给客户端
     * @param context
     * @param message
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext context, Object message) throws Exception {
        ByteBuf byteBuf = (ByteBuf) message;
        System.out.println("服务端地址：" + context.channel().remoteAddress());
        System.out.println("接收到服务端消息：" + byteBuf.toString(CharsetUtil.UTF_8));
    }

    /**
     * 处于不活动状态时调佣
     * @param context
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext context) throws Exception {
        System.err.println("运行中断重连...");
        reconnectClient.connect();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
        System.out.println("出现异常：" + cause);
        context.close();
    }

}
