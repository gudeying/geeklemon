package cn.geeklemon.server.websocket.param;


import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/22 9:34
 * Modified by : kavingu
 */
public class WebSocketParamHolder {
    private static final Map<Channel, WebSocketUrlParam> PARAM_MAP = new HashMap<>();
    private static final java.util.concurrent.locks.ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public static void add(Channel channel, WebSocketUrlParam param) {
        readWriteLock.writeLock().lock();
        try {
            PARAM_MAP.put(channel, param);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public static void remove(Channel channel) {
        readWriteLock.writeLock().lock();
        try {
            PARAM_MAP.remove(channel);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    public static String attr(Channel channel, String name) {
        readWriteLock.readLock().lock();
        try {
            return PARAM_MAP.get(channel).get(name);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public static WebSocketUrlParam getChannelParams(Channel channel) {
        return PARAM_MAP.get(channel);
    }
}
