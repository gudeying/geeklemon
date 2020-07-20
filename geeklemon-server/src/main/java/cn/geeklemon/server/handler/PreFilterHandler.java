package cn.geeklemon.server.handler;

import cn.geeklemon.server.request.HttpRequest;

public interface PreFilterHandler {
	public abstract PreHandlerResult handle(HttpRequest request);
}
