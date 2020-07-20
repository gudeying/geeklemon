package cn.geekelmon.cluster.bootstrap;

import io.netty.channel.Channel;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/14 9:18
 * Modified by : kavingu
 */
public class ServiceBusDefine {
    private Channel channel;

    public ServiceBusDefine(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }
}
