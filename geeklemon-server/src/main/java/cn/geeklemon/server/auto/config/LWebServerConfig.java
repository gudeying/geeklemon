package cn.geeklemon.server.auto.config;

import cn.geeklemon.server.config.*;

public class LWebServerConfig implements ServerConfig {
	private int port = 8080;
	private int workSize = 16;
	private int bossSize = 4;

	private int sessionTimeout = 3600 * 1000;

	@Override
	public int workSize() {
		return workSize;
	}

	@Override
	public int bossSize() {
		return bossSize;
	}

	@Override
	public int sessionTimeout() {
		return sessionTimeout;
	}

	@Override
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getWorkSize() {
		return workSize;
	}

	public void setWorkSize(int workSize) {
		this.workSize = workSize;
	}

	public int getBossSize() {
		return bossSize;
	}

	public void setBossSize(int bossSize) {
		this.bossSize = bossSize;
	}

	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

}
