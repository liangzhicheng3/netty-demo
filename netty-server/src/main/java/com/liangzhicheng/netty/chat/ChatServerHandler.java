package com.liangzhicheng.netty.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 实现简易聊天服务端处理器
 */
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    /**
     * 管理全局channel
     * GlobalEventExecutor.INSTANCE 全局事件执行器（单例）
     */
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 实现私聊功能，这里key存储用户的唯一标识（客户端的端口号）
     */
    private static Map<String, Channel> clients = new HashMap<String, Channel>();

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * channel连接状态就绪后调用
     * @param context
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        System.out.println(sdf.format(new Date()) + " \n 【用户】 " + this.getRemoteAddress(context) + " 上线了");
    }

    /**
     * 建立连接后第一个调用的方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String ip = channel.remoteAddress().toString();
        //将channelGroup中所有channel遍历并发送消息
        channelGroup.writeAndFlush(sdf.format(new Date()) + "\n 【用户】 " + ip + " 加入聊天室");
        channelGroup.add(channel);
        clients.put(ip.split(":")[1], channel);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, String message) throws Exception {
        Channel channel = context.channel();
        //如果内容包含#，那么就是私聊
        if(message.contains("#")){
            String id = message.split("#")[0];
            String body = message.split("#")[1];
            Channel userChannel = clients.get(id);
            String key = channel.remoteAddress().toString().split(":")[1];
            userChannel.writeAndFlush(sdf.format(new Date()) + "\n 【用户】 " + key + " 说：" + body);
            return;
        }
        //判断当前发送消息是否自己
        for(Channel cg : channelGroup){
            String ip = channel.remoteAddress().toString();
            if(channel != cg){
                cg.writeAndFlush(sdf.format(new Date()) + "\n 【用户】 " + ip + " 说：" + message);
            }else{
                cg.writeAndFlush(sdf.format(new Date()) + "\n 【自己】 " + ip + " 说：" + message);
            }
        }
    }

    /**
     * channel连接状态断开后触发
     * @param context
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext context) throws Exception {
        String ip = this.getRemoteAddress(context);
        System.out.println(sdf.format(new Date()) + " \n 【用户】 " + ip + " 下线了");
        //下线移除
        clients.remove(ip.split(":")[1]);
    }

    /**
     * 连接发生异常时触发
     * @param context
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext context,
                                Throwable cause) throws Exception {
        System.out.println("连接出现异常...");
        context.close();
    }

    /**
     * 获取远程地址
     * @param context
     * @return
     */
    private String getRemoteAddress(ChannelHandlerContext context){
        return context.channel().remoteAddress().toString();
    }

}
