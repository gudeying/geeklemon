package cn.geeklemon.server.websocket.context;

import cn.geeklemon.core.context.annotation.Value;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/22 8:41
 * Modified by : kavingu
 */
public class WebSocketConfig {
    @Value(name = "")
    private int port;
    private String prefix;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
