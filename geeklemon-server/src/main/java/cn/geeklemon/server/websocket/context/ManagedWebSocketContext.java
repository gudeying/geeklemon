package cn.geeklemon.server.websocket.context;

import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.server.websocket.support.WebSocketEndPointDefine;
import io.netty.channel.Channel;

public class ManagedWebSocketContext implements WebSocketContext {
	private ApplicationContext context;

	@Override
	public WebSocketEndPointDefine getWebSocketPoint(String url) {
		return EndPointHelper.getEndPoint(url);
	}

	@Override
	public Object getEndPointTarget(Class<?> cls) {
		return context.getBean(cls);
	}

	@Override
	public WebSocketEndPointDefine getWebSocketPoint(Channel channel) {
		return EndPointHelper.getEndPoint(channel);
	}

}
