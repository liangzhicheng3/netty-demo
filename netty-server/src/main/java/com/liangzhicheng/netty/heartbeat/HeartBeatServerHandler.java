package com.liangzhicheng.netty.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 实现心跳机制检测服务端处理器
 */
public class HeartBeatServerHandler extends SimpleChannelInboundHandler<String> {

    private int readerIdleTime = 0;

    @Override
    protected void channelRead0(ChannelHandlerContext context, String message) throws Exception {
        System.out.println("接收到heartbeat server消息：" + message);
        if("heartbeat packet".equals(message)){
            context.channel().writeAndFlush("ojbk");
        }else{
            System.out.println("其他消息处理");
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext context, Object event) throws Exception {
        IdleStateEvent idleStateEvent = (IdleStateEvent) event;
        String eventType = "";
        switch(idleStateEvent.state()){
            case READER_IDLE:
                eventType = "读空闲";
                //读空闲计数+1
                readerIdleTime++;
                break;
            case WRITER_IDLE:
                eventType = "写空闲";
                break;
            case ALL_IDLE:
                eventType = "读写空闲";
                break;
            default:
                break;
        }
        System.out.println(context.channel().remoteAddress() + "超时事件：" + eventType);
        if(readerIdleTime > 3){
            System.out.println("[heartbeat server] 读空闲超过3次，关闭连接释放更多资源");
            context.channel().writeAndFlush("idle close");
            context.channel().close();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        System.err.println(context.channel().remoteAddress() + "正在活跃着");
    }

}
