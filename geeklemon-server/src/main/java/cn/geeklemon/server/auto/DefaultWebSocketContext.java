package cn.geeklemon.server.auto;

import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.server.websocket.context.EndPointHelper;
import cn.geeklemon.server.websocket.context.WebSocketContext;
import cn.geeklemon.server.websocket.support.WebSocketEndPointDefine;
import io.netty.channel.Channel;

public class DefaultWebSocketContext implements WebSocketContext {
	private ApplicationContext applicationContext;

	public DefaultWebSocketContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public WebSocketEndPointDefine getWebSocketPoint(String url) {
		return EndPointHelper.getEndPoint(url);
	}

	@Override
	public Object getEndPointTarget(Class<?> cls) {
		return applicationContext.getBean(cls);
	}

	@Override
	public WebSocketEndPointDefine getWebSocketPoint(Channel channel) {
		return EndPointHelper.getEndPoint(channel);
	}

}
