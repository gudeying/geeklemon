package cn.geekelmon.cache.context;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;

public class DefaultCacheProvider implements CacheProvider {
    private final TimedCache<Object, Object> timedCache;
    /**
     * 默认一分钟过期
     */
    private final long defaultTimeOut;

    public DefaultCacheProvider(long defaultTimeOut, long pruneDelay) {
        this.defaultTimeOut = defaultTimeOut;
        this.timedCache = CacheUtil.newTimedCache(defaultTimeOut);
        timedCache.schedulePrune(pruneDelay);
    }


    @Override
    public Object getCacheValue(Object cacheKey) {
        return timedCache.get(cacheKey, false);
    }

    @Override
    public void putCache(Object cacheKey, Object value, long timeout) {
        timedCache.put(cacheKey, value, timeout);
    }

    @Override
    public void putCache(Object cacheKey, Object value) {
        timedCache.put(cacheKey, value, defaultTimeOut);
    }
}
