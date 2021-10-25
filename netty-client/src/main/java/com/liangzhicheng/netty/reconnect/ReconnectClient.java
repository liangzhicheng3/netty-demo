package com.liangzhicheng.netty.reconnect;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.TimeUnit;

/**
 * 实现断线重连客户端
 */
public class ReconnectClient {

    private String host;
    private Integer port;
    private Bootstrap bootstrap;

    public ReconnectClient(String host, Integer port){
        this.host = host;
        this.port = port;
        init();
    }

    private void init(){
        //创建⼀个NIO事件循环组（线程组）
        EventLoopGroup group = new NioEventLoopGroup();
        //创建客户端引导程序（启动程序）
        bootstrap = new Bootstrap();
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
                //自定义handler处理器
                pipeline.addLast(new ReconnectClientHandler(ReconnectClient.this));
            }
        });
    }

    public void connect() throws InterruptedException {
        System.out.println("客户端启动...");
        //启动客户端去连接服务端
        ChannelFuture channelFuture = bootstrap.connect(host, port);
        channelFuture.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if(!future.isSuccess()){
                    //重连交给后端线程执行
                    future.channel().eventLoop().schedule(() -> {
                        System.err.println("服务端正在重连...");
                        try{
                            connect();
                        }catch(Exception e){
                            System.out.println(e);
                        }
                    }, 3000, TimeUnit.MILLISECONDS);
                }else{
                    System.out.println("服务端连接成功...");
                }
            }
        });
        //对通道关闭进行监听
        channelFuture.channel().closeFuture().sync();
    }

    /**
     * 启动服务端main函数
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws InterruptedException {
        ReconnectClient reconnectClient = new ReconnectClient("127.0.0.1", 9090);
        reconnectClient.connect();
    }

}
