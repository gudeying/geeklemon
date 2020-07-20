package cn.geeklemon.server.handler;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.server.TemporaryDataHolder;
import cn.geeklemon.server.context.WebContext;
import cn.geeklemon.server.filter.WebFilter;
import cn.geeklemon.server.request.HttpRequest;
import cn.geeklemon.server.viewrender.ModelAndView;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 */

/**
 * 该接口被废弃。不在使用
 */
@Deprecated
public abstract class PreHandler extends ChannelInboundHandlerAdapter {
    private WebContext webContext;
    private final static Logger logger = LoggerFactory.getLogger(PreHandler.class);
    private io.netty.handler.codec.http.HttpRequest nettyRequest;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof io.netty.handler.codec.http.HttpRequest) {
            this.nettyRequest = (io.netty.handler.codec.http.HttpRequest) msg;
            HttpRequest httpRequest = TemporaryDataHolder.loadLemonRequest();

            List<WebFilter> filters = webContext.getFilters(nettyRequest.uri());

            if (!CollectionUtil.isEmpty(filters)) {
                for (WebFilter webFilter : filters) {
//					ModelAndView modelAndView = webFilter.handle(httpRequest);
//					if (modelAndView != null) {
//
//					}

                }
            }

            /*
             * 提交给下一个ChannelHandler去处理
             * 并且不需要调用ReferenceCountUtil.release(msg);来释放引用计数
             */
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        logger.error("ctx close,cause:", cause);
    }

    /**
     * 重定向 不能为空字符串，根目录使用"/"
     *
     * @param path
     */
    public void sendRedirect(String path) {
        Assert.notBlank(path);
        nettyRequest.setUri(path);
    }

    public PreHandlerResult handle(HttpRequest request) {
        return null;
    }

}
