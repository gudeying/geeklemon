package cn.geeklemon.server.response;

import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.geeklemon.server.TemporaryDataHolder;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.Cookie;

/**
 * 写响应
 *
 * @author houyi.wh
 * @date 2017/11/7
 */
@SuppressWarnings("rawtypes")
public class ResponseWriter extends SimpleChannelInboundHandler {

	private final static Logger LOGGER = LoggerFactory.getLogger(ResponseWriter.class);

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		HttpRequest request = null;
		Channel channel = ctx.channel();
		FullHttpResponse response = null;
		if (msg instanceof HttpRequest) {
			request = (HttpRequest) msg;
			if (request.uri().equals("/favicon.ico")) {
				return;
			}
			if (HttpUtil.is100ContinueExpected(request)) {
				ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
			}
			channel = ctx.channel();
			response = TemporaryDataHolder.loadHttpResponse();
			if (response == null) {
				response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
			}
			writeResponse(request, response, channel);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
		// 释放ThreadLocal对象
		TemporaryDataHolder.removeAll();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		ctx.close();
		LOGGER.error("ctx close,cause:", cause);
	}

	/**
	 * 响应消息
	 */
	private void writeResponse(HttpRequest request, FullHttpResponse response, Channel channel) {
		boolean close = isClose(request);
		// response.headers().add(HttpHeaderNames.CONTENT_LENGTH,
		// String.valueOf(response.content().readableBytes()));
		// 写cookie
		LemonHttpResponse lemonResponse = (LemonHttpResponse) TemporaryDataHolder.loadLemonResponse();
		Set<Cookie> responseCookies = lemonResponse.cookies();
		Map<CharSequence, Object> headers = lemonResponse.headers();
		if (MapUtil.isNotEmpty(headers)) {
			headers.forEach((key, value) -> {
				response.headers().add(key, value);
			});
		}
		if (CollectionUtil.isNotEmpty(responseCookies)) {
			for (Cookie cookie : responseCookies) {
				/**
				 * strict：不允许cookie重复名，取最后一个
				 */
				response.headers().add(HttpHeaderNames.SET_COOKIE,
						io.netty.handler.codec.http.cookie.ServerCookieEncoder.STRICT.encode(cookie));
			}
		}
		ChannelFuture future = channel.write(response);
		if (close) {
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}

	public boolean isClose(HttpRequest request) {
		return request.headers().contains(HttpHeaderNames.CONNECTION, "close", true)
				|| (request.protocolVersion().equals(HttpVersion.HTTP_1_0)
						&& !request.headers().contains(HttpHeaderNames.CONNECTION, "keep-alive", true));
	}

}
