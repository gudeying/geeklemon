package cn.geeklemon.server.handler;

import java.util.List;

import cn.geeklemon.server.response.LemonHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.geeklemon.server.TemporaryDataHolder;
import cn.geeklemon.server.common.RenderType;
import cn.geeklemon.server.context.WebContext;
import cn.geeklemon.server.filter.WebFilter;
import cn.geeklemon.server.response.HttpResponseUtil;
import cn.geeklemon.server.viewrender.ModelAndView;
import cn.hutool.core.collection.CollectionUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;

public class FilterHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilterHandler.class);
    private WebContext webContext;

    public FilterHandler(WebContext webContext) {
        this.webContext = webContext;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;

            LemonHttpResponse lemonHttpResponse = (LemonHttpResponse) TemporaryDataHolder.loadLemonResponse();
            lemonHttpResponse.setContext(ctx);
            TemporaryDataHolder.store(TemporaryDataHolder.HolderType.LEMON_RESPONSE, lemonHttpResponse);

            boolean accept = doFilter(request.uri());
            if (accept) {
                super.channelRead(ctx, msg);
            } else {
                lemonHttpResponse.complete();
                if (msg instanceof FullHttpRequest) {
                    try {
                        FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
                        fullHttpRequest.content().release();
                    } catch (Exception ignored) {
                    }
                }
            }

        }

    }

    private boolean doFilter(String uri) {
        List<WebFilter> filters = webContext.getFilters(uri);
        if (CollectionUtil.isEmpty(filters)) {
            return true;
        }
        for (WebFilter webFilter : filters) {
            boolean accept = webFilter.accept(TemporaryDataHolder.loadLemonRequest(), TemporaryDataHolder.loadLemonResponse());
            if (!accept) {
                return false;
            }
        }
        return true;
    }
}
