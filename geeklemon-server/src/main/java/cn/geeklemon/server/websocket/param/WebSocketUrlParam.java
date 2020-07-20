package cn.geeklemon.server.websocket.param;

import cn.hutool.core.lang.Assert;
import io.netty.channel.Channel;

import java.util.HashMap;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/22 9:36
 * Modified by : kavingu
 */
public class WebSocketUrlParam extends HashMap<String, String> {
    private Channel channel;

    public WebSocketUrlParam(Channel channel) {
        super();
        Assert.notNull(channel);
        this.channel = channel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        WebSocketUrlParam that = (WebSocketUrlParam) o;

        return channel != null ? channel.equals(that.channel) : that.channel == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (channel != null ? channel.hashCode() : 0);
        return result;
    }
}
