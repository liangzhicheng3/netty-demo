package com.liangzhicheng.netty.heartbeat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.security.SecureRandom;

/**
 * 实现心跳检测客户端
 */
public class HeartBeatClient {

    private String host;
    private Integer port;

    public HeartBeatClient(String host, Integer port){
        this.host = host;
        this.port = port;
    }

    /**
     * 初始化Netty客户端
     */
    private void init() throws Exception{
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
                    pipeline.addLast(new HeartBeatClientHandler());
                }
            });
            System.out.println("客户端启动...");
            Channel channel = bootstrap.connect(host, port).sync().channel();
            String text = "heartbeat packet";
            SecureRandom random = new SecureRandom();
            while(channel.isActive()){
                int num = random.nextInt(8);
                Thread.sleep(num * 1000);
                channel.writeAndFlush(text);
            }
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
        new HeartBeatClient("127.0.0.1", 9090).init();
    }

}
