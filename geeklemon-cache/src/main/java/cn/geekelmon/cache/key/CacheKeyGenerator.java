package cn.geekelmon.cache.key;

import java.lang.reflect.Method;

public interface CacheKeyGenerator {
    Object getCacheKey(Object object, Method method, Object[] args);
}
