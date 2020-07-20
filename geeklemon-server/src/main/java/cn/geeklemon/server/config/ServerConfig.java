package cn.geeklemon.server.config;

/**
 * @author : Kavin Gu Project Name : geeklemon Description :
 * @version : ${VERSION} 2019/9/12 10:34 Modified by : kavingu
 */
public interface ServerConfig {

	public int getPort();

	public int workSize();

	public int bossSize();

	int sessionTimeout();

}
