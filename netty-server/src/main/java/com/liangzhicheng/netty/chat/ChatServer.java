package com.liangzhicheng.netty.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * 实现简易聊天服务端
 */
public class ChatServer {

    private Integer port;

    public ChatServer(Integer port){
        this.port = port;
    }

    /**
     * 初始化netty服务端
     */
    private void init() throws InterruptedException {
        //创建两个线程组，bossGroup和workGroup，含有子线程NioEventLoop的个数默认为cpu核数的两倍
        //bossGroup只处理连接请求，workGroup处理客户端业务
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup(8);
        try {
            //创建服务端启动对象
            ServerBootstrap bootstrap = new ServerBootstrap();
            //设置线程组
            bootstrap.group(bossGroup, workGroup);
            //设置服务端通道的实现：NioServerSocketChannel
            bootstrap.channel(NioServerSocketChannel.class);
            //等待连接的队列长度
            bootstrap.option(ChannelOption.SO_BACKLOG, 128);
            //让客户端保持长期活动状态
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    //从channel中获取pipeline并添加Handler
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    //向pipeline加入编码器
                    pipeline.addLast("encoder", new StringEncoder());
                    //向pipeline加入解码器
                    pipeline.addLast("decoder", new StringDecoder());
                    //自定义handler处理器
                    pipeline.addLast(new ChatServerHandler());
                }
            });
            System.out.println("服务端开始启动...");
            //绑定一个端口并且同步，生成一个channelFuture异步对象，通过isDone()等方法可以判断异步事件的执行情况
            //启动服务端并绑定端口，bind是异步操作，sync方法是等待异步操作执行完毕
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if(future.isSuccess()){
                        System.out.println("服务端正在启动...");
                    }else{
                        System.out.println("服务端启动失败...");
                    }
                    if(future.isDone()){
                        System.out.println("服务端启动成功...OK");
                    }
                }
            });
            //等待服务端监听端口关闭，closeFuture是异步操作
            //通过sync方法同步等待通道关闭处理完毕，这里会阻塞等待通道关闭完成，内部调用是Object的wait()方法
            channelFuture.channel().closeFuture().sync();
            channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if(future.isCancelled()){
                        System.out.println("服务端正在关闭...");
                    }
                    if(future.isCancellable()){
                        System.out.println("服务端已经关闭...OK");
                    }
                }
            });
        }finally{
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    /**
     * 启动服务端main函数
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        new ChatServer(9090).init();
    }

}
