package cn.geeklemon.server.websocket.context;

import cn.geeklemon.server.websocket.support.WebSocketEndPointDefine;
import io.netty.channel.Channel;

public interface WebSocketContext {
	WebSocketEndPointDefine getWebSocketPoint(String url);

	Object getEndPointTarget(Class<?> cls);

	WebSocketEndPointDefine getWebSocketPoint(Channel channel);

}
