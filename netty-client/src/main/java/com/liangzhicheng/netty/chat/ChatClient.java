package com.liangzhicheng.netty.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.Scanner;

/**
 * 实现简易聊天客户端
 */
public class ChatClient {

    private String host;
    private Integer port;

    public ChatClient(String host, Integer port){
        this.host = host;
        this.port = port;
    }

    /**
     * 初始化Netty客户端
     */
    private void init() throws Exception {
        //创建⼀个NIO事件循环组（线程组）
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //创建客户端引导程序（启动程序）
            Bootstrap bootstrap = new Bootstrap();
            //设置线程组
            bootstrap.group(group);
            //设置客户端通道的实现：NioSocketChannel
            bootstrap.channel(NioSocketChannel.class);
            //设置通道初始化时的处理器
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                    //从channel中获取pipeline并添加Handler
                    ChannelPipeline pipeline = nioSocketChannel.pipeline();
                    //向pipeline加入编码器
                    pipeline.addLast("encoder", new StringEncoder());
                    //向pipeline加入解码器
                    pipeline.addLast("decoder", new StringDecoder());
                    //自定义handler处理器
                    pipeline.addLast(new ChatClientHandler());
                }
            });
            System.out.println("客户端开始启动...");
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if(future.isSuccess()){
                        System.out.println("客户端正在启动...");
                    }else{
                        System.out.println("客户端启动失败...");
                    }
                    if(future.isDone()){
                        System.out.println("客户端启动成功...OK");
                    }
                }
            });
            Thread.sleep(100);
            System.out.println(channelFuture.channel().localAddress().toString());
            System.out.println("====================");
            System.out.println("端口号#消息内容，可单独发给一个用户");
            System.out.println("====================");
            //控制台输入内容
            Channel channel = channelFuture.channel();
            Scanner scanner = new Scanner(System.in);
            while(scanner.hasNextLine()){
                channel.writeAndFlush(scanner.nextLine() + "\n");
            }
            channelFuture.channel().closeFuture().sync();
            scanner.close();
        } finally {
            group.shutdownGracefully();
        }
    }

    /**
     * 启动客户端main函数
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        new ChatClient("127.0.0.1", 9090).init();
    }

}
