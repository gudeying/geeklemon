package cn.geeklemon.server.context;

import java.util.List;

import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.server.config.ServerConfig;
import cn.geeklemon.server.controller.ControllerDefine;
import cn.geeklemon.server.filter.WebFilter;
import cn.geeklemon.server.intercepter.WebInterceptor;
import cn.geeklemon.server.request.HttpRequest;
import cn.geeklemon.server.request.RequestDefine;
import cn.geeklemon.server.viewrender.ModelAndView;
import cn.geeklemon.server.viewrender.ViewEngineConfig;
import cn.geeklemon.server.websocket.context.WebSocketContext;
import cn.geeklemon.server.websocket.support.WebSocketEndPointDefine;
import cn.hutool.extra.template.Engine;
import io.netty.channel.Channel;

/**
 * @author : Kavin Gu Project Name : geeklemon Description :
 * @version : ${VERSION} 2019/9/23 9:10 Modified by : kavingu
 */
public interface WebContext {

	ControllerDefine getController(RequestDefine define);

	ModelAndView invokForModelAndView(ControllerDefine define, HttpRequest request) throws Exception;

	List<WebFilter> getFilters(String url);

	List<WebInterceptor> getInterceptors(String url);

	Engine getEngine();

	ViewEngineConfig getViewEngineConfig();

	ServerConfig getServerConfig();

	WebSocketContext getWebSocketContext();

	void addController(Object controllerAnnotated);

	void addFilter(WebFilter filter);

	void addInterceptor(WebInterceptor interceptor);

	void addWebSocketEndPoint(Object endPoint);

	void setEngine(Engine engine);

	void setViewEngineConfig(ViewEngineConfig config);

	void setServerConfig(ServerConfig serverConfig);
}
