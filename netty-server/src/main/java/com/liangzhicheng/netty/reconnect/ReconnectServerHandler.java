package com.liangzhicheng.netty.reconnect;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * 实现断线重连服务端处理器
 */
public class ReconnectServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 读取客户端发送的消息
     * @param context
     * @param message
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext context, Object message) throws Exception {
        System.out.println("服务端读取线程：" + Thread.currentThread().getName());
        String content = ((ByteBuf) message).toString(CharsetUtil.UTF_8);
        System.out.println("客户端发送消息：" + content);
        if("close".equals(content)){
            context.close();
        }
    }

    /**
     * 消息读取完成
     * @param context
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext context) throws Exception {
        context.writeAndFlush(Unpooled.copiedBuffer("hello client".getBytes(CharsetUtil.UTF_8)));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
        System.out.println("出现异常：" + cause);
        context.close();
    }

}
