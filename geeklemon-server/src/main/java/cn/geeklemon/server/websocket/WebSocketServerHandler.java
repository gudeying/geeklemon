package cn.geeklemon.server.websocket;

import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.server.context.WebContext;
import cn.geeklemon.server.websocket.context.EndPointHelper;
import cn.geeklemon.server.websocket.param.UriParameterUtil;
import cn.geeklemon.server.websocket.param.WebSocketParamHolder;
import cn.geeklemon.server.websocket.param.WebSocketUrlParam;
import cn.geeklemon.server.websocket.support.WebSocketEndPointDefine;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @author : Kavin Gu Project Name : geeklemon Description :
 * @version : ${VERSION} 2019/10/21 17:00 Modified by : kavingu
 */
public class WebSocketServerHandler extends ChannelInboundHandlerAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketServerHandler.class);
	private WebContext webContext;

	private WebSocketServerHandshaker handShaker;

	public WebSocketServerHandler(WebContext applicationContext) {
		this.webContext = applicationContext;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof WebSocketFrame) {

			WebSocketFrame frame = (WebSocketFrame) msg;
			handleWebSocket(ctx, frame);
		} else if (msg instanceof HttpRequest) {
			HttpRequest request = (HttpRequest) msg;
			handleHttpRequest(ctx, request);
		}

	}

	private void handleHttpRequest(ChannelHandlerContext context, HttpRequest request) throws Exception {
		/*
		 * 请求头判断是不是建立webSocket的请求 webSocket建立需要依赖http
		 */

		String webSocketKey = request.headers().get(HttpHeaderNames.SEC_WEBSOCKET_KEY);
		if (StrUtil.isNotBlank(webSocketKey)) {
			String uri = request.uri();

			WebSocketEndPointDefine endPoint = EndPointHelper.getEndPoint(uri);
			if (ObjectUtil.isNull(endPoint)) {
				// 不处理事件
				LOGGER.warn("无效(没有相关处理方法)的webSocket请求:{}", request.uri());
				return;
			}
			/* 保存路径参数 */
			WebSocketUrlParam parameters = UriParameterUtil.parameters(uri, endPoint.getUri(), context.channel());
			WebSocketParamHolder.add(context.channel(), parameters);

			EndPointHelper.addEndPoint(context.channel(), endPoint);

			Method openMethod = endPoint.getOpenMethod();

			// 用户定义的onOpen事件
			invokeResult(openMethod, null, context.channel());

			/* 处理握手事件 */
			WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
					"ws://localhost:8888/websocket", null, false);
			handShaker = wsFactory.newHandshaker(request);
			if (handShaker == null) {
				WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(context.channel());
			} else {
				handShaker.handshake(context.channel(), request);
			}
		} else {
			super.channelRead(context, request);
		}

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		WebSocketEndPointDefine endPoint = EndPointHelper.getEndPoint(ctx.channel());
		if (ObjectUtil.isNotNull(endPoint)) {
			Method method = endPoint.getMsgMethod();
			invokeResult(method, null, ctx.channel());
		}
		super.exceptionCaught(ctx, cause);
	}

	private void handleWebSocket(ChannelHandlerContext ctx, WebSocketFrame frame) {
		// 判断是否关闭链路的指令
		if (frame instanceof CloseWebSocketFrame) {
			handShaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
			return;
		}
		// 判断是否ping消息
		if (frame instanceof PingWebSocketFrame) {
			ctx.write(new PongWebSocketFrame(frame.content().retain()));
		}
		// todo
		// 用户的websocket context处理
		WebSocketEndPointDefine endPoint = EndPointHelper.getEndPoint(ctx.channel());

		if (endPoint == null) {
			LOGGER.info("没有相关处理器处理该webSocket请求");// 如果出现，程序出错了
			return;
		}
		Method method = endPoint.getMsgMethod();
		Channel channel = ctx.channel();

		invokeResult(method, frame, channel);

		// ctx.writeAndFlush("shodao");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		/* 可以派发onClose */
		WebSocketEndPointDefine endPoint = EndPointHelper.getEndPoint(ctx.channel());
		if (ObjectUtil.isNotNull(endPoint)) {
			Method method = endPoint.getCloseMethod();
			invokeResult(method, null, ctx.channel());
		}
		super.channelInactive(ctx);
	}

	private void invokeResult(Method method, WebSocketFrame frame, Channel channel) {
		try {

			if (method == null) {
				return;
			}
			Class<?> target = method.getDeclaringClass();
			Object bean = webContext.getWebSocketContext().getEndPointTarget(target);
			if (ObjectUtil.isNull(bean)) {
				bean = target.newInstance();
			}

			Object[] methodParam = EndPointHelper.getParam(frame, method, channel, null);
			ReflectUtil.invoke(bean, method, methodParam);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
