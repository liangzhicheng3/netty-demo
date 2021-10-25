package com.liangzhicheng.netty.reconnect;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * handler的生命周期回调接口调用顺序
 * handlerAdded -> channelRegistered -> channelActive -> channelRead -> channelReadComplete -> channelInactive -> channelUnregistered -> handlerRemoved
 * handlerAdded：新建立的连接会按照初始化策略，把handler添加到该channel的pipeline中，
 * channelRegistered：当该连接分配到具体的worker线程后，该回调会被调用
 * channelActive：channel的准备工作已经完成，所有的pipeline添加完成，并分配到具体的线上，说明该channel准备就绪，可以使用
 * channelRead：客户端向服务端发来消息，每次都会回调该方法，表示有消息可读
 * channelReadComplete：服务端每次读完一次完整消息后，回调该方法，表示消息读取完成
 * channelInactive：当连接断开，该回调会被调用，说明这时候底层TCP连接已经被断开
 * channelUnregistered：对应channelRegistered，当连接关闭后，释放绑定worker线程
 * handlerRemoved：对应handlerAdded，将handler从该channel的pipeline移除后的回调方法
 */
public class LifeCycleInBoundHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void handlerAdded(ChannelHandlerContext context) throws Exception {
        System.out.println("handlerAdded：handler被添加到channel的pipeline中");
        super.handlerAdded(context);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext context) throws Exception {
        System.out.println("channelRegistered：channel注册到NioEventLoop");
        super.channelRegistered(context);
    }

    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        System.out.println("channelActive：channel准备就绪");
        super.channelActive(context);
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) throws Exception {
        System.out.println("channelRead：channel中有可读消息");
        super.channelRead(context, message);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) throws Exception {
        System.out.println("channelRead：channel读取消息完成");
        super.channelReadComplete(context);
    }

    @Override
    public void channelInactive(ChannelHandlerContext context) throws Exception {
        System.out.println("channelInactive：channel被关闭");
        super.channelInactive(context);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext context) throws Exception {
        System.out.println("channelUnregistered：channel取消和NioEventLoop的绑定");
        super.channelUnregistered(context);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext context) throws Exception {
        System.out.println("handlerRemoved：handler从channel的pipeline中移除");
        super.handlerRemoved(context);
    }

}
