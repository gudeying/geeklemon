package cn.geeklemon.server.handler;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/23 11:00
 * Modified by : kavingu
 */

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AfterHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(AfterHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 调用后置拦截器的方法
        afterHandle(ctx, msg);
        /*
         * 提交给下一个ChannelHandler去处理
         * 并且不需要调用ReferenceCountUtil.release(msg);来释放引用计数
         */
        ctx.fireChannelRead(msg);
    }

    /**
     * 后置拦截器拦截的方法
     */
    public abstract void afterHandle(ChannelHandlerContext ctx, Object msg);

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        logger.error("ctx close,cause:", cause);
    }
}
