package cn.geekelmon.cache.context;

public interface CacheProvider {
    Object getCacheValue(Object cacheKey);

    void putCache(Object cacheKey, Object value);

    void putCache(Object cacheKey, Object value, long timeout);
}
