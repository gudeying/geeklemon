package cn.geekelmon.cache.key;

import java.lang.reflect.Method;

/**
 * by : kavingu
 */
public class DefaultCacheKeyGenerator implements CacheKeyGenerator {

    @Override
    public Object getCacheKey(Object object, Method method, Object[] args) {
        return new DefaultCacheKey(object, method, args);
    }
}
