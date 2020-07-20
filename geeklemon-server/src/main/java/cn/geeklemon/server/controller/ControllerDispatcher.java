package cn.geeklemon.server.controller;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.nio.charset.Charset;
import java.util.List;

import io.netty.util.ReferenceCounted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.geeklemon.server.TemporaryDataHolder;
import cn.geeklemon.server.TemporaryDataHolder.HolderType;
import cn.geeklemon.server.context.WebContext;
import cn.geeklemon.server.intercepter.WebInterceptor;
import cn.geeklemon.server.request.HttpRequestUtil;
import cn.geeklemon.server.request.LemonHttpRequest;
import cn.geeklemon.server.request.RequestDefine;
import cn.geeklemon.server.response.HttpResponseUtil;
import cn.geeklemon.server.response.LemonHttpResponse;
import cn.geeklemon.server.viewrender.ModelAndView;
import cn.hutool.core.collection.CollectionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpUtil;

/**
 * @author : Kavin Gu Project Name : geeklemon Description :
 * @version : ${VERSION} 2019/9/23 12:54 Modified by : kavingu
 */
public class ControllerDispatcher extends ChannelInboundHandlerAdapter {
    private WebContext webContext;

    private final static Logger LOGGER = LoggerFactory.getLogger(ControllerDispatcher.class);

    public ControllerDispatcher(WebContext applicationContext) {
        this.webContext = applicationContext;
    }

    /**
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            if (HttpUtil.is100ContinueExpected(request)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
                return;
            }
            HttpResponse response = null;
            try {
                /*配合filter*/
                String uri = TemporaryDataHolder.loadLemonRequest().URI();

                HttpMethod httpMethod = request.method();
                // 根据路由获得具体的ControllerProxy

                RequestDefine define = new RequestDefine(uri, httpMethod,
                        TemporaryDataHolder.loadLemonRequest().parameterMap(), request);
                ControllerDefine controller = webContext.getController(define);
                if (controller == null) {
                    response = HttpResponseUtil.getForbiddenResponse();
                    if (!("/bad-request".equals(request.uri()))) {
                        LOGGER.warn(ctx.channel().remoteAddress() + ":" + request.uri());
                    }
                } else {
                    List<WebInterceptor> interceptors = webContext.getInterceptors(uri);
                    cn.geeklemon.server.request.HttpRequest lemonRequest = TemporaryDataHolder.loadLemonRequest();
                    if (CollectionUtil.isNotEmpty(interceptors)) {
                        for (WebInterceptor interceptor : interceptors) {
                            boolean preHandle = interceptor.preHandle(lemonRequest);
                            if (!preHandle) {
                                response = HttpResponseUtil.getForbiddenResponse();
                                boolean isClose = HttpResponseUtil.isClose(request);
                                HttpResponseUtil.send(response, ctx, isClose);
                                release(msg);
                                TemporaryDataHolder.removeAll();
                                return;
                            }
                        }
                    }
                    LemonHttpResponse lemonHttpResponse = (LemonHttpResponse) TemporaryDataHolder.loadLemonResponse();
                    lemonHttpResponse.setContext(ctx);
                    TemporaryDataHolder.store(HolderType.LEMON_RESPONSE, lemonHttpResponse);

                    ModelAndView modelAndView = webContext.invokForModelAndView(controller, lemonRequest);
                    lemonHttpResponse = (LemonHttpResponse) TemporaryDataHolder.loadLemonResponse();
                    lemonHttpResponse.complete();

                    Class<?> returnType = controller.getMethod().getReturnType();

                    if (returnType == void.class) {
                        TemporaryDataHolder.removeAll();
                        /* 内存泄漏 */
                        release(msg);
                        return;
                    }
                    if (CollectionUtil.isNotEmpty(interceptors)) {
                        for (WebInterceptor interceptor : interceptors) {
                            interceptor.postHandle(lemonRequest, modelAndView);
                        }
                    }
                    Exception exception = null;
                    try {
                        response = HttpResponseUtil.getResponseByModeAndView(modelAndView, webContext);
                        if (controller.getMapping().CrossOrigin()) {
                            /* 跨域 */
                            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
                            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "*");
                            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS,
                                    "GET, POST, PUT,DELETE");
                        }
                    } catch (Exception e) {
                        exception = e;
                    }
                    if (CollectionUtil.isNotEmpty(interceptors)) {
                        for (WebInterceptor interceptor : interceptors) {
                            interceptor.afterCompletion(lemonRequest, exception);
                        }
                    }

                }
                TemporaryDataHolder.storeHttpResponse(response);
            } catch (Exception e) {
                LOGGER.error("Server Internal Error,cause:", e);
                if (e instanceof IllegalArgumentException) {
                    response = HttpResponseUtil.getErrorResponse(e.getMessage());
                } else {
                    response = HttpResponseUtil.getServerErrorResponse();
                }
                TemporaryDataHolder.storeHttpResponse(response);
            }
        }
        /*
         * 提交给下一个ChannelHandler去处理
         * 并且不需要调用ReferenceCountUtil.release(msg);来释放引用计数
         */
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        LOGGER.error("ctx close,cause:", cause);
    }

    private void release(Object msg) {
        try {
            if (msg instanceof ReferenceCounted) {
                ReferenceCounted referenceCounted = (ReferenceCounted) msg;
                referenceCounted.release();
            }
        } catch (Exception ignored) {
        }
    }
}
