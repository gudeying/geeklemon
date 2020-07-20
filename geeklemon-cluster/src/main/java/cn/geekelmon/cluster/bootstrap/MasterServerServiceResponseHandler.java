package cn.geekelmon.cluster.bootstrap;

import io.netty.channel.*;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * *  这个类实际接收的是负责HTTP业务的slaver处理之后返回的消息<br>
 * *  持有和master相连接的客户端（浏览器）的channel，通过该channel把slaver回传的信息直接转发给浏览器
 * * @author houyi.wh
 * * @date 2018/1/18
 *
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/12 14:05
 * Modified by : kavingu
 */
public class MasterServerServiceResponseHandler extends ChannelInboundHandlerAdapter {

    private final Channel masterChannel;

    public MasterServerServiceResponseHandler(Channel inboundChannel) {
        this.masterChannel = inboundChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.read();
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        masterChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {

                if (future.isDone()) {
                    //*下一个chunk
                    ctx.channel().read();
                } else {
                    future.channel().close();
                }
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        MasterServerHandler.closeOnFlush(masterChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        MasterServerHandler.closeOnFlush(ctx.channel());
    }

}
