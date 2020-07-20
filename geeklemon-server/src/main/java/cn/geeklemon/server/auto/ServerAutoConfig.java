package cn.geeklemon.server.auto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.geeklemon.core.bean.factory.InitializingBean;
import cn.geeklemon.core.context.annotation.Autowired;
import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.core.util.PropsUtil;
import cn.geeklemon.server.auto.config.LWebServerConfig;
import cn.geeklemon.server.config.ServerConfig;
import cn.geeklemon.server.viewrender.ViewEngineConfig;
import cn.geeklemon.server.viewrender.engine.SimpleFileToStringEngine;
import cn.geeklemon.server.websocket.context.WebSocketContext;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.extra.template.Engine;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateException;
import cn.hutool.extra.template.TemplateUtil;
import cn.hutool.extra.template.TemplateConfig.ResourceMode;

public class ServerAutoConfig implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServerAutoConfig.class);

	@Autowired
	private ApplicationContext context;

	@Override
	public void afterPropsSet() {
		AutoWebContext webContext = new AutoWebContext(context);
		ServerConfig serverConfig = context.getBean(ServerConfig.class);
		if (serverConfig == null) {
			serverConfig = buildContext();
		}
		webContext.setServerConfig(serverConfig);
		WebSocketContext socketContext = context.getBean(WebSocketContext.class);
		if (socketContext == null) {
			socketContext = buildSocketContext();
		}
		webContext.setWebSocketContext(socketContext);

		ViewEngineConfig engineConfig = context.getBean(ViewEngineConfig.class);
		if (engineConfig != null) {
			webContext.setViewEngineConfig(engineConfig);
		} else {
			engineConfig = buildDefaultEngineConfig();
		}
		webContext.setViewEngineConfig(engineConfig);

		Engine engine = context.getBean(Engine.class);
		if (engine == null) {
			try {
				engine = TemplateUtil.createEngine(new TemplateConfig("", ResourceMode.CLASSPATH));
			} catch (TemplateException e) {
				LOGGER.warn("未配置模板引擎，将使用简单的文件转换");
				engine = new SimpleFileToStringEngine(engineConfig.getTemplatePath());
			}
		}

		webContext.setEngine(engine);
		ManagedWebServer webServer = new ManagedWebServer(webContext);
		ThreadUtil.excAsync(new Runnable() {
			@Override
			public void run() {
				webServer.start();
			}
		}, false);
	}

	private ViewEngineConfig buildDefaultEngineConfig() {
		ViewEngineConfig config = new ViewEngineConfig();
		String charSet = PropsUtil.getInstance().getValue(String.class, "template.charset");
		if (charSet != null) {
			config.setCharset(charSet);
		}
		String path = PropsUtil.getInstance().getValue(String.class, "template.path");
		if (path != null) {
			config.setTemplatePath(path);
		}

		String suffix = PropsUtil.getInstance().getValue(String.class, "template.suffix");
		if (suffix != null) {
			config.setSuffix(suffix);
		}
		String prefix = PropsUtil.getInstance().getValue(String.class, "template.prefix");
		if (prefix != null) {
			config.setPrefix(prefix);
		}
		return config;
	}

	private ServerConfig buildContext() {

		LWebServerConfig serverConfig = new LWebServerConfig();
		Integer bossSize = PropsUtil.getInstance().getValue(Integer.class, "server.bossGroupSize");
		if (bossSize != null && bossSize > 1) {
			serverConfig.setBossSize(bossSize);
		}
		Integer workSize = PropsUtil.getInstance().getValue(Integer.class, "server.workGroupSize");
		if (workSize != null && workSize > 1) {
			serverConfig.setWorkSize(workSize);
		}

		Integer port = PropsUtil.getInstance().getValue(Integer.class, "server.port");
		if (port != null && port > 1) {
			serverConfig.setPort(port);
		}
		Integer sessionTimeout = PropsUtil.getInstance().getValue(Integer.class, "server.sessionTimeout");
		if (sessionTimeout != null && sessionTimeout > 1000) {
			serverConfig.setSessionTimeout(sessionTimeout);
		}

		return serverConfig;

	}

	private WebSocketContext buildSocketContext() {
		return new DefaultWebSocketContext(context);
	}
}
